package net.mindoth.spellmaker.recipe;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlembicRecipe extends MultiIORecipe<AlembicRecipeInput> {

    private final Ingredient ingredient0;
    private final int ingredient0Amount;
    private final Ingredient ingredient1;
    private final int ingredient1Amount;
    private final ItemStackTemplate result0;
    private final Optional<ItemStackTemplate> result1;
    private final Optional<ItemStackTemplate> result2;
    private final Optional<ItemStackTemplate> result3;

    private AlembicRecipe(Ingredients inputs, ItemStackTemplates results) {
        this(inputs.ingredient0(), inputs.ingredient0Amount(), inputs.ingredient1(), inputs.ingredient1Amount(), results.result0(), results.result1(), results.result2(), results.result3());
    }

    public AlembicRecipe(Ingredient ingredient0, int ingredient0Amount, Ingredient ingredient1, int ingredient1Amount, ItemStackTemplate result0, Optional<ItemStackTemplate> result1, Optional<ItemStackTemplate> result2, Optional<ItemStackTemplate> result3) {
        this.ingredient0 = Objects.requireNonNull(ingredient0, "ingredient0");
        this.ingredient0Amount = ingredient0Amount;
        this.ingredient1 = Objects.requireNonNull(ingredient1, "ingredient1");
        this.ingredient1Amount = ingredient1Amount;
        this.result0 = Objects.requireNonNull(result0, "result0");
        this.result1 = Objects.requireNonNull(result1, "result1");
        this.result2 = Objects.requireNonNull(result2, "result2");
        this.result3 = Objects.requireNonNull(result3, "result3");
    }

    @Override
    public boolean matches(AlembicRecipeInput input, Level level) {
        if ( level.isClientSide() ) return false;
        ItemStack input0 = input.getItem(0);
        ItemStack input1 = input.getItem(1);
        boolean leftRight = getInput0().test(input0) && getInput1().test(input1);
        boolean rightLeft = getInput0().test(input1) && getInput1().test(input0);
        boolean amountsMatch = false;
        if ( leftRight && input0.count() >= getInput0Amount() && input1.count() >= getInput1Amount() ) amountsMatch = true;
        else if ( rightLeft && input1.count() >= getInput0Amount() && input0.count() >= getInput1Amount() ) amountsMatch = true;
        boolean match = (leftRight || rightLeft) && amountsMatch;
        return match;
    }

    @Override
    public ItemStack assemble(AlembicRecipeInput alembicRecipeInput) {
        return getResult0().create();
    }

    @Override
    public PlacementInfo placementInfo() {
        List<Ingredient> inputs = Lists.newArrayList();
        inputs.add(getInput0());
        inputs.add(getInput1());
        return PlacementInfo.create(inputs);
    }

    //Ingredients Codec
    public record Ingredients(Ingredient ingredient0, int ingredient0Amount, Ingredient ingredient1, int ingredient1Amount) {

        public static final Codec<Ingredients> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Ingredient.CODEC.fieldOf("ingredient0").forGetter(Ingredients::ingredient0),
                Codec.INT.fieldOf("ingredient0Amount").forGetter(Ingredients::ingredient0Amount),
                Ingredient.CODEC.fieldOf("ingredient1").forGetter(Ingredients::ingredient1),
                Codec.INT.fieldOf("ingredient1Amount").forGetter(Ingredients::ingredient1Amount)
                ).apply(builder, Ingredients::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Ingredients> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, Ingredients::ingredient0,
                ByteBufCodecs.INT, Ingredients::ingredient0Amount,
                Ingredient.CONTENTS_STREAM_CODEC, Ingredients::ingredient1,
                ByteBufCodecs.INT, Ingredients::ingredient1Amount,
                Ingredients::new
        );
    }

    public Ingredient getInput0() {
        return this.ingredient0;
    }

    public int getInput0Amount() {
        return this.ingredient0Amount;
    }

    public Ingredient getInput1() {
        return this.ingredient1;
    }

    public int getInput1Amount() {
        return this.ingredient1Amount;
    }

    public Ingredients getSerializedIngredients() {
        return new Ingredients(getInput0(), getInput0Amount(), getInput1(), getInput1Amount());
    }

    //Results Codec
    public record ItemStackTemplates(ItemStackTemplate result0, Optional<ItemStackTemplate> result1, Optional<ItemStackTemplate> result2, Optional<ItemStackTemplate> result3) {

        public static final Codec<ItemStackTemplates> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ItemStackTemplate.CODEC.fieldOf("result0").forGetter(ItemStackTemplates::result0),
                ItemStackTemplate.CODEC.optionalFieldOf("result1").forGetter(ItemStackTemplates::result1),
                ItemStackTemplate.CODEC.optionalFieldOf("result2").forGetter(ItemStackTemplates::result2),
                ItemStackTemplate.CODEC.optionalFieldOf("result3").forGetter(ItemStackTemplates::result3)
                ).apply(builder, ItemStackTemplates::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackTemplates> STREAM_CODEC = StreamCodec.composite(
                ItemStackTemplate.STREAM_CODEC, ItemStackTemplates::result0,
                OPTIONAL_ITEM_STACK_TEMPLATE_CODEC, ItemStackTemplates::result1,
                OPTIONAL_ITEM_STACK_TEMPLATE_CODEC, ItemStackTemplates::result2,
                OPTIONAL_ITEM_STACK_TEMPLATE_CODEC, ItemStackTemplates::result3,
                ItemStackTemplates::new
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemStackTemplate>> OPTIONAL_ITEM_STACK_TEMPLATE_CODEC = oISTSC(ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC));

    //optionalItemStackTemplateStreamCodec
    public static StreamCodec<RegistryFriendlyByteBuf, Optional<ItemStackTemplate>> oISTSC(StreamCodec<RegistryFriendlyByteBuf, Optional<ItemStackTemplate>> template) {

        return new StreamCodec<>() {
            @Override
            public Optional<ItemStackTemplate> decode(RegistryFriendlyByteBuf buf) {
                if ( buf.readBoolean() ) {
                    ItemStackTemplate code = ItemStackTemplate.STREAM_CODEC.decode(buf);
                    return Optional.of(code);
                }
                else return Optional.empty();
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, Optional<ItemStackTemplate> optional) {
                if ( optional.isPresent() ) {
                    buf.writeBoolean(true);
                    ItemStackTemplate.STREAM_CODEC.encode(buf, optional.get());
                }
                else buf.writeBoolean(false);
            }
        };
    }

    public ItemStackTemplate getResult0() {
        return this.result0;
    }

    public Optional<ItemStackTemplate> getResult1() {
        return this.result1;
    }

    public Optional<ItemStackTemplate> getResult2() {
        return this.result2;
    }

    public Optional<ItemStackTemplate> getResult3() {
        return this.result3;
    }

    public ItemStackTemplates getSerializedResults() {
        return new ItemStackTemplates(getResult0(), getResult1(), getResult2(), getResult3());
    }

    //Recipe Codec
    public static final MapCodec<AlembicRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredients.CODEC.fieldOf("ingredients").forGetter(AlembicRecipe::getSerializedIngredients),
                    ItemStackTemplates.CODEC.fieldOf("results").forGetter(AlembicRecipe::getSerializedResults)
                    ).apply(builder, AlembicRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AlembicRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredients.STREAM_CODEC, AlembicRecipe::getSerializedIngredients,
            ItemStackTemplates.STREAM_CODEC, AlembicRecipe::getSerializedResults,
            AlembicRecipe::new
    );

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new AlembicRecipeDisplay(
                getInput0().display(),
                getInput1().display(),
                new SlotDisplay.ItemStackSlotDisplay(getResult0()),
                new SlotDisplay.ItemStackSlotDisplay(getResult1()),
                new SlotDisplay.ItemStackSlotDisplay(getResult2()),
                new SlotDisplay.ItemStackSlotDisplay(getResult3()),
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
