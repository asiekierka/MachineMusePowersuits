//package lehjr.powersuits.client.render.item;
//
//import lehjr.powersuits.basemod.MPSConstants;
//import lehjr.powersuits.client.model.item.PowerFistModel2;
//import lehjr.powersuits.network.MPAPackets;
//import lehjr.powersuits.network.packets.CosmeticInfoPacket;
//import lehjr.numina.basemod.NuminaConstants;
//import lehjr.numina.util.capabilities.render.IHandHeldModelSpecNBT;
//import lehjr.numina.util.capabilities.render.CapabilityModelSpec;
//import lehjr.numina.util.capabilities.render.modelspec.ModelPartSpec;
//import lehjr.numina.util.capabilities.render.modelspec.ModelRegistry;
//import lehjr.numina.util.capabilities.render.modelspec.ModelSpec;
//import lehjr.numina.util.capabilities.render.modelspec.PartSpecBase;
//import lehjr.numina.util.math.Color;
//import lehjr.numina.util.nbt.NBTTagAccessor;
//import com.mojang.blaze3d.matrix.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.model.ItemTransforms;
//import net.minecraft.client.renderer.tileentity.ItemStackBlockEntityRenderer;
//import net.minecraft.entity.player.Player;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.vector.Transformation;
//
//public class PowerFistRenderer extends ItemStackBlockEntityRenderer {
//    PowerFistModel2 powerFist = new PowerFistModel2();
//    boolean isFiring = false;
//
//    @Override
//    public void renderByItem/*render*/(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
//
//
//        VertexConsumer builder = buffer.getBuffer(powerFist.getRenderType(MPSConstants.POWER_FIST_TEXTURE));
//
//        stack.getCapability(CapabilityModelSpec.RENDER).ifPresent(specNBTCap -> {
//            if (specNBTCap instanceof IHandHeldModelSpecNBT) {
//                CompoundTag renderSpec = specNBTCap.getRenderTag();
//
//                // Set the tag on the item so this lookup isn't happening on every loop.
//                if (renderSpec == null || renderSpec.isEmpty()) {
//                    renderSpec = specNBTCap.getDefaultRenderTag();
//
//                    // first person transform type insures THIS client's player is the one holding the item rather than this
//                    // client's player seeing another player holding it
//                    if (renderSpec != null && !renderSpec.isEmpty() &&
//                            (transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND ||
//                                    (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND))) {
//                        Player player = Minecraft.getInstance().player;
//                        int slot = -1;
//                        if (player.getHeldItemMainhand().equals(stack)) {
//                            slot = player.getInventory().currentItem;
//                        } else {
//                            for (int i = 0; i < player.getInventory().getSizeInventory(); i++) {
//                                if (player.getInventory().getStackInSlot(i).equals(stack)) {
//                                    slot = i;
//                                    break;
//                                }
//                            }
//                        }
//
//                        if (slot != -1) {
//                            specNBTCap.setRenderTag(renderSpec, NuminaConstants.TAG_RENDER);
//                            MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(slot, NuminaConstants.TAG_RENDER, renderSpec));
//                        }
//                    }
//                }
//
//                if (renderSpec != null) {
//                    int[] colours = renderSpec.getIntArray(NuminaConstants.COLOR;
//                    Color partColor;
//                    Transformation transform;
//
//                    for (CompoundTag nbt : NBTTagAccessor.getValues(renderSpec)) {
//                        PartSpecBase partSpec = ModelRegistry.getInstance().getPart(nbt);
//
//                        String partName = nbt.getString("part");
//
//
//
//                        if (partSpec instanceof ModelPartSpec) {
//
//                            // only process this part if it's for the correct hand
//                            if (partSpec.getBinding().getTarget().name().toUpperCase().equals(
//                                    transformType.equals(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ||
//                                            transformType.equals(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND) ?
//                                            "LEFTHAND" : "RIGHTHAND")) {
//
//                                transform = ((ModelSpec) partSpec.spec).getTransform(transformType);
//                                String itemState = partSpec.getBinding().getItemState();
//
//                                int ix = partSpec.getColorIndex(nbt);
//                                if (ix < colours.length && ix >= 0) {
//                                    partColor = new Color(colours[ix]);
//                                } else {
//                                    partColor = Color.WHITE;
//                                }
//                                boolean glow = ((ModelPartSpec) partSpec).getGlow(nbt);
//
//                                if ((!isFiring && (itemState.equals("all") || itemState.equals("normal"))) ||
//                                        (isFiring && (itemState.equals("all") || itemState.equals("firing")))) {
//
//                                    System.out.println("partname: " + partName);
//                                    matrixStack.push();
//                                    matrixStack.translate(transform.getTranslation().getX(), transform.getTranslation().getY(), transform.getTranslation().getZ());
//                                    matrixStack.rotate(transform.getRightRot());
//                                    matrixStack.scale(transform.getScale().getX(), transform.getScale().getX(), transform.getScale().getZ());
//                                    matrixStack.pop();
//
////
//                                    powerFist.renderPart(partName, matrixStack, builder, combinedLight, combinedOverlay, partColor.r, partColor.g, partColor.b, partColor.a);
//
//
//
//
////                                    builder.addAll(ModelHelper.getColoredQuadsWithGlowAndTransform(((ModelPartSpec) partSpec).getPart().getQuads(state, side, rand, extraData), partColor, transform, glow));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//
//
//              /*
//        matrixStack.scale(0.5F, 0.5F, 0.5F);
//
//
//
//
//                        <Transformation type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
//                <Transformation type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
//                <Transformation type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>
//
//
//                            <modelTransforms>
//                <Transformation type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
//                <Transformation type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
//                <Transformation type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>
//            </modelTransforms>
//         */
//
//
////        matrixStack.rotate(Vector3f.ZP.rotationDegrees(270F));
////        matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
////        for (String partName : powerFist.partlMap.keySet()) {
////            powerFist.renderPart(partName, matrixStack, builder, combinedLight, combinedOverlay, Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
////        }
//
//
//    }
//
//    String getPrefixString(ItemTransforms.TransformType transformType) {
//        switch ((transformType)) {
//            case FIRST_PERSON_LEFT_HAND:
//            case THIRD_PERSON_LEFT_HAND: {
//                return "powerfist_left.";
//            }
//
//            default:
//                return "powerfist_right.";
//        }
//    }
//}