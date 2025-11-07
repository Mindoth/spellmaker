package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class ModRecipePropertySets {

    public static final ResourceKey<RecipePropertySet> CALCINATOR_INPUT = register("calcinator_input");

    private static ResourceKey<RecipePropertySet> register(String name) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
    }
}
