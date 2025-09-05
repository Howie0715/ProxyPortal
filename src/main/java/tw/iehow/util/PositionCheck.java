package tw.iehow.util;

import net.minecraft.util.math.BlockPos;
import tw.iehow.data.PortalData;

public class PositionCheck {
    public static boolean isInPortalArea(BlockPos playerPos, PortalData.PositionGroup positions) {
        double minX = Math.min(positions.pos1.x, positions.pos2.x);
        double maxX = Math.max(positions.pos1.x, positions.pos2.x);
        double minY = Math.min(positions.pos1.y, positions.pos2.y);
        double maxY = Math.max(positions.pos1.y, positions.pos2.y);
        double minZ = Math.min(positions.pos1.z, positions.pos2.z);
        double maxZ = Math.max(positions.pos1.z, positions.pos2.z);

        return playerPos.getX() >= minX && playerPos.getX() <= maxX &&
                playerPos.getY() >= minY && playerPos.getY() <= maxY &&
                playerPos.getZ() >= minZ && playerPos.getZ() <= maxZ;
    }
}
