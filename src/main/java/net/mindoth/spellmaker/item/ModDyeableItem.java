package net.mindoth.spellmaker.item;

public interface ModDyeableItem /*extends DyeableLeatherItem*/ {
    /*String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";
    int WHITE = 16777215;
    int GRAY = 11711154;
    int BROWN = 10511680;
    int BLUE = 5804213;
    int RED = 12667459;

    default int getDefaultColor() {
        return WHITE;
    }

    @Override
    default boolean hasCustomColor(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99);
    }

    @Override
    default int getColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement(TAG_DISPLAY);
        if ( compoundtag != null && compoundtag.contains(TAG_COLOR, 99) ) return compoundtag.getInt(TAG_COLOR);
        return getDefaultColor();
    }

    @Override
    default void clearColor(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTagElement(TAG_DISPLAY);
        if ( compoundtag != null && compoundtag.contains(TAG_COLOR) ) compoundtag.remove(TAG_COLOR);
    }

    @Override
    default void setColor(ItemStack pStack, int pColor) {
        pStack.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, pColor);
    }*/
}
