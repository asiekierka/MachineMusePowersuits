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

package lehjr.numina.client.gui.slot;

import com.mojang.blaze3d.matrix.MatrixStack;
import lehjr.numina.client.gui.clickable.IClickable;
import lehjr.numina.client.gui.geometry.IDrawable;
import lehjr.numina.client.gui.geometry.IRect;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import lehjr.numina.client.gui.geometry.Rect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * A universal base for both IInventory and IItemHandler based slots
 *
 */
@Deprecated
public class UniversalSlot extends Slot implements IClickable {
    private static IInventory emptyInventory = new Inventory(0);
    private final IItemHandler itemHandler;
    protected IPressable onPressed;
    protected IReleasable onReleased;
    protected final boolean isIItemHandler;
    boolean isVisible;
    boolean isEnabled;
    Rect rect;

    public UniversalSlot(IInventory inventory, int index, int xPosition, int yPosition) {
        this(inventory, index, new MusePoint2D(xPosition, yPosition));
        this.rect = new Rect(x, y, x + 16, y + 16);
    }

    public UniversalSlot(IInventory inventory, int index, MusePoint2D position) {
        super(inventory, index, (int)position.x(), (int)position.y());
        this.index = index;
        this.itemHandler = new ItemStackHandler();
        isIItemHandler = false;
        this.rect = new Rect(position.x() -8, position.y() -8, position.x() + 8, position.y() + 8);
    }

    public UniversalSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, new MusePoint2D(xPosition, yPosition));
    }

    public UniversalSlot(IItemHandler itemHandler, int index, MusePoint2D position) {
        super(emptyInventory, index, (int)position.x(), (int)position.y());
        this.itemHandler = itemHandler;
        isIItemHandler = true;
        this.isVisible = true;
        this.isEnabled = true;
        this.rect= new Rect(position.x() -8, position.y() -8, position.x() + 8, position.y() + 8);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (!isIItemHandler)
            return super.mayPlace(stack);
        return itemHandler.isItemValid(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        if (!isIItemHandler)
            return super.getMaxStackSize();
        return this.itemHandler.getSlotLimit(this.index);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        if (!isIItemHandler)
            return super.getMaxStackSize(stack);

        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(index);
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;
            handlerModifiable.setStackInSlot(index, ItemStack.EMPTY);
            ItemStack remainder = handlerModifiable.insertItem(index, maxAdd, true);
            handlerModifiable.setStackInSlot(index, currentStack);
            return maxInput - remainder.getCount();
        } else {
            ItemStack remainder = handler.insertItem(index, maxAdd, true);
            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    public boolean isIItemHandler () {
        return isIItemHandler;
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn) {
        if (!isIItemHandler)
            return true;
        return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        if (!isIItemHandler)
            return super.remove(amount);
        return this.getItemHandler().extractItem(index, amount, false);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public IInventory getInventoryHandler() {
        return this.container;
    }

    @Override
    @Nonnull
    public ItemStack getItem() {
        if (!isIItemHandler)
            return super.getItem();
        return this.getItemHandler().getStackInSlot(index);
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void set(@Nonnull ItemStack stack) {
        if (!isIItemHandler)
            super.set(stack);
        else {
            ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
        }
        this.setChanged();
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

    }

    /** IClickable -------------------------------------------------------------------------------- */
    /**
     * Not implemented here due to being handled elsewhere
     *
     * @param matrixStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public float getZLevel() {
        return 0;
    }

    @Override
    public IDrawable setZLevel(float zLevel) {
        return this;
    }

    @Override
    public MusePoint2D center() {
        return rect.center();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void setOnPressed(IPressable onPressed) {
        this.onPressed = onPressed;
    }

    @Override
    public void setOnReleased(IReleasable onReleased) {
        this.onReleased = onReleased;
    }

    @Override
    public void onReleased() {
        if (this.isVisible && this.isEnabled && this.onReleased != null) {
            this.onReleased.onReleased(this);
        }
    }

    @Override
    public void onPressed() {
        if (this.isVisible && this.isEnabled && this.onPressed != null) {
            this.onPressed.onPressed(this);
        }
    }

    @Override
    public double top() {
        return rect.top();
    }

    @Override
    public Rect setTop(double value) {
        return rect.setTop(value);
    }

    @Override
    public double left() {
        return rect.left();
    }

    @Override
    public Rect setLeft(double value) {
        return rect.setLeft(value);
    }

    @Override
    public double bottom() {
        return rect.bottom();
    }

    @Override
    public Rect setBottom(double value) {
        return rect.setBottom(value);
    }

    @Override
    public double right() {
        return rect.right();
    }

    @Override
    public Rect setRight(double value) {
        return rect.setRight(value);
    }

    @Override
    public double width() {
        return rect.width();
    }

    @Override
    public Rect setWidth(double value) {
        return rect.setWidth(value);
    }

    @Override
    public double height() {
        return rect.height();
    }

    @Override
    public Rect setHeight(double value) {
        return rect.setHeight(value);
    }
    public void setPosition(MusePoint2D position) {
        this.rect.setPosition(position);
    }


    @Override
    public Rect setAbove(IRect otherBelowMe) {
        return rect.setAbove(otherBelowMe);
    }

    @Override
    public Rect setLeftOf(IRect otherRightOfMe) {
        return rect.setLeftOf(otherRightOfMe);
    }

    @Override
    public Rect setBelow(IRect otherAboveMe) {
        return rect.setBelow(otherAboveMe);
    }

    @Override
    public Rect setRightOf(IRect otherLeftOfMe) {
        return rect.setRightOf(otherLeftOfMe);
    }

    @Override
    public MusePoint2D getUL() {
        return rect.getUL();
    }

    @Override
    public MusePoint2D getWH() {
        return rect.getWH();
    }


    @Override
    public IRect setUL(MusePoint2D ul) {
        return rect.setUL(ul);
    }

    @Override
    public Rect setWH(MusePoint2D wh) {
        return rect.setWH(wh);
    }

    @Override
    public void moveBy(double x, double y) {
        this.rect.moveBy(x, y);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return rect.containsPoint(x, y);
    }

//
//    @Override
//    public void setDoThisOnChange(IDoThis iDoThis) {
//        this.iDoThis = iDoThis;
//    }
//
//    IRect.IDoThis iDoThis;
//    @Override
//    public void doThisOnChange() {
//        if (this.iDoThis != null) {
//            this.iDoThis.doThisOnChange(this);
//        }
//    }
}