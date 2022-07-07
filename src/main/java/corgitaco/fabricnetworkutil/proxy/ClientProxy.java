package corgitaco.fabricnetworkutil.proxy;

import corgitaco.fabricnetworkutil.FabricNetworkUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ClientProxy implements Proxy {
    public static final ClientProxy CLIENT_PROXY = new ClientProxy();

    private ClientProxy() {
    }

    @Override
    public <T> void register(ResourceLocation resourceLocation, Function<FriendlyByteBuf, T> function, BiConsumer<T, Level> consumer) {
        ClientPlayNetworking.registerGlobalReceiver(resourceLocation, (client, handler, buf, responseSender) -> {

            @Nullable
            ClientLevel level = client.level;

            T packet = function.apply(buf);

            client.execute(() -> {
                if (level != null) {
                    try {
                        consumer.accept(packet, level);
                    } catch (Exception e) {
                        FabricNetworkUtil.LOGGER.error("Failed to execute packet", e);

                        throw e;
                    }
                }
            });
        });
    }
}
