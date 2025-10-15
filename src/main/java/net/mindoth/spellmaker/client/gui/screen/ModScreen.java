package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ModScreen extends Screen {

    protected ModScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void drawTexture(ResourceLocation resourceLocation, int x, int y, int u, int v, int w, int h, int fileWidth, int fileHeight, GuiGraphics graphics) {
        graphics.blit(resourceLocation, x, y, u, v, w, h, fileWidth, fileHeight);
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
