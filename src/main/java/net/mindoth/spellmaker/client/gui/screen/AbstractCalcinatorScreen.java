package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

public class AbstractCalcinatorScreen<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> {

    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_screen.png");
    private final ResourceLocation litProgressSprite = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_lit_progress.png");
    private final ResourceLocation burnProgressSprite = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/calcinator_burn_progress.png");

    public AbstractCalcinatorScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        boolean i1;
        int j1;
        if ( this.menu.isLit() ) {
            i1 = true;
            j1 = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            guiGraphics.blit(this.litProgressSprite, i + 56, j + 36 + 14 - j1, 0, 14 - j1, 14, j1, 14, 14);
        }
        i1 = true;
        j1 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        guiGraphics.blit(this.burnProgressSprite, i + 79, j + 34, j1, 0, 0, j1, 16, 24, 16);
    }
}
