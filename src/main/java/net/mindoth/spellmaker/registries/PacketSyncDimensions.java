package net.mindoth.spellmaker.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncDimensions {

    public int id;

    public PacketSyncDimensions(int id) {
        this.id = id;
    }

    public PacketSyncDimensions(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if ( level != null ) {
                Entity entity = level.getEntity(this.id);
                if ( entity instanceof LivingEntity living ) {
                    living.refreshDimensions();
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
