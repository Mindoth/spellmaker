package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.StaffItem;
import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.rune.*;
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

    public static final RegistryObject<Item> ARCANE_CLOTH = ITEMS.register("arcane_cloth",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PARCHMENT = ITEMS.register("parchment",
            () -> new ParchmentItem(new Item.Properties(), 3));

    //Equipment
    public static final RegistryObject<Item> STAFF = ITEMS.register("staff",
            () -> new StaffItem(new Item.Properties().durability(512)));

    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new SpellBookItem(new Item.Properties()));

    public static final RegistryObject<Item> ROBE_HOOD = ITEMS.register("robe_hood",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_TOP = ITEMS.register("robe_top",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_BOTTOM = ITEMS.register("robe_bottom",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_BOOTS = ITEMS.register("robe_boots",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.BOOTS, new Item.Properties()));

    //Runes
    public static final RegistryObject<Item> EXCAVATE_RUNE = ITEMS.register("excavate_rune",
            () -> new ExcavateRuneItem(new Item.Properties(), SpellColor.GREEN, 5, 5, 2, 0, 0));

    public static final RegistryObject<Item> FIRE_RUNE = ITEMS.register("fire_rune",
            () -> new FireRuneItem(new Item.Properties(), SpellColor.GOLD, 1, 64, 1, 64, 1));

    public static final RegistryObject<Item> FROST_RUNE = ITEMS.register("frost_rune",
            () -> new FrostRuneItem(new Item.Properties(), SpellColor.AQUA, 2, 64, 1, 64, 1));

    public static final RegistryObject<Item> SHOCK_RUNE = ITEMS.register("shock_rune",
            () -> new ShockRuneItem(new Item.Properties(), SpellColor.DARK_BLUE, 3, 64, 1, 64, 5));

    public static final RegistryObject<Item> SLEEP_RUNE = ITEMS.register("sleep_rune",
            () -> new SleepRuneItem(new Item.Properties(), SpellColor.DARK_PURPLE, 3, 0, 0, 64, 2));

    public static final RegistryObject<Item> SHEEP_POLYMORPH_RUNE = ITEMS.register("sheep_polymorph_rune",
            () -> new SheepPolymorphRuneItem(new Item.Properties(), SpellColor.DARK_PURPLE, 5, 0, 0, 64, 1,
                    UUID.fromString("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final RegistryObject<Item> FISH_POLYMORPH_RUNE = ITEMS.register("fish_polymorph_rune",
            () -> new FishPolymorphItem(new Item.Properties(), SpellColor.DARK_PURPLE, 5, 0, 0, 64, 1,
                    UUID.fromString("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));
}
