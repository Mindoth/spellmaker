package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RuneSlot extends Slot {

    public boolean isOpen;

    public RuneSlot(Container pContainer, int pSlot, int pX, int pY, boolean isOpen) {
        super(pContainer, pSlot, pX, pY);
        this.isOpen = isOpen;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() == ModItems.RUNE_ESSENCE.get() && this.isOpen;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
