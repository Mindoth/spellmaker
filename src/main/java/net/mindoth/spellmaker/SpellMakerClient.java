package net.mindoth.spellmaker;

import net.mindoth.spellmaker.client.gui.screen.SpellMakingScreen;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiRenderer;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleRenderer;
import net.mindoth.spellmaker.item.DyeableMagickItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.registries.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class SpellMakerClient {
    public static void registerHandlers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(SpellMakerClient::registerEntityRenderers);
        modBus.addListener(SpellMakerClient::registerItemColors);
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for ( Item item : ForgeRegistries.ITEMS.getValues() ) {
            if ( item instanceof SpellBookItem ) {
                event.getItemColors().register((color, armor) -> armor > 0 ? -1 : ((DyeableMagickItem)color.getItem()).getColor(color), item);
            }
        }
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_SINGLE.get(), ProjectileSpellSingleRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE_MULTI.get(), ProjectileSpellMultiRenderer::new);
    }

    @Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerScreens(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenus.SPELL_MAKING_MENU.get(), SpellMakingScreen::new);
        }
    }
}
