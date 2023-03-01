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

package lehjr.numina.client.gui.geometry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import lehjr.numina.common.math.Color;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class SwirlyMuseCircle {
    public static final float detail = 2;
    protected FloatBuffer points;
    protected FloatBuffer colour;
    int numsegments;

    public SwirlyMuseCircle(Color c1, Color c2) {
        if (points == null) {
            points = GradientAndArcCalculator.getArcPoints(0, (float) (Math.PI * 2 + 0.0001), detail, 0, 0);
        }
        numsegments = points.limit() / 2;
        colour = GradientAndArcCalculator.getColourGradient(c1, c2, points.limit() / 2);
    }

    public void draw(PoseStack matrixStack, float radius, double x, double y, float zLevel) {
        float ratio = (System.currentTimeMillis() % 2000) / 2000.0F;
        colour.rewind();
        points.rewind();
        matrixStack.pushPose();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(radius / detail, radius / detail, 1.0F);
//        RenderSystem.rotatef((float) (-ratio * 360.0), 0, 0, 1);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-ratio * 360.0F));


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();


        buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        /**
         FIXME: this may not work, but it's worth a shot
         -----------------------------------------------
         Analysis: GL11.GL_LINE_LOOP is not accessible, lines look most promising

         GL_POINTS         = 0x0,
         GL_LINES          = 0x1, *** (most promising) ?
         GL_LINE_LOOP      = 0x2,
         GL_LINE_STRIP     = 0x3, *** (most promising) ?
         GL_TRIANGLES      = 0x4, *
         GL_TRIANGLE_STRIP = 0x5, *
         GL_TRIANGLE_FAN   = 0x6, *
         GL_QUADS          = 0x7,
         GL_QUAD_STRIP     = 0x8,
         GL_POLYGON        = 0x9;


        Either gonna be debug lines or debug strip... debug strip might be easiest



         */








        Matrix4f matrix4f = matrixStack.last().pose();

        while (points.hasRemaining() && colour.hasRemaining()) {
            buffer.vertex(matrix4f, points.get(), points.get(), zLevel).color(colour.get(), colour.get(), colour.get(), colour.get()).endVertex();
        }
        tessellator.end();

        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        matrixStack.popPose();
    }
}