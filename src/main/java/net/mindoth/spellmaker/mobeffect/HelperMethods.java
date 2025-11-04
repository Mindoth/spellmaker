package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Collection;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public abstract class HelperMethods {

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        var entity = event.getEntity();
        if ( entity.level().isClientSide ) return;
        entity.getActiveEffects().forEach(effect -> {
            if ( effect.getEffect().value() instanceof MobEffectEndCallback callback ) callback.onEffectRemoved(entity, effect.getAmplifier());
        });
    }

    public static Mob convertToWithEffects(Mob target, EntityType entityType, boolean transferInventory) {
        if ( target.isRemoved() ) return null;
        else {
            Collection<MobEffectInstance> effects = target.getActiveEffects();
            Mob t = (Mob)entityType.create(target.level());
            if ( t == null ) return null;
            else {
                t.copyPosition(target);
                t.setBaby(target.isBaby());
                t.setNoAi(target.isNoAi());
                if ( target.hasCustomName() ) {
                    t.setCustomName(target.getCustomName());
                    t.setCustomNameVisible(target.isCustomNameVisible());
                }
                if ( target.isPersistenceRequired() ) t.setPersistenceRequired();
                t.setInvulnerable(target.isInvulnerable());
                /*if ( transferInventory ) {
                    t.setCanPickUpLoot(target.canPickUpLoot());
                    EquipmentSlot[] var4 = EquipmentSlot.values();
                    int var5 = var4.length;
                    for ( int var6 = 0; var6 < var5; ++var6 ) {
                        EquipmentSlot equipmentslot = var4[var6];
                        ItemStack itemstack = target.getItemBySlot(equipmentslot);
                        if ( !itemstack.isEmpty() ) {
                            t.setItemSlot(equipmentslot, itemstack.copyAndClear());
                            t.setDropChance(equipmentslot, target.getEquipmentDropChance(equipmentslot));
                        }
                    }
                }*/
                for ( MobEffectInstance effect : effects ) t.addEffect(effect);
                target.level().addFreshEntity(t);
                if ( target.isPassenger() ) {
                    Entity entity = target.getVehicle();
                    target.stopRiding();
                    t.startRiding(entity, true);
                }
                target.discard();
                return t;
            }
        }
    }
}
