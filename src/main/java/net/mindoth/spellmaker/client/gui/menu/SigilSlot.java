package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SigilSlot extends Slot {

    public boolean isOpen;

    public SigilSlot(Container pContainer, int pSlot, int pX, int pY, boolean isOpen) {
        super(pContainer, pSlot, pX, pY);
        this.isOpen = isOpen;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof SigilItem && this.isOpen;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
