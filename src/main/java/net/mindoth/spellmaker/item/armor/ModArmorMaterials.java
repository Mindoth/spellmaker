package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.util.ModTags;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

public class ModArmorMaterials {

    public static final ArmorMaterial SIMPLE_ROBE_MATERIAL = new ArmorMaterial(5,
            Util.make(new EnumMap<>(ArmorType.class), attribute -> {
                attribute.put(ArmorType.BOOTS, 0);
                attribute.put(ArmorType.LEGGINGS, 0);
                attribute.put(ArmorType.CHESTPLATE, 0);
                attribute.put(ArmorType.HELMET, 0);
                attribute.put(ArmorType.BODY, 0);
            }), 25, SoundEvents.ARMOR_EQUIP_LEATHER,
            0, 0, ModTags.Items.WOOL_CLOTH_REPAIRABLE,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "simple_robe")));
}
