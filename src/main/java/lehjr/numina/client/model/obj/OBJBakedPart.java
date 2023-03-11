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

package lehjr.numina.client.model.obj;

import lehjr.numina.client.model.helper.ModelHelper;
import lehjr.numina.common.base.NuminaLogger;
import lehjr.numina.common.math.Color;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OBJBakedPart extends BakedModelWrapper {
    public OBJBakedPart(BakedModel originalModel) {
        super(originalModel);
    }

    /**
      * @param state
     * @param side
     * @param rand
     * @param extraData
     * @return
     */
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        // no extra data
        if (extraData == EmptyModelData.INSTANCE) {
            return originalModel.getQuads(state, side, rand, extraData);
        } else {
            // part is visibile
            boolean visible = extraData.hasProperty(OBJPartData.VISIBLE) ? extraData.getData(OBJPartData.VISIBLE) : true;
            if (visible) {
                // glow is opposite ambient occlusion
                boolean glow = (extraData.hasProperty(OBJPartData.GLOW) ? extraData.getData(OBJPartData.GLOW) : false);
                // color applied to all quads in the part
                Color color = extraData.hasProperty(OBJPartData.COLOR) ? new Color(extraData.getData(OBJPartData.COLOR)) : Color.WHITE;

//                NuminaLogger.logError("color in baked part: " + color);

                return ModelHelper.getColoredQuadsWithGlow(originalModel.getQuads(state, side, rand, extraData), color, glow);
            }
        }
        return new ArrayList<>();
    }
}