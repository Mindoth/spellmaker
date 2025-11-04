package net.mindoth.spellmaker.network;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.registries.ModData;
import net.minecraft.core.IdMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Objects;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = SpellMaker.MOD_ID)
public class ModNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(SpellMaker.MOD_ID).versioned("1.0.0").optional();

        payloadRegistrar.playToClient(SyncClientManaPacket.TYPE, SyncClientManaPacket.STREAM_CODEC, SyncClientManaPacket::handle);
        payloadRegistrar.playToServer(AskToOpenSpellBookPacket.TYPE, AskToOpenSpellBookPacket.STREAM_CODEC, AskToOpenSpellBookPacket::handle);
        payloadRegistrar.playToClient(OpenSpellBookPacket.TYPE, OpenSpellBookPacket.STREAM_CODEC, OpenSpellBookPacket::handle);
        payloadRegistrar.playToServer(RemoveScrollFromBookPacket.TYPE, RemoveScrollFromBookPacket.STREAM_CODEC, RemoveScrollFromBookPacket::handle);
        payloadRegistrar.playToServer(UpdateBookDataPacket.TYPE, UpdateBookDataPacket.STREAM_CODEC, UpdateBookDataPacket::handle);
        payloadRegistrar.playToServer(EditSpellFormPacket.TYPE, EditSpellFormPacket.STREAM_CODEC, EditSpellFormPacket::handle);
        payloadRegistrar.playToServer(EditSpellStatsPacket.TYPE, EditSpellStatsPacket.STREAM_CODEC, EditSpellStatsPacket::handle);
        payloadRegistrar.playToServer(MakeSpellPacket.TYPE, MakeSpellPacket.STREAM_CODEC, MakeSpellPacket::handle);
        payloadRegistrar.playToServer(DumpSpellPacket.TYPE, DumpSpellPacket.STREAM_CODEC, DumpSpellPacket::handle);
        payloadRegistrar.playToClient(SyncSizePacket.TYPE, SyncSizePacket.STREAM_CODEC, SyncSizePacket::handle);
        payloadRegistrar.playToClient(UpdateBookDataClientPacket.TYPE, UpdateBookDataClientPacket.STREAM_CODEC, UpdateBookDataClientPacket::handle);
    }

    private static <T> T legacyReadById(FriendlyByteBuf buf, IdMap<T> pIdMap) {
        int i = buf.readVarInt();
        return pIdMap.byId(i);
    }

    private static <T> void legacyWriteId(FriendlyByteBuf buf, IdMap<T> pIdMap, T pValue) {
        int i = pIdMap.getId(pValue);
        if ( i == -1 ) throw new IllegalArgumentException("Can't find id for '" + pValue + "' in map " + pIdMap);
        else buf.writeVarInt(i);
    }

    public static ItemStack readItem(FriendlyByteBuf buf) {
        if ( !buf.readBoolean() ) return ItemStack.EMPTY;
        else {
            Item item = legacyReadById(buf, BuiltInRegistries.ITEM);
            int i = buf.readByte();
            ItemStack stack = new ItemStack(item, i);
            ModData.setLegacyTag(stack, buf.readNbt());
            if ( buf.readBoolean() ) stack.set(DataComponents.CUSTOM_NAME, Component.literal(buf.readUtf()));
            return stack;
        }
    }

    public static void writeItemStack(FriendlyByteBuf buf, ItemStack stack) {
        if ( stack.isEmpty() ) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            Item item = stack.getItem();
            legacyWriteId(buf, BuiltInRegistries.ITEM, item);
            buf.writeByte(stack.getCount());
            CompoundTag compoundtag = ModData.getLegacyTag(stack);
            buf.writeNbt(compoundtag);
            if ( stack.has(DataComponents.CUSTOM_NAME) ) {
                buf.writeBoolean(true);
                buf.writeUtf(stack.getHoverName().getString());
            }
            else buf.writeBoolean(false);
        }
    }

    //TODO: Check for DataComponents
    public static boolean isSameItemSameTags(ItemStack pStack, ItemStack pOther) {
        if ( !pStack.is(pOther.getItem()) ) return false;
        else {
            CompoundTag stackTag = ModData.getLegacyTag(pStack);
            CompoundTag otherTag = ModData.getLegacyTag(pOther);
            return pStack.isEmpty() && pOther.isEmpty() || Objects.equals(stackTag, otherTag);
        }
    }
}
