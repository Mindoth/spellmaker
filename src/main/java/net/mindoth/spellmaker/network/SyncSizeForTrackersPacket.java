package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncSizeForTrackersPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncSizeForTrackersPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "sync_size_for_trackers"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSizeForTrackersPacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncSizeForTrackersPacket::encode, SyncSizeForTrackersPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int id;

    public SyncSizeForTrackersPacket(int id) {
        this.id = id;
    }

    public SyncSizeForTrackersPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public static void handle(SyncSizeForTrackersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            PacketDistributor.sendToPlayersTrackingEntity(player, new SyncSizePacket(packet.id, false));
            if ( player instanceof ServerPlayer serverPlayer ) PacketDistributor.sendToPlayer(serverPlayer, new SyncSizePacket(packet.id, false));
        });
    }
}
