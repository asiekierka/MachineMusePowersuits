package com.lehjr.numina.common.capabilities;

import com.lehjr.numina.common.capabilities.module.powermodule.*;
import com.lehjr.numina.common.capabilities.energy.ModuleEnergyWrapper;
import com.lehjr.numina.common.config.NuminaSettings;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import com.lehjr.numina.common.constants.TagConstants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BatteryCapabilityProvider implements ICapabilityProvider {

    public final PowerModule powerModule;
    private final ModuleEnergyWrapper energyStorage;

    private final LazyOptional<IPowerModule> powerModuleHolder;
    private final LazyOptional<IEnergyStorage> energyStorageHolder;

    public BatteryCapabilityProvider(final ItemStack module, int tier, int maxEnergy, int maxTransfer) {
        powerModule = new PowerModule(module, ModuleCategory.ENERGY_STORAGE, ModuleTarget.ALLITEMS, NuminaSettings::getModuleConfig) {
            @Override
            public int getTier() {
                return tier;
            }

            @Override
            public String getModuleGroup() {
                return "Battery";
            }

            {
                addBaseProperty(TagConstants.MAX_ENERGY, maxEnergy, "FE");
                addBaseProperty(TagConstants.MAX_TRAMSFER, maxTransfer, "FE");
            }};

        powerModuleHolder = LazyOptional.of(() -> powerModule);

        this.energyStorage = new ModuleEnergyWrapper(
                module,
                (int) powerModule.applyPropertyModifiers(TagConstants.MAX_ENERGY),
                (int) powerModule.applyPropertyModifiers(TagConstants.MAX_TRAMSFER)
        );

        energyStorageHolder = LazyOptional.of(() -> {
            energyStorage.onLoad();
            return energyStorage;
        });
    }

    /** ICapabilityProvider ----------------------------------------------------------------------- */
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        final LazyOptional<T> capabilityPowerModule = CapabilityPowerModule.POWER_MODULE.orEmpty(capability, powerModuleHolder);
        if (capabilityPowerModule.isPresent()) {
            return capabilityPowerModule;
        }

        final LazyOptional<T> energyCapability = CapabilityEnergy.ENERGY.orEmpty(capability, energyStorageHolder);
        if (energyCapability.isPresent()) {
            return energyCapability;
        }
        return LazyOptional.empty();
    }

    public void setMaxEnergy(){
        energyStorage.deserializeNBT(IntTag.valueOf(energyStorage.getMaxEnergyStored()));
        energyStorage.onValueChanged();
    }
}