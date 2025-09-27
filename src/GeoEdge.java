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

/**
 * Represents the directional edge between two GeoNodes. The edge
 * stores a calculated weight, which represents the cost for traversal
 * between those nodes. It also bases the weights on the ZoneTypes of
 * of the connected nodes.
 */
public class GeoEdge {
    private final GeoNode from;
    private final GeoNode target;
    private int weight;

    /**
     * Constructs a new GeoEdge object and calculates its initial
     * weight based on the zones of 'from' and 'to' nodes.
     *
     * @param from Starting GeoNode of this edge.
     * @param to Target GeoNode of this edge.
     */
    public GeoEdge(GeoNode from, GeoNode to) {
        this.from = from;
        this.target = to;
        this.weight = calculateWeight(from, to);
    }

    /**
     * Calculates the initial weight of the edge based on the zones
     * of the two nodes the edge connects.
     *
     * Rules:
     * 1. TERMINAL or PROPERTY_LINE are considered impassable.
     * 2. Connections within AERODROME have moderate weight.
     * 3. All connections between HOTSPOT zones have a weight of 1.
     * @param from Starting GeoNode.
     * @param to Target GeoNode.
     * @return Calculated integer weight of the edge.
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
     * Overrides the current weight to 1 for emergency purposes,
     * allowing the drone to enter the aerodrome for the purposes of
     * pursuing a bird that passes into it.
     *
     * NOTE: This is a placeholder method that is not fully implemented,
     * and would need additional constraints based on the full system.
     *
     * @param from Starting GeoNode
     * @param to Target GeoNode
     * @return Status message describing the weight change.
     */
    public String overrideWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        if (from.getZone() == ZoneType.AERODROME) {
            weight = 1;
        }
        return "Weight changed from " + tempWeight + " to " + weight + " for emergency purpose.";
    }

    /**
     * Restores the edge weight back to its initial value after overrideWeight()
     * has been used.
     * {@link #calculateWeight(GeoNode, GeoNode)}.
     *
     * NOTE: This is a placeholder method that is not fully implemented,
     * and would need additional constraints based on the full system.
     *
     * @param from Starting GeoNode
     * @param to Target GeoNode
     * @return
     */
    public String recoverWeight(GeoNode from, GeoNode to) {
        int tempWeight = weight;
        weight = calculateWeight(from, to);
        return "Weight restored from " + tempWeight + " to " + weight + " after emergency.";
    }

    /**
     * Returns the target GeoNode of this edge.
     *
     * @return GeoNode the edge points to.
     */
    public GeoNode getTarget() { return target; }

    /**
     * Returns the current weight of this edge
     *
     * @return The integer weight.
     */
    public int getWeight() { return weight; }

    /**
     * Returns the start GeoNode of this edge.
     *
     * @return GeoNode the edge comes from.
     */
    public GeoNode getFrom() { return from; }
}