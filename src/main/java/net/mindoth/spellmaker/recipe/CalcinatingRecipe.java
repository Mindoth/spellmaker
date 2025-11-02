package net.mindoth.spellmaker.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CalcinatingRecipe extends AbstractCookingRecipe {

    private final Ingredient input;
    private final ItemStack output;
    private final ResourceLocation id;

    public CalcinatingRecipe(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        super(Type.CALCINATING, pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime);
        this.input = pIngredient;
        this.output = pResult;
        this.id = pId;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if ( level.isClientSide ) return false;
        return this.input.test(container.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
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
        public static final ResourceLocation ID = new ResourceLocation(SpellMaker.MOD_ID, "calcinating");

        @Override
        public CalcinatingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String group = GsonHelper.getAsString(pJson, "group", "");
            CookingBookCategory category = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", (String)null), CookingBookCategory.MISC);
            JsonElement jsonelement = (JsonElement)(GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient"));
            Ingredient ingredient = Ingredient.fromJson(jsonelement, false);
            if ( !pJson.has("result") ) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack itemstack;
            if ( pJson.get("result").isJsonObject() ) itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            else {
                String s1 = GsonHelper.getAsString(pJson, "result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                itemstack = new ItemStack(BuiltInRegistries.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                    return new IllegalStateException("Item: " + s1 + " does not exist");
                }));
            }
            float exp = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
            int time = GsonHelper.getAsInt(pJson, "cookingtime", 200);
            return new CalcinatingRecipe(pRecipeId, group, category, ingredient, itemstack, exp, time);
        }

        @Override
        public @Nullable CalcinatingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            CookingBookCategory category = pBuffer.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            float exp = pBuffer.readFloat();
            int time = pBuffer.readVarInt();
            return new CalcinatingRecipe(pRecipeId, group, category, ingredient, output, exp, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CalcinatingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pBuffer.writeEnum(pRecipe.category());
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeFloat(pRecipe.experience);
            pBuffer.writeVarInt(pRecipe.cookingTime);
        }
    }
}
