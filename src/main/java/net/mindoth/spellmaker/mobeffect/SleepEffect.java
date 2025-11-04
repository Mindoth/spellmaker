package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class SleepEffect extends AbstractStunEffect {
    public SleepEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    //TODO: effect on start and end
    /*@Override
    public void addAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( !living.isSleeping() ) living.startSleeping(living.blockPosition());
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living.isSleeping() ) living.stopSleeping();
    }*/

    @SubscribeEvent
    public static void wakeUpWhenAttacked(LivingDamageEvent.Post event) {
        LivingEntity living = event.getEntity();
        if ( living.hasEffect(ModEffects.SLEEP) ) living.removeEffect(ModEffects.SLEEP);
    }
}
