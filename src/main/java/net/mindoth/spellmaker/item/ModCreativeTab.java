package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            SpellMaker.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SPELL_MAKER_TAB = CREATIVE_MODE_TABS.register("spellmaker_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.PARCHMENT.get())).title(Component.translatable("itemGroup.spellmaker_tab")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
