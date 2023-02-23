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

package lehjr.powersuits.common.event;

import lehjr.numina.client.control.PlayerMovementInputWrapper;
import lehjr.numina.client.sound.Musique;
import lehjr.numina.client.sound.SoundDictionary;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.capabilities.module.powermodule.PowerModuleCapability;
import lehjr.numina.common.config.NuminaSettings;
import lehjr.numina.common.energy.ElectricItemUtils;
import lehjr.numina.common.math.MathUtils;
import lehjr.numina.common.player.PlayerUtils;
import lehjr.powersuits.client.sound.MPSSoundDictionary;
import lehjr.powersuits.common.config.MPSSettings;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.constants.MPSRegistryNames;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum MovementManager {
    INSTANCE;
    static final double root2 = 1/Math.sqrt(2);
    public static final Map<UUID, Double> playerJumpMultipliers = new HashMap();
    /**
     * Gravity, in meters per tick per tick.
     */
    public static final double DEFAULT_GRAVITY = -0.0784000015258789;

    public double getPlayerJumpMultiplier(PlayerEntity player) {
        if (playerJumpMultipliers.containsKey(player.getUUID())) {
            return playerJumpMultipliers.get(player.getUUID());
        } else {
            return 0;
        }
    }

    public void setPlayerJumpTicks(PlayerEntity player, double number) {
        playerJumpMultipliers.put(player.getUUID(), number);
    }

    public double computeFallHeightFromVelocity(double velocity) {
        double ticks = velocity / DEFAULT_GRAVITY;
        return -0.5 * DEFAULT_GRAVITY * ticks * ticks;
    }

    public static void removeModifiers(@Nonnull ItemStack itemStack) {
        itemStack.removeTagKey("AttributeModifiers");
    }

    static double boolToVal(boolean boolIn) {
        return boolIn ? 1.0D : 0.0D;
    }

    public static double thrust(PlayerEntity player, double thrust, boolean flightControl) {
        PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
        double thrustUsed = 0;
        if (flightControl) {
            Vector3d desiredDirection = player.getLookAngle().normalize();
            double strafeX = desiredDirection.z;
            double strafeZ = -desiredDirection.x;

            double flightVerticality = player.getItemBySlot(EquipmentSlotType.HEAD).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .map(iModularItem -> iModularItem
                            .getOnlineModuleOrEmpty(MPSRegistryNames.FLIGHT_CONTROL_MODULE)
                            .getCapability(PowerModuleCapability.POWER_MODULE)
                            .map(pm -> pm.applyPropertyModifiers(MPSConstants.FLIGHT_VERTICALITY)).orElse(0D)).orElse(0D);

            int strafeState = ((playerInput.strafeRightKey ? -1 : 0) + (playerInput.strafeLeftKey ? 1 : 0));
            double forwardReverse = ((playerInput.reverseKey ? -1D : 0) + (playerInput.forwardKey ? 1D : 0));

            desiredDirection = new Vector3d(
                    (desiredDirection.x * forwardReverse + strafeX * strafeState),
                    (flightVerticality * desiredDirection.y * boolToVal(playerInput.forwardKey) + boolToVal(playerInput.jumpKey) - boolToVal(playerInput.downKey)),
                    (desiredDirection.z * forwardReverse + strafeZ * strafeState));

            desiredDirection = desiredDirection.normalize();

            // Brakes
            if (player.getDeltaMovement().y < 0 && desiredDirection.y >= 0) {
                if (-player.getDeltaMovement().y > thrust) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, thrust,0));
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrust -= player.getDeltaMovement().y;
                    thrustUsed += player.getDeltaMovement().y;
                    player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                }
            }

            if (player.getDeltaMovement().y < -1) {
                thrust += 1 + player.getDeltaMovement().y;
                thrustUsed -= 1 + player.getDeltaMovement().y;
                player.setDeltaMovement(player.getDeltaMovement().x, -1, player.getDeltaMovement().z);
            }

            if (Math.abs(player.getDeltaMovement().x) > 0 && desiredDirection.length() == 0) {
                if (Math.abs(player.getDeltaMovement().x) > thrust) {
                    player.setDeltaMovement(player.getDeltaMovement().add(
                            -(Math.signum(player.getDeltaMovement().x) * thrust), 0, 0));
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrust -= Math.abs(player.getDeltaMovement().x);
                    thrustUsed += Math.abs(player.getDeltaMovement().x);
                    player.setDeltaMovement(0, player.getDeltaMovement().y, player.getDeltaMovement().z);
                }
            }

            if (Math.abs(player.getDeltaMovement().z) > 0 && desiredDirection.length() == 0) {
                if (Math.abs(player.getDeltaMovement().z) > thrust) {
                    player.setDeltaMovement(
                            player.getDeltaMovement().subtract(
                                    0, 0, Math.signum(player.getDeltaMovement().z) * thrust));
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrustUsed += Math.abs(player.getDeltaMovement().z);
                    thrust -= Math.abs(player.getDeltaMovement().z);
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y, 0);
                }
            }
            // Thrusting, finally :V
            player.setDeltaMovement(player.getDeltaMovement().add(
                    thrust * desiredDirection.x,
                    thrust * desiredDirection.y,
                    thrust * desiredDirection.z
            ));
            thrustUsed += thrust;


        } else {
            Vector3d desiredDirection = player.getLookAngle().normalize();
            desiredDirection = new Vector3d(desiredDirection.x, 0, desiredDirection.z);
            desiredDirection.normalize();
            if (!playerInput.forwardKey) {
                player.setDeltaMovement(player.getDeltaMovement().add(0, thrust, 0));
            } else {

//                System.out.println("thrust: " + thrust +", thrust * root2: " + (thrust * root2) );

                player.setDeltaMovement(player.getDeltaMovement().add(
                        desiredDirection.x * thrust * root2 * boolToVal(playerInput.forwardKey),
                        thrust * root2,
                        desiredDirection.z * thrust * root2// * Math.signum(playerInput.forwardKey)
                ));
            }
            thrustUsed += thrust;
        }
        // Slow the player if they are going too fast
        double horzm2 = player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z;

        // currently comes out to 0.0625
        double horizontalLimit = MPSSettings.getMaxFlyingSpeed() * MPSSettings.getMaxFlyingSpeed() / 400;

        if (player.isCrouching() && horizontalLimit > 0.05) {
            horizontalLimit = 0.05;
        }

        if (horzm2 > horizontalLimit) {
            double ratio = Math.sqrt(horizontalLimit / horzm2);
            player.setDeltaMovement(
                    player.getDeltaMovement().x * ratio,
                    player.getDeltaMovement().y,
                    player.getDeltaMovement().z * ratio);
        }

        PlayerUtils.resetFloatKickTicks(player);
        return thrustUsed;
    }



    public static double computePlayerVelocity(PlayerEntity player) {
        return MathUtils.pythag(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
    }

   @SubscribeEvent
    public void handleLivingJumpEvent(LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getItemBySlot(EquipmentSlotType.LEGS).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .ifPresent(iModularItem -> iModularItem.getOnlineModuleOrEmpty(MPSRegistryNames.JUMP_ASSIST_MODULE).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(jumper -> {
                        double jumpAssist = jumper.applyPropertyModifiers(MPSConstants.MULTIPLIER) * 2;
                        double drain = jumper.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                        int avail = ElectricItemUtils.getPlayerEnergy(player);
                        if ((player.level.isClientSide()) && NuminaSettings.useSounds()) {
                            Musique.playerSound(player, MPSSoundDictionary.JUMP_ASSIST, SoundCategory.PLAYERS, (float) (jumpAssist / 8.0), (float) 1, false);
                        }

                        if (drain < avail) {
                            ElectricItemUtils.drainPlayerEnergy(player, (int) drain, false);
                            setPlayerJumpTicks(player, jumpAssist);
                            double jumpCompensationRatio = jumper.applyPropertyModifiers(MPSConstants.FOOD_COMPENSATION);
                            if (player.isSprinting()) {
                                player.getFoodData().addExhaustion((float) (-0.2F * jumpCompensationRatio));
                            } else {
                                player.getFoodData().addExhaustion((float) (-0.05F * jumpCompensationRatio));
                            }
                        }
                    }));
        }
    }

    @SubscribeEvent
    public void handleFallEvent(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && event.getDistance() > 3.0) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getItemBySlot(EquipmentSlotType.FEET).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .ifPresent(iModularItem -> iModularItem.getOnlineModuleOrEmpty(MPSRegistryNames.SHOCK_ABSORBER_MODULE).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(sa -> {
                        double distanceAbsorb = event.getDistance() * sa.applyPropertyModifiers(MPSConstants.MULTIPLIER);
                        if (player.level.isClientSide && NuminaSettings.useSounds()) {
                            Musique.playerSound(player, SoundDictionary.SOUND_EVENT_GUI_INSTALL, SoundCategory.PLAYERS, (float) (distanceAbsorb), (float) 1, false);
                        }
                        double drain = distanceAbsorb * sa.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                        int avail = ElectricItemUtils.getPlayerEnergy(player);
                        if (drain < avail) {
                            ElectricItemUtils.drainPlayerEnergy(player, (int) drain, false);
                            event.setDistance((float) (event.getDistance() - distanceAbsorb));
    //                        event.getEntityLiving().sendMessage(new TextComponentString("modified fall settings: [ damage : " + event.getDamageMultiplier() + " ], [ distance : " + event.getDistance() + " ]"));
                        }
                    }));
        }
    }
}
