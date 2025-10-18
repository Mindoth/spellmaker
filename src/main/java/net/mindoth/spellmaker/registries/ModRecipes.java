package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.recipe.SpellBookAddRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SpellMaker.MOD_ID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<SpellBookAddRecipe>> SPELL_BOOK_ADD_RECIPE =
            SERIALIZERS.register("spell_book_add_crafting", () -> new SimpleCraftingRecipeSerializer<>(SpellBookAddRecipe::new));
}
