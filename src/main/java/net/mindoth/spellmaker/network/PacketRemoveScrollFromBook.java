package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketRemoveScrollFromBook {

    public ItemStack book;
    public int size;
    public List<ItemStack> scrollList = Lists.newArrayList();
    public int index;

    public PacketRemoveScrollFromBook(ItemStack book, List<ItemStack> scrollList, int index) {
        this.book = book;
        this.size = scrollList.size();
        this.scrollList = scrollList;
        this.index = index;
    }

    public PacketRemoveScrollFromBook(FriendlyByteBuf buf) {
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
        context.enqueueWork(() -> {
            if ( context.getSender() != null ) {
                ServerPlayer player = context.getSender();
                if ( player.getInventory().contains(this.book) ) {
                    ItemStack book;
                    if ( ItemStack.isSameItemSameTags(player.getOffhandItem(), this.book)
                            && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) book = player.getOffhandItem();
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(this.book));
                    if ( player.addItem(this.scrollList.get(this.index)) ) {
                        Vec3 center = ShadowEvents.getEntityCenter(player);
                        ItemEntity drop = new ItemEntity(player.level(), center.x, center.y, center.z, this.scrollList.get(this.index));
                        drop.setDeltaMovement(0, 0, 0);
                        drop.setNoPickUpDelay();
                        player.level().addFreshEntity(drop);
                    }
                    this.scrollList.remove(this.index);
                    CompoundTag tag = SpellBookItem.constructBook(this.book, this.scrollList).getTag();
                    int newSlot = SpellBookItem.getNewSlotFromScrollRemoval(this.index, tag.getInt(SpellBookItem.NBT_KEY_BOOK_SLOT));
                    tag.putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, newSlot);
                    book.setTag(tag);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
