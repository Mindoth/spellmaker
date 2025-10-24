package net.mindoth.spellmaker.mobeffect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mixin.WalkAnimationStateMixin;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class PolymorphEffect extends MobEffect {
    public PolymorphEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static final String NBT_KEY_OLD_MOB = "sm_polymorphed_entity";

    public static final AttributeModifier POLYMORPH_NAME_TAG_DISTANCE = new AttributeModifier(UUID.fromString("84527dc5-d3e5-4550-98ed-c8186c5d3089"),
            "Polymorph Model", 0.0D, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier POLYMORPH_SPEED_MODIFIER = new AttributeModifier(UUID.fromString("cb37c083-b435-4c89-9949-5c7f7823f62e"),
            "Polymorph Speed", -0.05D, AttributeModifier.Operation.ADDITION);

    @Override
    public void addAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living.getPersistentData().contains(NBT_KEY_OLD_MOB) ) return;
        if ( living instanceof Mob target ) {
            if ( target.level() instanceof ServerLevel level && target.getType() != EntityType.SHEEP ) {
                CompoundTag tag = new CompoundTag();
                tag.putString("id", EntityType.getKey(target.getType()).toString());
                target.saveWithoutId(tag);
                Sheep sheep = target.convertTo(EntityType.SHEEP, false);
                if ( target.hasEffect(ModEffects.POLYMORPH.get()) ) sheep.addEffect(target.getEffect(ModEffects.POLYMORPH.get()));
                sheep.finalizeSpawn(level, level.getCurrentDifficultyAt(sheep.blockPosition()), MobSpawnType.CONVERSION, null, null);
                sheep.getPersistentData().put(NBT_KEY_OLD_MOB, tag);
            }
        }
        else if ( living instanceof Player player ) {
            AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
            if ( nameTagDistance != null && !nameTagDistance.hasModifier(POLYMORPH_NAME_TAG_DISTANCE) ) {
                nameTagDistance.addPermanentModifier(POLYMORPH_NAME_TAG_DISTANCE);
            }
            AttributeInstance speedModifier = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if ( speedModifier != null && !speedModifier.hasModifier(POLYMORPH_SPEED_MODIFIER) ) {
                speedModifier.addPermanentModifier(POLYMORPH_SPEED_MODIFIER);
                if ( player.isSprinting() ) player.setSprinting(false);
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living instanceof Mob target ) {
            if ( target.level() instanceof ServerLevel level && living.getPersistentData().contains(NBT_KEY_OLD_MOB) ) {
                transformBack(target.getPersistentData().getCompound(NBT_KEY_OLD_MOB), level, living);
            }
        }
        else if ( living instanceof Player player ) {
            AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
            if ( nameTagDistance != null && nameTagDistance.hasModifier(POLYMORPH_NAME_TAG_DISTANCE) ) {
                nameTagDistance.removeModifier(POLYMORPH_NAME_TAG_DISTANCE);
            }
            AttributeInstance speedModifier = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if ( speedModifier != null && speedModifier.hasModifier(POLYMORPH_SPEED_MODIFIER) ) {
                speedModifier.removeModifier(POLYMORPH_SPEED_MODIFIER);
            }
        }
    }

    private boolean transformBack(CompoundTag tag, ServerLevel level, LivingEntity living) {
        if ( tag.isEmpty() || !(living instanceof Mob oldMob) ) return false;
        ForgeRegistries.ENTITY_TYPES.getValue(EntityType.getKey(oldMob.getType()));
        return EntityType.create(tag, level).map((entity -> {
            entity.setPos(oldMob.position());
            entity.setDeltaMovement(oldMob.getDeltaMovement());
            if ( entity instanceof LivingEntity newLiving && newLiving.hasEffect(ModEffects.POLYMORPH.get()) ) {
                newLiving.removeEffect(ModEffects.POLYMORPH.get());
            }
            level.addFreshEntity(entity);
            oldMob.discard();
            return entity;
        })).isPresent();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderPrePolymorphedPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance != null && nameTagDistance.hasModifier(POLYMORPH_NAME_TAG_DISTANCE) ) {
            event.setCanceled(true);
            final Sheep sheep = EntityType.SHEEP.create(player.level());
            UUID sheepUUID = sheep.getUUID();
            sheep.setUUID(player.getUUID());
            syncEntityWithPlayer(sheep, player, event.getPartialTick());
            sheep.setUUID(sheepUUID);
            render(sheep, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }
    }

    private static void syncEntityWithPlayer(Sheep sheep, Player player, float partialTicks) {
        sheep.yBodyRotO = player.yBodyRotO;
        sheep.yBodyRot = player.yBodyRot;
        sheep.tickCount = player.tickCount;
        sheep.setXRot(player.getXRot());
        sheep.setYRot(player.getYRot());
        sheep.yHeadRot = player.yHeadRot;
        sheep.yRotO = player.yRotO;
        sheep.xRotO = player.xRotO;
        sheep.yHeadRotO = player.yHeadRotO;
        sheep.setInvisible(player.isInvisible());

        sheep.setOldPosAndRot();

        ((WalkAnimationStateMixin)sheep.walkAnimation).setPosition(player.walkAnimation.position());
        sheep.walkAnimation.setSpeed(player.walkAnimation.speed());
        ((WalkAnimationStateMixin)sheep.walkAnimation).setSpeedOld(((WalkAnimationStateMixin)player.walkAnimation).getSpeedOld());
        sheep.setDeltaMovement(player.getDeltaMovement());

        sheep.hurtTime = player.hurtTime;
        sheep.deathTime = player.deathTime;

        sheep.horizontalCollision = player.horizontalCollision;
        sheep.verticalCollision = player.verticalCollision;
        sheep.setOnGround(player.onGround());
        //sheep.setCrouching(player.isCrouching());
        sheep.setSwimming(player.isSwimming());
        sheep.setSprinting(player.isSprinting());

        sheep.swinging = player.swinging;
        sheep.swingingArm = player.swingingArm;
        sheep.swingTime = player.swingTime;

        sheep.setPose(player.getPose());
    }

    private static void render(LivingEntity sheep, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Minecraft instance = Minecraft.getInstance();
        float yaw = Mth.lerp(partialTicks, sheep.yRotO, sheep.getYRot());
        instance.getEntityRenderDispatcher().getRenderer(sheep).render(sheep, yaw, partialTicks, poseStack, buffer, light);
    }

    @SubscribeEvent
    public static void preventAttackWhilePolymorphed(AttackEntityEvent event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH.get()) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventInteractWhilePolymorphed(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH.get()) ) return;
        if ( event.isCancelable() ) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void polymorphBackWhenAttacked(final LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if ( living.hasEffect(ModEffects.POLYMORPH.get()) ) living.removeEffect(ModEffects.POLYMORPH.get());
    }
}
