package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.ClientMagickData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncClientManaPacket implements CustomPacketPayload {

    public static final Type<SyncClientManaPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "sync_client_mana"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClientManaPacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncClientManaPacket::encode, SyncClientManaPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public double mana;

    public SyncClientManaPacket(double mana) {
        this.mana = mana;
    }

    public SyncClientManaPacket(FriendlyByteBuf buf) {
        this.mana = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.mana);
    }

    public static void handle(SyncClientManaPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientMagickData.setCurrentMana(packet.mana));
    }
}
