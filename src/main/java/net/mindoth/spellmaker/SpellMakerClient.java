package net.mindoth.spellmaker;

import com.mojang.blaze3d.platform.InputConstants;
import net.mindoth.spellmaker.client.gui.screen.HudMana;
import net.mindoth.spellmaker.client.gui.screen.SpellMakingScreen;
import net.mindoth.spellmaker.client.model.SimpleRobeModel;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiRenderer;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleRenderer;
import net.mindoth.spellmaker.item.ModDyeableItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.armor.ColorableArmorItem;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketAskToOpenSpellBook;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

public class SpellMakerClient {
    public static void registerHandlers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(SpellMakerClient::registerEntityRenderers);
        modBus.addListener(SpellMakerClient::registerItemColors);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(SpellMakerClient::registerLayerDefinitions));
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for ( Item item : ForgeRegistries.ITEMS.getValues() ) {
            if ( item instanceof ColorableArmorItem || item instanceof SpellBookItem) {
                event.getItemColors().register((color, armor) -> armor > 0 ? -1 : ((ModDyeableItem)color.getItem()).getColor(color), item);
            }
        }
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_SINGLE.get(), ProjectileSpellSingleRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_MULTI.get(), ProjectileSpellMultiRenderer::new);
    }

    public static final ModelLayerLocation SIMPLE_ROBE = new ModelLayerLocation(new ResourceLocation(SpellMaker.MOD_ID, "main"), "simple_robe");

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SIMPLE_ROBE, SimpleRobeModel::createBodyLayer);
    }

    public static final String KEY_OPEN_SPELL_BOOK = "key.spellmaker.open_spell_book";
    public static final String KEY_CATEGORY_SPELLMAKER = "key.category.spellmaker";
    public static final KeyMapping OPEN_SPELL_BOOK = new KeyMapping(KEY_OPEN_SPELL_BOOK, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, KEY_CATEGORY_SPELLMAKER);

    @Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if ( mc.level == null ) return;
            onInput(mc, event.getKey(), event.getAction());
        }

        private static void onInput(Minecraft mc, int key, int keyAction) {
            Player player = mc.player;
            if ( mc.screen == null && keyAction == 0 && key == OPEN_SPELL_BOOK.getKey().getValue() ) {
                if ( !SpellBookItem.getSpellBookSlot(player).isEmpty() ) ModNetwork.sendToServer(new PacketAskToOpenSpellBook());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SPELL_BOOK);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "mana_hud", HudMana.OVERLAY);
        }

        @SubscribeEvent
        public static void registerScreens(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenus.SPELL_MAKING_MENU.get(), SpellMakingScreen::new);
        }
    }
}
