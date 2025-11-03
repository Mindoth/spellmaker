package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.recipe.SpellBookAddRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SpellMaker.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<SpellBookAddRecipe>> SPELL_BOOK_ADD_RECIPE =
            SERIALIZERS.register("spell_book_add_crafting", () -> new SimpleCraftingRecipeSerializer<>(SpellBookAddRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CalcinatingRecipe>> CALCINATING_RECIPE =
            SERIALIZERS.register("calcinating", () -> CalcinatingRecipe.Serializer.INSTANCE);
}
