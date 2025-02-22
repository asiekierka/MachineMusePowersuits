package lehjr.numina.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import lehjr.numina.client.gui.frame.IGuiFrame;
import lehjr.numina.client.gui.geometry.IRect;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ContainerlessGui extends Screen implements IRect {
    /** The X size of the inventory window in pixels. */
    public int imageWidth = 176;
    /** The Y size of the inventory window in pixels. */
    public int imageHeight = 166;
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    public int leftPos;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    public int topPos;

    protected List<IGuiFrame> frames;

    protected ContainerlessGui(Component titleIn) {
        super(titleIn);
        frames = new ArrayList();
    }

    public ContainerlessGui(Component titleIn, int guiWidth, int guiHeight) {
        this(titleIn);
        this.imageWidth = guiWidth;
        this.imageHeight = guiHeight;
    }

    @Override
    public void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    /**
     * Adds a frame to this gui's draw list.
     *
     * @param frame
     */
    public void addFrame(IGuiFrame frame) {
        frames.add(frame);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float frameTime) {
        this.renderBackground(matrixStack);
        update(mouseX, mouseY);
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 10);
        renderFrames(matrixStack, mouseX, mouseY, frameTime);
        matrixStack.translate(0, 0, 10);
        super.render(matrixStack, mouseX, mouseY, frameTime);
        matrixStack.translate(0, 0, 100);
        renderLabels(matrixStack, mouseX, mouseY);
        matrixStack.popPose();
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public void update(double x, double y) {
        for (IGuiFrame frame : frames) {
            frame.update(x, y);
        }
    }

    public void renderFrames(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (IGuiFrame frame : frames) {
            frame.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        renderFrameLabels(matrixStack, mouseX, mouseY);
    }

    public void renderFrameLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        frames.forEach(frame -> frame.renderLabels(matrixStack, mouseX, mouseY));
    }

    /**
     * Does gui pause the game in single player.
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        for (IGuiFrame frame : frames) {
            if (frame.mouseScrolled(mouseX, mouseY, dWheel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (IGuiFrame frame : frames) {
            frame.mouseClicked(x, y, button);
        }
        return true;
    }

    /**
     * Called when the mouse is moved or a mouse button is released. Signature:
     * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
     * mouseUp
     */

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int which) {
        for (IGuiFrame frame : frames) {
            if(frame.mouseReleased(mouseX, mouseY, which)) {
                return true;
            }
        }
        return false;
    }

    public void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        List<Component> tooltip = getToolTip(mouseX, mouseY);
        if (tooltip != null) {
            renderComponentTooltip(matrixStack,tooltip, mouseX,mouseY);
        }
    }

    public List<Component> getToolTip(int x, int y) {
        List<Component> hitTip;
        for (IGuiFrame frame : frames) {
            hitTip = frame.getToolTip(x, y);
            if (hitTip != null) {
                return hitTip;
            }
        }
        return null;
    }

    /**
     * @return the xSize
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * @param imageWidth the xSize to set
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * @return the ySize
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * @param imageHeight the ySize to set
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public MusePoint2D center() {
        return new MusePoint2D(getLeftPos(), getTopPos()).plus(getXSize() * 0.5, getYSize() * 0.5);
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public int getXSize() {
        return imageWidth;
    }

    public void setXSize(int xSize) {
        this.imageWidth = xSize;
        this.leftPos = (this.width - getXSize()) / 2;
    }

    public int getYSize() {
        return imageHeight;
    }

    public void setYSize(int ySize) {
        this.imageHeight = ySize;
        this.topPos = (this.height - getYSize()) / 2;
    }

    @Override
    public Minecraft getMinecraft() {
        this.minecraft = Minecraft.getInstance();
        return this.minecraft;
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param relx Relative X coordinate
     * @return Absolute X coordinate
     */
    public int absX(double relx) {
        int absx = (int) ((relx + 1) * getImageWidth() / 2);
        int xpadding = (width - getImageWidth()) / 2;
        return absx + xpadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relX(double absx) {
        int padding = (width - getImageWidth()) / 2;
        return (int) ((absx - padding) * 2 / getImageWidth() - 1);
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param rely Relative Y coordinate
     * @return Absolute Y coordinate
     */
    public int absY(double rely) {
        int absy = (int) ((rely + 1) * imageHeight / 2);
        int ypadding = (height - imageHeight) / 2;
        return absy + ypadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relY(float absy) {
        int padding = (height - getYSize()) / 2;
        return (int) ((absy - padding) * 2 / getYSize() - 1);
    }
    protected IRect belowMe;
    protected IRect aboveMe;
    protected IRect leftOfMe;
    protected IRect rightOfMe;

    @Override
    public double left() {
        return leftPos;
    }

    @Override
    public IRect setLeft(double value) {
        leftPos = (int) value;
        return this;
    }

    @Override
    public double top() {
        return topPos;
    }

    @Override
    public IRect setTop(double value) {
        this.topPos = (int) value;
        return this;
    }

    @Override
    public double right() {
        return left() + imageWidth;
    }

    @Override
    public IRect setRight(double value) {
        return setLeft(value - imageWidth);
    }

    @Override
    public double bottom() {
        return top() + imageHeight;
    }

    @Override
    public IRect setBottom(double value) {
        return setTop(value - imageHeight);
    }

    @Override
    public double width() {
        return imageWidth;
    }

    @Override
    public IRect setWidth(double value) {
        this.imageWidth = (int) value;
        return this;
    }

    @Override
    public double height() {
        return imageHeight;
    }

    @Override
    public IRect setHeight(double value) {
        this.imageHeight = (int) value;
        return this;
    }

    @Override
    public void setPosition(MusePoint2D positionIn) {
        IRect.super.setPosition(positionIn);
    }

    @Override
    public MusePoint2D getUL() {
        return new MusePoint2D(left(), top());
    }

    @Override
    public IRect setUL(MusePoint2D ul) {
        setLeft(ul.x());
        setTop(ul.y());
        return this;
    }

    @Override
    public MusePoint2D getWH() {
        return new MusePoint2D(imageWidth, imageHeight);
    }

    @Override
    public IRect setWH(MusePoint2D wh) {
        setWidth(wh.x());
        setHeight(wh.y());
        return this;
    }

    @Override
    public IRect setAbove(IRect belowMe) {
        this.belowMe = belowMe;
        return this;
    }

    @Override
    public IRect setLeftOf(IRect rightOfMe) {
        this.rightOfMe = rightOfMe;
        return this;
    }

    @Override
    public IRect setBelow(IRect aboveMe) {
        this.aboveMe = aboveMe;
        return this;
    }

    @Override
    public IRect setRightOf(IRect leftOfMe) {
        this.leftOfMe = leftOfMe;
        return this;
    }
}
