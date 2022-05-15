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

package lehjr.numina.util.client.gui.gemoetry;

import com.mojang.blaze3d.matrix.PoseStack;
import lehjr.numina.util.math.Color;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class DrawableRelativeRect extends RelativeRect implements IDrawableRect {
    Color backgroundColor;
    Color borderColor;
    Color backgroundColor2 = null;
    float cornerradius = 3;
    float zLevel = 1;
    boolean shrinkBorder = true;

    public DrawableRelativeRect(double left, double top, double right, double bottom, boolean growFromMiddle,
                                Color backgroundColor,
                                Color borderColor) {
        super(left, top, right, bottom, growFromMiddle);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public DrawableRelativeRect(RelativeRect ref, Color backgroundColor, Color borderColor) {
        super(ref.left(), ref.top(), ref.right(), ref.bottom(), ref.growFromMiddle());
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public DrawableRelativeRect(double left, double top, double right, double bottom,
                                Color backgroundColor,
                                Color borderColor) {
        super(left, top, right, bottom, false);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public DrawableRelativeRect(MusePoint2D ul, MusePoint2D br,
                                Color backgroundColor,
                                Color borderColor) {
        super(ul, br);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public DrawableRelativeRect(Color backgroundColor, Color borderColor) {
        super();
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public DrawableRelativeRect(Color backgroundColor, Color borderColor, boolean growFromMiddle) {
        super(growFromMiddle);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    @Override
    public DrawableRelativeRect copyOf() {
        return new DrawableRelativeRect(super.left(), super.top(), super.right(), super.bottom(),
                this.growFromMiddle , backgroundColor, borderColor);
    }

    @Override
    public float getZLevel() {
        return this.zLevel;
    }

    @Override
    public IDrawable setZLevel(float zLevelIn) {
        zLevel = zLevelIn;
        return this;
    }

    /**
     * determine if the border should be smaller than the background rectangle (like tooltips)
     *
     * @param shrinkBorder
     */
    public void setShrinkBorder(boolean shrinkBorder) {
        this.shrinkBorder = shrinkBorder;
    }

    public DrawableRelativeRect setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public DrawableRelativeRect setSecondBackgroundColor(Color backgroundColor2In) {
        backgroundColor2 = backgroundColor2In;
        return this;
    }

    public DrawableRelativeRect setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public FloatBuffer preDraw(double shrinkBy) {
        return preDraw(left() + shrinkBy, top() + shrinkBy, right() - shrinkBy, bottom() - shrinkBy);
    }

    public FloatBuffer preDraw(double left, double top, double right, double bottom) {
        FloatBuffer vertices;
        // top left corner
        FloatBuffer corner = GradientAndArcCalculator.getArcPoints(
                (float)Math.PI,
                (float)(3.0 * Math.PI / 2.0),
                getCornerradius(),
                (float)(left + getCornerradius()),
                (float)(top + getCornerradius()));

        vertices = BufferUtils.createFloatBuffer(corner.limit() * 4);
        vertices.put(corner);

        // bottom left corner
        corner = GradientAndArcCalculator.getArcPoints(
                (float)(3.0 * Math.PI / 2.0F),
                (float)(2.0 * Math.PI),
                getCornerradius(),
                (float)(left + getCornerradius()),
                (float)(bottom - getCornerradius()));
        vertices.put(corner);

        // bottom right corner
        corner = GradientAndArcCalculator.getArcPoints(
                0,
                (float) (Math.PI / 2.0),
                getCornerradius(),
                (float)(right - getCornerradius()),
                (float)(bottom - getCornerradius()));
        vertices.put(corner);

        // top right corner
        corner = GradientAndArcCalculator.getArcPoints(
                (float) (Math.PI / 2.0),
                (float) Math.PI,
                getCornerradius(),
                (float)(right - getCornerradius()),
                (float)(top + getCornerradius()));
        vertices.put(corner);
        vertices.flip();

        return vertices;
    }

    public void drawBackground(PoseStack matrixStack, FloatBuffer vertices) {
        drawBuffer(matrixStack, vertices, backgroundColor, GL11.GL_TRIANGLE_FAN);
    }

    public void drawBackground(PoseStack matrixStack, FloatBuffer vertices, FloatBuffer colours) {
        drawBuffer(matrixStack, vertices, colours, GL11.GL_TRIANGLE_FAN);
    }

    public void drawBorder(PoseStack matrixStack, FloatBuffer vertices) {
        drawBuffer(matrixStack, vertices, borderColor, GL11.GL_LINE_LOOP);
    }

    void drawBuffer(PoseStack matrixStack, FloatBuffer vertices, Color colour, int glMode) {
        preDraw(glMode, DefaultVertexFormats.POSITION_COLOR);
        addVerticesToBuffer(matrixStack.last().pose(), vertices, colour);
        drawTesselator();
        postDraw();
    }

    void drawBuffer(PoseStack matrixStack, FloatBuffer vertices, FloatBuffer colours, int glMode) {
        preDraw(glMode, DefaultVertexFormats.POSITION_COLOR);
        addVerticesToBuffer(matrixStack.last().pose(), vertices, colours);
        drawTesselator();
        postDraw();
    }

    public float getCornerradius() {
        return cornerradius;
    }

    public DrawableRelativeRect setCornerradius(float cornerradiusIn) {
        this.cornerradius = cornerradiusIn;
        return this;
    }

    public FloatBuffer getVertices(double shrinkBy) {
        return getVertices(left() + shrinkBy, top() + shrinkBy, right() - shrinkBy, bottom() - shrinkBy);
    }

    public FloatBuffer getVertices(double left, double top, double right, double bottom) {
        FloatBuffer vertices;
        // top left corner
        FloatBuffer corner = GradientAndArcCalculator.getArcPoints(
                (float) Math.PI,
                (float) (3.0 * Math.PI / 2.0),
                getCornerradius(),
                (float) (left + getCornerradius()),
                (float) (top + getCornerradius()));

        vertices = BufferUtils.createFloatBuffer(corner.limit() * 4);
        vertices.put(corner);

        // bottom left corner
        corner = GradientAndArcCalculator.getArcPoints(
                (float) (3.0 * Math.PI / 2.0F),
                (float) (2.0 * Math.PI),
                getCornerradius(),
                (float) (left + getCornerradius()),
                (float) (bottom - getCornerradius()));
        vertices.put(corner);

        // bottom right corner
        corner = GradientAndArcCalculator.getArcPoints(
                0,
                (float) (Math.PI / 2.0),
                getCornerradius(),
                (float) (right - getCornerradius()),
                (float) (bottom - getCornerradius()));
        vertices.put(corner);

        // top right corner
        corner = GradientAndArcCalculator.getArcPoints(
                (float) (Math.PI / 2.0),
                (float) Math.PI,
                getCornerradius(),
                (float) (right - getCornerradius()),
                (float) (top + getCornerradius()));
        vertices.put(corner);
        vertices.flip();

        return vertices;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float frameTime) {

        FloatBuffer vertices = preDraw(0);

        if (backgroundColor2 != null) {
            FloatBuffer colours = GradientAndArcCalculator.getColorGradient(backgroundColor,
                    backgroundColor2, vertices.limit() * 4);
            drawBackground(matrixStack, vertices, colours);
        } else {
            drawBackground(matrixStack, vertices);
        }

        if (shrinkBorder) {
            vertices = preDraw(1);
        } else {
            vertices.rewind();
        }
        drawBorder(matrixStack, vertices);
    }

    @Override
    public RelativeRect getRect() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(super.toString());
        stringbuilder.append("Background Color: ").append(backgroundColor.toString()).append("\n");
        stringbuilder.append("Background Color 2: ").append(backgroundColor2 == null? "null" : backgroundColor2.toString()).append("\n");
        stringbuilder.append("Border Color: ").append(borderColor.toString()).append("\n");
        return stringbuilder.toString();
    }
}