package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class WoolRobeItem extends ModArmorItem implements CustomModelArmor {
    public WoolRobeItem(Properties properties, ArmorMaterial material, ArmorType type, AttributeContainer... extraAttributes) {
        super(properties, material, type, extraAttributes);
    }

    @Override
    public ModArmorItem hoodItem() {
        return (ModArmorItem) ModItems.WOOL_ROBE_HOOD.get();
    }
}
