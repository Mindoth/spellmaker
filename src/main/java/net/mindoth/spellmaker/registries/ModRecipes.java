package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.recipe.DistillingRecipe;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.recipe.SpellBookAddRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SpellMaker.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SpellMaker.MOD_ID);

    public static final Supplier<RecipeSerializer<SpellBookAddRecipe>> SPELL_BOOK_ADD_RECIPE =
            RECIPE_SERIALIZERS.register("spell_book_add_crafting", () -> SpellBookAddRecipe.SERIALIZER);

    public static final Supplier<RecipeType<CalcinatingRecipe>> CALCINATING_RECIPE_TYPE =
            RECIPE_TYPES.register("calcinating", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CalcinatingRecipe>> CALCINATING_SERIALIZER =
            RECIPE_SERIALIZERS.register("calcinating", () -> CalcinatingRecipe.SERIALIZER);

    public static final Supplier<RecipeType<DistillingRecipe>> DISTILLING_RECIPE_TYPE =
            RECIPE_TYPES.register("distilling", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DistillingRecipe>> DISTILLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("distilling", () -> DistillingRecipe.SERIALIZER);



    private static ResourceKey<RecipePropertySet> register(String name) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, name));
    }

    public static final ResourceKey<RecipePropertySet> CALCINATOR_INPUT = register("calcinator_input");
    public static final ResourceKey<RecipePropertySet> ALEMBIC_INPUT_0 = register("alembic_input_0");
    public static final ResourceKey<RecipePropertySet> ALEMBIC_INPUT_1 = register("alembic_input_1");



    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, SpellMaker.MOD_ID);

    public static final Supplier<RecipeBookCategory> CALCINATOR_CATEGORY = RECIPE_BOOK_CATEGORIES.register("calcinating", RecipeBookCategory::new);
    public static final Supplier<RecipeBookCategory> ALEMBIC_CATEGORY = RECIPE_BOOK_CATEGORIES.register("distilling", RecipeBookCategory::new);
}
