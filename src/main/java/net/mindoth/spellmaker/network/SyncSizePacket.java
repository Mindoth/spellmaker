package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncSizePacket implements CustomPacketPayload {

    public static final Type<SyncSizePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "sync_size"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSizePacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncSizePacket::encode, SyncSizePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int id;

    public SyncSizePacket(int id) {
        this.id = id;
    }

    public SyncSizePacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public static void handle(SyncSizePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if ( level != null ) {
                Entity entity = level.getEntity(packet.id);
                if ( entity instanceof LivingEntity living ) living.refreshDimensions();
            }
        });
    }
}
