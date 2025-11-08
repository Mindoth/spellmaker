package net.mindoth.spellmaker.mobeffect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.sigil.PolymorphSigilItem;
import net.mindoth.spellmaker.mixin.EntityMixin;
import net.mindoth.spellmaker.mixin.WalkAnimationStateMixin;
import net.mindoth.spellmaker.network.SyncSizeForTrackersPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = SpellMaker.MOD_ID, value = Dist.CLIENT)
public abstract class ClientHelperMethods {

    @SubscribeEvent
    public static void syncPolymorphSizeClient(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if ( player == null ) return;
        AttributeInstance nameTagDistance = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        if ( nameTagDistance.hasModifier(PolymorphSigilItem.SYNC_POLYMORPH_SIZE_CLIENT.id()) ) {
            nameTagDistance.removeModifier(PolymorphSigilItem.SYNC_POLYMORPH_SIZE_CLIENT);
            //player.refreshDimensions();
            ClientPacketDistributor.sendToServer(new SyncSizeForTrackersPacket(player.getId()));
        }
    }

    @SubscribeEvent
    public static void renderPolymorphedPlayer(RenderPlayerEvent.Pre<AbstractClientPlayer> event) {
        Minecraft mc = Minecraft.getInstance();
        if ( mc.level == null || !(mc.level.getEntity(event.getRenderState().id) instanceof Player player) ) return;
        AttributeInstance nameTagDistance = player.getAttribute(NeoForgeMod.NAMETAG_DISTANCE);
        if ( nameTagDistance == null ) return;
        for ( AttributeModifier modifier : nameTagDistance.getModifiers() ) {
            if ( PolymorphEffect.getTypeFromUUID(modifier.id()) != null
                    && PolymorphEffect.getTypeFromUUID(modifier.id()).create(player.level(), EntitySpawnReason.EVENT) instanceof LivingEntity living ) {
                renderPolymorphModel(living, player, event);
                event.setCanceled(true);
                break;
            }
        }
    }

    private static void renderPolymorphModel(LivingEntity living, Player player, RenderPlayerEvent.Pre<AbstractClientPlayer> event) {
        UUID livingUUID = living.getUUID();
        living.setUUID(player.getUUID());
        syncEntityWithPlayer(living, player);
        living.setUUID(livingUUID);
        render(living, event);
        //poseStack.pushPose();
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

        Pose pose = living.getPose();
        living.setPose(player.getPose());
        if ( pose != living.getPose() || (living.getDimensions(living.getPose()) != player.getDimensions(player.getPose())) ) living.refreshDimensions();
    }

    //TODO: render polymorphed player
    private static void render(LivingEntity living, RenderPlayerEvent.Pre<AbstractClientPlayer> event) {
        /*Minecraft instance = Minecraft.getInstance();
        float yaw = Mth.lerp(event.getPartialTick(), living.yRotO, living.getYRot());
        //instance.getEntityRenderDispatcher().getRenderer(living).render(living, yaw, partialTicks, poseStack, buffer, light);
        EntityRenderer renderer = instance.getEntityRenderDispatcher().getRenderer(living);
        EntityRenderState state = renderer.createRenderState(living, event.getPartialTick());
        Entity camera = instance.getCameraEntity();
        renderer.submit(state, event.getPoseStack(), event.getSubmitNodeCollector(), null);*/
    }

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
}
