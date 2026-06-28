package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PlayClientNotifySoundPacket implements CustomPacketPayload {

    public static final Type<PlayClientNotifySoundPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, "client_notify_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayClientNotifySoundPacket> STREAM_CODEC =
            CustomPacketPayload.codec(PlayClientNotifySoundPacket::encode, PlayClientNotifySoundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public byte type;
    public float volume;
    public float pitch;

    public PlayClientNotifySoundPacket(byte type, float volume, float pitch) {
        this.type = type;
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlayClientNotifySoundPacket(FriendlyByteBuf buf) {
        this.type = buf.readByte();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.type);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    public static void handle(PlayClientNotifySoundPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft instance = Minecraft.getInstance();
            playClientNotifySound(packet.type, packet.volume, packet.pitch, instance.player, instance.level);
        });
    }

    public static void playClientNotifySound(byte type, float volume, float pitch, LocalPlayer player, ClientLevel level) {
        SoundEvent sound = StaffItem.getSoundByByte(type);
        if ( sound == null ) return;
        level.playLocalSound(player, sound, SoundSource.PLAYERS, volume, pitch);
    }
}
