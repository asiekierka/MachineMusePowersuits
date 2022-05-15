package lehjr.powersuits.client.control;

import lehjr.numina.network.NuminaPackets;
import lehjr.numina.network.packets.ToggleRequestPacket;
import lehjr.powersuits.client.event.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputConstants;
import net.minecraft.util.ResourceLocation;

public class MPSKeyBinding extends KeyBinding {
    public final ResourceLocation registryName;
    public boolean showOnHud = true;
    public boolean toggleval = false;

    public MPSKeyBinding(ResourceLocation registryName, String name, int key, String category) {
        super(name, key, category);
        this.registryName = registryName;
    }

    /**
     * Do not use this
     */
    @Override
    public void setKey(InputConstants.Input key) {
        super.setKey(key);
        KeybindManager.INSTANCE.writeOutKeybindSetings();
        RenderEventHandler.INSTANCE.makeKBDisplayList();
    }

    /**
     * Use this one to set the key from inside MPS
     * @param key
     */
    public void setKeyInternal(InputConstants.Input key) {
        super.setKey(key);
    }


    public void toggleModules() {
        ClientPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
    // FIXME: needed client side?
//        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
//            player.getInventory().getItem(i).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
//                    .filter(IModularItem.class::isInstance)
//                    .map(IModularItem.class::cast)
//                    .ifPresent(handler -> handler.toggleModule(registryName, toggleval));
//        }
        NuminaPackets.CHANNEL_INSTANCE.sendToServer(new ToggleRequestPacket(registryName, toggleval));

        toggleval = !toggleval;
    }
}