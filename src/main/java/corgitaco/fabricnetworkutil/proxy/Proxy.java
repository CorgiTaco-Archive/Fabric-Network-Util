package corgitaco.fabricnetworkutil.proxy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;

public sealed interface Proxy permits ClientProxy, ServerProxy {

    <T> void register(ResourceLocation resourceLocation, Function<FriendlyByteBuf, T> function, BiConsumer<T, Level> consumer);
}
