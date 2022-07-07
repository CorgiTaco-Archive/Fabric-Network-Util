package corgitaco.fabricnetworkutil;

import corgitaco.fabricnetworkutil.proxy.ClientProxy;
import corgitaco.fabricnetworkutil.proxy.Proxy;
import corgitaco.fabricnetworkutil.proxy.ServerProxy;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FabricNetworkHandler {
    private final Map<Class<?>, Pair<ResourceLocation, BiConsumer<?, FriendlyByteBuf>>> map;

    public FabricNetworkHandler() {
        this(new ConcurrentHashMap<>());
    }

    public FabricNetworkHandler(Map<Class<?>, Pair<ResourceLocation, BiConsumer<?, FriendlyByteBuf>>> map) {
        this.map = map;
    }

    // Should be in server proxy.
    public <T extends S2CPacket> void register(ResourceLocation resourceLocation, S2CPacket.Handler<T> handler) {
        register(resourceLocation, handler.clazz(), handler.write(), handler.read(), handler.handle());
    }

    public <T> void register(ResourceLocation resourceLocation, Class<T> clazz, BiConsumer<T, FriendlyByteBuf> biConsumer, Function<FriendlyByteBuf, T> function, BiConsumer<T, Level> consumer) {
        map.put(clazz, new ObjectObjectImmutablePair<>(resourceLocation, biConsumer));

        getProxy().register(resourceLocation, function, consumer);
    }

    // Server Side. Should not be in a common sided class. Move into server proxy.
    public <T> void sendToPlayer(ServerPlayer player, T packet) {
        Pair<ResourceLocation, BiConsumer<?, FriendlyByteBuf>> pair = map.get(packet.getClass());

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        //noinspection unchecked
        ((BiConsumer<T, FriendlyByteBuf>) pair.right()).accept(packet, buf);

        ServerPlayNetworking.send(player, pair.first(), buf);
    }

    public <T> void sendToPlayers(List<ServerPlayer> list, T packet) {
        list.forEach(player -> sendToPlayer(player, packet));
    }

    public static Proxy getProxy() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ?
                ClientProxy.CLIENT_PROXY :
                ServerProxy.SERVER_PROXY;
    }
}
