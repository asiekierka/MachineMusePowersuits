package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.api.module.EnumModuleTarget;
import net.machinemuse.numina.api.module.IPlayerTickModule;
import net.machinemuse.numina.api.module.IToggleableModule;
import net.machinemuse.numina.api.module.ModuleManager;
import net.machinemuse.powersuits.api.constants.MPSModuleConstants;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.item.powersuits.module.PowerModuleBase;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.powersuits.utils.MuseItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Objects;

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends PowerModuleBase implements IToggleableModule, IPlayerTickModule {
    public static final String SPRINT_ENERGY_CONSUMPTION = "Sprint Energy Consumption";
    public static final String SPRINT_SPEED_MULTIPLIER = "Sprint Speed Multiplier";
    public static final String SPRINT_FOOD_COMPENSATION = "Sprint Exhaustion Compensation";
    public static final String WALKING_ENERGY_CONSUMPTION = "Walking Energy Consumption";
    public static final String WALKING_SPEED_MULTIPLIER = "Walking Speed Multiplier";
    public static final UUID TAGUUID = new UUID(-7931854408382894632L, -8160638015224787553L);

    public SprintAssistModule(String resourceDommain, String UnlocalizedName) {
        super(EnumModuleTarget.LEGSONLY, resourceDommain, UnlocalizedName);
        addSimpleTradeoffDouble(this, "Power", SPRINT_ENERGY_CONSUMPTION, "J", 0, 10, SPRINT_SPEED_MULTIPLIER, "%", 1, 2);
        addSimpleTradeoffDouble(this, "Compensation", SPRINT_ENERGY_CONSUMPTION, "J", 0, 2, SPRINT_FOOD_COMPENSATION, "%", 0, 1);
        addSimpleTradeoffDouble(this, "Walking Assist", WALKING_ENERGY_CONSUMPTION, "J", 0, 10, WALKING_SPEED_MULTIPLIER, "%", 1, 1);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.servoMotor, 4));
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (item == player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) { // now you actually have to wear these to get the speed boost
//            double motionX = player.posX - player.lastTickPosX;
//            double motionY = player.posY - player.lastTickPosY;
//            double motionZ = player.posZ - player.lastTickPosZ;
//            double horzMovement = Math.sqrt(motionX * motionX + motionZ * motionZ);

            double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
            double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
            if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                if (player.isSprinting()) {
                    double exhaustion = Math.round(horzMovement * 100.0F) * 0.01;
                    double sprintCost = ModuleManager.getInstance().computeModularPropertyDouble(item, SPRINT_ENERGY_CONSUMPTION);
                    if (sprintCost < totalEnergy) {
                        double sprintMultiplier = ModuleManager.getInstance().computeModularPropertyDouble(item, SPRINT_SPEED_MULTIPLIER);
                        double exhaustionComp = ModuleManager.getInstance().computeModularPropertyDouble(item, SPRINT_FOOD_COMPENSATION);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                        setMovementModifier(item, sprintMultiplier * 1.2);
                        player.getFoodStats().addExhaustion((float) (-0.01 * exhaustion * exhaustionComp));
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                } else {
                    double cost = ModuleManager.getInstance().computeModularPropertyDouble(item, WALKING_ENERGY_CONSUMPTION);
                    if (cost < totalEnergy) {
                        double walkMultiplier = ModuleManager.getInstance().computeModularPropertyDouble(item, WALKING_SPEED_MULTIPLIER);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                        setMovementModifier(item, walkMultiplier);
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                }
            }
        } else
            onPlayerTickInactive(player, item);
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        if (item != null) {
            NBTTagList modifiers = item.getTagCompound().getTagList("AttributeModifiers", (byte) 10);
            if (!modifiers.hasNoTags()) {
                for (int i = 0; i < modifiers.tagCount(); i++) {
                    NBTTagCompound tag = modifiers.getCompoundTagAt(i);
                    if (Objects.equals(new net.machinemuse.powersuits.item.module.movement.AttributeModifier(tag).name, "Sprint Assist")) {
                        tag.setDouble("Amount", 0);
                    }
                }
            }
        }
    }

    public void setMovementModifier(ItemStack item, double multiplier) {
        NBTTagList modifiers = item.getTagCompound().getTagList("AttributeModifiers", (byte) 10); // Type 10 for tag compound
        NBTTagCompound sprintModifiers = new NBTTagCompound();
        item.getTagCompound().setTag("AttributeModifiers", modifiers);
        for (int i = 0; i < modifiers.tagCount(); i++) {
            NBTTagCompound tag = modifiers.getCompoundTagAt(i);
            if (Objects.equals(new AttributeModifier(tag).name, "Sprint Assist")) {
                sprintModifiers = tag;
                sprintModifiers.setInteger("Operation", 1);
                sprintModifiers.setDouble("Amount", multiplier - 1);
                sprintModifiers.setString("Slot", EntityEquipmentSlot.LEGS.getName());
            }
        }

        //TODO: fix this so knockback does not have to be readded
        // this should be when first created
        if (sprintModifiers.hasNoTags()) {
            modifiers.appendTag(new AttributeModifier(1, TAGUUID, multiplier - 1, "generic.movementSpeed", "Sprint Assist", EntityEquipmentSlot.LEGS).toNBT());

            // add knockback resistance back because it doesn't show in tooltip after AttributeModifiers tag is added
            modifiers.appendTag(new AttributeModifier(0, TAGUUID, 0.25,
                    SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), EntityEquipmentSlot.LEGS).toNBT());

        }
    }

    @Override
    public String getCategory() {
        return MPSModuleConstants.CATEGORY_MOVEMENT;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.sprintAssist;
    }
}