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

    public AskToOpenSpellBookPacket() {
    }

    public AskToOpenSpellBookPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static void handle(AskToOpenSpellBookPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if ( context.player() instanceof ServerPlayer player ) {
                player.stopUsingItem();
                if ( SpellBookItem.getTaggedSpellBookSlot(player).isEmpty() ) {
                    ItemStack book = SpellBookItem.getSpellBookSlot(player);
                    SpellBookItem.handleSignature(player, book);
                    SpellBookItem.openSpellBook(player, book);
                }
                else SpellBookItem.openSpellBook(player, SpellBookItem.getTaggedSpellBookSlot(player));
            }
        });
    }
}
