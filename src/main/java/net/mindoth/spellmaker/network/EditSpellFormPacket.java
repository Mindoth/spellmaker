package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EditSpellFormPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditSpellFormPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "edit_spell_form"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditSpellFormPacket> STREAM_CODEC =
            CustomPacketPayload.codec(EditSpellFormPacket::encode, EditSpellFormPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public CompoundTag tag;

    public EditSpellFormPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public EditSpellFormPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    public static void handle(EditSpellFormPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processSpellFormEditing(packet.tag);
            }
        });
    }
}
