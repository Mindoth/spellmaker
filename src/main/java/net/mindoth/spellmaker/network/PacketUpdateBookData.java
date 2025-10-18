package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateBookData {

    public ItemStack book;
    public int size;
    public List<ItemStack> scrollList = Lists.newArrayList();
    public int index;

    public PacketUpdateBookData(ItemStack book, List<ItemStack> scrollList, int index) {
        this.book = book;
        this.size = scrollList.size();
        this.scrollList = scrollList;
        this.index = index;
    }

    public PacketUpdateBookData(FriendlyByteBuf buf) {
        this.book = buf.readItem();
        int size = buf.readVarInt();
        for ( int i = 0; i < size; i++ ) this.scrollList.add(buf.readItem());
        this.index = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.book);
        buf.writeVarInt(this.size);
        for ( ItemStack stack : this.scrollList ) buf.writeItem(stack);
        buf.writeInt(this.index);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        contextSupplier.get().enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.getInventory().contains(this.book) ) {
                    ItemStack book;
                    if ( ItemStack.isSameItemSameTags(player.getOffhandItem(), this.book) && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) book = player.getOffhandItem();
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(this.book));
                    CompoundTag newTag = SpellBookItem.constructBook(this.book, this.scrollList).getTag();
                    if (newTag.contains(SpellBookItem.NBT_KEY_BOOK_SLOT) ) newTag.remove(SpellBookItem.NBT_KEY_BOOK_SLOT);
                    newTag.putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, this.index);
                    book.setTag(newTag);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
