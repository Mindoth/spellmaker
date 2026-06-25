package net.mindoth.spellmaker.recipe;

import net.minecraft.world.item.crafting.*;

public abstract class MultiIORecipe<T extends RecipeInput> implements Recipe<T> {

    @Override
    public final String group() {
        return "";
    }

    @Override
    public final boolean isSpecial() {
        return false;
    }

    @Override
    public final boolean showNotification() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }
}
