package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.consumable.ModFood;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.armor.ArcaneRobeItem;
import net.mindoth.spellmaker.item.armor.ForestRobeItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.armor.WoolRobeItem;
import net.mindoth.spellmaker.item.sigil.*;
import net.mindoth.spellmaker.item.tool.SickleItem;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mindoth.spellmaker.item.armor.ModArmorItem.withMagickAttributes;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellMaker.MOD_ID);

    //Materials
    public static final DeferredItem<Item> PLANT_FIBER = ITEMS.registerItem("plant_fiber", (properties) -> new Item(properties));

    public static final DeferredItem<Item> WOOD_ASH = ITEMS.registerItem("wood_ash", (properties) -> new Item(properties));
    public static final DeferredItem<Item> ALCHEMICAL_ASH = ITEMS.registerItem("alchemical_ash", (properties) -> new Item(properties));

    public static final DeferredItem<Item> BASIC_SIGIL_ESSENCE = ITEMS.registerItem("basic_sigil_essence", (properties) -> new Item(properties));
    public static final DeferredItem<Item> REFINED_SIGIL_ESSENCE = ITEMS.registerItem("refined_sigil_essence", (properties) -> new Item(properties));

    public static final DeferredItem<Item> DESTRUCTION_DUST = ITEMS.registerItem("destruction_dust", (properties) -> new Item(properties));
    public static final DeferredItem<Item> ALTERATION_DUST = ITEMS.registerItem("alteration_dust", (properties) -> new Item(properties));
    public static final DeferredItem<Item> RESTORATION_DUST = ITEMS.registerItem("restoration_dust", (properties) -> new Item(properties));
    public static final DeferredItem<Item> CONJURATION_DUST = ITEMS.registerItem("conjuration_dust", (properties) -> new Item(properties));
    public static final DeferredItem<Item> ILLUSION_DUST = ITEMS.registerItem("illusion_dust", (properties) -> new Item(properties));
    public static final DeferredItem<Item> ARCANE_DUST = ITEMS.registerItem("arcane_dust", (properties) -> new Item(properties));

    public static final DeferredItem<Item> ARCANE_GEM = ITEMS.registerItem("arcane_gem", (properties) -> new Item(properties));
    public static final DeferredItem<Item> MANA_ESSENCE = ITEMS.registerItem("mana_essence", (properties) -> new Item(properties));
    public static final DeferredItem<Item> LIVING_ESSENCE = ITEMS.registerItem("living_essence", (properties) -> new Item(properties));

    public static final DeferredItem<Item> PARCHMENT = ITEMS.registerItem("parchment", (properties) -> new ParchmentItem(properties, 3));

    public static final DeferredItem<Item> WOOL_CLOTH = ITEMS.registerItem("wool_cloth", (properties) -> new Item(properties));
    public static final DeferredItem<Item> ARCANE_CLOTH = ITEMS.registerItem("arcane_cloth", (properties) -> new Item(properties));
    public static final DeferredItem<Item> FOREST_CLOTH = ITEMS.registerItem("forest_cloth", (properties) -> new Item(properties));

    //Consumables
    public static final DeferredItem<Item> GOLDEN_BREAD = ITEMS.registerItem("golden_bread",
            (properties) -> new Item(properties.food(ModFood.GOLDEN_BREAD)));

    //Tools
    public static final DeferredItem<Item> SICKLE = ITEMS.registerItem("sickle",
            (properties) -> new SickleItem(properties.durability(238)));

    //Equipment
    public static final DeferredItem<Item> SPELL_BOOK = ITEMS.registerItem("spell_book",
            (properties) -> new SpellBookItem(properties));

    public static final DeferredItem<Item> WOODEN_STAFF = ITEMS.registerItem("wooden_staff",
            (properties) -> new StaffItem(properties.durability(128), 0, 0));

    public static final DeferredItem<Item> GOLDEN_STAFF = ITEMS.registerItem("golden_staff",
            (properties) -> new StaffItem(properties.durability(512), 0, 0, withMagickAttributes(0, 0, 0.05D, 0)));

    public static final DeferredItem<Item> NETHERITE_STAFF = ITEMS.registerItem("netherite_staff",
            (properties) -> new StaffItem(properties.durability(1024).fireResistant(), 0, 0, withMagickAttributes(0, 0, 0.1D, 0)));

    public static final DeferredItem<Item> WOOL_ROBE_HOOD = ITEMS.registerItem("wool_robe_hood",
            (properties) -> new WoolRobeItem(properties, ModArmorMaterials.WOOL_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(15, 1, 0.0D, 0)));

    public static final DeferredItem<Item> WOOL_ROBE_HAT = ITEMS.registerItem("wool_robe_hat",
            (properties) -> new WoolRobeItem(properties, ModArmorMaterials.WOOL_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(15, 1, 0.0D, 0)));

    public static final DeferredItem<Item> WOOL_ROBE_TOP = ITEMS.registerItem("wool_robe_top",
            (properties) -> new WoolRobeItem(properties, ModArmorMaterials.WOOL_ROBE_MATERIAL, ArmorType.CHESTPLATE, withMagickAttributes(40, 1, 0.0D, 0)));

    public static final DeferredItem<Item> WOOL_ROBE_BOTTOM = ITEMS.registerItem("wool_robe_bottom",
            (properties) -> new WoolRobeItem(properties, ModArmorMaterials.WOOL_ROBE_MATERIAL, ArmorType.LEGGINGS, withMagickAttributes(30, 1, 0.0D, 0)));

    public static final DeferredItem<Item> WOOL_ROBE_BOOTS = ITEMS.registerItem("wool_robe_boots",
            (properties) -> new WoolRobeItem(properties, ModArmorMaterials.WOOL_ROBE_MATERIAL, ArmorType.BOOTS, withMagickAttributes(15, 1, 0.0D, 0)));

    public static final DeferredItem<Item> ARCANE_ROBE_HOOD = ITEMS.registerItem("arcane_robe_hood",
            (properties) -> new ArcaneRobeItem(properties, ModArmorMaterials.ARCANE_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(30, 1, 0.05D, 0)));

    public static final DeferredItem<Item> ARCANE_ROBE_HAT = ITEMS.registerItem("arcane_robe_hat",
            (properties) -> new ArcaneRobeItem(properties, ModArmorMaterials.ARCANE_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(30, 1, 0.05D, 0)));

    public static final DeferredItem<Item> ARCANE_ROBE_TOP = ITEMS.registerItem("arcane_robe_top",
            (properties) -> new ArcaneRobeItem(properties, ModArmorMaterials.ARCANE_ROBE_MATERIAL, ArmorType.CHESTPLATE, withMagickAttributes(80, 1, 0.05D, 0)));

    public static final DeferredItem<Item> ARCANE_ROBE_BOTTOM = ITEMS.registerItem("arcane_robe_bottom",
            (properties) -> new ArcaneRobeItem(properties, ModArmorMaterials.ARCANE_ROBE_MATERIAL, ArmorType.LEGGINGS, withMagickAttributes(60, 1, 0.05D, 0)));

    public static final DeferredItem<Item> ARCANE_ROBE_BOOTS = ITEMS.registerItem("arcane_robe_boots",
            (properties) -> new ArcaneRobeItem(properties, ModArmorMaterials.ARCANE_ROBE_MATERIAL, ArmorType.BOOTS, withMagickAttributes(30, 1, 0.05D, 0)));

    public static final DeferredItem<Item> FOREST_ROBE_HOOD = ITEMS.registerItem("forest_robe_hood",
            (properties) -> new ForestRobeItem(properties, ModArmorMaterials.FOREST_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(30, 2, 0.0D, 0.5D)));

    public static final DeferredItem<Item> FOREST_ROBE_TOP = ITEMS.registerItem("forest_robe_top",
            (properties) -> new ForestRobeItem(properties, ModArmorMaterials.FOREST_ROBE_MATERIAL, ArmorType.CHESTPLATE, withMagickAttributes(80, 2, 0.0D, 0.5D)));

    public static final DeferredItem<Item> FOREST_ROBE_BOTTOM = ITEMS.registerItem("forest_robe_bottom",
            (properties) -> new ForestRobeItem(properties, ModArmorMaterials.FOREST_ROBE_MATERIAL, ArmorType.LEGGINGS, withMagickAttributes(60, 2, 0.0D, 0.5D)));

    public static final DeferredItem<Item> FOREST_ROBE_BOOTS = ITEMS.registerItem("forest_robe_boots",
            (properties) -> new ForestRobeItem(properties, ModArmorMaterials.FOREST_ROBE_MATERIAL, ArmorType.BOOTS, withMagickAttributes(30, 2, 0.0D, 0.5D)));

    //Sigils

    //Destruction
    public static final DeferredItem<Item> FIRE_SIGIL = ITEMS.registerItem("fire_sigil",
            (properties) -> new FireSigilItem(properties, SpellColor.FIRE,
                    0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> FROST_SIGIL = ITEMS.registerItem("frost_sigil",
            (properties) -> new FrostSigilItem(properties, SpellColor.FROST,
                    0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> SHOCK_SIGIL = ITEMS.registerItem("shock_sigil",
            (properties) -> new ShockSigilItem(properties, SpellColor.LIGHTNING,
                    0, 0, 64, 2, 0, 64, 4));

    public static final DeferredItem<Item> SLEEP_SIGIL = ITEMS.registerItem("sleep_sigil",
            (properties) -> new SleepSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 0, 0, 0, 64, 1));

    //Alteration
    public static final DeferredItem<Item> SHEEP_FORM_SIGIL = ITEMS.registerItem("sheep_form_sigil",
            (properties) -> new SheepFormSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 0, 0, 0, 64, 1,
                    Identifier.parse("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityTypes.SHEEP));

    public static final DeferredItem<Item> FISH_FORM_SIGIL = ITEMS.registerItem("fish_form_sigil",
            (properties) -> new FishFormSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 0, 0, 0, 64, 1,
                    Identifier.parse("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityTypes.COD));

    public static final DeferredItem<Item> CHICKEN_FORM_SIGIL = ITEMS.registerItem("chicken_form_sigil",
            (properties) -> new ChickenFormSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 0, 0, 0, 64, 1,
                    Identifier.parse("baa27f16-774d-4767-99e0-218112d9241f"), EntityTypes.CHICKEN));

    public static final DeferredItem<Item> WOLF_FORM_SIGIL = ITEMS.registerItem("wolf_form_sigil",
            (properties) -> new WolfFormSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 0, 0, 0, 64, 1,
                    Identifier.parse("78b10eca-0e4c-4f77-9ce1-e8f736fb8d0a"), EntityTypes.WOLF));

    //Restoration
    public static final DeferredItem<Item> GROWTH_SIGIL = ITEMS.registerItem("growth_sigil",
            (properties) -> new GrowthSigilItem(properties, SpellColor.NATURE,
                    15, 0, 0, 0, 0, 0, 0));


    //Arcane
    public static final DeferredItem<Item> EXCAVATION_SIGIL = ITEMS.registerItem("excavation_sigil",
            (properties) -> new ExcavationSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 4, 5, 0, 0, 0));

    public static final DeferredItem<Item> FORCE_SIGIL = ITEMS.registerItem("force_sigil",
            (properties) -> new ForceSigilItem(properties, SpellColor.ARCANE,
                    0, -10, 10, 10, 0, 0, 0));

    public static final DeferredItem<Item> DISPEL_SIGIL = ITEMS.registerItem("dispel_sigil",
            (properties) -> new DispelSigilItem(properties, SpellColor.ARCANE,
                    0, 0, 64, 40, 0, 0, 0));

}
