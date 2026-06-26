package net.mindoth.spellmaker.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings("removal")
@JeiPlugin
public class JeiModPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CalcinatingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new DistillingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeCache recipes = new RecipeCache();

        registration.addRecipes(CalcinatingRecipeCategory.CALCINATING_TYPE, recipes.getAllCalcinatingRecipes());
        registration.addRecipes(DistillingRecipeCategory.DISTILLING_TYPE, recipes.getAllDistillingRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(CalcinatingRecipeCategory.CALCINATING_TYPE, new ItemStack(ModBlocks.CALCINATOR.get().asItem()));
        registration.addCraftingStation(DistillingRecipeCategory.DISTILLING_TYPE, new ItemStack(ModBlocks.ALEMBIC.get().asItem()));
    }
}
