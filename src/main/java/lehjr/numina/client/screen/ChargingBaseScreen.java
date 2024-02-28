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

package lehjr.numina.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import lehjr.numina.client.gui.meter.EnergyMeter;
import lehjr.numina.client.render.NuminaRenderer;
import lehjr.numina.common.config.NuminaSettings;
import lehjr.numina.common.constants.NuminaConstants;
import lehjr.numina.common.container.ChargingBaseMenu;
import lehjr.numina.common.map.NuminaRegistry;
import lehjr.numina.common.math.Color;
import lehjr.numina.common.string.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChargingBaseScreen extends AbstractContainerScreen<ChargingBaseMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(NuminaConstants.MOD_ID, "textures/gui/container/chargingbase.png");
    static final Component ENERGYSTRING = Component.translatable("numina.energy").append(": ");
    EnergyMeter energyMeter;

    public ChargingBaseScreen(ChargingBaseMenu pMenu, Inventory pInventory, Component pTitle) {
        super(pMenu, pInventory, pTitle);
        energyMeter = new EnergyMeter(NuminaSettings::getEnergyMeterConfig);
    }

    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(gfx);
        super.render(gfx, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(gfx, pMouseX, pMouseY);
        energyMeter.draw(gfx, 71 + leftPos, 58 + topPos, menu.getEnergyForMeter());
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = this.leftPos;
        int j = this.topPos;
        gfx.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        super.renderLabels(gfx, mouseX, mouseY);
        StringUtils.drawShadowedString(gfx, ENERGYSTRING,
                (float)(imageWidth - 102 - font.width(ENERGYSTRING)),
                (float)(this.imageHeight - 108.0),
                new Color(4210752));

        String energyString = new StringBuilder()
                .append(StringUtils.formatNumberShort(menu.getEnergy()))
                .append(" FE").toString();

        StringUtils.drawShadowedString(gfx,
                Component.literal(energyString),
                (float)(imageWidth -71),
                (float)(this.imageHeight - 108.0),
                new Color(4210752));
    }
}