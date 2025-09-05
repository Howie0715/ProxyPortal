package tw.iehow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import tw.iehow.data.PortalManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.iehow.teleport.ProxyPacket;

public class ProxyPortal implements ModInitializer {
	public static final String MOD_ID = "proxyportal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        PortalManager.initializePortals();
        PortalCommand.register();
        PayloadTypeRegistry.playS2C().register(ProxyPacket.PACKET_ID, ProxyPacket.codec);
        LOGGER.info("ProxyPortal loaded successfully with {} portals", PortalManager.getPortalCount());
    }
}