package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AskToOpenSpellBookPacket implements CustomPacketPayload {

    public static final Type<AskToOpenSpellBookPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "ask_to_open_spell_book"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AskToOpenSpellBookPacket> STREAM_CODEC =
            CustomPacketPayload.codec(AskToOpenSpellBookPacket::encode, AskToOpenSpellBookPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ItemStack book;

    public AskToOpenSpellBookPacket(ItemStack book) {
        this.book = book;
    }

    public AskToOpenSpellBookPacket(FriendlyByteBuf buf) {
        this.book = ModNetwork.readItemStack(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        ModNetwork.writeItemStack(buf, this.book);
    }

    public static void handle(AskToOpenSpellBookPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                ItemStack book = null;
                if ( player.getInventory().contains(packet.book) ) {
                    if ( ItemStack.isSameItemSameComponents(player.getOffhandItem(), packet.book) && !(player.getMainHandItem().getItem() instanceof SpellBookItem) ) {
                        book = player.getOffhandItem();
                    }
                    else book = player.getInventory().getItem(player.getInventory().findSlotMatchingItem(packet.book));
                }
                else if ( !SpellBookItem.getSpellBookSlot(player).isEmpty() ) {
                    book = SpellBookItem.getSpellBookSlot(player);
                    SpellBookItem.handleSignature(player, book);
                }
                if ( book != null ) {
                    player.stopUsingItem();
                    SpellBookItem.openSpellBook(player, book);
                }
            }
        });
    }
}
