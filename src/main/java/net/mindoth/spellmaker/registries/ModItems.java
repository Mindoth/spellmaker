package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.consumable.ModFood;
import net.mindoth.spellmaker.item.ModDyeableItem;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.armor.ArcaneRobeItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.armor.WoolRobeItem;
import net.mindoth.spellmaker.item.sigil.*;
import net.mindoth.spellmaker.item.weapon.ColorableSpellBookItem;
import net.mindoth.spellmaker.item.weapon.ColorableStaffItem;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mindoth.spellmaker.item.armor.ModArmorItem.withMagickAttributes;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellMaker.MOD_ID);

    //Materials
    public static final DeferredItem<Item> SIGIL_ESSENCE = ITEMS.register("sigil_essence",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WOOL_CLOTH = ITEMS.register("wool_cloth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ARCANE_CLOTH = ITEMS.register("arcane_cloth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ARCANE_GEM = ITEMS.register("arcane_gem",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PARCHMENT = ITEMS.register("parchment",
            () -> new ParchmentItem(new Item.Properties(), 3));

    public static final DeferredItem<Item> WOOD_ASH = ITEMS.register("wood_ash",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DESTRUCTION_DUST = ITEMS.register("destruction_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ALTERATION_DUST = ITEMS.register("alteration_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RESTORATION_DUST = ITEMS.register("restoration_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CONJURATION_DUST = ITEMS.register("conjuration_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ILLUSION_DUST = ITEMS.register("illusion_dust",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ARCANE_DUST = ITEMS.register("arcane_dust",
            () -> new Item(new Item.Properties()));

    //Consumables
    public static final DeferredItem<Item> GOLDEN_BREAD = ITEMS.registerItem("golden_bread",
            (properties) -> new Item(properties.food(ModFood.GOLDEN_BREAD)));

    //Equipment
    public static final DeferredItem<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new ColorableSpellBookItem(new Item.Properties(),
                    ModDyeableItem.BLUE));

    public static final DeferredItem<Item> WOODEN_STAFF = ITEMS.register("wooden_staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(128),
                    ModDyeableItem.GRAY));

    public static final DeferredItem<Item> GOLDEN_STAFF = ITEMS.register("golden_staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(128),
                    ModDyeableItem.GRAY));

    public static final DeferredItem<Item> NETHERITE_STAFF = ITEMS.register("netherite_staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(128),
                    ModDyeableItem.GRAY));

    public static final DeferredItem<Item> WOOL_ROBE_HOOD = ITEMS.register("wool_robe_hood",
            () -> new WoolRobeItem(ModArmorMaterials.WOOL_ROBE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(11)),
                    ModDyeableItem.GRAY, withMagickAttributes(15, 0.05D)));

    public static final DeferredItem<Item> WOOL_ROBE_HAT = ITEMS.register("wool_robe_hat",
            () -> new WoolRobeItem(ModArmorMaterials.WOOL_ROBE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(11)),
                    ModDyeableItem.GRAY, withMagickAttributes(15, 0.05D)));

    public static final DeferredItem<Item> WOOL_ROBE_TOP = ITEMS.register("wool_robe_top",
            () -> new WoolRobeItem(ModArmorMaterials.WOOL_ROBE, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(16)),
                    ModDyeableItem.GRAY, withMagickAttributes(40, 0.05D)));

    public static final DeferredItem<Item> WOOL_ROBE_BOTTOM = ITEMS.register("wool_robe_bottom",
            () -> new WoolRobeItem(ModArmorMaterials.WOOL_ROBE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(16)),
                    ModDyeableItem.GRAY, withMagickAttributes(30, 0.05D)));

    public static final DeferredItem<Item> WOOL_ROBE_BOOTS = ITEMS.register("wool_robe_boots",
            () -> new WoolRobeItem(ModArmorMaterials.WOOL_ROBE, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(13)),
                    ModDyeableItem.GRAY, withMagickAttributes(15, 0.05D)));

    public static final DeferredItem<Item> ARCANE_ROBE_HOOD = ITEMS.register("arcane_robe_hood",
            () -> new ArcaneRobeItem(ModArmorMaterials.ARCANE_ROBE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(11)),
                    ModDyeableItem.BLUE, withMagickAttributes(30, 0.10D)));

    public static final DeferredItem<Item> ARCANE_ROBE_HAT = ITEMS.register("arcane_robe_hat",
            () -> new ArcaneRobeItem(ModArmorMaterials.ARCANE_ROBE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(11)),
                    ModDyeableItem.BLUE, withMagickAttributes(30, 0.10D)));

    public static final DeferredItem<Item> ARCANE_ROBE_TOP = ITEMS.register("arcane_robe_top",
            () -> new ArcaneRobeItem(ModArmorMaterials.ARCANE_ROBE, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(16)),
                    ModDyeableItem.BLUE, withMagickAttributes(80, 0.10D)));

    public static final DeferredItem<Item> ARCANE_ROBE_BOTTOM = ITEMS.register("arcane_robe_bottom",
            () -> new ArcaneRobeItem(ModArmorMaterials.ARCANE_ROBE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(16)),
                    ModDyeableItem.BLUE, withMagickAttributes(60, 0.10D)));

    public static final DeferredItem<Item> ARCANE_ROBE_BOOTS = ITEMS.register("arcane_robe_boots",
            () -> new ArcaneRobeItem(ModArmorMaterials.ARCANE_ROBE, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(13)),
                    ModDyeableItem.BLUE, withMagickAttributes(30, 0.10D)));

    //Sigils


    //Destruction
    public static final DeferredItem<Item> FIRE_SIGIL = ITEMS.register("fire_sigil",
            () -> new FireSigilItem(new Item.Properties(), SpellColor.FIRE, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> FROST_SIGIL = ITEMS.register("frost_sigil",
            () -> new FrostSigilItem(new Item.Properties(), SpellColor.FROST, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> SHOCK_SIGIL = ITEMS.register("shock_sigil",
            () -> new ShockSigilItem(new Item.Properties(), SpellColor.LIGHTNING, 0, 0, 64, 2, 0, 64, 4));

    public static final DeferredItem<Item> SLEEP_SIGIL = ITEMS.register("sleep_sigil",
            () -> new SleepSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1));

    //Alteration
    public static final DeferredItem<Item> EXCAVATION_SIGIL = ITEMS.register("excavation_sigil",
            () -> new ExcavationSigilItem(new Item.Properties(), SpellColor.NATURE, 0, 0, 4, 5, 0, 0, 0));

    public static final DeferredItem<Item> SHEEP_FORM_SIGIL = ITEMS.register("sheep_form_sigil",
            () -> new SheepFormSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final DeferredItem<Item> FISH_FORM_SIGIL = ITEMS.register("fish_form_sigil",
            () -> new FishFormSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));

    public static final DeferredItem<Item> CHICKEN_FORM_SIGIL = ITEMS.register("chicken_form_sigil",
            () -> new ChickenFormSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("baa27f16-774d-4767-99e0-218112d9241f"), EntityType.CHICKEN));

    //Arcane
    public static final DeferredItem<Item> FORCE_SIGIL = ITEMS.register("force_sigil",
            () -> new ForceSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, -10, 10, 10, 0, 0, 0));
}
