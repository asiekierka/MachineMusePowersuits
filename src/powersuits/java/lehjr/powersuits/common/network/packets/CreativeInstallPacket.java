package lehjr.powersuits.common.network.packets;

import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.item.ItemUtils;
import lehjr.numina.common.network.NuminaPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CreativeInstallPacket {
    protected EquipmentSlot slotType;
    protected ResourceLocation regName;

    public CreativeInstallPacket() {
    }

    public CreativeInstallPacket(EquipmentSlot slotType, ResourceLocation regName) {
        this.slotType = slotType;
        this.regName = regName;
    }

    public static void write(CreativeInstallPacket msg, FriendlyByteBuf packetBuffer) {
        packetBuffer.writeEnum(msg.slotType);
        packetBuffer.writeResourceLocation(msg.regName);
    }

    public static CreativeInstallPacket read(FriendlyByteBuf packetBuffer) {
        return new CreativeInstallPacket(packetBuffer.readEnum(EquipmentSlot.class), packetBuffer.readResourceLocation());
    }

    public static void sendToClient(ServerPlayer entity, EquipmentSlot slotType, ResourceLocation regName) {
        NuminaPackets.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity),
                new CreativeInstallPacket(slotType, regName));
    }


    public static void handle(CreativeInstallPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final Player player = ctx.get().getSender();
            EquipmentSlot slotType = message.slotType;
            ResourceLocation regName = message.regName;
            Item item = ForgeRegistries.ITEMS.getValue(regName);
            ItemStack module = new ItemStack(item, 1);

            module.getCapability(ForgeCapabilities.ENERGY).ifPresent(iEnergyStorage -> iEnergyStorage.receiveEnergy(iEnergyStorage.getMaxEnergyStored(), false));

            ItemUtils.getItemFromEntitySlot(player, slotType).getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .ifPresent(iModularItem -> {
                        if (!iModularItem.getInstalledModuleNames().contains(regName)) {
                            for (int index = 0; index < iModularItem.getSlots(); index++) {
                                if (iModularItem.insertItem(index, module, false).sameItem(ItemStack.EMPTY)) {
                                    break;
                                }
                            }
                        }
                    });

            if (player instanceof ServerPlayer) {
                sendToClient((ServerPlayer) player, slotType, regName);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}