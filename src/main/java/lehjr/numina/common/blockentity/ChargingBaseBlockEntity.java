/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lehjr.numina.common.blockentity;

import lehjr.numina.common.base.NuminaObjects;
import lehjr.numina.common.capabilities.energy.BlockEnergyStorage;
import lehjr.numina.common.capabilities.energy.BlockEnergyWrapper;
import lehjr.numina.common.config.NuminaSettings;
import lehjr.numina.common.energy.ElectricItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public class ChargingBaseBlockEntity extends BlockEntity {
    public ChargingBaseBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(NuminaObjects.CHARGING_BASE_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    /*
     * Fetch the entities within a given position
     */
    @Nullable
    public List<LivingEntity> getEntities() {
        assert level != null;
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(this.worldPosition), entity -> entity instanceof LivingEntity);
    }

    private final ItemStackHandler itemHandler = createHandler();
    private final BlockEnergyStorage energyStorage = createEnergy();

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IEnergyStorage> tileEnergy = LazyOptional.of(() -> energyStorage);

    private final BlockEnergyWrapper energyWrapperStorage = createWrapper();
    private final LazyOptional<IEnergyStorage> energyWrapper = LazyOptional.of(() -> energyWrapperStorage);

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
        tileEnergy.invalidate();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        ChargingBaseBlockEntity be = (ChargingBaseBlockEntity) blockEntity;

        if (level.isClientSide) {
            return;
        }

        List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, new AABB(be.getBlockPos()), entity -> entity instanceof LivingEntity);

        for (LivingEntity entity : entityList) {
            be.sendOutPower(entity);
        }

        BlockState newState = state.setValue(BlockStateProperties.POWERED, be.energyWrapper.map(IEnergyStorage::getEnergyStored).orElse(0) > 0);

        if (state != newState) {
            level.setBlock(be.getBlockPos(), newState, 3);
        }
    }

    private void sendOutPower(LivingEntity entity) {
        energyWrapper.ifPresent(wrapper-> {
            int received = (int)ElectricItemUtils.givePlayerEnergy(entity, wrapper.getEnergyStored(), false);
            if (received > 0) {
                wrapper.extractEnergy(received, false);
                setChanged();
            }
        });
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void load(CompoundTag nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        energyStorage.deserializeNBT(nbt.get("energy"));
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        save(pTag);
    }

    public CompoundTag save(CompoundTag nbt) {
        nbt.put("inv", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.serializeNBT());
        return nbt;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!stack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private BlockEnergyStorage createEnergy() {
        return new BlockEnergyStorage(NuminaSettings.chargingBaseMaxPower(), NuminaSettings.chargingBaseMaxPower()) {
            @Override
            public void onValueChanged() {
                setChanged();
            }
        };
    }

    private BlockEnergyWrapper createWrapper() {
        return new BlockEnergyWrapper(this.tileEnergy, this.handler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyWrapper.cast();
        }
        return super.getCapability(cap, side);
    }

    public LazyOptional<IEnergyStorage> getBatteryEnergyHandler() {
        return handler.map(iItemHandler -> iItemHandler.getStackInSlot(0).getCapability(ForgeCapabilities.ENERGY)).orElse(LazyOptional.empty());
    }

    public LazyOptional<IEnergyStorage> getBlockEnergyHandler() {
        return tileEnergy;
    }
}