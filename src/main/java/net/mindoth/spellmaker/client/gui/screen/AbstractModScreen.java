package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractModScreen extends Screen {

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected AbstractModScreen(Component pTitle) {
        super(pTitle);
    }

    public static void renderTexture(Button button, GuiGraphics pGuiGraphics, ResourceLocation pTexture, int pX, int pY, int pUOffset, int pVOffset, int pTextureDifference, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        int i = pVOffset;
        if ( !button.isActive() ) i = pVOffset + pTextureDifference * 2;
        else if ( button.isHoveredOrFocused() ) i = pVOffset + pTextureDifference;
        //RenderSystem.enableDepthTest();
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, pTexture, pX, pY, (float)pUOffset, (float)i, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    protected void renderItemWithDecorations(GuiGraphics graphics, ItemStack stack, int xPos, int yPos) {
        graphics.renderItem(stack, xPos, yPos);
        graphics.renderItemDecorations(this.font, stack, xPos, yPos);
    }

    public static @NotNull List<String> putTextToLines(String spellDesc, Font font, int rowLimit) {
        List<String> words = Arrays.stream(spellDesc.split(" ")).toList();
        List<String> lines = Lists.newArrayList();
        StringBuilder desc = new StringBuilder();
        for ( int i = 0; i < words.size(); i++ ) {
            boolean isLast = i == words.size() - 1;
            String word = words.get(i);
            String tempString = desc.isEmpty() ? word : " " + word;
            boolean isOverLimit = font.width(desc + tempString) >= rowLimit;
            if ( isOverLimit ) {
                lines.add(desc.toString());
                desc.setLength(0);
                desc.append(word);
            }
            else desc.append(tempString);
            if ( isLast ) lines.add(desc.toString());
        }
        return lines;
    }
}
