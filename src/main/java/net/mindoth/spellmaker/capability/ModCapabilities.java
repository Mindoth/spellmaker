package net.mindoth.spellmaker.capability;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.playermagic.MagickData;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagickProvider;
import net.mindoth.spellmaker.network.SyncClientManaPacket;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class ModCapabilities {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SpellMaker.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MagickData>> MAGICK_DATA = ATTACHMENT_TYPES.register("magick_data",
            () -> AttachmentType.builder((holder) -> holder instanceof ServerPlayer serverPlayer ? new MagickData(serverPlayer)
                    : new MagickData()).serialize(new PlayerMagickProvider()).build());

    @SubscribeEvent
    public static void baseManaRegen(LevelTickEvent.Post event) {
        if ( event.getLevel().isClientSide ) return;
        event.getLevel().players().stream().toList().forEach(player -> {
            if ( !(player instanceof ServerPlayer serverPlayer ) || player.isDeadOrDying() || player.isRemoved() ) return;
            MagickData magick = MagickData.getPlayerMagickData(serverPlayer);
            final double maxMana = serverPlayer.getAttributeValue(ModAttributes.MANA_MAX);
            final double currentMana = magick.getCurrentMana();
            final double manaRegen = serverPlayer.getAttributeValue(ModAttributes.MANA_REGENERATION);
            if ( player.tickCount % 20 == 0 ) changeMana(player, manaRegen);
            if ( currentMana > maxMana ) changeMana(player, maxMana - currentMana);
        });
    }

    //ANY CHANGES IN A PLAYER'S RESOURCE SHOULD BE DONE HERE
    public static void changeMana(Entity entity, double addition) {
        if ( !(entity instanceof ServerPlayer serverPlayer) || serverPlayer.isRemoved() || (serverPlayer.isCreative() && addition < 0) ) return;
        MagickData magick = MagickData.getPlayerMagickData(serverPlayer);
        final double maxMana = serverPlayer.getAttributeValue(ModAttributes.MANA_MAX);
        final double currentMana = magick.getCurrentMana();
        final double newMana = Math.max(0.0D, Math.min(maxMana, currentMana + addition));
        magick.setCurrentMana(newMana);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncClientManaPacket(newMana));
    }

    public static final String NBT_KEY_NOT_FIRST_LOGIN = ("sm_notFirstLogIn");

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if ( !(event.getEntity() instanceof ServerPlayer serverPlayer) || serverPlayer.level().isClientSide ) return;
        CompoundTag playerData = serverPlayer.getPersistentData();
        CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        MagickData magick = MagickData.getPlayerMagickData(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncClientManaPacket(magick.getCurrentMana()));
        if ( data.getBoolean(NBT_KEY_NOT_FIRST_LOGIN) ) PacketDistributor.sendToPlayer(serverPlayer, new SyncClientManaPacket(magick.getCurrentMana()));
        else changeMana(serverPlayer, Integer.MAX_VALUE);

        //KEEP THIS LAST
        if ( !data.getBoolean(NBT_KEY_NOT_FIRST_LOGIN) ) {
            data.putBoolean(NBT_KEY_NOT_FIRST_LOGIN, true);
            playerData.put(Player.PERSISTED_NBT_TAG, data);
        }
    }

    @SubscribeEvent
    public static void onPlayerCreatedAfterDeath(PlayerEvent.Clone event) {
        if ( !(event.getEntity() instanceof ServerPlayer serverPlayer) || !event.isWasDeath() ) return;
        MagickData oldMagicData = MagickData.getPlayerMagickData(event.getOriginal());
        MagickData newMagicData = MagickData.getPlayerMagickData(serverPlayer);
        newMagicData.copyFrom(oldMagicData);
    }
}
