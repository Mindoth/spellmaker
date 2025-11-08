package net.mindoth.spellmaker.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

public class StunChatScreen extends ChatScreen {

    private static final ResourceLocation EFFECT_BACKGROUND_LARGE_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_large");
    private static final ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_small");

    public int leftPos = 0;
    public int imageWidth = 0;
    public int topPos = 0;
    private final StunChatScreen screen;
    @Nullable
    private MobEffectInstance hoveredEffect;

    private boolean initHappened = false;
    private boolean chatReset = false;

    public StunChatScreen(String pInitial, boolean isDraft) {
        super(pInitial, isDraft);
        this.minecraft = Minecraft.getInstance();
        this.screen = this;
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

    /*public boolean canSeeEffects() {
        int i = this.leftPos + this.screen.imageWidth + 2;
        int j = this.screen.width - i;
        return j >= 32;
    }

    public void renderEffects(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.hoveredEffect = null;
        int i = this.screen.leftPos + this.screen.imageWidth + 2;
        int j = this.screen.width - i;
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if ( !collection.isEmpty() && j >= 32 ) {
            boolean flag = j >= 120;
            var event = net.neoforged.neoforge.client.ClientHooks.onScreenPotionSize(screen, j, !flag, i);
            if (event.isCanceled()) return;
            flag = !event.isCompact();
            i = event.getHorizontalOffset();
            int k = 33;
            if ( collection.size() > 5 ) k = 132 / (collection.size() - 1);
            Iterable<MobEffectInstance> iterable = collection.stream().filter(net.neoforged.neoforge.client.ClientHooks::shouldRenderEffect).sorted().collect(java.util.stream.Collectors.toList());
            this.renderBackgrounds(guiGraphics, i, k, iterable, flag);
            this.renderIcons(guiGraphics, i, k, iterable, flag);
            if ( flag ) this.renderLabels(guiGraphics, i, k, iterable);
            else if (mouseX >= i && mouseX <= i + 33) {
                int l = this.screen.topPos;
                for ( MobEffectInstance mobeffectinstance : iterable ) {
                    if ( mouseY >= l && mouseY <= l + k ) this.hoveredEffect = mobeffectinstance;
                    l += k;
                }
            }
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if ( this.hoveredEffect != null ) {
            List<Component> list = List.of(
                    this.getEffectName(this.hoveredEffect),
                    MobEffectUtil.formatDuration(this.hoveredEffect, 1.0F, this.minecraft.level.tickRateManager().tickrate())
            );
            // Neo: Allow mods to adjust the tooltip shown when hovering a mob effect.
            //And so we dont care about it
            //list = net.neoforged.neoforge.client.ClientHooks.getEffectTooltip(screen, this.hoveredEffect, list);
            guiGraphics.setTooltipForNextFrame(this.screen.getFont(), list, Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderBackgrounds(GuiGraphics guiGraphics, int x, int y, Iterable<MobEffectInstance> activeEffects, boolean large) {
        int i = this.screen.topPos;
        for ( MobEffectInstance mobeffectinstance : activeEffects ) {
            if ( large ) guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_LARGE_SPRITE, x, i, 120, 32);
            else guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SMALL_SPRITE, x, i, 32, 32);
            i += y;
        }
    }

    private void renderIcons(GuiGraphics guiGraphics, int x, int y, Iterable<MobEffectInstance> activeEffects, boolean large) {
        int i = this.screen.topPos;
        for ( MobEffectInstance mobeffectinstance : activeEffects ) {
            //wonder what this is...
            //var renderer = net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
            //if (renderer.renderInventoryIcon(mobeffectinstance, screen, guiGraphics, x + (large ? 6 : 7), i, 0)) {
            //    i += y;
            //    continue;
            //}
            Holder<MobEffect> holder = mobeffectinstance.getEffect();
            ResourceLocation resourcelocation = Gui.getMobEffectSprite(holder);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, x + (large ? 6 : 7), i + 7, 18, 18);
            i += y;
        }
    }

    private void renderLabels(GuiGraphics guiGraphics, int x, int y, Iterable<MobEffectInstance> activeEffects) {
        int i = this.screen.topPos;
        for ( MobEffectInstance mobeffectinstance : activeEffects ) {
            //I wonder what this is...*var renderer = net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
            //if (renderer.renderInventoryText(mobeffectinstance, screen, guiGraphics, x, i, 0)) {
            //    i += y;
            //    continue;
            //}
            Component component = this.getEffectName(mobeffectinstance);
            guiGraphics.drawString(this.screen.getFont(), component, x + 10 + 18, i + 6, -1);
            Component component1 = MobEffectUtil.formatDuration(mobeffectinstance, 1.0F, this.minecraft.level.tickRateManager().tickrate());
            guiGraphics.drawString(this.screen.getFont(), component1, x + 10 + 18, i + 6 + 10, -8421505);
            i += y;
        }
    }

    private Component getEffectName(MobEffectInstance effect) {
        MutableComponent mutablecomponent = effect.getEffect().value().getDisplayName().copy();
        if ( effect.getAmplifier() >= 1 && effect.getAmplifier() <= 9 ) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + (effect.getAmplifier() + 1)));
        }
        return mutablecomponent;
    }*/

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
