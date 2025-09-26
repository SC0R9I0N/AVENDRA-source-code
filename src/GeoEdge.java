//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  GeoNode.java
//
//  This is an object class file for GeoEdge objects to be
//  used in DroneRoutingDemo.java. This object class contains
//  necessary methods for operating on GeoEdge objects such
//  as data retrieval and weight manipulation and calculation.
//  This is designed to work in conjunction with
//  ZoneType.java and GeoEdge.java and is inadequate when
//  not considering those files.
//
//  The methods overrideWeight() and recoverWeight() are
//  not fully implemented methods, rather they are test
//  methods for the full system design when the drone is
//  required to disregard instruction and enter the
//  aerodrome to pursue a bird.
//************************************************************

public class GeoEdge {
    private final GeoNode from;
    private final GeoNode target;
    private int weight;

    /**
     *
     * @param from
     * @param to
     */
    public GeoEdge(GeoNode from, GeoNode to) {
        this.from = from;
        this.target = to;
        this.weight = calculateWeight(from, to);
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    private int calculateWeight(GeoNode from, GeoNode to) {
        if (from.getZone() == ZoneType.TERMINAL || to.getZone() == ZoneType.TERMINAL ||
                from.getZone() == ZoneType.PROPERTY_LINE || to.getZone() == ZoneType.PROPERTY_LINE) {
            return Integer.MAX_VALUE;
        } else if (from.getZone() == ZoneType.AERODROME && to.getZone() == ZoneType.AERODROME) {
            return 5;
        } else {
            return 1;
        }
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public String overrideWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        if (from.getZone() == ZoneType.AERODROME) {
            weight = 1;
        }
        return "Weight changed from " + tempWeight + " to " + weight + " for emergency purpose.";
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public String recoverWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        weight = calculateWeight(from, to);
        return "Weight restored from " + tempWeight + " to " + weight + " after emergency.";
    }

    /**
     *
     * @return
     */
    public GeoNode getTarget() { return target; }

    /**
     *
     * @return
     */
    public int getWeight() { return weight; }
}