package net.mindoth.spellmaker;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.item.ModCreativeTab;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.registries.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(SpellMaker.MOD_ID)
public class SpellMaker {
    public static final String MOD_ID = "spellmaker";

    public SpellMaker(IEventBus modBus, ModContainer modContainer) {
        if ( FMLEnvironment.dist == Dist.CLIENT ) SpellMakerClient.registerHandlers(modBus, modContainer);
        addRegistries(modBus);
        modBus.addListener(this::addCreative);
    }


    private void addRegistries(final IEventBus modBus) {
        ModCreativeTab.CREATIVE_MODE_TABS.register(modBus);
        ModData.DATA_COMPONENT_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModAttributes.ATTRIBUTES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModSpellForms.SPELL_FORMS.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModCapabilities.ATTACHMENT_TYPES.register(modBus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if ( event.getTab() == ModCreativeTab.SPELL_MAKER_TAB.get() ) {
            for ( DeferredHolder<Block, ? extends Block> block : ModBlocks.BLOCKS.getEntries() ) event.accept(block.get());
            for ( DeferredHolder<Item, ? extends Item> item : ModItems.ITEMS.getEntries() ) event.accept(item.get());
        }
    }
}
