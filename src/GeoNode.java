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

/**
 * Represents a single geographic point or location in the routing graph.
 * A GeoNode has a unique ID, a specific zone type, and precise coordinates
 * for longitude, latitude, and altitude. It also maintains the outgoing edges
 * to other GeoNodes, forming connections in the graph
 */
public class GeoNode {
    private final String id;
    private final ZoneType zone;
    private final double latitude;
    private final double longitude;
    private final double altitude;
    private final List<GeoEdge> edges = new ArrayList<>();

    /**
     * Constructs a new GeoNode with the specified properties.
     * @param id The unique identifier for the node.
     * @param zone The zone the node belongs to (HOTSPOT, TERMINAL, etc.).
     * @param latitude Geographic latitude of the node in degrees.
     * @param longitude Geographic longitude of the node in degrees
     * @param altitude Altitude of the node in meters.
     */
    public GeoNode(String id, ZoneType zone, double latitude, double longitude, double altitude) {
        this.id = id;
        this.zone = zone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    /**
     * Adds an outgoing edge from the specified node to another.
     *
     * @param edge The GeoEdge added.
     */
    public void addEdge(GeoEdge edge) {
        edges.add(edge);
    }

    /**
     * Returns the list of all outgoing edges from this node.
     *
     * @return A list of GeoEdge objects.
     */
    public List<GeoEdge> getEdges() { return edges; }

    /**
     * Returns the zone type of this node.
     *
     * @return The ZoneType enum value.
     */
    public ZoneType getZone() { return zone; }

    /**
     * Returns the unique identifier of this node.
     *
     * @return The ID as a String.
     */
    public String getId() { return id; }

    /**
     * Returns the latitude of this node.
     *
     * @return Latitude in degrees.
     */
    public double getLatitude() { return latitude; }

    /**
     * Returns the longitude of this node.
     *
     * @return Longitude in degrees.
     */
    public double getLongitude() { return longitude; }

    /**
     * Returns the altitude of this node.
     *
     * @return Altitude in meters.
     */
    public double getAltitude() { return altitude; }

    /**
     * Returns a formatted string representation of the GeoNode.
     * The format includes ID, zone type, and the coordinates.
     *
     * @return String formatted as "ID [Zone] @ (lat, lon, alt)".
     */
    @Override
    public String toString() {
        return String.format("%s [%s] @ (%.6f, %.6f, %.1fm)", id, zone, latitude, longitude, altitude);
    }
}