package net.mindoth.spellmaker.mobeffect;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.sigil.PolymorphSigilItem;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class PolymorphEffect extends MobEffect implements SyncedMobEffect {
    public PolymorphEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static final String NBT_KEY_OLD_MOB = "sm_polymorphed_entity";
    public static final String NBT_KEY_RE_POLYMORPH = "sm_re_polymorphed_entity";

    @SubscribeEvent
    public static void reAddedPolymorphEffect(MobEffectEvent.Added event) {
        if ( !(event.getEffectInstance().getEffect().value() instanceof PolymorphEffect) ) return;
        if ( event.getOldEffectInstance() == null ) return;
        if ( event.getEntity() instanceof Mob mob ) mob.getPersistentData().putBoolean(NBT_KEY_RE_POLYMORPH, true);
        else if ( event.getEntity() instanceof Player player ) removeModifiers(player);
    }

    public static void doPolymorph(LivingEntity living, AttributeModifier nameTagModifier) {
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance != null && !nameTagDistance.hasModifier(nameTagModifier.id()) ) nameTagDistance.addPermanentModifier(nameTagModifier);
        if ( living instanceof Mob target ) polymorphMob(target, nameTagModifier);
        else if ( living instanceof Player player ) polymorphPlayer(player, nameTagModifier);
    }

    public static void removeModifiers(LivingEntity living) {
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        List<AttributeModifier> nameTagModifierList = Lists.newArrayList();
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( getSigilFromUUID(modifier.id()) != null ) nameTagModifierList.add(modifier);
        for ( AttributeModifier modifier : nameTagModifierList ) getSigilFromUUID(modifier.id()).removeModifiers(living);
    }

    private static void polymorphPlayer(Player player, AttributeModifier nameTagModifier) {
        PolymorphSigilItem sigil = getSigilFromUUID(nameTagModifier.id());
        if ( sigil != null ) {
            sigil.addStatModifiers(player);
            PolymorphSigilItem.syncDimensions(player);
        }
    }

    private static void polymorphMob(Mob target, AttributeModifier nameTagModifier) {
        if ( !(target.level() instanceof ServerLevel) ) return;
        if ( target.getPersistentData().contains(NBT_KEY_OLD_MOB) ) {
            finalizeMobTransformation(target, getTypeFromUUID(nameTagModifier.id()), target.getPersistentData().getCompound(NBT_KEY_OLD_MOB).get());
        }
        else {
            try ( ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LogUtils.getLogger()) ) {
                TagValueOutput output = TagValueOutput.createWithContext(reporter, target.registryAccess());
                target.save(output);
                finalizeMobTransformation(target, getTypeFromUUID(nameTagModifier.id()), output.buildResult());
            }
        }
    }

    private static void finalizeMobTransformation(Mob target, EntityType entityType, CompoundTag tag) {
        target.convertTo(entityType, new ConversionParams(ConversionType.SINGLE, false, true, target.getTeam()), EntitySpawnReason.CONVERSION, (newMob) -> {
            newMob.getPersistentData().put(NBT_KEY_OLD_MOB, tag);
        });
    }

    @Override
    public void onEffectRemoved(LivingEntity living, int pAmplifier, boolean isWasDeath) {
        if ( isWasDeath ) return;
        if ( living instanceof Mob mob ) {
            if ( !(mob.level() instanceof ServerLevel) ) return;
            if ( mob.getPersistentData().contains(NBT_KEY_RE_POLYMORPH) ) mob.getPersistentData().remove(NBT_KEY_RE_POLYMORPH);
            else if ( mob.getPersistentData().contains(NBT_KEY_OLD_MOB) ) restoreMob(mob.getPersistentData().getCompound(NBT_KEY_OLD_MOB).get(), mob);
        }
        else if ( living instanceof Player player ) {
            removeModifiers(player);
            PolymorphSigilItem.syncDimensions(player);
        }
    }

    private static void restoreMob(CompoundTag tag, LivingEntity living) {
        if ( tag.isEmpty() || !(living instanceof Mob disguisedMob) ) return;
        try ( ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LogUtils.getLogger()) ) {
            ValueInput input = TagValueInput.create(reporter, disguisedMob.registryAccess(), tag);
            EntityType.create(input, disguisedMob.level(), EntitySpawnReason.CONVERSION).map((entity -> {
                entity.snapTo(disguisedMob.position(), disguisedMob.getYRot(), disguisedMob.getXRot());
                entity.setDeltaMovement(disguisedMob.getDeltaMovement());
                if ( entity instanceof LivingEntity newLiving ) {
                    if ( newLiving.hasEffect(ModEffects.POLYMORPH) ) newLiving.removeEffect(ModEffects.POLYMORPH);
                }
                entity.level().addFreshEntity(entity);
                disguisedMob.discard();
                return entity;
            }));
        }
    }

    public static EntityType getTypeFromUUID(ResourceLocation id) {
        for ( ResourceLocation key : BuiltInRegistries.ITEM.keySet() ) {
            Item item = BuiltInRegistries.ITEM.getValue(key);
            if ( item instanceof PolymorphSigilItem rune && Objects.equals(rune.getUUID().toString(), id.toString()) ) return rune.getEntityType();
        }
        return null;
    }

    public static PolymorphSigilItem getSigilFromUUID(ResourceLocation id) {
        for ( ResourceLocation key : BuiltInRegistries.ITEM.keySet() ) {
            Item item = BuiltInRegistries.ITEM.getValue(key);
            if ( item instanceof PolymorphSigilItem rune && Objects.equals(rune.getUUID().toString(), id.toString()) ) return rune;
        }
        return null;
    }

    public static boolean isPolymorphed(LivingEntity living) {
        if ( living.getAttributes() == null ) return false;
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return false;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( getSigilFromUUID(modifier.id()) != null ) return true;
        return false;
    }

    public static PolymorphSigilItem getFormSigil(LivingEntity living) {
        if ( living.getAttributes() == null ) return null;
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return null;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( PolymorphEffect.getTypeFromUUID(modifier.id()) != null ) {
            return PolymorphEffect.getSigilFromUUID(modifier.id());
        }
        return null;
    }

    public static EntityType getPolymorphType(LivingEntity living) {
        if ( living.getAttributes() == null ) return null;
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return null;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( PolymorphEffect.getTypeFromUUID(modifier.id()) != null ) {
            return PolymorphEffect.getTypeFromUUID(modifier.id());
        }
        return null;
    }
}
