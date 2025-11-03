package net.mindoth.spellmaker.item.weapon;

import net.mindoth.spellmaker.item.ModDyeableItem;

public class ColorableStaffItem extends StaffItem implements ModDyeableItem {

    private final int defaultColor;

    /*@Override
    public int getDefaultColor() {
        return this.defaultColor;
    }*/

    public ColorableStaffItem(Properties pProperties, int defaultColor) {
        super(pProperties);
        this.defaultColor = defaultColor;
    }
}
