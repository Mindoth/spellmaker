package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketReorderSpellBook {

    public ItemStack book;
    public int size;
    public List<ItemStack> scrollList = Lists.newArrayList();
    public int slot;
    public boolean isUp;

    public PacketReorderSpellBook(ItemStack book, List<ItemStack> scrollList, int slot, boolean isUp) {
        this.book = book;
        this.size = scrollList.size();
        this.scrollList = scrollList;
        this.slot = slot;
        this.isUp = isUp;
    }

    public PacketReorderSpellBook(FriendlyByteBuf buf) {
        this.book = buf.readItem();
        int size = buf.readVarInt();
        for ( int i = 0; i < size; i++ ) this.scrollList.add(buf.readItem());
        this.slot = buf.readInt();
        this.isUp = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.book);
        buf.writeVarInt(this.size);
        for ( ItemStack stack : this.scrollList ) buf.writeItem(stack);
        buf.writeInt(this.slot);
        buf.writeBoolean(this.isUp);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.getInventory().contains(this.book) ) {
                    ItemStack book;
                    if ( ItemStack.isSameItemSameTags(player.getOffhandItem(), this.book)
                            && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) book = player.getOffhandItem();
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(this.book));

                    ItemStack first = this.scrollList.get(this.slot).copy();
                    ItemStack second;
                    if ( this.isUp ) {
                        second = this.scrollList.get(this.slot - 1).copy();
                        this.scrollList.set(this.slot - 1, first);
                    }
                    else {
                        second = this.scrollList.get(this.slot + 1).copy();
                        this.scrollList.set(this.slot + 1, first);
                    }
                    this.scrollList.set(this.slot, second);

                    book.setTag(SpellBookItem.constructBook(this.book, this.scrollList).getTag());
                }
            }
        });
    }
}
