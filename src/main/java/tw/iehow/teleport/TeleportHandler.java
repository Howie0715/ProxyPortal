package tw.iehow.teleport;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import tw.iehow.data.PortalData;

public class TeleportHandler {
    public static void teleportPlayer(ServerPlayer player, PortalData portalData) {
        ServerPlayNetworking.send(player, new ProxyPacket("Connect", portalData.destinationServer));
    }
}
