package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.item.ParchmentItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ParchmentSlot extends Slot {

    public ParchmentSlot(Container pContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pContainer, pSlot, pXPosition, pYPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ParchmentItem && !this.hasItem();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
