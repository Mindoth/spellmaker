package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class EditSpellStatsPacket implements CustomPacketPayload {

    public static final Type<EditSpellStatsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "edit_spell_stats"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditSpellStatsPacket> STREAM_CODEC =
            CustomPacketPayload.codec(EditSpellStatsPacket::encode, EditSpellStatsPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public byte flag;
    public List<Integer> list;
    public int size;

    public EditSpellStatsPacket(byte flag, List<Integer> list) {
        this.flag = flag;
        this.list = list;
        this.size = list.size();
    }

    public EditSpellStatsPacket(FriendlyByteBuf buf) {
        this.flag = buf.readByte();
        this.size = buf.readVarInt();
        List<Integer> tempList = Lists.newArrayList();
        for ( int i = 0; i < this.size; i++ ) tempList.add(buf.readInt());
        this.list = tempList;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.flag);
        buf.writeVarInt(this.size);
        for ( Integer integer : this.list ) buf.writeInt(integer);
    }

    public static void handle(EditSpellStatsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processSpellStatEditing(packet.flag, packet.list);
            }
        });
    }
}
