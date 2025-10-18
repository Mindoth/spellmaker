package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketEditSpellForm {

    public CompoundTag tag;

    public PacketEditSpellForm(CompoundTag tag) {
        this.tag = tag;
    }

    public PacketEditSpellForm(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.containerMenu instanceof SpellMakingMenu menu ) menu.processSpellFormEditing(this.tag);
            }
        });
    }
}
