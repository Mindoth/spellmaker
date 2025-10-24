package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ModDyeableItem extends DyeableLeatherItem {
    String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";
    int WHITE = 16777215;
    int BLUE = 5804213;
    int BROWN = 10511680;
    int RED = 12667459;
    int GRAY = 6843241;

    @Override
    default boolean hasCustomColor(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99);
    }

    @Override
    default int getColor(ItemStack stack) {
        Item item = stack.getItem();
        CompoundTag compoundtag = stack.getTagElement(TAG_DISPLAY);
        if ( compoundtag != null && compoundtag.contains(TAG_COLOR, 99) ) return compoundtag.getInt(TAG_COLOR);
        else if ( item instanceof SpellBookItem || item instanceof ColorableArmorItem ) return BLUE;
        return WHITE;
    }

    @Override
    default void clearColor(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTagElement(TAG_DISPLAY);
        if ( compoundtag != null && compoundtag.contains(TAG_COLOR) ) compoundtag.remove(TAG_COLOR);
    }

    @Override
    default void setColor(ItemStack pStack, int pColor) {
        pStack.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, pColor);
    }
}
