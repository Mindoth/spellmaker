package net.mindoth.spellmaker.capability;

import com.mojang.serialization.Codec;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.network.SyncClientManaPacket;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class ModCapabilities {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SpellMaker.MOD_ID);

    public static final Supplier<AttachmentType<Double>> MAGICK_DATA = ATTACHMENT_TYPES.register("magick_data",
            () -> AttachmentType.builder(() -> ModAttributes.MANA_MAX.get().getDefaultValue()).serialize(Codec.DOUBLE.fieldOf("magick_data")).build());

    @SubscribeEvent
    public static void baseManaRegen(LevelTickEvent.Post event) {
        if ( event.getLevel().isClientSide() ) return;
        event.getLevel().players().stream().toList().forEach(holder -> {
            if ( !(holder instanceof ServerPlayer player ) || player.isDeadOrDying() || player.isRemoved() ) return;
            final double maxMana = player.getAttributeValue(ModAttributes.MANA_MAX);
            final double currentMana = player.getData(MAGICK_DATA);
            final double manaRegen = player.getAttributeValue(ModAttributes.MANA_REGENERATION);
            if ( player.tickCount % 20 == 0 && currentMana < maxMana ) changeMana(player, manaRegen, maxMana);
            if ( player.tickCount > 1 && currentMana > maxMana ) changeMana(holder, maxMana - currentMana, maxMana);
        });
    }

    //ANY CHANGES IN A PLAYER'S RESOURCE SHOULD BE DONE HERE
    public static void changeMana(Entity entity, double addition, double max) {
        if ( !(entity instanceof ServerPlayer player) || player.isRemoved() || (player.isCreative() && addition < 0) ) return;
        final double currentMana = player.getData(MAGICK_DATA);
        final double newMana = Math.max(0.0D, Math.min(max, currentMana + addition));
        player.setData(MAGICK_DATA, newMana);
        PacketDistributor.sendToPlayer(player, new SyncClientManaPacket(newMana));
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if ( !(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide() ) return;
        PacketDistributor.sendToPlayer(player, new SyncClientManaPacket(player.getData(MAGICK_DATA)));
    }

    @SubscribeEvent
    public static void onPlayerCreatedAfterDeath(PlayerEvent.Clone event) {
        if ( !(event.getEntity() instanceof ServerPlayer player) ) return;
        if ( event.isWasDeath() && event.getOriginal().hasData(MAGICK_DATA) ) {
            player.setData(MAGICK_DATA, event.getOriginal().getData(MAGICK_DATA));
            double max = event.getOriginal().getAttributeValue(ModAttributes.MANA_MAX);
            changeMana(player, max, max);
        }
    }
}
