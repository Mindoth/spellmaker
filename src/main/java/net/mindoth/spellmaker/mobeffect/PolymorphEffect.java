package net.mindoth.spellmaker.mobeffect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.rune.PolymorphRuneItem;
import net.mindoth.spellmaker.mixin.EntityMixin;
import net.mindoth.spellmaker.mixin.WalkAnimationStateMixin;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketOpenSpellBook;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.registries.PacketSyncDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class PolymorphEffect extends MobEffect {
    public PolymorphEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static final String NBT_KEY_OLD_MOB = "sm_polymorphed_entity";

    public static EntityType getTypeFromUUID(UUID uuid) {
        for ( Item item : ForgeRegistries.ITEMS.getValues() ) {
            if ( item instanceof PolymorphRuneItem rune && Objects.equals(rune.getUUID().toString(), uuid.toString()) ) return rune.getEntityType();
        }
        return null;
    }

    public static PolymorphRuneItem getRuneFromUUID(UUID uuid) {
        for ( Item item : ForgeRegistries.ITEMS.getValues() ) {
            if ( item instanceof PolymorphRuneItem rune && Objects.equals(rune.getUUID().toString(), uuid.toString()) ) return rune;
        }
        return null;
    }

    public static boolean isPolymorphed(LivingEntity living) {
        if ( living.getAttributes() == null ) return false;
        AttributeInstance nameTagDistance = living.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return false;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( getRuneFromUUID(modifier.getId()) != null ) return true;
        return false;
    }

    public static void doPolymorph(LivingEntity living, AttributeModifier nameTagModifier) {
        AttributeInstance nameTagDistance = living.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance != null && !nameTagDistance.hasModifier(nameTagModifier) ) nameTagDistance.addPermanentModifier(nameTagModifier);
        if ( living instanceof Mob target ) polymorphMob(target);
        else if ( living instanceof Player player ) polymorphPlayer(player, nameTagModifier);
    }

    private static void polymorphPlayer(Player player, AttributeModifier nameTagModifier) {
        PolymorphRuneItem rune = getRuneFromUUID(nameTagModifier.getId());
        if ( rune != null ) {
            syncDimensions(player);
            rune.addStatModifiers(player);
        }
    }

    //TODO: Refresh dimensions on client. Figure out where to refresh the damn dimensions client-side
    private static void syncDimensions(Player player) {
        player.refreshDimensions();
        //ModNetwork.sendToPlayersTrackingEntity(new PacketSyncDimensions(player.getId()), player, true);
    }

    /*@OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void test(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if ( player != null ) player.refreshDimensions();
    }*/

    public static EntityDimensions getPolymorphDimensions(LivingEntity living) {
        if ( living.getAttributes() == null ) return null;
        AttributeInstance nameTagDistance = living.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return null;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( PolymorphEffect.getTypeFromUUID(modifier.getId()) != null ) {
            return PolymorphEffect.getTypeFromUUID(modifier.getId()).getDimensions();
        }
        return null;
    }

    private static void polymorphMob(Mob target) {
        if ( target.getPersistentData().contains(NBT_KEY_OLD_MOB) || !(target.level() instanceof ServerLevel level) ) return;
        AttributeInstance nameTagDistance = target.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) {
            if ( getTypeFromUUID(modifier.getId()) != null ) {
                transformMob(target, level, getTypeFromUUID(modifier.getId()));
                break;
            }
        }
    }

    private static void transformMob(Mob target, ServerLevel level, EntityType entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(target.getType()).toString());
        target.saveWithoutId(tag);
        Mob mob = target.convertTo(entityType, false);
        if ( mob == null ) return;
        if ( target.hasEffect(ModEffects.POLYMORPH.get()) ) mob.addEffect(target.getEffect(ModEffects.POLYMORPH.get()));
        ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CONVERSION, null, null);
        mob.getPersistentData().put(NBT_KEY_OLD_MOB, tag);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living instanceof Mob mob ) {
            if ( !living.getPersistentData().contains(NBT_KEY_OLD_MOB) || !(mob.level() instanceof ServerLevel level) ) return;
            restoreMob(mob.getPersistentData().getCompound(NBT_KEY_OLD_MOB), level, mob);
        }
        else if ( living instanceof Player player ) {
            removeModifiers(player);
            syncDimensions(player);
        }
    }

    public static void removeModifiers(LivingEntity living) {
        AttributeInstance nameTagDistance = living.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance != null ) {
            List<AttributeModifier> nameTagModifierList = Lists.newArrayList();
            for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( getRuneFromUUID(modifier.getId()) != null ) nameTagModifierList.add(modifier);
            for ( AttributeModifier modifier : nameTagModifierList ) getRuneFromUUID(modifier.getId()).removeModifiers(living);
        }
    }

    private void restoreMob(CompoundTag tag, ServerLevel level, LivingEntity living) {
        if ( tag.isEmpty() || !(living instanceof Mob oldMob) ) return;
        ForgeRegistries.ENTITY_TYPES.getValue(EntityType.getKey(oldMob.getType()));
        EntityType.create(tag, level).map((entity -> {
            entity.setPos(oldMob.position());
            entity.setDeltaMovement(oldMob.getDeltaMovement());
            if ( entity instanceof LivingEntity newLiving ) {
                if ( newLiving.hasEffect(ModEffects.POLYMORPH.get()) ) newLiving.removeEffect(ModEffects.POLYMORPH.get());
                removeModifiers(newLiving);
            }
            level.addFreshEntity(entity);
            oldMob.discard();
            return entity;
        }));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderPolymorphedPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        AttributeInstance nameTagDistance = player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
        if ( nameTagDistance == null ) return;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) {
            if ( getTypeFromUUID(modifier.getId()) != null && getTypeFromUUID(modifier.getId()).create(player.level()) instanceof LivingEntity living ) {
                renderPolymorphModel(living, player, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
                event.setCanceled(true);
                break;
            }
        }
    }

    private static void renderPolymorphModel(LivingEntity living, Player player, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        UUID livingUUID = living.getUUID();
        living.setUUID(player.getUUID());
        syncEntityWithPlayer(living, player);
        living.setUUID(livingUUID);
        render(living, partialTicks, poseStack, buffer, light);
    }

    private static void syncEntityWithPlayer(LivingEntity living, Player player) {
        living.yBodyRotO = player.yBodyRotO;
        living.yBodyRot = player.yBodyRot;

        living.yHeadRotO = player.yHeadRotO;
        living.yHeadRot = player.yHeadRot;

        living.yRotO = player.yRotO;
        living.setYRot(player.getYRot());

        living.xRotO = player.xRotO;
        living.setXRot(player.getXRot());

        ((WalkAnimationStateMixin)living.walkAnimation).setPosition(player.walkAnimation.position());
        living.walkAnimation.setSpeed(player.walkAnimation.speed());
        ((WalkAnimationStateMixin)living.walkAnimation).setSpeedOld(((WalkAnimationStateMixin)player.walkAnimation).getSpeedOld());

        ((EntityMixin)living).setWasTouchingWater(((EntityMixin)player).getWasTouchingWater());

        living.setDeltaMovement(player.getDeltaMovement());

        living.hurtTime = player.hurtTime;
        living.deathTime = player.deathTime;
        living.tickCount = player.tickCount;
        living.setInvisible(player.isInvisible());

        living.horizontalCollision = player.horizontalCollision;
        living.verticalCollision = player.verticalCollision;
        living.setOnGround(player.onGround());
        //living.setCrouching(player.isCrouching());
        living.setSwimming(player.isSwimming());
        living.setSprinting(player.isSprinting());

        living.swinging = player.swinging;
        living.swingingArm = player.swingingArm;
        living.swingTime = player.swingTime;

        /*Pose pose = living.getPose();
        living.setPose(player.getPose());
        if ( pose != living.getPose() ) living.refreshDimensions();*/
        //player.refreshDimensions();
    }

    private static void render(LivingEntity living, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Minecraft instance = Minecraft.getInstance();
        float yaw = Mth.lerp(partialTicks, living.yRotO, living.getYRot());
        instance.getEntityRenderDispatcher().getRenderer(living).render(living, yaw, partialTicks, poseStack, buffer, light);
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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void counterFovChangeWhilePolymorphed(ComputeFovModifierEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if ( player == null ) return;
        if ( !PolymorphEffect.isPolymorphed(player) ) return;
        double amount = 0;
        for ( AttributeInstance instance : player.getAttributes().getSyncableAttributes() ) {
            for ( AttributeModifier modifier : instance.getModifiers() ) {
                if ( Objects.equals(modifier.getId().toString(), PolymorphRuneItem.POLYMORPH_SPEED_MODIFIER_UUID.toString()) ) amount += modifier.getAmount();
            }
        }
        if ( amount != 0 ) event.setNewFovModifier(newFovCalc(player, amount));
    }

    private static float newFovCalc(Player player, double amount) {
        float f = 1.0F;
        if ( player.getAbilities().flying ) f *= 1.1F;
        f *= ((float)(player.getAttributeValue(Attributes.MOVEMENT_SPEED) - amount) / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        if ( player.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f) ) f = 1.0F;
        ItemStack itemstack = player.getUseItem();
        if ( player.isUsingItem() ) {
            if ( itemstack.is(Items.BOW) ) {
                int i = player.getTicksUsingItem();
                float f1 = (float)i / 20.0F;
                if ( f1 > 1.0F ) f1 = 1.0F;
                else f1 *= f1;
                f *= 1.0F - f1 * 0.15F;
            }
            else if ( Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping() ) return 0.1F;
        }
        return f;
    }
}
