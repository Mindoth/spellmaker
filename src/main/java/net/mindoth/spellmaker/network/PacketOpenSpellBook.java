package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.client.gui.screen.SpellBookScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenSpellBook {

    public ItemStack book;
    public int page;

    public PacketOpenSpellBook(ItemStack book, int page) {
        this.book = book;
        this.page = page;
    }

    public PacketOpenSpellBook(FriendlyByteBuf buf) {
        this.book = buf.readItem();
        this.page = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.book);
        buf.writeInt(this.page);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> SpellBookScreen.open(this.book, this.page));
        contextSupplier.get().setPacketHandled(true);
    }
}
