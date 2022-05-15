package com.lehjr.mpsrecipecreator.client.gui;

import com.lehjr.mpsrecipecreator.basemod.DataPackWriter;
import com.lehjr.mpsrecipecreator.container.MPARCContainer;
import com.mojang.blaze3d.matrix.PoseStack;
import lehjr.numina.util.client.gui.ExtendedContainerScreen;
import lehjr.numina.util.client.gui.gemoetry.MusePoint2D;
import lehjr.numina.util.client.render.MuseRenderer;
import lehjr.numina.util.math.Color;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.Component;

import javax.annotation.Nullable;

/**
 * @author Dries007
 */
public class MPARCGui extends ExtendedContainerScreen<MPARCContainer> {
    protected long creationTime;

    final int slotWidth = 18;
    final int spacer = 4;

    private final ExtInventoryFrame inventoryFrame;
    private final RecipeOptionsFrame recipeOptions;
    private final RecipeDisplayFrame recipeDisplayFrame;
    private final SlotOptionsFrame slotOptions;

    // text box
    public StackTextDisplayFrame tokenTxt;

    // separate frame for each slot
//    private final SlotOptionsFrame slotOptions;

    protected final Color topBorderColor = new Color(0.216F, 0.216F, 0.216F, 1.0F);
    protected final Color bottomBorderColor = Color.WHITE.withAlpha(0.8F);
    protected final Color  backgroundColor = Color.GREY_GUI_BACKGROUND; //new Color(0.545F, 0.545F, 0.545F, 1.0F);


    protected final Color gridColor = new Color(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Color gridBorderColor = Color.LIGHT_BLUE.withAlpha(0.8F);
    protected final Color gridBackGound = new Color(0.545F, 0.545F, 0.545F, 1);
    public RecipeGen recipeGen;

    public MPARCGui(MPARCContainer container, PlayerInventory playerInventory, Component title) {
        super(container, playerInventory, title, 400, 300, false);
        float zLevel = getBlitOffset();


        inventoryFrame = new ExtInventoryFrame(
                new MusePoint2D(0, 0),
                new MusePoint2D(0, 0),
                zLevel,
                container,
                backgroundColor,
                topBorderColor,
                bottomBorderColor,
                this,
                ulGetter());
        inventoryFrame.enableAndShow();
        addFrame(inventoryFrame);

        recipeOptions = new RecipeOptionsFrame(
                Color.DARKBLUE,
                gridBackGound,
                this
        );
        addFrame(recipeOptions);
        recipeGen = new RecipeGen(container, recipeOptions);

        // display for stack string in slot
        tokenTxt = new StackTextDisplayFrame(Color.DARKBLUE);
        addFrame(tokenTxt);

        slotOptions = new SlotOptionsFrame(
                new MusePoint2D(0, 0),
                new MusePoint2D(0, 0),
                recipeGen,
                container,
                Color.DARKBLUE,
                gridBorderColor,
                Color.DARK_GREY,
                Color.LIGHT_GREY,
                Color.BLACK);
        addFrame(slotOptions);

        recipeDisplayFrame = new RecipeDisplayFrame(Color.DARKBLUE);
        addFrame(recipeDisplayFrame);
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.player.containerMenu = this.menu;


        // left side of inventory slots
        double inventoryLeft = backgroundRect.finalRight() - spacer * 2 - 9 * slotWidth;

        // set the ulShift before setting init, since ulshift is set in init
        inventoryFrame.init(
                inventoryLeft - spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalTop() + spacer + 188);

        recipeOptions.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer,
                inventoryLeft - spacer * 2,
                backgroundRect.finalTop() + spacer + 150
        );

        slotOptions.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 150,
                inventoryLeft - spacer * 2,
                backgroundRect.finalTop() + spacer + 188);

        tokenTxt.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 188,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalTop() + spacer * 2 + 188 + 20
        );
        tokenTxt.setVisible(true);

        recipeDisplayFrame.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalTop() + spacer * 2 + 212,
                backgroundRect.finalRight() - spacer,
                backgroundRect.finalBottom() - spacer);
    }

    public void resetRecipes() {
        slotOptions.reset();
        recipeGen.reset();
        getMenu().craftMatrix.clearContent();
        getMenu().craftResult.clearContent();
    }

//    @Override
//    public boolean mouseClicked(double x, double y, int button) {
//        System.out.println("event listeners size:" + getEventListeners().size());
//
//
////        boolean test = super.mouseClicked(x, y, button);
////        System.out.println("test: " + test);
////        return test;
//
//        // pick block = middle mouse button (2)
//        // attack button = left mouse button (0)
//        // place block/use item = right mounse button (1)
//
//        System.out.println("selected slot index: " + getSelectedSlot(x, y) == null ? null : container.inventorySlots.indexOf(getSelectedSlot(x, y)));
//
////            InputConstants.Input mouseKey = InputConstants.Type.MOUSE.getOrMakeInput(button);
////            boolean flag = Minecraft.getInstance().gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
////            boolean flag = Minecraft.getInstance().gameSettings.keyBindUseItem.isActiveAndMatches(mouseKey);
////            boolean flag = Minecraft.getInstance().gameSettings.keyBindAttack.isActiveAndMatches(mouseKey);
//
//        return super.mouseClicked(x, y, button);
//    }




    @Nullable
    private Slot getSelectedSlot(double mouseX, double mouseY) {
        for(int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = this.menu.slots.get(i);
            // isSlotSelected
            if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive()) {
                return slot;
            }
        }

        return null;
    }

    public void save() {
        if(menu.getSlot(0).hasItem()) {
            DataPackWriter.writeRecipe(recipeGen.getRecipeJson(),recipeDisplayFrame.title + ".json");
        }
    }


    @Override
    public void renderLabels(PoseStack matrixStack, int x, int y) {
//        super.renderLabels(matrixStack, x, y);
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);

        // reset's the slot's settings when teh contents changed.
        int slotChanged = menu.getSlotChanged();
        if (slotChanged != -1) {
            recipeGen.useOredict.put(slotChanged, false);
            recipeGen.setOreTagIndex(slotChanged,0);
            // no oredict for result
            if (slotChanged < 0) {
                slotOptions.useOreDictCheckbox[slotChanged - 1].setChecked(false);
            }
        }

        int activeSlot = slotOptions.getActiveSlotID();

        if (activeSlot >= 0) {
            tokenTxt.setLabel(recipeGen.getStackToken(activeSlot));
        }

        recipeDisplayFrame.setFileName(recipeGen.getFileName());
        recipeDisplayFrame.setRecipe(recipeGen.getRecipeJson());
    }

    public void selectSlot(int index) {
        slotOptions.selectSlot(index);
        tokenTxt.setSlot(index);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Title
        MuseRenderer.drawShadowedStringCentered(matrixStack, "MPS-RecipeCreator", backgroundRect.centerx(), backgroundRect.finalTop() - 20);
        renderTooltip(matrixStack, mouseX, mouseY);
    }


    @Override
    public void onClose() {
        super.onClose();
    }
}