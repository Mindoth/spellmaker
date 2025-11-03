package net.mindoth.spellmaker.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class CalcinatingRecipe extends AbstractCookingRecipe {

    private final Ingredient input;
    private final ItemStack output;

    public CalcinatingRecipe(String pGroup, CookingBookCategory pCategory, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        super(Type.CALCINATING, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime);
        this.input = pIngredient;
        this.output = pResult;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        if ( level.isClientSide ) return false;
        return this.input.test(input.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.CALCINATING;
    }

    public static class Type implements RecipeType<CalcinatingRecipe> {
        public static final Type CALCINATING = new Type();
        public static final String ID = "calcinating";
    }

    public static class Serializer implements RecipeSerializer<CalcinatingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "calcinating");

        private static final MapCodec<CalcinatingRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(group -> group.group),
                CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(category -> category.category),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ingredient -> ingredient.ingredient),
                ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(result -> result.result),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(experience -> experience.experience),
                Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(cookingTime -> cookingTime.cookingTime)
                )
                .apply(instance, CalcinatingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CalcinatingRecipe> streamCodec = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        public static CalcinatingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CookingBookCategory category = buf.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            float exp = buf.readFloat();
            int time = buf.readVarInt();
            return new CalcinatingRecipe(group, category, ingredient, output, exp, time);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, CalcinatingRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookingTime);
        }

        @Override
        public MapCodec<CalcinatingRecipe> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CalcinatingRecipe> streamCodec() {
            return this.streamCodec;
        }
    }
}
