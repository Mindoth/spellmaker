package net.mindoth.spellmaker;

import net.mindoth.spellmaker.item.ModCreativeTab;
import net.mindoth.spellmaker.registries.ModBlocks;
import net.mindoth.spellmaker.registries.ModItems;
import net.mindoth.spellmaker.registries.ModMenus;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;

@Mod(SpellMaker.MOD_ID)
public class SpellMaker {
    public static final String MOD_ID = "spellmaker";

    public SpellMaker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if ( FMLEnvironment.dist == Dist.CLIENT ) SpellMakerClient.registerHandlers();
        addRegistries(modEventBus);
    }


    private void addRegistries(final IEventBus modEventBus) {
        ModCreativeTab.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModSpellForms.SPELL_FORMS.register(modEventBus);
        //KEEP THESE LAST
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //ModNetwork.init();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if ( event.getTab() == ModCreativeTab.SPELL_MAKER_TAB.get() ) {
            for ( RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries() ) event.accept(block);
            for ( RegistryObject<Item> item : ModItems.ITEMS.getEntries() ) event.accept(item);
        }
    }
}
