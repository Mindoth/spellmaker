package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.item.ModDyeableItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;

public class ColorableArmorItem extends ModArmorItem implements ModDyeableItem {

    private final int defaultColor;

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    public ColorableArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, int defaultColor, AttributeContainer... attributes) {
        super(pMaterial, pType, pProperties, attributes);
        this.defaultColor = defaultColor;
    }
}
