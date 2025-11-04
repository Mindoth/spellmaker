package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class UpdateBookDataPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBookDataPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "update_book_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBookDataPacket> STREAM_CODEC =
            CustomPacketPayload.codec(UpdateBookDataPacket::encode, UpdateBookDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ItemStack book;
    public int size;
    public List<ItemStack> scrollList = Lists.newArrayList();
    public int index;
    public boolean refresh;
    public boolean isRemoval;

    public UpdateBookDataPacket(ItemStack book, List<ItemStack> scrollList, int index, boolean refresh, boolean isRemoval) {
        this.book = book;
        this.size = scrollList.size();
        this.scrollList = scrollList;
        this.index = index;
        this.refresh = refresh;
        this.isRemoval = isRemoval;
    }

    public UpdateBookDataPacket(FriendlyByteBuf buf) {
        this.book = ModNetwork.readItem(buf);
        int size = buf.readVarInt();
        for ( int i = 0; i < size; i++ ) this.scrollList.add(ModNetwork.readItem(buf));
        this.index = buf.readInt();
        this.refresh = buf.readBoolean();
        this.isRemoval = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        ModNetwork.writeItemStack(buf, this.book);
        buf.writeVarInt(this.size);
        for ( ItemStack stack : this.scrollList ) ModNetwork.writeItemStack(buf, stack);
        buf.writeInt(this.index);
        buf.writeBoolean(this.refresh);
        buf.writeBoolean(this.isRemoval);
    }

    public static void handle(UpdateBookDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.getInventory().contains(packet.book) ) {
                    ItemStack book;
                    if ( ItemStack.isSameItemSameComponents(player.getOffhandItem(), packet.book) && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) book = player.getOffhandItem();
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(packet.book));
                    CompoundTag newTag = ModData.getLegacyTag(SpellBookItem.constructBook(packet.book, packet.scrollList));
                    if ( packet.index != newTag.getInt(SpellBookItem.NBT_KEY_BOOK_SLOT) ) newTag.putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, packet.index);
                    ModData.setLegacyTag(book, newTag);
                    PacketDistributor.sendToPlayer(player, new UpdateBookDataClientPacket(packet.index, packet.refresh, packet.isRemoval));
                }
            }
        });
    }
}
