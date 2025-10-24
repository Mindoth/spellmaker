package net.mindoth.spellmaker.capability;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagick;
import net.mindoth.spellmaker.capability.playermagic.PlayerMagickProvider;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketSyncClientMana;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class ModCapabilities {

    @SubscribeEvent
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
                if ( data.getBoolean(NBT_KEY_NOT_FIRST_LOGIN) ) ModNetwork.sendToPlayer(new PacketSyncClientMana(magic.getCurrentMana()), serverPlayer);
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
    public static void baseManaRegen(TickEvent.LevelTickEvent event) {
        if ( event.phase != TickEvent.Phase.END || event.level.isClientSide ) return;
        event.level.players().stream().toList().forEach(player -> {
            if ( !(player instanceof ServerPlayer serverPlayer ) || player.isDeadOrDying() || player.isRemoved() ) return;
            serverPlayer.getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(magic -> {
                final double maxMana = serverPlayer.getAttributeValue(ModAttributes.MANA_MAX.get());
                final double currentMana = magic.getCurrentMana();
                final double manaRegen = 1;
                if ( player.tickCount % 20 == 0 ) changeMana(player, manaRegen);
                if ( currentMana > maxMana ) changeMana(player, maxMana - currentMana);
            });
        });
    }

    //ANY CHANGES IN A PLAYER'S RESOURCE SHOULD BE DONE HERE
    public static void changeMana(Entity entity, double addition) {
        if ( !(entity instanceof ServerPlayer serverPlayer) || serverPlayer.isRemoved() || (serverPlayer.isCreative() && addition < 0) ) return;
        serverPlayer.getCapability(PlayerMagickProvider.PLAYER_MAGICK).ifPresent(magic -> {
            final double maxMana = serverPlayer.getAttributeValue(ModAttributes.MANA_MAX.get());
            final double currentMana = magic.getCurrentMana();
            final double newMana = Math.max(0.0D, Math.min(maxMana, currentMana + addition));
            magic.setCurrentMana(newMana);
            ModNetwork.sendToPlayer(new PacketSyncClientMana(newMana), serverPlayer);
        });
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerMagick.class);
    }
}
