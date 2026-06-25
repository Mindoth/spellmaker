package net.mindoth.spellmaker.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record AlembicRecipeInput(List<ItemStack> inputs) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return this.inputs.get(i);
    }

    @Override
    public int size() {
        return 2;
    }
}
