package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.MapCodec;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CalcinatingRecipe extends AbstractCookingRecipe {

    private final Ingredient input;
    private final ItemStackTemplate output;

    public CalcinatingRecipe(Recipe.CommonInfo commonInfo, CookingBookInfo bookInfo, Ingredient ingredient, ItemStackTemplate result, float experience, int cookingTime) {
        super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
        this.input = ingredient;
        this.output = result;
    }

    public static final MapCodec<CalcinatingRecipe> MAP_CODEC = cookingMapCodec(CalcinatingRecipe::new, 200);
    public static final StreamCodec<RegistryFriendlyByteBuf, CalcinatingRecipe> STREAM_CODEC = cookingStreamCodec(CalcinatingRecipe::new);
    public static final RecipeSerializer<CalcinatingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<CalcinatingRecipe> getSerializer() {
        return SERIALIZER;
    }

    public static class Type implements RecipeType<CalcinatingRecipe> {
        public static final Type CALCINATING = new Type();
        public static final String ID = "calcinating";
    }

    @Override
    public RecipeType<CalcinatingRecipe> getType() {
        return Type.CALCINATING;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        if ( level.isClientSide() ) return false;
        return this.input.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pInput) {
        return this.output.create().copy();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.CALCINATOR_CATEGORY.get();
    }

    @Override
    protected Item furnaceIcon() {
        return ModBlocks.CALCINATOR.asItem();
    }

    @Override
    public String group() {
        return this.bookInfo.group();
    }
}
