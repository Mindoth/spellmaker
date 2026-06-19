package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mobeffect.AbstractStunEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class StunScreen extends Screen {

    protected StunScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            if ( event.key() == 256 ) pauseGame(false);
            else if ( event.key() == 84 ) instance.gui.setScreen(new StunChatScreen("", false));
        }
        return true;
    }

    public static void pauseGame(boolean pPauseOnly) {
        Minecraft instance = Minecraft.getInstance();
        boolean flag = instance.hasSingleplayerServer() && !instance.getSingleplayerServer().isPublished();
        if ( flag ) {
            instance.gui.setScreen(new PauseScreen(!pPauseOnly));
            //Apparently this is no longer needed
            //instance.getSoundManager().pause();
        }
        else instance.gui.setScreen(new PauseScreen(true));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @SubscribeEvent
    public static void clientStunTick(ClientTickEvent.Pre event) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            Player player = instance.player;
            if ( AbstractStunEffect.isStunned(player) ) {
                if ( !(instance.gui.screen() instanceof StunScreen || instance.gui.screen() instanceof StunChatScreen) && (instance.gui.screen() == null || !instance.gui.screen().isPauseScreen()) ) {
                    instance.gui.setScreen(new StunScreen(Component.literal("")));
                }
            }
            else if ( instance.gui.screen() instanceof StunScreen || instance.gui.screen() instanceof StunChatScreen ) player.closeContainer();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
