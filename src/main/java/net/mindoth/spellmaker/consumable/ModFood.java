package net.mindoth.spellmaker.consumable;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class ModFood {
    public static final FoodProperties GOLDEN_BREAD = new FoodProperties.Builder().nutrition(6).saturationModifier(1.2F).alwaysEdible().build();

    @SubscribeEvent
    public static void onFoodConsume(LivingEntityUseItemEvent.Finish event) {
        if ( !(event.getEntity() instanceof Player player) || event.getItem().getItem() != ModItems.GOLDEN_BREAD.get() ) return;
        ModCapabilities.changeMana(player, 25.0D, player.getAttributeValue(ModAttributes.MANA_MAX));
    }
}
