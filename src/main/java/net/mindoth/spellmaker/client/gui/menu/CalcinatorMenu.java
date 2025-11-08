package net.mindoth.spellmaker.client.gui.menu;

import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModRecipePropertySets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;

public class CalcinatorMenu extends AbstractFurnaceMenu {

    public CalcinatorMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        super(ModMenus.CALCINATOR_MENU.get(), CalcinatingRecipe.Type.CALCINATING, ModRecipePropertySets.CALCINATOR_INPUT, RecipeBookType.FURNACE, pContainerId, pPlayerInventory);
    }

    public CalcinatorMenu(int pContainerId, Inventory pPlayerInventory, Container pFurnaceContainer, ContainerData pFurnaceData) {
        super(ModMenus.CALCINATOR_MENU.get(), CalcinatingRecipe.Type.CALCINATING, ModRecipePropertySets.CALCINATOR_INPUT, RecipeBookType.FURNACE, pContainerId, pPlayerInventory, pFurnaceContainer, pFurnaceData);
    }

    @Override
    protected boolean canSmelt(ItemStack stack) {
        return this.level.recipeAccess().propertySet(ModRecipePropertySets.CALCINATOR_INPUT).test(stack);
    }
}
