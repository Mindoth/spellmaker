package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.rune.ExcavateRuneItem;
import net.mindoth.spellmaker.item.rune.FireRuneItem;
import net.mindoth.spellmaker.item.rune.FrostRuneItem;
import net.mindoth.spellmaker.item.rune.ShockRuneItem;
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
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new SpellBookItem(new Item.Properties()));

    //Runes
    public static final RegistryObject<Item> EXCAVATE_RUNE = ITEMS.register("excavate_rune",
            () -> new ExcavateRuneItem(new Item.Properties(), SpellColor.GREEN, true, false, 1, 2, 0));

    public static final RegistryObject<Item> FIRE_RUNE = ITEMS.register("fire_rune",
            () -> new FireRuneItem(new Item.Properties(), SpellColor.YELLOW, true, true, 1, 1, 1));

    public static final RegistryObject<Item> FROST_RUNE = ITEMS.register("frost_rune",
            () -> new FrostRuneItem(new Item.Properties(), SpellColor.BLUE, true, true, 2, 1, 2));

    public static final RegistryObject<Item> SHOCK_RUNE = ITEMS.register("shock_rune",
            () -> new ShockRuneItem(new Item.Properties(), SpellColor.BLUE, true, true, 3, 1, 3));
}
