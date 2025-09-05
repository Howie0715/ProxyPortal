package tw.iehow.data;

public class PortalData {
    public String portalName;
    public PositionGroup positions;
    public String destinationServer;

    public PortalData() {
    }

    public PortalData(String portalName, PositionGroup positions, String destinationServer) {
        this.portalName = portalName;
        this.positions = positions;
        this.destinationServer = destinationServer;
    }

    public static class PositionGroup {
        public String dimension;
        public Position pos1;
        public Position pos2;

        public PositionGroup() {
        }

        public PositionGroup(String dimension, Position pos1, Position pos2) {
            this.dimension = dimension;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }
    }

    public static class Position {
        public double x;
        public double y;
        public double z;

        public Position() {
        }

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
