package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> NEEDS_ARCANUM_TOOL = createTag("needs_arcanum_tool");
        public static final TagKey<Block> INCORRECT_FOR_ARCANUM_TOOL = createTag("incorrect_for_arcanum_tool");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ARCANUM_REPAIRABLE = createTag("arcanum_repairable");
        public static final TagKey<Item> WOOL_CLOTH_REPAIRABLE = createTag("wool_cloth_repairable");
        public static final TagKey<Item> ARCANE_CLOTH_REPAIRABLE = createTag("arcane_cloth_repairable");
        public static final TagKey<Item> FOREST_CLOTH_REPAIRABLE = createTag("forest_cloth_repairable");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
        }
    }
}
