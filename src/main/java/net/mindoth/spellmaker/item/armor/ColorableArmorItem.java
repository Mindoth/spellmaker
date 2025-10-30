package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.item.ModDyeableItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class ColorableArmorItem extends ModArmorItem implements ModDyeableItem {

    private final int defaultColor;

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    public ColorableArmorItem(ModArmorMaterials pMaterial, ArmorItem.Type pType, Item.Properties pProperties, int defaultColor) {
        super(pMaterial, pType, pProperties);
        this.defaultColor = defaultColor;
    }
}
