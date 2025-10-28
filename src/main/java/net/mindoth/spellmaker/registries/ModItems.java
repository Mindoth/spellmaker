package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.item.weapon.ColorableStaffItem;
import net.mindoth.spellmaker.item.sigil.*;
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
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new SpellBookItem(new Item.Properties()));

    public static final RegistryObject<Item> STAFF = ITEMS.register("staff",
            () -> new ColorableStaffItem(new Item.Properties().durability(512)));

    public static final RegistryObject<Item> ROBE_HOOD = ITEMS.register("robe_hood",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_TOP = ITEMS.register("robe_top",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_BOTTOM = ITEMS.register("robe_bottom",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> ROBE_BOOTS = ITEMS.register("robe_boots",
            () -> new ColorableArmorItem(ModArmorMaterials.ROBE, ArmorItem.Type.BOOTS, new Item.Properties()));

    //Runes
    public static final RegistryObject<Item> EXCAVATION_SIGIL = ITEMS.register("excavation_sigil",
            () -> new ExcavationSigilItem(new Item.Properties(), SpellColor.NATURE, 0, 5, 5, 0, 0));

    public static final RegistryObject<Item> FIRE_SIGIL = ITEMS.register("fire_sigil",
            () -> new FireSigilItem(new Item.Properties(), SpellColor.FIRE, 0, 64, 1, 64, 1));

    public static final RegistryObject<Item> FROST_SIGIL = ITEMS.register("frost_sigil",
            () -> new FrostSigilItem(new Item.Properties(), SpellColor.FROST, 0, 64, 1, 64, 1));

    public static final RegistryObject<Item> SHOCK_SIGIL = ITEMS.register("shock_sigil",
            () -> new ShockSigilItem(new Item.Properties(), SpellColor.LIGHTNING, 0, 64, 2, 64, 2));

    public static final RegistryObject<Item> SLEEP_SIGIL = ITEMS.register("sleep_sigil",
            () -> new SleepSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 64, 1));

    public static final RegistryObject<Item> SHEEP_TRANSFORMATION_SIGIL = ITEMS.register("sheep_transformation_sigil",
            () -> new SheepTransformationSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 64, 1,
                    UUID.fromString("84527dc5-d3e5-4550-98ed-c8186c5d3089"), EntityType.SHEEP));

    public static final RegistryObject<Item> FISH_TRANSFORMATION_SIGIL = ITEMS.register("fish_transformation_sigil",
            () -> new FishTransformationSigilItem(new Item.Properties(), SpellColor.ARCANE, 0, 0, 0, 64, 1,
                    UUID.fromString("b2bc1fd5-a121-42cf-b7cb-d29c61e3211c"), EntityType.COD));
}
