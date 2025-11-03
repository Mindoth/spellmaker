package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MakeSpellPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MakeSpellPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "make_spell"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MakeSpellPacket> STREAM_CODEC =
            CustomPacketPayload.codec(MakeSpellPacket::encode, MakeSpellPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public String name;

    public MakeSpellPacket(String name) {
        this.name = name;
    }

    public MakeSpellPacket(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
    }

    public static void handle(MakeSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processMaking(packet.name);
            }
        });
    }
}
