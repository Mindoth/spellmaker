package net.mindoth.spellmaker;

import com.mojang.blaze3d.platform.InputConstants;
import net.mindoth.spellmaker.client.gui.screen.CalcinatorScreen;
import net.mindoth.spellmaker.client.gui.screen.HudMana;
import net.mindoth.spellmaker.client.gui.screen.SpellMakingScreen;
import net.mindoth.spellmaker.client.model.SimpleRobeModel;
import net.mindoth.spellmaker.config.ModClientConfig;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiRenderer;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleRenderer;
import net.mindoth.spellmaker.item.ModDyeableItem;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.network.AskToOpenSpellBookPacket;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.registries.ModItems;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class SpellMakerClient {
    public static void registerHandlers(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(SpellMakerClient::registerEntityRenderers);
        modBus.addListener(SpellMakerClient::registerItemColors);
        modBus.addListener(SpellMakerClient::registerLayerDefinitions);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
    }

    //TODO: Armor renderer to reflect dyed color
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for ( ResourceLocation id : ModItems.ITEMS.getRegistry().get().keySet() ) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if ( item instanceof ModDyeableItem modItem ) {
                event.register((stack, layer) -> layer > 0 ? -1 : DyedItemColor.getOrDefault(stack, modItem.getDefaultColor()), item);
            }
        }
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_SINGLE.get(), ProjectileSpellSingleRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_MULTI.get(), ProjectileSpellMultiRenderer::new);
    }

    public static final ModelLayerLocation SIMPLE_ROBE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "main"), "simple_robe");

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SIMPLE_ROBE, SimpleRobeModel::createBodyLayer);
    }

    @EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
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
                if ( !SpellBookItem.getTaggedSpellBookSlot(player).isEmpty() ) PacketDistributor.sendToServer(new AskToOpenSpellBookPacket(true));
                else if ( !SpellBookItem.getSpellBookSlot(player).isEmpty() ) PacketDistributor.sendToServer(new AskToOpenSpellBookPacket(false));
            }
        }
    }

    public static final String KEY_OPEN_SPELL_BOOK = "key.spellmaker.open_spell_book";
    public static final String KEY_CATEGORY_SPELLMAKER = "key.category.spellmaker";
    public static final KeyMapping OPEN_SPELL_BOOK = new KeyMapping(KEY_OPEN_SPELL_BOOK, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, KEY_CATEGORY_SPELLMAKER);

    @EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SPELL_BOOK);
        }

        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.AIR_LEVEL, ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "mana_hud"), HudMana.OVERLAY);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenus.CALCINATOR_MENU.get(), CalcinatorScreen::new);
            event.register(ModMenus.SPELL_MAKING_MENU.get(), SpellMakingScreen::new);
        }
    }
}
