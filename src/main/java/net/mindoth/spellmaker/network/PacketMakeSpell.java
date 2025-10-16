package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMakeSpell {

    public String name;

    public PacketMakeSpell(String name) {
        this.name = name;
    }

    public PacketMakeSpell(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.containerMenu instanceof SpellMakingMenu ) ((SpellMakingMenu)player.containerMenu).processMaking(this.name);
            }
        });
    }
}
