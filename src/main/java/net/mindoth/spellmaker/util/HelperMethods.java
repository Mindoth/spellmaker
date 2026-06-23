package net.mindoth.spellmaker.util;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.sigil.PolymorphSigilItem;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.mobeffect.SyncedMobEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public abstract class HelperMethods {

    @SubscribeEvent
    public static void preventAttackWhilePolymorphed(AttackEntityEvent event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        PolymorphSigilItem sigil = PolymorphEffect.getFormSigil(player);
        if ( sigil == null ) return;
        if ( sigil.canAttack(player) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void adjustAttackWhilePolymorphed(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();
        if ( !(source instanceof Player player) ) return;
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        PolymorphSigilItem sigil = PolymorphEffect.getFormSigil(player);
        if ( sigil == null ) return;
        if ( !sigil.canAttack(player) ) return;
        event.setAmount(event.getOriginalAmount() + sigil.getStrengthModifier());
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
        Item item = event.getItemStack().getItem();
        if ( item instanceof StaffItem
                || item instanceof SpellBookItem
                || item.components().has(DataComponents.FOOD) ) return;
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
