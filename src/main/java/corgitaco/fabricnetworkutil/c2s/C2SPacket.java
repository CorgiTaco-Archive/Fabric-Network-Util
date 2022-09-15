package corgitaco.fabricnetworkutil.c2s;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface C2SPacket {

    void write(FriendlyByteBuf buf);

    void handle(MinecraftServer level, ServerPlayer player, PacketSender sender);

    record Handler<T extends C2SPacket>(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> write,
                                        Function<FriendlyByteBuf, T> read,
                                        Handle<T> handle) {
    }

    @FunctionalInterface
    interface Handle<T extends C2SPacket> {

        void handle(T packet, MinecraftServer server, ServerPlayer player, PacketSender packetSender);
    }
}