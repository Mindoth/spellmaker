package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.item.SpellBookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAskToOpenSpellBook {

    public PacketAskToOpenSpellBook() {
    }

    public PacketAskToOpenSpellBook(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                player.stopUsingItem();
                ItemStack book = SpellBookItem.getSpellBookSlot(player);
                SpellBookItem.openSpellBook(player, book);
            }
        });
    }
}
