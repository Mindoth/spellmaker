package net.mindoth.spellmaker.recipe;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SpellMaker.MOD_ID);

    public static final Supplier<RecipeSerializer<SpellBookAddRecipe>> SPELL_BOOK_ADD_RECIPE =
            SERIALIZERS.register("spell_book_add_crafting", () -> SpellBookAddRecipe.SERIALIZER);

    public static final Supplier<RecipeSerializer<CalcinatingRecipe>> CALCINATING_RECIPE =
            SERIALIZERS.register("calcinating", () -> CalcinatingRecipe.SERIALIZER);



    private static ResourceKey<RecipePropertySet> register(String name) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
    }

    public static final ResourceKey<RecipePropertySet> CALCINATOR_INPUT = register("calcinator_input");



    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, SpellMaker.MOD_ID);

    public static final Supplier<RecipeBookCategory> CALCINATOR_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("calcinating", RecipeBookCategory::new);
}
