package net.mindoth.spellmaker.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public abstract class MultiIORecipe<T extends RecipeInput> implements Recipe<T> {

    @Override
    public final boolean isSpecial() {
        return true;
    }

    @Override
    public final boolean showNotification() {
        return false;
    }

    @Override
    public final String group() {
        return "";
    }

    @Override
    public final PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public final RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public final boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public final ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }
}
