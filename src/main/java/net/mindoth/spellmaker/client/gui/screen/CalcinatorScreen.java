package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.CalcinatorMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CalcinatorScreen extends AbstractCalcinatorScreen<CalcinatorMenu> {
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_lit_progress.png");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_burn_progress.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_screen.png");

    public CalcinatorScreen(CalcinatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, new SmeltingRecipeBookComponent(), playerInventory, title, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
    }
}
