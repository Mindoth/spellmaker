package net.mindoth.spellmaker.item.weapon;

import net.mindoth.spellmaker.item.SpellBookItem;

public class ColorableSpellBookItem extends SpellBookItem {

    private final int defaultColor;

    /*@Override
    public int getDefaultColor() {
        return this.defaultColor;
    }*/

    public ColorableSpellBookItem(Properties pProperties, int defaultColor) {
        super(pProperties);
        this.defaultColor = defaultColor;
    }
}
