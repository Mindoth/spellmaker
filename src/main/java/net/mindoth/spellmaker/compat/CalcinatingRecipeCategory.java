package net.mindoth.spellmaker.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings("removal")
public class CalcinatingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<CalcinatingRecipe>> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/jei/jei_calcinator_screen.png");
    public static final MutableComponent TITLE = Component.translatable("jei.category.calcinating");

    public static final IRecipeType<RecipeHolder<CalcinatingRecipe>> CALCINATING_TYPE = createRecipeType(SpellMaker.MOD_ID, "calcinating");

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> mezz.jei.api.recipe.RecipeType<RecipeHolder<T>> createRecipeType(String namespace, String path) {
        Class<? extends RecipeHolder<T>> holderClass = (Class<? extends RecipeHolder<T>>) (Object) RecipeHolder.class;
        return RecipeType.create(namespace, path, holderClass);
    }

    private final IDrawableStatic background;

    public CalcinatingRecipeCategory(IGuiHelper guiHelper) {
        super(CALCINATING_TYPE, TITLE, guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CALCINATOR)), 176, 83);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 83);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CalcinatingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 17).addIngredients(recipe.value().input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.value().result().create());
    }

    @Override
    public void draw(RecipeHolder<CalcinatingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics, 0, 0);
    }
}
