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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, SpellMaker.MOD_ID);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> SIMPLE_ROBE = register("simple_robe",
            makeArmorMap(0, 0, 0, 0),
            15,
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

    /*SIMPLE_ROBE("simple_robe", 4, new int[]{ 0, 0, 0, 0 }, 25, SoundEvents.ARMOR_EQUIP_LEATHER,
            0, 0, () -> Ingredient.of(ModItems.WOOL_CLOTH.get()), Map.of(
            ModAttributes.MANA_MAX.get(), new AttributeModifier(ResourceLocation.parse(), 25.0D, AttributeModifier.Operation.ADD_VALUE),
            ModAttributes.MANA_COST_MULTIPLIER.get(), new AttributeModifier("Mana Discount", 0.05D, AttributeModifier.Operation.ADD_VALUE)
    ));

    private final String name;
    private final int durabilityModifier;
    private final int[] protectionAmounts;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;
    private final Map<Attribute, AttributeModifier> additionalAttributes;

    private static final int[] BASE_DURABILITY = { 11, 16, 15, 13 };

    ModArmorMaterials(String name, int durabilityModifier, int[] protectionAmounts, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient, Map<Attribute, AttributeModifier> additionalAttributes) {
        this.name = name;
        this.durabilityModifier = durabilityModifier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {
        return BASE_DURABILITY[pType.ordinal()] * this.durabilityModifier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {
        return this.protectionAmounts[pType.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return SpellMaker.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public Map<Attribute, AttributeModifier> getAdditionalAttributes() {
        return this.additionalAttributes;
    }*/
}
