package tw.iehow.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private static final Map<UUID, Boolean> playersInPortal = new HashMap<>();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.age % 5 == 0) {
            checkPortalCollision(player);
        }
    }

    @Unique
    private void checkPortalCollision(ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        String currentDimension = player.getEntityWorld().getRegistryKey().getValue().toString();
        UUID playerId = player.getUuid();

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
