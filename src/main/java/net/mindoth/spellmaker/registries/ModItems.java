package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.StaffItem;
import net.mindoth.spellmaker.item.rune.*;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpellMaker.MOD_ID);

    //Materials
    public static final RegistryObject<Item> RUNE_ESSENCE = ITEMS.register("rune_essence",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PARCHMENT = ITEMS.register("parchment",
            () -> new ParchmentItem(new Item.Properties(), 1));

    public static final RegistryObject<Item> ARCANE_PARCHMENT = ITEMS.register("arcane_parchment",
            () -> new ParchmentItem(new Item.Properties(), 2));

    //Equipment
    public static final RegistryObject<Item> STAFF = ITEMS.register("staff",
            () -> new StaffItem(new Item.Properties().durability(512)));

    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new SpellBookItem(new Item.Properties()));

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
            () -> new SleepRuneItem(new Item.Properties(), SpellColor.DARK_PURPLE, 3, 0, 0, 64, 4));

    public static final RegistryObject<Item> POLYMORPH_RUNE = ITEMS.register("polymorph_rune",
            () -> new PolymorphRuneItem(new Item.Properties(), SpellColor.DARK_PURPLE, 10, 0, 0, 64, 5));
}
