package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.AlembicMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class AlembicScreen extends AbstractContainerScreen<AlembicMenu> {

    private static final Identifier LIT_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/alembic_lit_progress.png");
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID,"textures/gui/alembic_burn_progress.png");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID,"textures/gui/alembic_screen.png");

    public AlembicScreen(AlembicMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractRenderState(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.extractTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        int burnProgressWidth;
        if ( this.menu.isLit() ) {
            burnProgressWidth = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, i + 56 , j + 36 + 14 - burnProgressWidth, 0, 14 - burnProgressWidth,
                    14, burnProgressWidth, 14, 14);
        }

        burnProgressWidth = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, i + 79, j + 34, 0, 0,
                burnProgressWidth, 16, 24, 16);
    }
}
