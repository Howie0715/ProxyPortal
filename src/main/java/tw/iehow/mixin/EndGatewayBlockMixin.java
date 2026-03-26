package tw.iehow.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tw.iehow.data.PortalData;
import tw.iehow.data.PortalManager;
import tw.iehow.util.PositionCheck;

@Mixin(EndGatewayBlock.class)
public class EndGatewayBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void cancelPortalBlockTeleport(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl, CallbackInfo ci) {
        for (PortalData portalData : PortalManager.getAllPortals()) {
            if (!portalData.positions.dimension.equals(entity.level().dimension().identifier().toString())) continue;
            if (PositionCheck.isInPortalArea(pos, portalData.positions)) {
                ci.cancel();
                break;
            }
        }
    }
}
