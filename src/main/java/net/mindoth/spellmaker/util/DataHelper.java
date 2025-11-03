package net.mindoth.spellmaker.util;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
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
        ResourceLocation key = ResourceLocation.parse(tag.getString(ParchmentItem.NBT_KEY_SPELL_FORM));
        return ModSpellForms.SPELL_FORM_REGISTRY.get(key);
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
        String stringList = tag.getString(ParchmentItem.NBT_KEY_SPELL_SIGILS);
        List<ItemStack> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(string));
            ItemStack stack = new ItemStack(item);
            if ( stack.getItem() instanceof SigilItem || stack.isEmpty() ) list.add(stack);
        }
        return list;
    }

    public static LinkedHashMap<SigilItem, List<Integer>> createMapFromTag(CompoundTag tag) {
        List<SigilItem> sigilList = DataHelper.getSigilListFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_SIGILS));
        List<Integer> magnitudes = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES));
        List<Integer> durations = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS));
        return createMapFromLists(sigilList, magnitudes, durations);
    }

    public static LinkedHashMap<SigilItem, List<Integer>> createMapFromLists(List<SigilItem> sigilList, List<Integer> magnitudes, List<Integer> durations) {
        LinkedHashMap<SigilItem, List<Integer>> map = new LinkedHashMap<>();
        for ( int i = 0; i < sigilList.size(); i++ ) map.put(sigilList.get(i), Arrays.asList(magnitudes.get(i), durations.get(i)));
        return map;
    }

    public static LinkedHashMap<SigilItem, List<Integer>> createMapFromStackLists(List<ItemStack> sigilList, List<Integer> magnitudes, List<Integer> durations) {
        LinkedHashMap<SigilItem, List<Integer>> map = new LinkedHashMap<>();
        for ( int i = 0; i < sigilList.size(); i++ ) if ( sigilList.get(i).getItem() instanceof SigilItem sigil ) map.put(sigil, Arrays.asList(magnitudes.get(i), durations.get(i)));
        return map;
    }

    public static String getStringFromSigilList(List<SigilItem> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0; i < list.size(); i++ ) {
            Item item = list.get(i);
            if ( i > 0 ) stringBuilder.append(",");
            stringBuilder.append(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        return stringBuilder.toString();
    }

    public static List<SigilItem> getSigilListFromString(String stringList) {
        List<SigilItem> list = Lists.newArrayList();
        for ( String string : List.of(stringList.split(",")) ) {
            if ( BuiltInRegistries.ITEM.get(ResourceLocation.parse(string)) instanceof SigilItem sigil ) list.add(sigil);
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
