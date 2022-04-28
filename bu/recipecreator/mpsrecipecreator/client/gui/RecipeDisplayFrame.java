package com.lehjr.mpsrecipecreator.client.gui;

import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lehjr.numina.util.client.gui.frame.ScrollableFrame;
import lehjr.numina.util.client.render.MuseRenderer;
import lehjr.numina.util.math.Color;

public class RecipeDisplayFrame extends ScrollableFrame {
    String[] recipe = new String[0];
    String title;;

    public RecipeDisplayFrame(Color backgroundColor) {
        super();
        reset();
        setBackgroundColor(backgroundColor);
    }

    public void setFileName(String fileName) {
        title = fileName;
    }

    public void setRecipe(String recipeIn) {
        recipe = recipeIn.split("\n");
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);
        this.setTotalSize(25 + recipe.length * 12);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isEnabled() && this.isVisible()) {
            this.drawBackground(matrixStack);
            this.drawBorder(matrixStack, 0.0D);
            setCurrentScrollPixels(Math.min(getCurrentScrollPixels(), getMaxScrollPixels()));
            super.preRender(matrixStack, mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -getCurrentScrollPixels(), 0);
            MuseRenderer.drawLeftAlignedShadowedString(matrixStack, "FileName: " + title,
                    finalLeft() + 4,
                    finalTop() + 12);

            if (recipe.length > 0) {
                for (int index = 0; index < recipe.length; index ++) {
                    MuseRenderer.drawLeftAlignedShadowedString(matrixStack, recipe[index],
                            finalLeft() + 4,
                            (finalTop() + 12) + (12 * index));
                }
            }
            RenderSystem.popPose();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    public void reset() {
        recipe = new String[0];
        setFileName("");
    }
}