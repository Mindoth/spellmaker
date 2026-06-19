package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.client.model.WoolRobeModel;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class WoolRobeItem extends ModArmorItem implements CustomModelArmor {
    public WoolRobeItem(Properties properties, ArmorMaterial material, ArmorType type, AttributeContainer... extraAttributes) {
        super(properties, material, type, extraAttributes);
    }

    @Override
    public HumanoidModel<?> getCustomArmorModel() {
        return new WoolRobeModel(WoolRobeModel.createLayerByType(type, this == ModItems.WOOL_ROBE_HOOD.get()).bakeRoot());
    }
}
