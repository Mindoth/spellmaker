package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlembicRecipe extends MultiIORecipe<RecipeInput> {

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

    public static final RecipeSerializer<AlembicRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public static class Type implements RecipeType<AlembicRecipe> {
        public static final AlembicRecipe.Type DISTILLING = new AlembicRecipe.Type();
        public static final String ID = "distilling";
    }

    private final Ingredient middleInput;
    private final Optional<Ingredient> topOptional;
    private final Optional<Ingredient> bottomOptional;
    private final ItemStackTemplate result;

    private AlembicRecipe(Ingredients ingredients, ItemStackTemplate result) {
        this(ingredients.middle(), result, ingredients.top(), ingredients.bottom());
    }

    public AlembicRecipe(Ingredient middleInput, ItemStackTemplate result, Optional<Ingredient> topOptional, Optional<Ingredient> bottomOptional) {
        this.middleInput = Objects.requireNonNull(middleInput, "middleInput");
        this.result = Objects.requireNonNull(result, "result");
        this.topOptional = Objects.requireNonNull(topOptional, "topOptional");
        this.bottomOptional = Objects.requireNonNull(bottomOptional, "bottomOptional");
    }

    public ItemStackTemplate result() {
        return this.result;
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
    public List<RecipeDisplay> display() {
        return List.of(new AlembicRecipeDisplay(
                this.middleInput.display(),
                this.topOptional.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE),
                this.bottomOptional.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(ModBlocks.ALEMBIC.asItem())));
    }

    public Ingredient getMiddleInput() {
        return this.middleInput;
    }

    public Optional<Ingredient> getTopOptional() {
        return this.topOptional;
    }

    public Optional<Ingredient> getBottomOptional() {
        return this.bottomOptional;
    }

    private Ingredients getSerializedIngredients() {
        return new Ingredients(this.topOptional, this.middleInput, this.bottomOptional);
    }

    private record Ingredients(Optional<Ingredient> top, Ingredient middle, Optional<Ingredient> bottom) {

        public static final Codec<Ingredients> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        Ingredient.CODEC.optionalFieldOf("top").forGetter(Ingredients::top),
                        Ingredient.CODEC.fieldOf("middle").forGetter(Ingredients::middle),
                        Ingredient.CODEC.optionalFieldOf("bottom").forGetter(Ingredients::bottom))
                .apply(builder, Ingredients::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Ingredients> STREAM_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                Ingredients::top,
                Ingredient.CONTENTS_STREAM_CODEC,
                Ingredients::middle,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                Ingredients::bottom,
                Ingredients::new
        );
    }
}
