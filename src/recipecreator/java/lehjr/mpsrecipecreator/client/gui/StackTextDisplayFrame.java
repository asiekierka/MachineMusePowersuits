package lehjr.mpsrecipecreator.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import lehjr.numina.client.gui.frame.ScrollableFrame;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import lehjr.numina.client.gui.geometry.Rect;
import lehjr.numina.common.string.StringUtils;

public class StackTextDisplayFrame extends ScrollableFrame {
    String label = "";
    int slot = -1;

    public StackTextDisplayFrame() {
        super(new Rect(MusePoint2D.ZERO, MusePoint2D.ZERO));
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setLabel(String labelIn) {
        label = labelIn;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        StringUtils.drawLeftAlignedShadowedString(matrixStack, slot != -1 ? "Slot " + slot + ": " +
                this.label : "No slot selected", left() + 4, centerY());
    }
}