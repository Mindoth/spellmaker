package net.mindoth.spellmaker.client.gui.menu;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("removal")
public class AlembicResultSlot extends SlotItemHandler {

    public AlembicResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }
}
