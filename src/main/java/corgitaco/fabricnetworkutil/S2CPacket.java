package corgitaco.fabricnetworkutil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface S2CPacket {

    void write(FriendlyByteBuf buf);

    void handle(Level level);

    record Handler<T extends S2CPacket>(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> write,
                                        Function<FriendlyByteBuf, T> read,
                                        BiConsumer<T, Level> handle) {
    }
}