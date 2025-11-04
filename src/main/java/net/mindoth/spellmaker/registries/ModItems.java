package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
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
    public static final DeferredItem<Item> RUNE_ESSENCE = ITEMS.register("rune_essence",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WOOL_CLOTH = ITEMS.register("wool_cloth",
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

    //Equipment
    public static final DeferredItem<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new ColorableSpellBookItem(new Item.Properties(), 5804213));

    public static final DeferredItem<Item> WOODEN_STAFF = ITEMS.register("wooden_staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(512), 11711154));

    public static final DeferredItem<Item> SIMPLE_ROBE_HOOD = ITEMS.register("simple_robe_hood",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(11)),
                    11711154, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_TOP = ITEMS.register("simple_robe_top",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(16)),
                    11711154, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_BOTTOM = ITEMS.register("simple_robe_bottom",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(16)),
                    11711154, withMagickAttributes(25, 0.05D)));

    public static final DeferredItem<Item> SIMPLE_ROBE_BOOTS = ITEMS.register("simple_robe_boots",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(13)),
                    11711154, withMagickAttributes(25, 0.05D)));

    //Runes
    public static final DeferredItem<Item> EXCAVATION_SIGIL = ITEMS.register("excavation_sigil",
            () -> new ExcavationSigilItem(new Item.Properties(), SpellColor.NATURE, 0, 0, 4, 5, 0, 0, 0));

    public static final DeferredItem<Item> FIRE_SIGIL = ITEMS.register("fire_sigil",
            () -> new FireSigilItem(new Item.Properties(), SpellColor.FIRE, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> FROST_SIGIL = ITEMS.register("frost_sigil",
            () -> new FrostSigilItem(new Item.Properties(), SpellColor.FROST, 0, 0, 64, 2, 0, 64, 1));

    public static final DeferredItem<Item> SHOCK_SIGIL = ITEMS.register("shock_sigil",
            () -> new ShockSigilItem(new Item.Properties(), SpellColor.LIGHTNING, 0, 0, 64, 2, 0, 64, 4));

    public static final DeferredItem<Item> SLEEP_SIGIL = ITEMS.register("sleep_sigil",
            () -> new SleepSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1));

    public static final DeferredItem<Item> SHEEP_FORM_SIGIL = ITEMS.register("sheep_form_sigil",
            () -> new SheepFormSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final DeferredItem<Item> FISH_FORM_SIGIL = ITEMS.register("fish_form_sigil",
            () -> new FishFormSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    ResourceLocation.parse("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));
}
