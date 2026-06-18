package net.mindoth.spellmaker.client.gui.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

import java.util.List;

public class AbstractCalcinatorScreen<T extends AbstractFurnaceMenu> extends AbstractRecipeBookScreen<T> {

    private final Identifier texture;
    private final Identifier litProgressSprite;
    private final Identifier burnProgressSprite;

    public AbstractCalcinatorScreen(T menu, Inventory playerInventory, Component title, Component recipeFilterName, Identifier texture, Identifier litProgressSprite, Identifier burnProgressSprite, List<RecipeBookComponent.TabInfo> tabInfos) {
        super(menu, new FurnaceRecipeBookComponent(menu, recipeFilterName, tabInfos), playerInventory, title);
        this.texture = texture;
        this.litProgressSprite = litProgressSprite;
        this.burnProgressSprite = burnProgressSprite;
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        boolean i1;
        int j1;
        if ( this.menu.isLit() ) {
            i1 = true;
            j1 = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.litProgressSprite, i + 56, j + 36 + 14 - j1, 0, 14 - j1, 14, j1, 14, 14);
        }
        i1 = true;
        j1 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.burnProgressSprite, i + 79, j + 34, 0, 0, j1, 16, 24, 16);
    }
}
