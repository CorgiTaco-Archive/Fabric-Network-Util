package corgitaco.fabricnetworkutil;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

@FunctionalInterface
public interface PacketDeserializer<T extends Packet> extends Function<FriendlyByteBuf, T> {
}
