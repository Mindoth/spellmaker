package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.item.ModDyeableItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ColorableArmorItem extends ModArmorItem implements ModDyeableItem {

    private final int defaultColor;

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    public ColorableArmorItem(Holder<ArmorMaterial> pMaterial, ArmorType pType, Properties pProperties, int defaultColor, AttributeContainer... attributes) {
        super(pProperties);
        this.defaultColor = defaultColor;
    }
}
