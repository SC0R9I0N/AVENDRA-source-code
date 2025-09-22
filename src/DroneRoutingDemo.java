import java.util.*;

public class DroneRoutingDemo {
    public static void main(String[] args) {
        List<GeoNode> nodes = new ArrayList<>();

        // Terminal corners (rooftop altitude)
        GeoNode tNW = new GeoNode("T-NW", ZoneType.TERMINAL, 40.4910, -80.2330, 330);
        GeoNode tNE = new GeoNode("T-NE", ZoneType.TERMINAL, 40.4910, -80.2310, 330);
        GeoNode tSE = new GeoNode("T-SE", ZoneType.TERMINAL, 40.4890, -80.2310, 330);
        GeoNode tSW = new GeoNode("T-SW", ZoneType.TERMINAL, 40.4890, -80.2330, 330);

        // Aerodrome corners (ground level)
        GeoNode aNW = new GeoNode("A-NW", ZoneType.AERODROME, 40.4935, -80.2405, 280);
        GeoNode aNE = new GeoNode("A-NE", ZoneType.AERODROME, 40.4935, -80.2380, 280);
        GeoNode aSE = new GeoNode("A-SE", ZoneType.AERODROME, 40.4910, -80.2380, 280);
        GeoNode aSW = new GeoNode("A-SW", ZoneType.AERODROME, 40.4910, -80.2405, 280);
        GeoNode aRunway = new GeoNode("A-Runway", ZoneType.AERODROME, 40.4922, -80.2392, 275); // Slight dip

        // Hotspots (not yet connected)
        GeoNode[] hotspots = new GeoNode[10];
        for (int i = 0; i < 10; i++) {
            double lat = 40.4880 + (Math.random() * 0.007); // Simulated spread
            double lon = -80.2450 + (Math.random() * 0.015);
            double alt = 300 + (Math.random() * 20); // Bird flight altitude
            hotspots[i] = new GeoNode("H" + (i + 1), ZoneType.HOTSPOT, lat, lon, alt);
        }

        // Add all nodes to list
        nodes.addAll(List.of(tNW, tNE, tSE, tSW, aNW, aNE, aSE, aSW, aRunway));
        nodes.addAll(Arrays.asList(hotspots));

        // Connect terminal perimeter (rooftop loop)
        tNW.addEdge(new GeoEdge(tNW, tNE));
        tNE.addEdge(new GeoEdge(tNE, tSE));
        tSE.addEdge(new GeoEdge(tSE, tSW));
        tSW.addEdge(new GeoEdge(tSW, tNW));

        // Connect aerodrome perimeter
        aNW.addEdge(new GeoEdge(aNW, aNE));
        aNE.addEdge(new GeoEdge(aNE, aSE));
        aSE.addEdge(new GeoEdge(aSE, aSW));
        aSW.addEdge(new GeoEdge(aSW, aNW));
        aRunway.addEdge(new GeoEdge(aRunway, aNW));
        aRunway.addEdge(new GeoEdge(aRunway, aSE));

        // Property line nodes (enclosing terminal + aerodrome)
        GeoNode pNW = new GeoNode("P-NW", ZoneType.PROPERTY_LINE, 40.4945, -80.2460, 285);
        GeoNode pNE = new GeoNode("P-NE", ZoneType.PROPERTY_LINE, 40.4945, -80.2290, 285);
        GeoNode pSE = new GeoNode("P-SE", ZoneType.PROPERTY_LINE, 40.4870, -80.2290, 285);
        GeoNode pSW = new GeoNode("P-SW", ZoneType.PROPERTY_LINE, 40.4870, -80.2460, 285);

        // Add to node list
        nodes.addAll(List.of(pNW, pNE, pSE, pSW));

        // Connect property line perimeter
        pNW.addEdge(new GeoEdge(pNW, pNE));
        pNE.addEdge(new GeoEdge(pNE, pSE));
        pSE.addEdge(new GeoEdge(pSE, pSW));
        pSW.addEdge(new GeoEdge(pSW, pNW));

        // Print graph
        for (GeoNode node : nodes) {
            System.out.println("Edges from " + node + ":");
            for (GeoEdge edge : node.getEdges()) {
                System.out.println("  → " + edge.getTarget().getId() + " (weight: " + edge.getWeight() + ")");
            }
        }
    }
}