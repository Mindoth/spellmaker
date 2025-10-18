package net.mindoth.spellmaker.util;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class DataHelper {

    public static String getStringFromForm(SpellForm form) {
        return ModSpellForms.SPELL_FORM_REGISTRY.get().getKey(form).toString();
    }

    public static SpellForm getFormFromNbt(CompoundTag tag) {
        ResourceLocation key = new ResourceLocation(tag.getString(ParchmentItem.NBT_KEY_SPELL_FORM));
        return ModSpellForms.SPELL_FORM_REGISTRY.get().getValue(key);
    }

    public static String getStringFromSpellStack(List<ItemStack> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < list.size(); i++ ) {
            Item item = list.get(i).getItem();
            if ( i > 0 ) stringBuilder.append(",");
            stringBuilder.append(ForgeRegistries.ITEMS.getKey(item).toString());
        }
        return stringBuilder.toString();
    }

    public static List<ItemStack> getSpellStackFromTag(CompoundTag tag) {
        String stringList = tag.getString(ParchmentItem.NBT_KEY_SPELL_RUNES);
        List<ItemStack> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(string));
            ItemStack stack = new ItemStack(item);
            if ( stack.getItem() instanceof RuneItem || stack.isEmpty() ) list.add(stack);
        }
        return list;
    }

    public static LinkedHashMap<RuneItem, List<Integer>> createMapFromTag(CompoundTag tag) {
        LinkedHashMap<RuneItem, List<Integer>> map = new LinkedHashMap<>();
        List<Integer> magnitudes = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES));
        List<Integer> durations = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS));
        for ( int i = 0; i < DataHelper.getSpellStackFromTag(tag).size(); i++ ) {
            ItemStack itemStack = DataHelper.getSpellStackFromTag(tag).get(i);
            if ( itemStack.getItem() instanceof RuneItem rune ) map.put(rune, Arrays.asList(magnitudes.get(i), durations.get(i)));
        }
        return map;
    }

    public static List<RuneItem> getRuneListFromString(String stringList) {
        List<RuneItem> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            if ( ForgeRegistries.ITEMS.getValue(new ResourceLocation(string)) instanceof RuneItem rune ) list.add(rune);
        }
        return list;
    }

    public static String getStringFromStats(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < list.size(); i++ ) {
            if ( i > 0 ) stringBuilder.append(",");
            stringBuilder.append(list.get(i).toString());
        }
        return stringBuilder.toString();
    }

    public static List<Integer> getStatsFromString(String string) {
        List<Integer> stats = Lists.newArrayList();
        for ( String stat : List.of(string.split(",")) ) stats.add(Integer.parseInt(stat));
        return stats;
    }
}
