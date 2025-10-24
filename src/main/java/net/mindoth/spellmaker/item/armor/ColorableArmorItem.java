package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.item.ModDyeableItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class ColorableArmorItem extends ModArmorItem implements ModDyeableItem {
    public ColorableArmorItem(ModArmorMaterials pMaterial, ArmorItem.Type pType, Item.Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }
}
