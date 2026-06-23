package net.mindoth.spellmaker.item.sigil;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.registries.ModItems;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public abstract class PolymorphSigilItem extends AbstractSigilItem {

    private final Identifier id;
    public Identifier getUUID() {
        return this.id;
    }
    private final EntityType entityType;
    public EntityType getEntityType() {
        return this.entityType;
    }

    public PolymorphSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, Identifier id, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
        this.id = id;
        this.entityType = entityType;
    }

    @Override
    public boolean canAffectBlock(Block block) {
        return false;
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !(target instanceof LivingEntity living) || !target.isAttackable() || !target.isAlive() ) return;
        int duration = stats.get(1);
        int polymorphTicks = duration * 20;
        MobEffectInstance instance = new MobEffectInstance(ModEffects.POLYMORPH, polymorphTicks, 0, false, false);
        living.addEffect(instance);
        if ( CommonHooks.canMobEffectBeApplied(living, instance, source) ) {
            living.forceAddEffect(instance, null);
            PolymorphEffect.doPolymorph(living, new AttributeModifier(getUUID(), 0.0D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public void addStatModifiers(LivingEntity living) {
        addSpeedModifier(living);
        addHealthModifier(living);
        addSwimSpeedModifier(living);
    }

    public static final Identifier POLYMORPH_SPEED_MODIFIER_UUID = Identifier.parse("0ca369c9-8322-4247-a63d-15a464e0f889");

    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, 0.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    private void addSpeedModifier(LivingEntity living) {
        AttributeModifier modifier = getSpeedModifier();
        if ( modifier.amount() == 0 ) return;
        AttributeInstance speedAddition = living.getAttribute(Attributes.MOVEMENT_SPEED);
        if ( speedAddition != null && !speedAddition.hasModifier(modifier.id()) ) {
            speedAddition.addPermanentModifier(modifier);
            if ( living.isSprinting() ) living.setSprinting(false);
        }
    }

    protected AttributeModifier getHealthModifier(float currentMaxHealth) {
        return new AttributeModifier(getUUID(), 0.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    private void addHealthModifier(LivingEntity living) {
        AttributeModifier modifier = getHealthModifier(living.getMaxHealth());
        if ( modifier.amount() == 0 ) return;
        AttributeInstance healthAddition = living.getAttribute(Attributes.MAX_HEALTH);
        if ( healthAddition != null && !healthAddition.hasModifier(modifier.id()) ) {
            float health = (float)(living.getMaxHealth() + modifier.amount());
            living.setHealth(Math.min(living.getHealth(), health));
            healthAddition.addPermanentModifier(modifier);
        }
    }

    //Attack Damage attribute modifiers don't get removed for some reason...
    /*
    protected AttributeModifier getStrengthModifier() {
        return new AttributeModifier(getUUID(), 0.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    private void addStrengthModifier(LivingEntity living) {
        AttributeModifier modifier = getStrengthModifier();
        if ( modifier.amount() == 0 ) return;
        AttributeInstance strengthAddition = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if ( strengthAddition != null && !strengthAddition.hasModifier(modifier.id()) ) {
            strengthAddition.addPermanentModifier(modifier);
        }
    }
    */
    public int getStrengthModifier() {
        return 0;
    }

    protected AttributeModifier getSwimSpeedModifier() {
        return new AttributeModifier(getUUID(), 0.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    private void addSwimSpeedModifier(LivingEntity living) {
        AttributeModifier modifier = getSwimSpeedModifier();
        if ( modifier.amount() == 0 ) return;
        AttributeInstance swimSpeedAddition = living.getAttribute(NeoForgeMod.SWIM_SPEED);
        if ( swimSpeedAddition != null && !swimSpeedAddition.hasModifier(modifier.id()) ) {
            swimSpeedAddition.addPermanentModifier(modifier);
        }
    }

    public void removeModifiers(LivingEntity living) {
        HashMap<AttributeInstance, List<AttributeModifier>> map = new HashMap<>();
        for ( AttributeInstance instance : living.getAttributes().getSyncableAttributes() ) {
            List<AttributeModifier> list = Lists.newArrayList();
            for ( AttributeModifier modifier : instance.getModifiers() ) {
                if ( PolymorphEffect.getSigilFromUUID(modifier.id()) != null || Objects.equals(modifier.id().toString(), POLYMORPH_SPEED_MODIFIER_UUID.toString()) ) {
                    list.add(modifier);
                }
            }
            if ( !list.isEmpty() ) map.put(instance, list);
        }
        for ( AttributeInstance instance : map.keySet() ) {
            if ( instance != null ) {
                for ( AttributeModifier modifier : map.get(instance) ) {
                    instance.removeModifier(modifier);
                }
            }
        }
    }

    public static final AttributeModifier SYNC_POLYMORPH_SIZE_CLIENT = new AttributeModifier(Identifier.parse("9eb86aa6-343f-430c-8296-1a5fe6b400fa"),
            0.0D, AttributeModifier.Operation.ADD_VALUE);

    public static void syncDimensions(LivingEntity living) {
        if ( !(living instanceof ServerPlayer player) ) return;
        player.refreshDimensions();
        AttributeInstance nameTagDistance = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        if ( !nameTagDistance.hasModifier(SYNC_POLYMORPH_SIZE_CLIENT.id()) ) nameTagDistance.addPermanentModifier(SYNC_POLYMORPH_SIZE_CLIENT);
    }

    @SubscribeEvent
    public static void refreshPolymorphSize(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if ( !(entity instanceof Player player) || player.isRemoved() || player.id == 0 || player.tickCount < 0 ) return;
        if ( !PolymorphEffect.isPolymorphed(player) || PolymorphEffect.getPolymorphType(player) == null ) return;
        EntityType type = PolymorphEffect.getPolymorphType(player);
        if ( type == null ) return;
        Entity creation = type.create(player.level(), EntitySpawnReason.EVENT);
        if ( creation == null ) return;
        EntityDimensions dimensions = type.getDimensions().withEyeHeight(creation.getEyeHeight());
        event.setNewSize(dimensions);
    }

    public boolean canSprint(LivingEntity living) {
        return false;
    }

    public boolean canAttack(LivingEntity living) {
        return false;
    }

    public static boolean isSprintPrevented(LivingEntity living) {
        PolymorphSigilItem sigil = PolymorphEffect.getFormSigil(living);
        if ( sigil != null ) return !sigil.canSprint(living);
        return false;
    }

    public List<Holder<MobEffect>> polymorphEffects(LivingEntity living) {
        return Lists.newArrayList();
    }

    public void extraSync(LivingEntity living, Player player, float partialTick) {
    }

    //Because of NeoForge, these methods have to be here instead of in their respective Sigil Classes...
    public static boolean isFish(LivingEntity living) {
        if ( !(living instanceof Player player) ) return false;
        return PolymorphEffect.isPolymorphed(player) && PolymorphEffect.getFormSigil(player) == ModItems.FISH_FORM_SIGIL.get();
    }

    @SubscribeEvent
    public static void fishPolymorphBreathe(LivingBreatheEvent event) {
        if ( !(event.getEntity() instanceof Player player) || player.isCreative() ) return;
        if ( !isFish(player) ) return;
        event.setCanBreathe(player.isInWater());
    }
}
