package corgitaco.fabricnetworkutil.c2s;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FabricC2SNetworkHandler {
    public static final Logger LOGGER = LogManager.getLogger();

    private static final Map<Class<? extends C2SPacket>, BiConsumer<?, FriendlyByteBuf>> ENCODERS = new ConcurrentHashMap<>();
    private static final Map<Class<? extends C2SPacket>, ResourceLocation> PACKET_IDS = new ConcurrentHashMap<>();

    public static <T extends C2SPacket> void register(ResourceLocation id, C2SPacket.Handler<T> handler) {
        registerMessage(id, handler.clazz(), handler.write(), handler.read(), handler.handle());
    }

    private static <T extends C2SPacket> void registerMessage(ResourceLocation id, Class<T> clazz,
                                                              BiConsumer<T, FriendlyByteBuf> encode,
                                                              Function<FriendlyByteBuf, T> decode,
                                                              C2SPacket.Handle<T> handler) {
        ENCODERS.put(clazz, encode);
        PACKET_IDS.put(clazz, id);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ClientProxy.registerServerReceiver(id, decode, handler);
        }
    }

    public static <MSG extends C2SPacket> void sendToServer(MSG packet) {
        ResourceLocation packetId = PACKET_IDS.get(packet.getClass());
        @SuppressWarnings("unchecked")
        BiConsumer<MSG, FriendlyByteBuf> encoder = (BiConsumer<MSG, FriendlyByteBuf>) ENCODERS.get(packet.getClass());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encoder.accept(packet, buf);
        ClientPlayNetworking.send(packetId, buf);
    }

    public static class ClientProxy {

        public static <T extends C2SPacket> void registerServerReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode,
                                                                        C2SPacket.Handle<T> handler) {
            ServerPlayNetworking.registerGlobalReceiver(id, (server, player, serverGamePacketListener, buf, responseSender) -> {
                buf.retain();
                server.execute(() -> {
                    T packet = decode.apply(buf);
                    try {
                        handler.handle(packet, server, player, responseSender);
                    } catch (Throwable throwable) {
                        LOGGER.error("Packet failed: ", throwable);
                        throw throwable;
                    }
                    buf.release();
                });
            });
        }
    }
}