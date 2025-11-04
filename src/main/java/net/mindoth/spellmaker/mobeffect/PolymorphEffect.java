package net.mindoth.spellmaker.mobeffect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.item.sigil.PolymorphSigilItem;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.mixin.EntityMixin;
import net.mindoth.spellmaker.mixin.WalkAnimationStateMixin;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class PolymorphEffect extends MobEffect {
    public PolymorphEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static final String NBT_KEY_OLD_MOB = "sm_polymorphed_entity";
    public static final String NBT_KEY_RE_POLYMORPH = "sm_re_polymorphed_entity";

    public static void doPolymorph(LivingEntity living, AttributeModifier nameTagModifier) {
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance != null && !nameTagDistance.hasModifier(nameTagModifier.id()) ) nameTagDistance.addPermanentModifier(nameTagModifier);
        if ( living instanceof Mob target ) polymorphMob(target, nameTagModifier);
        else if ( living instanceof Player player ) polymorphPlayer(player, nameTagModifier);
    }

    private static void polymorphPlayer(Player player, AttributeModifier nameTagModifier) {
        PolymorphSigilItem sigil = getSigilFromUUID(nameTagModifier.id());
        if ( sigil != null ) {
            sigil.addStatModifiers(player);
            PolymorphSigilItem.syncDimensions(player);
        }
    }

    @SubscribeEvent
    public static void reAddedPolymorphEffect(MobEffectEvent.Added event) {
        if ( !(event.getEffectInstance().getEffect() instanceof PolymorphEffect) ) return;
        if ( event.getOldEffectInstance() == null ) return;
        if ( event.getEntity() instanceof Mob mob ) mob.getPersistentData().putBoolean(NBT_KEY_RE_POLYMORPH, true);
    }

    private static void polymorphMob(Mob target, AttributeModifier nameTagModifier) {
        if ( !(target.level() instanceof ServerLevel level) ) return;
        if ( target.getPersistentData().contains(NBT_KEY_OLD_MOB) ) reTransformMob(target, level, getTypeFromUUID(nameTagModifier.id()));
        else transformMob(target, level, getTypeFromUUID(nameTagModifier.id()));
    }

    private static void transformMob(Mob target, ServerLevel level, EntityType entityType) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(target.getType()).toString());
        target.saveWithoutId(tag);
        Mob mob = target.convertTo(entityType, false);
        if ( mob == null ) return;
        if ( target.hasEffect(ModEffects.POLYMORPH) ) mob.addEffect(target.getEffect(ModEffects.POLYMORPH));
        EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CONVERSION, null);
        mob.getPersistentData().put(NBT_KEY_OLD_MOB, tag);
    }

    private static void reTransformMob(Mob target, ServerLevel level, EntityType entityType) {
        CompoundTag tag = target.getPersistentData().getCompound(NBT_KEY_OLD_MOB);
        Mob mob = target.convertTo(entityType, false);
        if ( mob == null ) return;
        if ( target.hasEffect(ModEffects.POLYMORPH) ) mob.addEffect(target.getEffect(ModEffects.POLYMORPH));
        EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CONVERSION, null);
        mob.getPersistentData().put(NBT_KEY_OLD_MOB, tag);
    }

    //TODO: effect on start and end
    /*@Override
    public void addAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        removeModifiers(living, false);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living instanceof Mob mob ) {
            if ( !(mob.level() instanceof ServerLevel level) ) return;
            if ( mob.getPersistentData().getBoolean(NBT_KEY_RE_POLYMORPH) ) mob.getPersistentData().remove(NBT_KEY_RE_POLYMORPH);
            else if ( mob.getPersistentData().contains(NBT_KEY_OLD_MOB) ) restoreMob(mob.getPersistentData().getCompound(NBT_KEY_OLD_MOB), level, mob);
        }
        else if ( living instanceof Player player ) removeModifiers(player, true);
    }*/

    public static void removeModifiers(LivingEntity living, boolean doSync) {
        AttributeInstance nameTagDistance = living.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        List<AttributeModifier> nameTagModifierList = Lists.newArrayList();
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) if ( getSigilFromUUID(modifier.id()) != null ) nameTagModifierList.add(modifier);
        for ( AttributeModifier modifier : nameTagModifierList ) getSigilFromUUID(modifier.id()).removeModifiers(living);
        if ( doSync ) PolymorphSigilItem.syncDimensions(living);
    }

    private void restoreMob(CompoundTag tag, ServerLevel level, LivingEntity living) {
        if ( tag.isEmpty() || !(living instanceof Mob oldMob) ) return;
        BuiltInRegistries.ENTITY_TYPE.get(EntityType.getKey(oldMob.getType()));
        EntityType.create(tag, level).map((entity -> {
            entity.setPos(oldMob.position());
            entity.setDeltaMovement(oldMob.getDeltaMovement());
            if ( entity instanceof LivingEntity newLiving ) {
                if ( newLiving.hasEffect(ModEffects.POLYMORPH) ) newLiving.removeEffect(ModEffects.POLYMORPH);
                removeModifiers(newLiving, false);
            }
            level.addFreshEntity(entity);
            oldMob.discard();
            return entity;
        }));
    }

    @SubscribeEvent
    public static void preventAttackWhilePolymorphed(AttackEntityEvent event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        if ( event instanceof PlayerInteractEvent.RightClickItem itemEvent
                && (itemEvent.getItemStack().getItem() instanceof StaffItem || itemEvent.getItemStack().getItem() instanceof SpellBookItem) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void preventPolymorphedEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        if ( !player.hasEffect(ModEffects.POLYMORPH) ) return;
        event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderPolymorphedPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        AttributeInstance nameTagDistance = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) {
            if ( getTypeFromUUID(modifier.id()) != null && getTypeFromUUID(modifier.id()).create(player.level()) instanceof LivingEntity living ) {
                renderPolymorphModel(living, player, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
                event.setCanceled(true);
                break;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderPolymorphModel(LivingEntity living, Player player, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        UUID livingUUID = living.getUUID();
        living.setUUID(player.getUUID());
        syncEntityWithPlayer(living, player);
        living.setUUID(livingUUID);
        render(living, partialTicks, poseStack, buffer, light);
    }

    @OnlyIn(Dist.CLIENT)
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

        Pose pose = living.getPose();
        living.setPose(player.getPose());
        if ( pose != living.getPose() || (living.getDimensions(living.getPose()) != player.getDimensions(player.getPose())) ) living.refreshDimensions();
    }

    @OnlyIn(Dist.CLIENT)
    private static void render(LivingEntity living, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Minecraft instance = Minecraft.getInstance();
        float yaw = Mth.lerp(partialTicks, living.yRotO, living.getYRot());
        instance.getEntityRenderDispatcher().getRenderer(living).render(living, yaw, partialTicks, poseStack, buffer, light);
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
                if ( Objects.equals(modifier.id().toString(), PolymorphSigilItem.POLYMORPH_SPEED_MODIFIER_UUID.toString()) ) amount += modifier.amount();
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

    public static EntityType getTypeFromUUID(ResourceLocation uuid) {
        for ( ResourceLocation id : BuiltInRegistries.ITEM.keySet() ) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if ( item instanceof PolymorphSigilItem rune && Objects.equals(rune.getUUID().toString(), uuid.toString()) ) return rune.getEntityType();
        }
        return null;
    }

    public static PolymorphSigilItem getSigilFromUUID(ResourceLocation uuid) {
        for ( ResourceLocation id : BuiltInRegistries.ITEM.keySet() ) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if ( item instanceof PolymorphSigilItem rune && Objects.equals(rune.getUUID().toString(), uuid.toString()) ) return rune;
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

    public static PolymorphSigilItem getTransformationSigil(LivingEntity living) {
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
