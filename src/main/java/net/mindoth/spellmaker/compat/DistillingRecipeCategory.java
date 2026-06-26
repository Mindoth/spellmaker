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
import net.mindoth.spellmaker.recipe.DistillingRecipe;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings("removal")
public class DistillingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<DistillingRecipe>> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/jei/jei_alembic_screen.png");
    public static final MutableComponent TITLE = Component.translatable("jei.category.distilling");

    public static final IRecipeType<RecipeHolder<DistillingRecipe>> DISTILLING_TYPE = createRecipeType(SpellMaker.MOD_ID, "distilling");

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> mezz.jei.api.recipe.RecipeType<RecipeHolder<T>> createRecipeType(String namespace, String path) {
        Class<? extends RecipeHolder<T>> holderClass = (Class<? extends RecipeHolder<T>>) (Object) RecipeHolder.class;
        return RecipeType.create(namespace, path, holderClass);
    }

    private final IDrawableStatic background;

    public DistillingRecipeCategory(IGuiHelper guiHelper) {
        super(DISTILLING_TYPE, TITLE, guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALEMBIC)), 176, 83);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 83);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DistillingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56 - 9, 17).addItemStack(new ItemStack(recipe.value().getInput0().getValues().get(0).value(), recipe.value().getInput0Amount()));
        builder.addSlot(RecipeIngredientRole.INPUT, 56 + 9, 17).addItemStack(new ItemStack(recipe.value().getInput1().getValues().get(0).value(), recipe.value().getInput1Amount()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116 - 10, 35 - 9).addItemStack(recipe.value().getResult0().create());
        if ( recipe.value().getResult1().isPresent() ) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116 + 8, 35 - 9).addItemStack(recipe.value().getResult1().get().create());
        }
        if ( recipe.value().getResult2().isPresent() ) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116 - 10, 35 + 9).addItemStack(recipe.value().getResult2().get().create());
        }
        if ( recipe.value().getResult3().isPresent() ) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 116 + 8, 35 + 9).addItemStack(recipe.value().getResult3().get().create());
        }
    }

    @Override
    public void draw(RecipeHolder<DistillingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics, 0, 0);
    }
}
