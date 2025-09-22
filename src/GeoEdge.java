public class GeoEdge {
    private final GeoNode from;
    private final GeoNode target;
    private int weight;

    public GeoEdge(GeoNode from, GeoNode to) {
        this.from = from;
        this.target = to;
        this.weight = calculateWeight(from, to);
    }

    private int calculateWeight(GeoNode from, GeoNode to) {
        if (from.getZone() == ZoneType.TERMINAL || to.getZone() == ZoneType.TERMINAL ||
                from.getZone() == ZoneType.PROPERTY_LINE || to.getZone() == ZoneType.PROPERTY_LINE) {
            return Integer.MAX_VALUE; // Terminal is a hard barrier
        } else if (from.getZone() == ZoneType.AERODROME && to.getZone() == ZoneType.AERODROME) {
            return 5; // Higher cost inside aerodrome
        } else {
            return 1; // Outside aerodrome or mixed zones
        }
    }

    public String overrideWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        weight = 1;
        return "Weight changed from " + tempWeight + " to " + weight + " for emergency purpose.";
    }

    public String recoveryWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        weight = calculateWeight(from, to);
        return "Weight restored from " + tempWeight + " to " + weight + " after emergency.";
    }

    public GeoNode getTarget() { return target; }
    public int getWeight() { return weight; }
}