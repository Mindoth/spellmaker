package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ModDyeableItem;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.armor.ModArmorItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.sigil.*;
import net.mindoth.spellmaker.item.weapon.ColorableSpellBookItem;
import net.mindoth.spellmaker.item.weapon.ColorableStaffItem;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mindoth.spellmaker.item.armor.ModArmorItem.withMagickAttributes;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellMaker.MOD_ID);

    //Materials
    public static final DeferredItem<Item> SIGIL_ESSENCE = ITEMS.registerItem("sigil_essence",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> WOOL_CLOTH = ITEMS.registerItem("wool_cloth",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> PARCHMENT = ITEMS.registerItem("parchment",
            (properties) -> new ParchmentItem(properties, 3));

    public static final DeferredItem<Item> WOOD_ASH = ITEMS.registerItem("wood_ash",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> DESTRUCTION_DUST = ITEMS.registerItem("destruction_dust",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> ALTERATION_DUST = ITEMS.registerItem("alteration_dust",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> RESTORATION_DUST = ITEMS.registerItem("restoration_dust",
            (properties) -> new Item(properties));

    public static final DeferredItem<Item> MYSTIC_DUST = ITEMS.registerItem("mystic_dust",
            (properties) -> new Item(properties));

    //Equipment
    public static final DeferredItem<Item> SPELL_BOOK = ITEMS.registerItem("spell_book",
            (properties) -> new ColorableSpellBookItem(properties,
                    ModDyeableItem.BLUE));

    public static final DeferredItem<Item> WOODEN_STAFF = ITEMS.registerItem("wooden_staff",
            (properties) -> new ColorableStaffItem(properties.durability(512),
                    ModDyeableItem.GRAY));

    public static final DeferredItem<Item> SIMPLE_ROBE_HOOD = ITEMS.registerItem("simple_robe_hood",
            (properties) -> new ModArmorItem(properties, ModArmorMaterials.SIMPLE_ROBE_MATERIAL, ArmorType.HELMET, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_TOP = ITEMS.registerItem("simple_robe_top",
            (properties) -> new ModArmorItem(properties, ModArmorMaterials.SIMPLE_ROBE_MATERIAL, ArmorType.CHESTPLATE, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_BOTTOM = ITEMS.registerItem("simple_robe_bottom",
            (properties) -> new ModArmorItem(properties, ModArmorMaterials.SIMPLE_ROBE_MATERIAL, ArmorType.LEGGINGS, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_BOOTS = ITEMS.registerItem("simple_robe_boots",
            (properties) -> new ModArmorItem(properties, ModArmorMaterials.SIMPLE_ROBE_MATERIAL, ArmorType.BOOTS, withMagickAttributes(25, 0.05D)));

    //Sigils

    //Destruction
    public static final DeferredItem<Item> FIRE_SIGIL = ITEMS.registerItem("fire_sigil",
            (properties) -> new FireSigilItem(properties, SpellColor.FIRE, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> FROST_SIGIL = ITEMS.registerItem("frost_sigil",
            (properties) -> new FrostSigilItem(properties, SpellColor.FROST, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> SHOCK_SIGIL = ITEMS.registerItem("shock_sigil",
            (properties) -> new ShockSigilItem(properties, SpellColor.LIGHTNING, 0, 0, 64, 2, 0, 64, 4));

    public static final DeferredItem<Item> SLEEP_SIGIL = ITEMS.registerItem("sleep_sigil",
            (properties) -> new SleepSigilItem(properties, SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1));

    //Alteration
    public static final DeferredItem<Item> EXCAVATION_SIGIL = ITEMS.registerItem("excavation_sigil",
            (properties) -> new ExcavationSigilItem(properties, SpellColor.NATURE, 0, 0, 4, 5, 0, 0, 0));

    public static final DeferredItem<Item> SHEEP_FORM_SIGIL = ITEMS.registerItem("sheep_form_sigil",
            (properties) -> new SheepFormSigilItem(properties, SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final DeferredItem<Item> FISH_FORM_SIGIL = ITEMS.registerItem("fish_form_sigil",
            (properties) -> new FishFormSigilItem(properties, SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));

    public static final DeferredItem<Item> CHICKEN_FORM_SIGIL = ITEMS.registerItem("chicken_form_sigil",
            (properties) -> new ChickenFormSigilItem(properties, SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("baa27f16-774d-4767-99e0-218112d9241f"), EntityType.CHICKEN));

    //Restoration

    //Mystic
    public static final DeferredItem<Item> FORCE_SIGIL = ITEMS.registerItem("force_sigil",
            (properties) -> new ForceSigilItem(properties, SpellColor.ARCANE, 0, -10, 10, 10, 0, 0, 0));

}
