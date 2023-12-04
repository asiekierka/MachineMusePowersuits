package lehjr.numina.client.gui.clickable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import lehjr.numina.client.render.IconUtils;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.item.ItemUtils;
import lehjr.numina.common.math.Color;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;

/**
 * A left hand toggleable tab widget based on Minecraft's RecipeTabToggleWidget
 */
public class ModularItemTabToggleWidget extends Clickable {
    protected ResourceLocation resourceLocation;
    protected boolean isStateActive;
    protected double xTexStart;
    protected double yTexStart;
    protected double xDiffTex;
    protected double yDiffTex;

    private float animationTime;

    @Nonnull
    ItemStack icon;
    EquipmentSlot type;

    public ModularItemTabToggleWidget(EquipmentSlot type) {
        super(0, 0, 35, 27, false);
        this.isStateActive = false;
        this.initTextureValues(153, 2, 35, 0, new ResourceLocation("textures/gui/recipe_book.png"));

        this.type = type;
        Player player = getMinecraft().player;
        ItemStack test = ItemUtils.getItemFromEntitySlot(player, type);

        this.icon = test.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast)
                .map(iItemHandler -> test).orElse(ItemStack.EMPTY);
    }

    public void initTextureValues(int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation) {
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.xDiffTex = pXDiffTex;
        this.yDiffTex = pYDiffTex;
        this.resourceLocation = pResourceLocation;
    }

    public void setStateActive(boolean active) {
        this.isStateActive = active;
    }

    public EquipmentSlot getSlotType() {
        return type;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
        if (this.animationTime > 0.0F) {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
            matrixStack.pushPose();
            matrixStack.translate((float)(this.left() + 8), (float)(this.top() + 12), 0.0F);
            matrixStack.scale(1.0F, f, 1.0F);
            matrixStack.translate((float)(-(this.left() + 8)), (float)(-(this.top() + 12)), 0.0F);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.resourceLocation);

        RenderSystem.disableDepthTest();
        float i = (float) this.xTexStart;
        float j = (float) this.yTexStart;
        if (this.isStateActive) {
            i += this.xDiffTex;
        }

        if (this.containsPoint(mouseX, mouseY)) {
            j += this.yDiffTex;
        }

        float k = (float) this.left();
        if (this.isStateActive) {
            k -= 2;
        }

        IconUtils.INSTANCE.blit(matrixStack, k, (float)this.top(), i, j, (float) this.width(), (float) this.height());
        RenderSystem.enableDepthTest();
        this.renderIcon(matrixStack);
        if (this.animationTime > 0.0F) {
            matrixStack.popPose();
            this.animationTime -= partialTick;
        }
    }

    /**
     * Renders the item icons for the tabs. Some tabs have 2 icons, some just one.
     */
    private void renderIcon(PoseStack matrixStack) {
        int offset = this.isStateActive? -2 : -2;
        RenderSystem.disableDepthTest();
        if (this.icon.isEmpty()) {
            if (EquipmentSlot.MAINHAND.equals(type)) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                IconUtils.getIcon().weapon.draw(matrixStack, left() + 9 + offset, top() + 5, Color.WHITE);
            } else {
                Pair<ResourceLocation, ResourceLocation> pair = IconUtils.getSlotBackground(type);
                if (pair != null) {
                    TextureAtlasSprite textureatlassprite = getMinecraft().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                    RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                    getMinecraft().screen.blit(matrixStack, (int)left() + 10 + offset, (int)top() + 5, getMinecraft().screen.getBlitOffset(), 16, 16, textureatlassprite);
                }
            }
            RenderSystem.enableDepthTest();
        } else {
            getMinecraft().getItemRenderer().renderAndDecorateItem(icon, (int)left() + 9 + offset, (int)top() + 6);
        }
    }
}
