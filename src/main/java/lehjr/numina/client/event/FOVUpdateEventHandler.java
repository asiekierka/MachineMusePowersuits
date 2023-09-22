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

package lehjr.numina.client.event;

import lehjr.numina.common.config.NuminaSettings;
import lehjr.numina.common.constants.NuminaConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:07 PM, 10/17/13
 * <p>
 * Ported to Java by lehjr on 10/10/16.
 */

@OnlyIn(Dist.CLIENT)
public class FOVUpdateEventHandler {


    public FOVUpdateEventHandler() {

    }

    public static final Lazy<KeyMapping> fovToggleKey = Lazy.of(()-> new KeyMapping("key..numina.fovfixtoggle", GLFW.GLFW_KEY_UNKNOWN, "key.categories." + NuminaConstants.MOD_ID));

    public boolean fovIsActive = NuminaSettings.fovFixDefaultState();





    @SubscribeEvent
    public void onFOVUpdate(ComputeFovModifierEvent e) {
        if (NuminaSettings.useFovFix()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (fovToggleKey.get().consumeClick()) {
                fovIsActive = !fovIsActive;
                if (fovIsActive) {
                    player.sendSystemMessage(Component.translatable("message.numina.fovfixtoggle.disabled"));//, player.getUUID());
                } else {
                    player.sendSystemMessage(Component.translatable("message.numina.fovfixtoggle.disabled"));//, player.getUUID());
                }
            }

            if (fovIsActive) {
                AttributeInstance attributeinstance = e.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
                e.setNewFovModifier((float) (e.getNewFovModifier() / ((attributeinstance.getValue() / e.getPlayer().getAbilities().getWalkingSpeed() + 1.0) / 2.0)));if (NuminaSettings.useFovNormalize()) {
                    if (e.getPlayer().isSprinting()) {
                        e.setNewFovModifier(e.getNewFovModifier() + 0.15F);
                    }
                }
            }
        }
    }
}