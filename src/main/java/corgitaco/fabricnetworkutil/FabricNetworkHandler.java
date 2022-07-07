package corgitaco.fabricnetworkutil;

import corgitaco.fabricnetworkutil.proxy.ClientProxy;
import corgitaco.fabricnetworkutil.proxy.Proxy;
import corgitaco.fabricnetworkutil.proxy.ServerProxy;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class FabricNetworkHandler {
    private final Map<Class<?>, Pair<ResourceLocation, PacketSerializer<?>>> map;

    public FabricNetworkHandler() {
        this(new ConcurrentHashMap<>());
    }

    public FabricNetworkHandler(Map<Class<?>, Pair<ResourceLocation, PacketSerializer<?>>> map) {
        this.map = map;
    }

    public <T extends Packet> void register(ResourceLocation resourceLocation, Packet.Handler<T> handler) {
        register(resourceLocation, handler.clazz(), handler.serializer(), handler.deserializer(), handler.handle());
    }

    public <T extends Packet> void register(ResourceLocation resourceLocation, Class<T> clazz, PacketSerializer<T> serializer, PacketDeserializer<T> deserializer, BiConsumer<T, Level> consumer) {
        map.put(clazz, new ObjectObjectImmutablePair<>(resourceLocation, serializer));

        getProxy().register(resourceLocation, deserializer, consumer);
    }

    public <T extends Packet> Pair<ResourceLocation, PacketSerializer<? extends Packet>> getPair(Class<T> clazz) {
        return map.get(clazz);
    }

    public static Proxy getProxy() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ?
                ClientProxy.CLIENT_PROXY :
                ServerProxy.SERVER_PROXY;
    }
}
