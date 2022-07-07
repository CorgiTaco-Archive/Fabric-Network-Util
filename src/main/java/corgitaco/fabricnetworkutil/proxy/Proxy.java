package corgitaco.fabricnetworkutil.proxy;

import corgitaco.fabricnetworkutil.Packet;
import corgitaco.fabricnetworkutil.PacketDeserializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public sealed interface Proxy permits ClientProxy, ServerProxy {

    <T extends Packet> void register(ResourceLocation resourceLocation, PacketDeserializer<T> deserializer, BiConsumer<T, Level> consumer);
}
