package net.mindoth.spellmaker;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.item.ModCreativeTab;
import net.mindoth.spellmaker.recipe.CalcinatingRecipe;
import net.mindoth.spellmaker.recipe.DistillingRecipe;
import net.mindoth.spellmaker.registries.ModLootModifiers;
import net.mindoth.spellmaker.registries.ModRecipes;
import net.mindoth.spellmaker.registries.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(SpellMaker.MOD_ID)
public class SpellMaker {
    public static final String MOD_ID = "spellmaker";

    public SpellMaker(IEventBus modBus, ModContainer modContainer, Dist dist) {
        if ( dist.isClient() ) SpellMakerClient.registerHandlers(modBus, modContainer);
        addRegistries(modBus);
        addListeners(modBus);
    }

    private void addRegistries(final IEventBus modBus) {
        ModCreativeTab.CREATIVE_MODE_TABS.register(modBus);
        ModData.DATA_COMPONENT_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModAttributes.ATTRIBUTES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_BOOK_CATEGORIES.register(modBus);
        ModCapabilities.ATTACHMENT_TYPES.register(modBus);
        ModSpellForms.SPELL_FORMS.register(modBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modBus);
    }

    private void addListeners(IEventBus modBus) {
        modBus.addListener(this::registerRegistries);
        NeoForge.EVENT_BUS.addListener(this::onDataPackSync);
    }

    private void registerRegistries(NewRegistryEvent event) {
        event.register(ModSpellForms.SPELL_FORM_REGISTRY);
    }

    private void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.CALCINATING_RECIPE_TYPE.get(), ModRecipes.DISTILLING_RECIPE_TYPE.get());
    }
}
