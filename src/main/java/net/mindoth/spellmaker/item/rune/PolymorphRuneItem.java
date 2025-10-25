package net.mindoth.spellmaker.item.rune;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PolymorphRuneItem extends RuneItem {

    private final UUID uuid;
    public UUID getUUID() {
        return this.uuid;
    }
    private final EntityType entityType;
    public EntityType getEntityType() {
        return this.entityType;
    }

    public PolymorphRuneItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier, UUID uuid, EntityType entityType) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
        this.uuid = uuid;
        this.entityType = entityType;
    }

    @Override
    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) {
            if ( !entity.isAttackable() || !entity.isAlive() || !(entity instanceof LivingEntity living) ) return;
            int duration = stats.get(1);
            int polymorphTicks = duration * 20;
            if ( !living.hasEffect(ModEffects.POLYMORPH.get()) && living.addEffect(new MobEffectInstance(ModEffects.POLYMORPH.get(), polymorphTicks, 0, false, false)) ) {
                PolymorphEffect.doPolymorph(living, new AttributeModifier(getUUID(), "Polymorph", 0.0D, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    public void addStatModifiers(LivingEntity living) {
        addSpeedModifier(living);
        addSwimSpeedModifier(living);
    }

    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(getUUID(), "Polymorph Speed", 0.0D, AttributeModifier.Operation.ADDITION);
    }

    private void addSpeedModifier(LivingEntity living) {
        AttributeInstance speedAddition = living.getAttribute(Attributes.MOVEMENT_SPEED);
        if ( speedAddition != null && !speedAddition.hasModifier(getSpeedModifier()) ) {
            speedAddition.addPermanentModifier(getSpeedModifier());
            if ( living.isSprinting() ) living.setSprinting(false);
        }
    }

    protected AttributeModifier getSwimSpeedModifier() {
        return new AttributeModifier(getUUID(), "Polymorph Swim Speed", 0.0D, AttributeModifier.Operation.ADDITION);
    }

    private void addSwimSpeedModifier(LivingEntity living) {
        AttributeInstance swimSpeedAddition = living.getAttribute(ForgeMod.SWIM_SPEED.get());
        if ( swimSpeedAddition != null && !swimSpeedAddition.hasModifier(getSwimSpeedModifier()) ) {
            swimSpeedAddition.addPermanentModifier(getSwimSpeedModifier());
        }
    }

    public void removeModifiers(LivingEntity living) {
        HashMap<AttributeInstance, List<AttributeModifier>> map = new HashMap<>();
        for ( AttributeInstance instance : living.getAttributes().getSyncableAttributes() ) {
            List<AttributeModifier> list = Lists.newArrayList();
            for ( AttributeModifier modifier : instance.getModifiers() ) if ( PolymorphEffect.getRuneFromUUID(modifier.getId()) != null ) list.add(modifier);
            if ( !list.isEmpty() ) map.put(instance, list);
        }
        for ( AttributeInstance instance : map.keySet() ) {
            if ( instance != null) {
                for ( AttributeModifier modifier : map.get(instance) ) instance.removeModifier(modifier);
            }
        }
    }
}
