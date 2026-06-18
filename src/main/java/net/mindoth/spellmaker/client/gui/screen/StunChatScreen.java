package net.mindoth.spellmaker.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

public class StunChatScreen extends ChatScreen {

    private boolean initHappened = false;
    private boolean chatReset = false;

    public StunChatScreen(String pInitial, boolean isDraft) {
        super(pInitial, isDraft);
    }

    @Override
    public void init() {
        super.init();
        this.initHappened = true;
    }

    //Stupid dumb feature needed to remove the "t" minecraft inserts when opening custom chat with T-key
    @Override
    public void tick() {
        if ( this.initHappened && !this.chatReset ) {
            this.input.setValue("");
            this.chatReset = true;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            if ( event.key() == 256 ) instance.setScreen(new StunScreen(Component.literal("")));
            else if ( event.key() != 335 ) return super.keyPressed(event);
        }
        return true;
    }
}
