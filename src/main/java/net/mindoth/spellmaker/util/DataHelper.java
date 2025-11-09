package net.mindoth.spellmaker.util;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.AbstractSigilItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class DataHelper {

    public static String getStringFromForm(AbstractSpellForm form) {
        return ModSpellForms.SPELL_FORM_REGISTRY.getKey(form).toString();
    }

    public static AbstractSpellForm getFormFromNbt(CompoundTag tag) {
        ResourceLocation key = ResourceLocation.parse(tag.getString(ParchmentItem.NBT_KEY_SPELL_FORM).get());
        return ModSpellForms.SPELL_FORM_REGISTRY.getValue(key);
    }

    public static String getStringFromSpellStack(List<ItemStack> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < list.size(); i++ ) {
            Item item = list.get(i).getItem();
            if ( i > 0 ) stringBuilder.append(",");
            stringBuilder.append(BuiltInRegistries.ITEM.getKey(item));
        }
        return stringBuilder.toString();
    }

    public static List<ItemStack> getSpellStackFromTag(CompoundTag tag) {
        String stringList = tag.getString(ParchmentItem.NBT_KEY_SPELL_SIGILS).get();
        List<ItemStack> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(string));
            ItemStack stack = new ItemStack(item);
            if ( stack.getItem() instanceof AbstractSigilItem || stack.isEmpty() ) list.add(stack);
        }
        return list;
    }

    public static LinkedHashMap<AbstractSigilItem, List<Integer>> createMapFromTag(CompoundTag tag) {
        List<AbstractSigilItem> sigilList = DataHelper.getSigilListFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_SIGILS).get());
        List<Integer> magnitudes = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES).get());
        List<Integer> durations = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS).get());
        return createMapFromLists(sigilList, magnitudes, durations);
    }

    public static LinkedHashMap<AbstractSigilItem, List<Integer>> createMapFromLists(List<AbstractSigilItem> sigilList, List<Integer> magnitudes, List<Integer> durations) {
        LinkedHashMap<AbstractSigilItem, List<Integer>> map = new LinkedHashMap<>();
        for ( int i = 0; i < sigilList.size(); i++ ) map.put(sigilList.get(i), Arrays.asList(magnitudes.get(i), durations.get(i)));
        return map;
    }

    public static LinkedHashMap<AbstractSigilItem, List<Integer>> createMapFromStackLists(List<ItemStack> sigilList, List<Integer> magnitudes, List<Integer> durations) {
        LinkedHashMap<AbstractSigilItem, List<Integer>> map = new LinkedHashMap<>();
        for ( int i = 0; i < sigilList.size(); i++ ) if ( sigilList.get(i).getItem() instanceof AbstractSigilItem sigil ) map.put(sigil, Arrays.asList(magnitudes.get(i), durations.get(i)));
        return map;
    }

    public static String getStringFromSigilList(List<AbstractSigilItem> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < list.size(); i++ ) {
            Item item = list.get(i);
            if ( i > 0 ) stringBuilder.append(",");
            stringBuilder.append(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        return stringBuilder.toString();
    }

    public static List<AbstractSigilItem> getSigilListFromString(String stringList) {
        List<AbstractSigilItem> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            if ( BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(string)) instanceof AbstractSigilItem sigil ) list.add(sigil);
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
