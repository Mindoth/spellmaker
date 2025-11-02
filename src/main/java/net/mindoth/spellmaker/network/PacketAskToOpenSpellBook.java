package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.item.SpellBookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAskToOpenSpellBook {

    public boolean tagged;

    public PacketAskToOpenSpellBook(boolean tagged) {
        this.tagged = tagged;
    }

    public PacketAskToOpenSpellBook(FriendlyByteBuf buf) {
        this.tagged = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.tagged);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                player.stopUsingItem();
                ItemStack book = this.tagged ? SpellBookItem.getTaggedSpellBookSlot(player) : SpellBookItem.getSpellBookSlot(player);
                SpellBookItem.openSpellBook(player, book);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
