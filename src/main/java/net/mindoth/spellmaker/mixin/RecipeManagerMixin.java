package net.mindoth.spellmaker.mixin;

import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> RECIPE_PROPERTY_SETS;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void afterStaticInit(CallbackInfo callback) {
        Map<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> copy = new LinkedHashMap<>(RECIPE_PROPERTY_SETS);
        copy.put(ModRecipes.CALCINATOR_INPUT, recipe -> recipe instanceof CalcinatingRecipe m ? Optional.of(m.input()) : Optional.empty());
        RECIPE_PROPERTY_SETS = Map.copyOf(copy);
    }
}
