package net.mindoth.spellmaker.client.gui.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("removal")
public class AlembicFuelSlot extends SlotItemHandler {
    private final AlembicMenu menu;

    public AlembicFuelSlot(AlembicMenu menu, IItemHandler itemHandler, int slot, int x, int y) {
        super(itemHandler, slot, x, y);
        this.menu = menu;
    }

    public boolean mayPlace(ItemStack itemStack) {
        return this.menu.isFuel(itemStack) || isBucket(itemStack);
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return isBucket(itemStack) ? 1 : super.getMaxStackSize(itemStack);
    }

    public static boolean isBucket(ItemStack itemStack) {
        return itemStack.is(Items.BUCKET);
    }
}
