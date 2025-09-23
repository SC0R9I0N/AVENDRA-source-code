import java.util.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DroneRoutingDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        List<GeoNode> nodes = buildGraph();
        Scene scene = new Scene(GraphVisualization.render(nodes), 800, 600); // Render it
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Routing Visualization");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private List<GeoNode> buildGraph() {
        List<GeoNode> nodes = new ArrayList<>();

        // Terminal corners
        GeoNode tNW = new GeoNode("T-NW", ZoneType.TERMINAL, 40.4910, -80.2330, 330);
        GeoNode tNE = new GeoNode("T-NE", ZoneType.TERMINAL, 40.4910, -80.2310, 330);
        GeoNode tSE = new GeoNode("T-SE", ZoneType.TERMINAL, 40.4890, -80.2310, 330);
        GeoNode tSW = new GeoNode("T-SW", ZoneType.TERMINAL, 40.4890, -80.2330, 330);

        // Aerodrome corners
        GeoNode aNW = new GeoNode("A-NW", ZoneType.AERODROME, 40.4935, -80.2405, 280);
        GeoNode aNE = new GeoNode("A-NE", ZoneType.AERODROME, 40.4935, -80.2380, 280);
        GeoNode aSE = new GeoNode("A-SE", ZoneType.AERODROME, 40.4910, -80.2380, 280);
        GeoNode aSW = new GeoNode("A-SW", ZoneType.AERODROME, 40.4910, -80.2405, 280);
        GeoNode aRunway = new GeoNode("A-Runway", ZoneType.AERODROME, 40.4922, -80.2392, 275);

        // Property line
        GeoNode pNW = new GeoNode("P-NW", ZoneType.PROPERTY_LINE, 40.4945, -80.2460, 285);
        GeoNode pNE = new GeoNode("P-NE", ZoneType.PROPERTY_LINE, 40.4945, -80.2290, 285);
        GeoNode pSE = new GeoNode("P-SE", ZoneType.PROPERTY_LINE, 40.4870, -80.2290, 285);
        GeoNode pSW = new GeoNode("P-SW", ZoneType.PROPERTY_LINE, 40.4870, -80.2460, 285);

        // Hotspots
        GeoNode[] hotspots = new GeoNode[10];
        for (int i = 0; i < 10; i++) {
            double lat = 40.4880 + (Math.random() * 0.007);
            double lon = -80.2450 + (Math.random() * 0.015);
            double alt = 300 + (Math.random() * 20);
            hotspots[i] = new GeoNode("H" + (i + 1), ZoneType.HOTSPOT, lat, lon, alt);
        }

        // Add all nodes
        nodes.addAll(List.of(tNW, tNE, tSE, tSW, aNW, aNE, aSE, aSW, aRunway, pNW, pNE, pSE, pSW));
        nodes.addAll(Arrays.asList(hotspots));

        // Connect terminal
        tNW.addEdge(new GeoEdge(tNW, tNE));
        tNE.addEdge(new GeoEdge(tNE, tSE));
        tSE.addEdge(new GeoEdge(tSE, tSW));
        tSW.addEdge(new GeoEdge(tSW, tNW));

        // Connect aerodrome
        aNW.addEdge(new GeoEdge(aNW, aNE));
        aNE.addEdge(new GeoEdge(aNE, aSE));
        aSE.addEdge(new GeoEdge(aSE, aSW));
        aSW.addEdge(new GeoEdge(aSW, aNW));
        aRunway.addEdge(new GeoEdge(aRunway, aNW));
        aRunway.addEdge(new GeoEdge(aRunway, aSE));

        // Connect property line
        pNW.addEdge(new GeoEdge(pNW, pNE));
        pNE.addEdge(new GeoEdge(pNE, pSE));
        pSE.addEdge(new GeoEdge(pSE, pSW));
        pSW.addEdge(new GeoEdge(pSW, pNW));

        return nodes;
    }
}