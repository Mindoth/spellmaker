package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.CalcinatorMenu;
import net.mindoth.spellmaker.registries.ModItems;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

import java.util.List;

public class CalcinatorScreen extends AbstractCalcinatorScreen<CalcinatorMenu> {

    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_lit_progress.png");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_burn_progress.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_screen.png");
    private static final Component FILTER_NAME = Component.translatable("tooltip.recipebook.toggleRecipes.calcinatable");
    private static final List<RecipeBookComponent.TabInfo> TABS;

    public CalcinatorScreen(CalcinatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
    }

    static {
        TABS = List.of(new RecipeBookComponent.TabInfo(ModItems.WOOD_ASH.get(), ModRecipes.CALCINATOR_CATEGORY.get()));
    }
}
