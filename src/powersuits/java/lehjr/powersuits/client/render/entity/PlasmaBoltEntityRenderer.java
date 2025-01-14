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

package lehjr.powersuits.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lehjr.numina.client.render.IconUtils;
import lehjr.numina.common.base.NuminaLogger;
import lehjr.numina.common.constants.NuminaConstants;
import lehjr.numina.common.math.Color;
import lehjr.powersuits.client.event.ModelBakeEventHandler;
import lehjr.powersuits.common.entity.PlasmaBallEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.util.NonNullLazy;

public class PlasmaBoltEntityRenderer extends EntityRenderer<PlasmaBallEntity> {
    static final Color color1 = new Color(0.3F, 0.3F, 1F, 0.3F);
    static final Color color2 = new Color(0.4F, 0.4F, 1F, 0.5F);
    static final Color color3 = new Color(0.8F, 0.8F, 1F, 0.7F);
    static final Color color4 = new Color(1F, 1F, 1F, 0.9F);
    // NonNullLazy doesn't init until called
    public static final NonNullLazy<BakedModel> modelSphere = NonNullLazy.of(() -> ModelBakeEventHandler.INSTANCE.getBakedItemModel(ModelBakeEventHandler.plasmaBall));
    protected static final RandomSource rand = RandomSource.create(42L);

    public PlasmaBoltEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(PlasmaBallEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        float size = entityIn.getChargePercent();//.getActualSize();
//        NuminaLogger.logDebug("size: " + size);

        if(size > 0) {
            renderPlasma(matrixStackIn, bufferIn, size);//12.5F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PlasmaBallEntity entity) {
        return NuminaConstants.TEXTURE_WHITE;
    }

    public static void renderPlasma(PoseStack matrixStackIn, MultiBufferSource bufferIn, double size) {
        matrixStackIn.pushPose();
        float scalFactor = 3;

        float scale = (float) (size * 0.0625);
        /*
            max size: 50
            ----------------
            max scale: 2.5
            --------------



            min size: 10
            ---------------
            min scale: 0.5
            ---------------
         */
        matrixStackIn.scale(scale, scale, scale);

        // Lightning renderer // seems to be working?
        for (int i = 0; i < 9; i++) {
            double angle1 = (Math.random() * 2 * Math.PI);
            double angle2 = (Math.random() * 2 * Math.PI);
            double angle3 = (Math.random() * 2 * Math.PI);
            IconUtils.getIcon().lightning.drawLightning(bufferIn, matrixStackIn,
                    (float)(Math.cos(angle1) * 0.5), (float)(Math.sin(angle1) * 0.5), (float)(Math.cos(angle3) * 0.5),
                    (float) (Math.cos(angle2) * 5), (float)(Math.sin(angle2) * 5), (float)(Math.sin(angle3) * 5),
                    new Color(1F, 1F, 1F, 0.9F));
        }

        // spheres
        {
            int millisPerCycle = 500;
            double timeScale = Math.cos((System.currentTimeMillis() % millisPerCycle) * 2.0 / millisPerCycle - 1.0);
            renderPlasmaBall(matrixStackIn, bufferIn, 4F*scalFactor, color1.withAlpha(0.15F));
            renderPlasmaBall(matrixStackIn, bufferIn, (float) (3+timeScale /2F)*scalFactor,color2.withAlpha(0.25F));
            renderPlasmaBall(matrixStackIn, bufferIn, (float) (2+timeScale)*scalFactor,color3.withAlpha(0.4F));
            renderPlasmaBall(matrixStackIn, bufferIn, (float) (1+timeScale)*scalFactor,color4.withAlpha(0.75F));
        }
        matrixStackIn.popPose();
    }

    static void renderPlasmaBall(PoseStack matrixStackIn, MultiBufferSource bufferIn, float scale, Color color) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.scale(scale, scale, scale);
        renderSphere(bufferIn, getSphereRenderType(),
                matrixStackIn, /*combinedLight*/0x00F000F0, color);
        matrixStackIn.popPose();
    }

    private static RenderType getSphereRenderType() {
        return RenderType.entityTranslucentCull(NuminaConstants.TEXTURE_WHITE);
    }

    public static void renderSphere(MultiBufferSource bufferIn, RenderType rt, PoseStack matrixStackIn, int packedLightIn, Color color) {
        renderSphere(bufferIn, rt, matrixStackIn, packedLightIn, OverlayTexture.NO_OVERLAY, color);
    }

    public static void renderSphere(MultiBufferSource bufferIn, RenderType rt, PoseStack matrixStackIn, int packedLightIn, int overlay, Color color) {
        VertexConsumer bb = bufferIn.getBuffer(rt);
        NonNullLazy<BakedModel> model = modelSphere;
        if (model.get() == null) {
            NuminaLogger.logError("PlasmaBoltRenderer Render Spere quads not yet implemented");
            return;
        }
//        else {
//            NuminaLogger.logDebug("model class: " + model.getClass() +", quads: " + model.get().getQuads(null, null, rand/*, ModelData.EMPTY*/).size());
//        }

        for (BakedQuad quad : model.get().getQuads(null, null, rand)) {
            bb.putBulkData(matrixStackIn.last(), quad, color.r, color.g, color.b, color.a, packedLightIn, overlay, true);
        }
    }
}