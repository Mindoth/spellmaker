package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.screen.SpellBookScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenSpellBookPacket implements CustomPacketPayload {

    public static final Type<OpenSpellBookPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "open_spell_book"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSpellBookPacket> STREAM_CODEC =
            CustomPacketPayload.codec(OpenSpellBookPacket::encode, OpenSpellBookPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ItemStack book;
    public int page;

    public OpenSpellBookPacket(ItemStack book, int page) {
        this.book = book;
        this.page = page;
    }

    public OpenSpellBookPacket(FriendlyByteBuf buf) {
        this.book = ModNetwork.readItemStack(buf);
        this.page = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        ModNetwork.writeItemStack(buf, this.book);
        buf.writeInt(this.page);
    }

    public static void handle(OpenSpellBookPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> SpellBookScreen.open(packet.book, packet.page));
    }
}
