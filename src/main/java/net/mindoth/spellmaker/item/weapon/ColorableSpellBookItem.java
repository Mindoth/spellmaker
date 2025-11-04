package net.mindoth.spellmaker.item.weapon;

import net.mindoth.spellmaker.item.ModDyeableItem;

public class ColorableSpellBookItem extends SpellBookItem implements ModDyeableItem {

    private final int defaultColor;

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    public ColorableSpellBookItem(Properties pProperties, int defaultColor) {
        super(pProperties);
        this.defaultColor = defaultColor;
    }
}
