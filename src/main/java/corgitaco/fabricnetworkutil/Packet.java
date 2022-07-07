package corgitaco.fabricnetworkutil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface Packet {

    void write(FriendlyByteBuf buf);

    void handle(Level level);

    record Handler<T extends Packet>(Class<T> clazz, PacketSerializer<T> serializer, PacketDeserializer<T> deserializer, BiConsumer<T, Level> handle) {
    }
}