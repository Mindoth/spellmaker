package net.mindoth.spellmaker.item.armor;

import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;

public class ModArmorMaterials {

    public static ResourceKey<EquipmentAsset> SIMPLE_ROBE = EquipmentAssets.createId("simple_robe");

    public static final ArmorMaterial SIMPLE_ROBE_MATERIAL = new ArmorMaterial(5,
            Util.make(new EnumMap<>(ArmorType.class), attribute -> {
                attribute.put(ArmorType.BOOTS, 0);
                attribute.put(ArmorType.LEGGINGS, 0);
                attribute.put(ArmorType.CHESTPLATE, 0);
                attribute.put(ArmorType.HELMET, 0);
                attribute.put(ArmorType.BODY, 0);
            }), 25, SoundEvents.ARMOR_EQUIP_LEATHER,
            0, 0, Tags.Items.STRINGS, SIMPLE_ROBE);
}
