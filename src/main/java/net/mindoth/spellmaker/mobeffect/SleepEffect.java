package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class SleepEffect extends AbstractStunEffect implements MobEffectEndCallback {
    public SleepEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void onEffectStarted(LivingEntity living, int amp) {
        if ( !living.isSleeping() ) living.startSleeping(living.blockPosition());
    }

    @Override
    public void onEffectRemoved(LivingEntity living, int amp) {
        if ( living.isSleeping() ) living.stopSleeping();
    }

    @SubscribeEvent
    public static void wakeUpWhenAttacked(LivingDamageEvent.Post event) {
        LivingEntity living = event.getEntity();
        if ( living.hasEffect(ModEffects.SLEEP) ) living.removeEffect(ModEffects.SLEEP);
    }
}
