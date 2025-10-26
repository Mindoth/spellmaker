package net.mindoth.spellmaker.item.rune;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketSyncSize;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
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

        syncDimensions(living);
    }

    public static final UUID POLYMORPH_SPEED_MODIFIER_UUID = UUID.fromString("0ca369c9-8322-4247-a63d-15a464e0f889");
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, "Polymorph Speed", 0.0D, AttributeModifier.Operation.ADDITION);
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
            for ( AttributeModifier modifier : instance.getModifiers() ) {
                if ( PolymorphEffect.getRuneFromUUID(modifier.getId()) != null || Objects.equals(modifier.getId().toString(), POLYMORPH_SPEED_MODIFIER_UUID.toString()) ) {
                    list.add(modifier);
                }
            }
            if ( !list.isEmpty() ) map.put(instance, list);
        }
        for ( AttributeInstance instance : map.keySet() ) {
            if ( instance != null) {
                for ( AttributeModifier modifier : map.get(instance) ) instance.removeModifier(modifier);
            }
        }
        syncDimensions(living);
    }

    private static final AttributeModifier SYNC_POLYMORPH_SIZE = new AttributeModifier(UUID.fromString("9eb86aa6-343f-430c-8296-1a5fe6b400fa"),
            "Polymorph Size", 0.0D, AttributeModifier.Operation.ADDITION);

    private static void syncDimensions(LivingEntity living) {
        if ( !(living instanceof Player player) ) return;
        AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return;
        if ( !nameTagDistance.hasModifier(SYNC_POLYMORPH_SIZE) ) nameTagDistance.addPermanentModifier(SYNC_POLYMORPH_SIZE);
    }

    @SubscribeEvent
    public static void syncPolymorphSize(TickEvent.PlayerTickEvent event) {
        if ( event.side == LogicalSide.CLIENT || event.phase != TickEvent.Phase.START ) return;
        Player player = event.player;
        if ( player == null ) return;
        AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return;
        if ( nameTagDistance.hasModifier(SYNC_POLYMORPH_SIZE) ) {
            nameTagDistance.removeModifier(SYNC_POLYMORPH_SIZE);
            player.refreshDimensions();
            ModNetwork.sendToPlayersTrackingEntity(new PacketSyncSize(player.getId()), player, true);
        }
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void refreshPolymorphSize(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if ( !(entity instanceof Player player) || player.isRemoved() || player.getId() <= 0 || player.tickCount < 0 ) return;
        if ( !PolymorphEffect.isPolymorphed(player) || PolymorphEffect.getPolymorphType(player) == null ) return;
        EntityType type = PolymorphEffect.getPolymorphType(player);
        if ( type == null ) return;
        EntityDimensions dimensions = type.getDimensions();
        event.setNewSize(dimensions);
        Entity creation = type.create(player.level());
        if ( creation == null ) return;
        event.setNewEyeHeight(creation.getEyeHeight());
    }
}
