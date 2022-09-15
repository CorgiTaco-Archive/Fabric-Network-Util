package corgitaco.fabricnetworkutil.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface C2SPacket {

    void write(FriendlyByteBuf buf);

    void handle(MinecraftServer level);

    record Handler<T extends C2SPacket>(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> write,
                                        Function<FriendlyByteBuf, T> read,
                                        BiConsumer<T, MinecraftServer> handle) {
    }
}