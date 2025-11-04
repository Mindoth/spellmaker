package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.screen.SpellBookScreen;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateBookDataClientPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBookDataClientPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "update_book_data_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBookDataClientPacket> STREAM_CODEC =
            CustomPacketPayload.codec(UpdateBookDataClientPacket::encode, UpdateBookDataClientPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int index;
    public boolean refresh;
    public boolean isRemoval;

    public UpdateBookDataClientPacket(int index, boolean refresh, boolean isRemoval) {
        this.index = index;
        this.refresh = refresh;
        this.isRemoval = isRemoval;
    }

    public UpdateBookDataClientPacket(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.refresh = buf.readBoolean();
        this.isRemoval = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
        buf.writeBoolean(this.refresh);
        buf.writeBoolean(this.isRemoval);
    }

    public static void handle(UpdateBookDataClientPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( Minecraft.getInstance().screen instanceof SpellBookScreen screen ) {
                if ( packet.isRemoval ) {
                    screen.scrollList.remove(packet.index);
                    int newSlot = SpellBookItem.getNewSlotFromScrollRemoval(packet.index, screen.getSelectedSlot());
                    ModData.getLegacyTag(screen.book).putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, newSlot);
                }
                else ModData.getLegacyTag(screen.book).putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, packet.index);
                if ( packet.refresh ) {
                    screen.createPages(true);
                    screen.publicClearWidgets();
                    screen.buildButtons();
                }
            }
        });
    }
}
