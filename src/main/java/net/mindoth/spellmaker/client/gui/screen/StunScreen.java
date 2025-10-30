package net.mindoth.spellmaker.client.gui.screen;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mobeffect.AbstractStunEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public class StunScreen extends Screen {
    protected StunScreen(Component pTitle) {
        super(pTitle);
    }

    public int stunScreenLeftPos = 2;
    public int stunScreenImageWidth = 0;
    public int stunScreenTopPos = 2;

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderEffects(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderEffects(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int i = this.stunScreenLeftPos + this.stunScreenImageWidth;
        int j = this.width - i;
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if ( !collection.isEmpty() && j >= 32 ) {
            boolean flag = j >= 120;
            var event = net.minecraftforge.client.ForgeHooksClient.onScreenPotionSize(this, j, !flag, i);
            if (event.isCanceled()) return;
            flag = !event.isCompact();
            i = event.getHorizontalOffset();
            int k = 33;
            if ( collection.size() > 5 ) k = 132 / (collection.size() - 1);
            Iterable<MobEffectInstance> iterable = collection.stream().filter(net.minecraftforge.client.ForgeHooksClient::shouldRenderEffect).sorted().collect(java.util.stream.Collectors.toList());
            this.renderBackgrounds(pGuiGraphics, i, k, iterable, flag);
            this.renderIcons(pGuiGraphics, i, k, iterable, flag);
            if ( flag ) this.renderLabels(pGuiGraphics, i, k, iterable);
            else if ( pMouseX >= i && pMouseX <= i + 33 ) {
                int l = this.stunScreenTopPos;
                MobEffectInstance mobeffectinstance = null;
                for( MobEffectInstance mobeffectinstance1 : iterable ) {
                    if ( pMouseY >= l && pMouseY <= l + k ) mobeffectinstance = mobeffectinstance1;
                    l += k;
                }
                if ( mobeffectinstance != null ) {
                    List<Component> list = List.of(this.getEffectName(mobeffectinstance), MobEffectUtil.formatDuration(mobeffectinstance, 1.0F));
                    pGuiGraphics.renderTooltip(this.font, list, Optional.empty(), pMouseX, pMouseY);
                }
            }

        }
    }

    private void renderBackgrounds(GuiGraphics pGuiGraphics, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects, boolean pIsSmall) {
        int i = this.stunScreenTopPos;
        for ( MobEffectInstance mobeffectinstance : pEffects ) {
            if ( mobeffectinstance.getEffect() instanceof AbstractStunEffect ) {
                if ( pIsSmall ) pGuiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, pRenderX, i, 0, 166, 120, 32);
                else pGuiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, pRenderX, i, 0, 198, 32, 32);
                i += pYOffset;
            }
        }

    }

    private void renderIcons(GuiGraphics pGuiGraphics, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects, boolean pIsSmall) {
        MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
        int i = this.stunScreenTopPos;
        for ( MobEffectInstance mobeffectinstance : pEffects ) {
            if ( mobeffectinstance.getEffect() instanceof AbstractStunEffect ) {
                MobEffect mobeffect = mobeffectinstance.getEffect();
                TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
                pGuiGraphics.blit(pRenderX + (pIsSmall ? 6 : 7), i + 7, 0, 18, 18, textureatlassprite);
                i += pYOffset;
            }
        }

    }

    private void renderLabels(GuiGraphics pGuiGraphics, int pRenderX, int pYOffset, Iterable<MobEffectInstance> pEffects) {
        int i = this.stunScreenTopPos;
        for ( MobEffectInstance mobeffectinstance : pEffects ) {
            if ( mobeffectinstance.getEffect() instanceof AbstractStunEffect ) {
                Component component = this.getEffectName(mobeffectinstance);
                pGuiGraphics.drawString(this.font, component, pRenderX + 10 + 18, i + 6, 16777215);
                Component component1 = MobEffectUtil.formatDuration(mobeffectinstance, 1.0F);
                pGuiGraphics.drawString(this.font, component1, pRenderX + 10 + 18, i + 6 + 10, 8355711);
                i += pYOffset;
            }
        }
    }

    private Component getEffectName(MobEffectInstance pEffect) {
        MutableComponent mutablecomponent = pEffect.getEffect().getDisplayName().copy();
        if ( pEffect.getAmplifier() >= 1 && pEffect.getAmplifier() <= 9 ) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + (pEffect.getAmplifier() + 1)));
        }

        return mutablecomponent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            if ( key == 256 ) pauseGame(false);
            else if ( key == 257 ) instance.setScreen(new StunChatScreen(""));
        }
        return true;
    }

    public static void pauseGame(boolean pPauseOnly) {
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
            if ( AbstractStunEffect.isStunned(player) ) {
                if ( !(instance.screen instanceof StunScreen || instance.screen instanceof StunChatScreen) && (instance.screen == null || !instance.screen.isPauseScreen()) ) {
                    instance.setScreen(new StunScreen(Component.literal("")));
                }
            }
            else if ( instance.screen instanceof StunScreen || instance.screen instanceof StunChatScreen ) player.closeContainer();
        }
    }
}
