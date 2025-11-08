package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public abstract class HelperMethods {

    @SubscribeEvent
    public static void preventAttackWhilePolymorphed(AttackEntityEvent event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        if ( event instanceof PlayerInteractEvent.RightClickItem itemEvent
                && (itemEvent.getItemStack().getItem() instanceof StaffItem || itemEvent.getItemStack().getItem() instanceof SpellBookItem) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        var entity = event.getEntity();
        if ( entity.level().isClientSide() ) return;
        entity.getActiveEffects().forEach(effect -> {
            if ( effect.getEffect().value() instanceof SyncedMobEffect callback ) callback.onEffectRemoved(entity, effect.getAmplifier(), true);
        });
    }
}
