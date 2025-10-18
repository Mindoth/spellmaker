package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class StunScreen extends Screen {
    public StunScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if ( pKeyCode == 256 ) pauseGame(false);
        return true;
    }

    private void pauseGame(boolean pPauseOnly) {
        Minecraft instance = Minecraft.getInstance();
        boolean flag = instance.hasSingleplayerServer() && !instance.getSingleplayerServer().isPublished();
        if ( flag ) {
            instance.setScreen(new PauseScreen(!pPauseOnly));
            instance.getSoundManager().pause();
        }
        else instance.setScreen(new PauseScreen(true));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientStunTick(TickEvent.ClientTickEvent event) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            Player player = instance.player;
            if ( ModEffects.isStunned(player) ) {
                if ( !(instance.screen instanceof StunScreen) ) {
                    if ( instance.screen == null || !instance.screen.isPauseScreen() ) instance.setScreen(new StunScreen(Component.literal("")));
                }
            }
            else {
                if ( instance.screen instanceof StunScreen ) player.closeContainer();
            }
        }
    }
}
