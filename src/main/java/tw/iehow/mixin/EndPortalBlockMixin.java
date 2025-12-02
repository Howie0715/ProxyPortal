package tw.iehow.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.iehow.data.PortalData;
import tw.iehow.data.PortalManager;
import tw.iehow.util.PositionCheck;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void cancelPortalBlockTeleport(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        for (PortalData portalData : PortalManager.getAllPortals()) {
            if (!portalData.positions.dimension.equals(entity.getEntityWorld().getRegistryKey().getValue().toString())) continue;
            if (PositionCheck.isInPortalArea(pos, portalData.positions)) {
                ci.cancel();
                break;
            }
        }
    }
}
