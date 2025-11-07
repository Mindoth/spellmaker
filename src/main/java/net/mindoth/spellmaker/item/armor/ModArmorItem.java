package net.mindoth.spellmaker.item.armor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ModArmorItem extends Item {

    public final ArmorMaterial material;
    public final ArmorType type;

    public ModArmorItem(Properties properties, ArmorMaterial material, ArmorType type) {
        super(properties.humanoidArmor(material, type));
        this.material = material;
        this.type = type;
    }
}
