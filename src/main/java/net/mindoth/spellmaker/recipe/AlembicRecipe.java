package net.mindoth.spellmaker.recipe;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class AlembicRecipe extends MultiIORecipe<AlembicRecipeInput> {

    private final Ingredient ingredient0;
    private final Ingredient ingredient1;
    private final ItemStackTemplate result;

    private AlembicRecipe(Ingredients ingredients, ItemStackTemplate result) {
        this(ingredients.ingredient0(), ingredients.ingredient1(), result);
    }

    public AlembicRecipe(Ingredient ingredient0, Ingredient ingredient1, ItemStackTemplate result) {
        this.ingredient0 = Objects.requireNonNull(ingredient0, "ingredient0");
        this.ingredient1 = Objects.requireNonNull(ingredient1, "ingredient1");
        this.result = Objects.requireNonNull(result, "result");
    }

    public Ingredient getInput0() {
        return this.ingredient0;
    }

    public Ingredient getInput1() {
        return this.ingredient1;
    }

    private Ingredients getSerializedIngredients() {
        return new Ingredients(getInput0(), getInput1());
    }

    public record Ingredients(Ingredient ingredient0, Ingredient ingredient1) {

        public static final Codec<Ingredients> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Ingredient.CODEC.fieldOf("ingredient0").forGetter(Ingredients::ingredient0),
                Ingredient.CODEC.fieldOf("ingredient1").forGetter(Ingredients::ingredient1)
                ).apply(builder, Ingredients::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Ingredients> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                Ingredients::ingredient0,
                Ingredient.CONTENTS_STREAM_CODEC,
                Ingredients::ingredient1,
                Ingredients::new
        );
    }

    @Override
    public boolean matches(AlembicRecipeInput AlembicRecipeInput, Level level) {
        if ( level.isClientSide() ) return false;
        return getInput0().test(AlembicRecipeInput.getItem(0)) && getInput1().test(AlembicRecipeInput.getItem(1));
    }

    @Override
    public PlacementInfo placementInfo() {
        List<Ingredient> inputs = Lists.newArrayList();
        inputs.add(getInput0());
        inputs.add(getInput1());
        return PlacementInfo.create(inputs);
    }

    @Override
    public ItemStack assemble(AlembicRecipeInput alembicRecipeInput) {
        return result().create();
    }

    public static final MapCodec<AlembicRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredients.CODEC.fieldOf("ingredients").forGetter(AlembicRecipe::getSerializedIngredients),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(ir -> ir.result))
                    .apply(builder, AlembicRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlembicRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredients.STREAM_CODEC,
            AlembicRecipe::getSerializedIngredients,
            ItemStackTemplate.STREAM_CODEC,
            AlembicRecipe::result,
            AlembicRecipe::new
    );

    public ItemStackTemplate result() {
        return this.result;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new AlembicRecipeDisplay(
                getInput0().display(),
                getInput1().display(),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.ALEMBIC.asItem())));
    }

    public static final RecipeSerializer<AlembicRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public static class Type implements RecipeType<AlembicRecipe> {
        public static final AlembicRecipe.Type DISTILLING = new AlembicRecipe.Type();
        public static final String ID = "distilling";
    }

    @Override
    public RecipeSerializer<AlembicRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<AlembicRecipe> getType() {
        return Type.DISTILLING;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.ALEMBIC_CATEGORY.get();
    }
}
