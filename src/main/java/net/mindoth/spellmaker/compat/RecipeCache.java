package net.mindoth.spellmaker.compat;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.recipe.DistillingRecipe;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class RecipeCache {

    private static RecipeMap RECIPE_MAP;
    private static final Set<RecipeType<?>> KNOWN_TYPES = new HashSet<>();

    public RecipeCache() {
    }

    public List<RecipeHolder<CalcinatingRecipe>> getAllCalcinatingRecipes() {
        return RECIPE_MAP.byType(ModRecipes.CALCINATING_RECIPE_TYPE.get()).stream().toList();
    }

    public List<RecipeHolder<DistillingRecipe>> getAllDistillingRecipes() {
        return RECIPE_MAP.byType(ModRecipes.DISTILLING_RECIPE_TYPE.get()).stream().toList();
    }

    @SubscribeEvent
    public static void getRecipes(RecipesReceivedEvent event) {
        RECIPE_MAP = event.getRecipeMap();
        KNOWN_TYPES.clear();
        KNOWN_TYPES.addAll(event.getRecipeTypes());
    }
}
