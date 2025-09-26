//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  GeoNode.java
//
//  This is an object class file for GeoNode objects to be
//  used in DroneRoutingDemo.java. This object class contains
//  necessary methods for operating on GeoNode objects such
//  as data retrieval, adding edges, and a toString method.
//  This is designed to work in conjunction with
//  ZoneType.java and GeoEdge.java and is inadequate when
//  not considering those files.
//************************************************************

import java.util.*;

public class GeoNode {
    private final String id;
    private final ZoneType zone;
    private final double latitude;
    private final double longitude;
    private final double altitude;
    private final List<GeoEdge> edges = new ArrayList<>();

    /**
     *
     * @param id
     * @param zone
     * @param latitude
     * @param longitude
     * @param altitude
     */
    public GeoNode(String id, ZoneType zone, double latitude, double longitude, double altitude) {
        this.id = id;
        this.zone = zone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    /**
     *
     * @param edge
     */
    public void addEdge(GeoEdge edge) {
        edges.add(edge);
    }

    /**
     *
     * @return
     */
    public List<GeoEdge> getEdges() { return edges; }

    /**
     *
     * @return
     */
    public ZoneType getZone() { return zone; }

    /**
     *
     * @return
     */
    public String getId() { return id; }

    /**
     *
     * @return
     */
    public double getLatitude() { return latitude; }

    /**
     *
     * @return
     */
    public double getLongitude() { return longitude; }

    /**
     *
     * @return
     */
    public double getAltitude() { return altitude; }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s [%s] @ (%.6f, %.6f, %.1fm)", id, zone, latitude, longitude, altitude);
    }
}