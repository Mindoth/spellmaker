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

public class DumpSpellPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DumpSpellPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "dump_spell"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DumpSpellPacket> STREAM_CODEC =
            CustomPacketPayload.codec(DumpSpellPacket::encode, DumpSpellPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public DumpSpellPacket() {
    }

    public DumpSpellPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(DumpSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processDumping();
            }
        });
    }
}
