package corgitaco.fabricnetworkutil.proxy;

import corgitaco.fabricnetworkutil.FabricNetworkHandler;
import corgitaco.fabricnetworkutil.FabricNetworkUtil;
import corgitaco.fabricnetworkutil.Packet;
import io.netty.buffer.Unpooled;
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

    public static <T extends Packet> void send(FabricNetworkHandler handler, T packet) {
        var pair = handler.getPair(packet.getClass());

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        //noinspection unchecked
        ((BiConsumer<T, FriendlyByteBuf>) pair.right()).accept(packet, buf);

        ClientPlayNetworking.send(pair.left(), buf);
    }
}
