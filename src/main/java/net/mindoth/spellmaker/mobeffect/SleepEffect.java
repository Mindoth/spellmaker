package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
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
    public static void wakeUpWhenAttacked(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if ( living.hasEffect(ModEffects.SLEEP.getHolder().get()) ) living.removeEffect(ModEffects.SLEEP.getHolder().get());
    }
}
