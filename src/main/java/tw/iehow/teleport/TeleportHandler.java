package tw.iehow.teleport;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import tw.iehow.data.PortalData;

public class TeleportHandler {
    public static void teleportPlayer(ServerPlayerEntity player, PortalData portalData) {
        ServerPlayNetworking.send(player, new ProxyPacket("Connect", portalData.destinationServer));
    }
}
