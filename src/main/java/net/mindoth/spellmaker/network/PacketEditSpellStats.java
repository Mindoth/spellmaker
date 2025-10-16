package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketEditSpellStats {

    public byte flag;
    public List<Integer> list;
    public int size;

    public PacketEditSpellStats(byte flag, List<Integer> list) {
        this.flag = flag;
        this.list = list;
        this.size = list.size();
    }

    public PacketEditSpellStats(FriendlyByteBuf buf) {
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

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processSpellStatEditing(this.flag, this.list);
            }
        });
    }
}
