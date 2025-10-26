package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.playermagic.ClientMagickData;
import net.mindoth.spellmaker.item.castingitem.StaffItem;
import net.mindoth.spellmaker.item.armor.ModArmorItem;
import net.mindoth.spellmaker.item.rune.FishPolymorphItem;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HudMana implements IGuiOverlay {

    public static final HudMana OVERLAY = new HudMana();
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static final ResourceLocation MANA_EMPTY_BAR = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/mana_empty_bar.png");
    private static final ResourceLocation MANA_FULL_BAR = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/mana_full_bar.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float pt, int width, int height) {
        Player player = MINECRAFT.player;
        if ( player == null ) return;
        if ( !shouldDisplayMana() ) return;
        double maxMana = player.getAttributeValue(ModAttributes.MANA_MAX.get());
        double currentMana = ClientMagickData.getCurrentMana();
        String mana = (int)currentMana + "/" + (int)maxMana;
        int posX = (MINECRAFT.getWindow().getGuiScaledWidth() / 2) + 10;
        int posY = MINECRAFT.getWindow().getGuiScaledHeight() - 49;
        if ( player.getAirSupply() != player.getMaxAirSupply() || FishPolymorphItem.isFish(player) ) posY -= 10;
        double manaPercentage = currentMana / maxMana;
        int barPercentage = (int)(manaPercentage * 79.0D);
        int barWidth = Math.max(0, Math.min(barPercentage, 79));
        graphics.blit(MANA_EMPTY_BAR, posX, posY, 0, 0, 81, 9, 81, 9);
        graphics.blit(MANA_FULL_BAR, posX + 1, posY + 1, 0, 0, barWidth, 7, 79, 7);
        graphics.drawString(gui.getFont(), mana, posX + 20, posY - 9, 8370139);
    }

    private static boolean shouldDisplayMana() {
        Player player = MINECRAFT.player;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return !(player.isSpectator() || player.isCreative())
                && (ClientMagickData.getCurrentMana() < player.getAttributeValue(ModAttributes.MANA_MAX.get())
                || StaffItem.isValidCastingItem(main) || StaffItem.isValidCastingItem(off) || ModArmorItem.isWearingMagicArmor(player));
    }
}
