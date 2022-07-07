package corgitaco.fabricnetworkutil.proxy;

import corgitaco.fabricnetworkutil.FabricNetworkHandler;
import corgitaco.fabricnetworkutil.FabricNetworkUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ServerProxy implements Proxy {
    public static final ServerProxy SERVER_PROXY = new ServerProxy();

    private ServerProxy() {
    }

    @Override
    public <T> void register(ResourceLocation resourceLocation, Function<FriendlyByteBuf, T> function, BiConsumer<T, Level> consumer) {
        ServerPlayNetworking.registerGlobalReceiver(resourceLocation, (server, player, handler, buf, responseSender) -> {

            T packet = function.apply(buf);

            server.execute(() -> {
                try {
                    consumer.accept(packet, player.level);
                } catch (Exception e) {
                    FabricNetworkUtil.LOGGER.error("Failed to execute packet", e);

                    throw e;
                }
            });
        });
    }

    public static <T> void sendToPlayer(FabricNetworkHandler handler, ServerPlayer player, T packet) {
        var pair = handler.getPair(packet.getClass());

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        //noinspection unchecked
        ((BiConsumer<T, FriendlyByteBuf>) pair.right()).accept(packet, buf);

        ServerPlayNetworking.send(player, pair.left(), buf);
    }

    public static <T> void sendToPlayers(FabricNetworkHandler handler, List<ServerPlayer> list, T packet) {
        list.forEach(player -> sendToPlayer(handler, player, packet));
    }
}
