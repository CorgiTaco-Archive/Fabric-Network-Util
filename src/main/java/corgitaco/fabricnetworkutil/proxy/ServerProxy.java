package corgitaco.fabricnetworkutil.proxy;

import corgitaco.fabricnetworkutil.FabricNetworkUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

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
}
