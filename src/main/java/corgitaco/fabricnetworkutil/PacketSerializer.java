package corgitaco.fabricnetworkutil;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface PacketSerializer<T extends Packet> extends BiConsumer<T, FriendlyByteBuf> {
}
