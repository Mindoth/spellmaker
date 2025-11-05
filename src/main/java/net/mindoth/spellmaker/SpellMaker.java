package net.mindoth.spellmaker;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.item.ModCreativeTab;
import net.mindoth.spellmaker.item.armor.ModArmorMaterials;
import net.mindoth.spellmaker.registries.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(SpellMaker.MOD_ID)
public class SpellMaker {
    public static final String MOD_ID = "spellmaker";

    public SpellMaker(IEventBus modBus, ModContainer modContainer, Dist dist) {
        if ( dist.isClient() ) SpellMakerClient.registerHandlers(modBus, modContainer);
        addRegistries(modBus);
        modBus.addListener(this::registerRegistries);
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
        ModRecipes.SERIALIZERS.register(modBus);
        ModCapabilities.ATTACHMENT_TYPES.register(modBus);
        ModSpellForms.SPELL_FORMS.register(modBus);
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ModSpellForms.SPELL_FORM_REGISTRY);
    }
}
