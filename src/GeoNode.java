import java.util.*;

public class GeoNode {
    private final String id;
    private final ZoneType zone;
    private final double latitude;
    private final double longitude;
    private final double altitude;
    private final List<GeoEdge> edges = new ArrayList<>();

    public GeoNode(String id, ZoneType zone, double latitude, double longitude, double altitude) {
        this.id = id;
        this.zone = zone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public void addEdge(GeoEdge edge) {
        edges.add(edge);
    }

    public List<GeoEdge> getEdges() { return edges; }
    public ZoneType getZone() { return zone; }
    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAltitude() { return altitude; }

    @Override
    public String toString() {
        return String.format("%s [%s] @ (%.6f, %.6f, %.1fm)", id, zone, latitude, longitude, altitude);
    }
}