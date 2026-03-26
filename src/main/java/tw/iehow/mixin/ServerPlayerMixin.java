package tw.iehow.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.iehow.data.PortalData;
import tw.iehow.data.PortalManager;
import tw.iehow.teleport.TeleportHandler;
import tw.iehow.util.PositionCheck;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Unique
    private static final Map<UUID, Boolean> playersInPortal = new HashMap<>();

    public ServerPlayerMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.tickCount % 5 == 0) {
            checkPortalCollision(player);
        }
    }

    @Unique
    private void checkPortalCollision(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        String currentDimension = player.level().dimension().identifier().toString();
        UUID playerId = player.getUUID();

        boolean isInAnyPortal = false;

        for (PortalData portal : PortalManager.getAllPortals()) {
            if (!portal.positions.dimension.equals(currentDimension)) continue;

            if (PositionCheck.isInPortalArea(playerPos, portal.positions)) {
                isInAnyPortal = true;
                if (!playersInPortal.getOrDefault(playerId, false)) {
                    TeleportHandler.teleportPlayer(player, portal);
                    playersInPortal.put(playerId, true);
                }
                break;
            }
        }

        if (!isInAnyPortal) {
            playersInPortal.remove(playerId);
        }
    }
}
