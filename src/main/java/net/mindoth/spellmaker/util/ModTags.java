package net.mindoth.spellmaker.util;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> WOOL_CLOTH_REPAIRABLE = createTag("wool_cloth_repairable");
        public static final TagKey<Item> ARCANE_CLOTH_REPAIRABLE = createTag("arcane_cloth_repairable");
        public static final TagKey<Item> RUNE_CLOTH_REPAIRABLE = createTag("rune_cloth_repairable");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
        }
    }
}
