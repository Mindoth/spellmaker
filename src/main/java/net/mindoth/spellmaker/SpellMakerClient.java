package net.mindoth.spellmaker;

import com.mojang.blaze3d.platform.InputConstants;
import net.mindoth.spellmaker.client.gui.screen.CalcinatorScreen;
import net.mindoth.spellmaker.client.gui.screen.HudMana;
import net.mindoth.spellmaker.client.gui.screen.SpellMakingScreen;
import net.mindoth.spellmaker.config.ModClientConfig;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiRenderer;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleRenderer;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.network.AskToOpenSpellBookPacket;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class SpellMakerClient {
    public static void registerHandlers(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(SpellMakerClient::registerEntityRenderers);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_SINGLE.get(), ProjectileSpellSingleRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_MULTI.get(), ProjectileSpellMultiRenderer::new);
    }

    @EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft instance = Minecraft.getInstance();
            if ( instance.level == null ) return;
            onInput(instance, event.getKey(), event.getAction());
        }

        private static void onInput(Minecraft instance, int key, int keyAction) {
            Player player = instance.player;
            if ( player == null ) return;
            if ( instance.screen == null ) {
                if ( key == OPEN_SPELL_BOOK.getKey().getValue() ) {
                    if ( keyAction == 1 && !SpellBookItem.getSpellBookSlot(player).isEmpty() ) ClientPacketDistributor.sendToServer(new AskToOpenSpellBookPacket());
                }
            }
        }
    }

    public static final KeyMapping.Category KEY_CATEGORY_SPELLMAKER = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "spellmaker"));

    public static final KeyMapping OPEN_SPELL_BOOK = new KeyMapping("key.spellmaker.open_spell_book", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, KEY_CATEGORY_SPELLMAKER);

    @EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SPELL_BOOK);
        }

        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.AIR_LEVEL, ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "mana_hud"), new HudMana());
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenus.CALCINATOR_MENU.get(), CalcinatorScreen::new);
            event.register(ModMenus.SPELL_MAKING_MENU.get(), SpellMakingScreen::new);
        }
    }
}
