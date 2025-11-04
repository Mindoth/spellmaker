package net.mindoth.spellmaker.network;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class RemoveScrollFromBookPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RemoveScrollFromBookPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "remove_scroll_from_book"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveScrollFromBookPacket> STREAM_CODEC =
            CustomPacketPayload.codec(RemoveScrollFromBookPacket::encode, RemoveScrollFromBookPacket::new);

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

    public RemoveScrollFromBookPacket(ItemStack book, List<ItemStack> scrollList, int index, boolean refresh, boolean isRemoval) {
        this.book = book;
        this.size = scrollList.size();
        this.scrollList = scrollList;
        this.index = index;
        this.refresh = refresh;
        this.isRemoval = isRemoval;
    }

    public RemoveScrollFromBookPacket(FriendlyByteBuf buf) {
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

    public static void handle(RemoveScrollFromBookPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                if ( player.getInventory().contains(packet.book) ) {
                    ItemStack book;
                    if ( ModNetwork.isSameItemSameTags(player.getOffhandItem(), packet.book)
                            && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) book = player.getOffhandItem();
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(packet.book));
                    if ( player.addItem(packet.scrollList.get(packet.index)) ) {
                        Vec3 center = ShadowEvents.getEntityCenter(player);
                        ItemEntity drop = new ItemEntity(player.level(), center.x, center.y, center.z, packet.scrollList.get(packet.index));
                        drop.setDeltaMovement(0, 0, 0);
                        drop.setNoPickUpDelay();
                        player.level().addFreshEntity(drop);
                    }
                    packet.scrollList.remove(packet.index);
                    CompoundTag tag = ModData.getLegacyTag(SpellBookItem.constructBook(packet.book, packet.scrollList));
                    int newSlot = SpellBookItem.getNewSlotFromScrollRemoval(packet.index, tag.getInt(SpellBookItem.NBT_KEY_BOOK_SLOT));
                    tag.putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, newSlot);
                    ModData.setLegacyTag(book, tag);
                    PacketDistributor.sendToPlayer(player, new UpdateBookDataClientPacket(packet.index, packet.refresh, packet.isRemoval));
                }
            }
        });
    }
}
