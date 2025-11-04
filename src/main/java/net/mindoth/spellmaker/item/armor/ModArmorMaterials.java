package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, SpellMaker.MOD_ID);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> SIMPLE_ROBE = register("simple_robe",
            makeArmorMap(0, 0, 0, 0),
            25,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(ModItems.WOOL_CLOTH.get()),
            0,
            0);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            Supplier<Ingredient> repairIngredient,
            float toughness,
            float knockbackResistance
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, name)));
        return ARMOR_MATERIALS.register(name, ()-> new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance));
    }

    static public EnumMap<ArmorItem.Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), (holder) -> {
            holder.put(ArmorItem.Type.BOOTS, boots);
            holder.put(ArmorItem.Type.LEGGINGS, leggings);
            holder.put(ArmorItem.Type.CHESTPLATE, chestplate);
            holder.put(ArmorItem.Type.HELMET, helmet);
        });
    }
}
