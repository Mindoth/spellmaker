package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ModDyeableItem;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.sigil.*;
import net.mindoth.spellmaker.item.weapon.ColorableSpellBookItem;
import net.mindoth.spellmaker.item.weapon.ColorableStaffItem;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpellMaker.MOD_ID);

    //Materials
    public static final RegistryObject<Item> RUNE_ESSENCE = ITEMS.register("rune_essence",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WOOL_CLOTH = ITEMS.register("wool_cloth",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PARCHMENT = ITEMS.register("parchment",
            () -> new ParchmentItem(new Item.Properties(), 3));

    public static final RegistryObject<Item> WOOD_ASH = ITEMS.register("wood_ash",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DESTRUCTION_DUST = ITEMS.register("destruction_dust",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ALTERATION_DUST = ITEMS.register("alteration_dust",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RESTORATION_DUST = ITEMS.register("restoration_dust",
            () -> new Item(new Item.Properties()));

    //Equipment
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new ColorableSpellBookItem(new Item.Properties(), ModDyeableItem.BLUE));

    public static final RegistryObject<Item> WOODEN_STAFF = ITEMS.register("wooden_staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(512), ModDyeableItem.GRAY));

    public static final RegistryObject<Item> SIMPLE_ROBE_HOOD = ITEMS.register("simple_robe_hood",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.HELMET, new Item.Properties(), ModDyeableItem.GRAY));

    public static final RegistryObject<Item> SIMPLE_ROBE_TOP = ITEMS.register("simple_robe_top",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ModDyeableItem.GRAY));

    public static final RegistryObject<Item> SIMPLE_ROBE_BOTTOM = ITEMS.register("simple_robe_bottom",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.LEGGINGS, new Item.Properties(), ModDyeableItem.GRAY));

    public static final RegistryObject<Item> SIMPLE_ROBE_BOOTS = ITEMS.register("simple_robe_boots",
            () -> new ColorableArmorItem(ModArmorMaterials.SIMPLE_ROBE, ArmorItem.Type.BOOTS, new Item.Properties(), ModDyeableItem.GRAY));

    //Runes
    public static final RegistryObject<Item> EXCAVATION_SIGIL = ITEMS.register("excavation_sigil",
            () -> new ExcavationSigilItem(new Item.Properties(), SpellColor.NATURE, 0, 0, 4, 5, 0, 0, 0));

    public static final RegistryObject<Item> FIRE_SIGIL = ITEMS.register("fire_sigil",
            () -> new FireSigilItem(new Item.Properties(), SpellColor.FIRE, 0, 0, 64, 2, 0, 64, 1));

    public static final RegistryObject<Item> FROST_SIGIL = ITEMS.register("frost_sigil",
            () -> new FrostSigilItem(new Item.Properties(), SpellColor.FROST, 0, 0, 64, 2, 0, 64, 1));

    public static final RegistryObject<Item> SHOCK_SIGIL = ITEMS.register("shock_sigil",
            () -> new ShockSigilItem(new Item.Properties(), SpellColor.LIGHTNING, 0, 0, 64, 2, 0, 64, 4));

    public static final RegistryObject<Item> SLEEP_SIGIL = ITEMS.register("sleep_sigil",
            () -> new SleepSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1));

    public static final RegistryObject<Item> SHEEP_FORM_SIGIL = ITEMS.register("sheep_form_sigil",
            () -> new SheepTransformationSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    UUID.fromString("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final RegistryObject<Item> FISH_FORM_SIGIL = ITEMS.register("fish_form_sigil",
            () -> new FishTransformationSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 0, 0, 64, 1,
                    UUID.fromString("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));
}
