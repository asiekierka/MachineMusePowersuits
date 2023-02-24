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

package lehjr.numina.client.gui.clickable;

import com.mojang.blaze3d.matrix.MatrixStack;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import lehjr.numina.client.render.IconUtils;
import lehjr.numina.client.render.NuminaRenderer;
import lehjr.numina.common.capabilities.module.powermodule.ModuleCategory;
import lehjr.numina.common.capabilities.module.powermodule.PowerModuleCapability;
import lehjr.numina.common.math.Colour;
import lehjr.numina.common.string.AdditionalInfo;
import lehjr.numina.common.string.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Extends the Clickable class to make a clickable Augmentation; note that this
 * will not be an actual item.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class ClickableModule extends Clickable {
    final Colour checkmarkcolour = new Colour(0.0F, 0.667F, 0.0F, 1.0F);
    boolean allowed = true;
    boolean installed = false;
    boolean isEnabled = true;
    boolean isVisible = true;
    ItemStack module;
    int inventorySlot;
    public final ModuleCategory category;
    Integer tier;
    ResourceLocation regName;

    public ClickableModule(@Nonnull ItemStack module, MusePoint2D position, int inventorySlot, ModuleCategory category) {
        super(MusePoint2D.ZERO, new MusePoint2D(16, 16));
        super.setPosition(position);
        this.module = module;
        this.inventorySlot = inventorySlot;
        this.category = category;
        allowed = module.getCapability(PowerModuleCapability.POWER_MODULE).map(pm->pm.isAllowed()).orElse(false);
        tier = module.getCapability(PowerModuleCapability.POWER_MODULE).map(pm-> pm.getTier()).orElse(null);
        this.regName = module.getItem().getRegistryName();
    }

    @Nullable
    public Integer getTier() {
        return tier;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (containsPoint(x, y)) {
            return module.getTooltipLines(Minecraft.getInstance().player,
                    AdditionalInfo.doAdditionalInfo() ?
                            ITooltipFlag.TooltipFlags.ADVANCED :
                    ITooltipFlag.TooltipFlags.NORMAL);
        }
        return null;
    }

    public ResourceLocation getRegName() {
        return this.regName;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // TODO: extra text and options to disable if player doesn't have the module available

        if (!getModule().isEmpty()) {
            NuminaRenderer.drawModuleAt(matrixStack, left(), top(), getModule(), true);
            if (!allowed) {
                matrixStack.pushPose();
                matrixStack.translate(0, 0, 250);
                String string = StringUtils.wrapMultipleFormatTags("X", StringUtils.FormatCodes.Bold, StringUtils.FormatCodes.DarkRed);
                StringUtils.drawShadowedString(matrixStack, string, centerX() + 3, centerY() + 1);
                matrixStack.popPose();
            } else if (installed) {
                matrixStack.pushPose();
                matrixStack.translate(0, 0,250);
                IconUtils.getIcon().checkmark.draw(matrixStack, left() + 1, top() + 1, checkmarkcolour.withAlpha(0.6F));
                matrixStack.popPose();
            }
        }
    }

    @Nonnull
    public ItemStack getModule() {
        return module;
    }

    public boolean equals(ClickableModule other) {
        return this.module == other.getModule();
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public boolean isInstalled() {
        return installed;
    }
}