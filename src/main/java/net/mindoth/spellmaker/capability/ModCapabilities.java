package net.mindoth.spellmaker.capability;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.playermagic.MagickData;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagickProvider;
import net.mindoth.spellmaker.network.SyncClientManaPacket;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
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

    /*@SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if ( event.getObject().level().isClientSide ) return;
        if ( event.getObject() instanceof Player player ) {
            if ( !player.getCapability(PlayerMagickProvider.PLAYER_MAGICK).isPresent() ) {
                event.addCapability(new ResourceLocation(SpellMaker.MOD_ID, PlayerMagick.SM_MAGICK), new PlayerMagickProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCreatedAfterDeath(PlayerEvent.Clone event) {
        if ( event.getEntity() instanceof ServerPlayer serverPlayer ) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                    if ( event.isWasDeath() ) changeMana(serverPlayer, Integer.MAX_VALUE);
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    public static final String NBT_KEY_NOT_FIRST_LOGIN = ("sm_notFirstLogIn");

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if ( event.getEntity().level().isClientSide ) return;
        Player player = event.getEntity();
        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        if ( event.getEntity() instanceof ServerPlayer serverPlayer ) {
            serverPlayer.getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(magic -> {
                if ( data.getBoolean(NBT_KEY_NOT_FIRST_LOGIN) ) ModNetwork.sendToPlayer(new SyncClientManaPacket(magic.getCurrentMana()), serverPlayer);
                else changeMana(serverPlayer, Integer.MAX_VALUE);
            });
        }

        //KEEP THIS LAST
        if ( !data.getBoolean(NBT_KEY_NOT_FIRST_LOGIN) ) {
            data.putBoolean(NBT_KEY_NOT_FIRST_LOGIN, true);
            playerData.put(Player.PERSISTED_NBT_TAG, data);
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerMagick.class);
    }*/
}
