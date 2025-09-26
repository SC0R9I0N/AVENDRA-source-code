import java.util.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DroneRoutingDemo extends Application {

    private static final double AERODROME_RADIUS_DEGREES = 0.0025;
    private static final int AERODROME_OUTLINE_NODES = 24;

    private List<GeoNode> nodes;
    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        this.nodes = buildGraph();
        this.root = new BorderPane();

        // Initial rendering of the graph
        updateVisualization();

        // Create the key
        VBox key = createKey();
        root.setRight(key); // Place the key on the right side of the BorderPane

        Button runPathButton = new Button("Run Optimal Path");
        runPathButton.setOnAction(e -> {
            clearHotspotEdges();
            DronePathfinder.createOptimalRouteEdges(this.nodes);
            updateVisualization();
        });

        root.setBottom(runPathButton);

        Scene scene = new Scene(root, 950, 600); // Increased width to accommodate the key
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Routing Visualization");
        primaryStage.show();
    }

    private void updateVisualization() {
        Group graphGroup = GraphVisualization.render(this.nodes);
        root.setCenter(graphGroup);
    }

    // New method to create the legend/key
    private VBox createKey() {
        VBox keyBox = new VBox(10); // Spacing of 10 pixels between elements
        keyBox.setPadding(new Insets(20, 10, 10, 10)); // Padding around the key
        keyBox.setStyle("-fx-background-color: #333333;"); // Dark background for contrast

        Text title = new Text("Graph Key");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        keyBox.getChildren().add(title);

        keyBox.getChildren().add(createKeyItem("Terminal", Color.MEDIUMPURPLE));
        keyBox.getChildren().add(createKeyItem("Aerodrome", Color.BLUE));
        keyBox.getChildren().add(createKeyItem("Property Line", Color.HOTPINK));
        keyBox.getChildren().add(createKeyItem("Hotspot", Color.LIMEGREEN));
        keyBox.getChildren().add(createKeyItem("Optimal Path", Color.GOLD));

        return keyBox;
    }

    // Helper method to create a single key item
    private VBox createKeyItem(String label, Color color) {
        Circle circle = new Circle(5);
        circle.setFill(color);
        Text text = new Text(label);
        text.setFill(Color.WHITE);

        VBox item = new VBox(5);
        item.getChildren().addAll(circle, text);
        return item;
    }

    private void clearHotspotEdges() {
        for (GeoNode node : nodes) {
            if (node.getZone() == ZoneType.HOTSPOT) {
                node.getEdges().clear();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean isWithinAerodrome(double lat, double lon, GeoNode aCenter) {
        double latDiff = lat - aCenter.getLatitude();
        double lonDiff = lon - aCenter.getLongitude();
        double distanceSquared = (latDiff * latDiff) + (lonDiff * lonDiff);
        return distanceSquared <= (AERODROME_RADIUS_DEGREES * AERODROME_RADIUS_DEGREES);
    }

    private boolean isWithinTerminal(double lat, double lon) {
        boolean withinTopPart = (lat <= 40.4910 && lat >= 40.4905) &&
                (lon >= -80.2330 && lon <= -80.2310);
        boolean withinBottomPart = (lat <= 40.4895 && lat >= 40.4890) &&
                (lon >= -80.2330 && lon <= -80.2310);
        boolean withinVerticalPart = (lat <= 40.4910 && lat >= 40.4890) &&
                (lon >= -80.2330 && lon <= -80.2310);
        return withinTopPart || withinBottomPart || withinVerticalPart;
    }

    private List<GeoNode> buildGraph() {
        List<GeoNode> nodes = new ArrayList<>();

        // U-shaped Terminal (8 nodes, creating a shape that opens to the east)
        GeoNode t_N_outer = new GeoNode("T-N-outer", ZoneType.TERMINAL, 40.4910, -80.2330, 330);
        GeoNode t_N_inner = new GeoNode("T-N-inner", ZoneType.TERMINAL, 40.4905, -80.2330, 330);
        GeoNode t_E_inner_N = new GeoNode("T-E-inner-N", ZoneType.TERMINAL, 40.4905, -80.2315, 330);
        GeoNode t_E_inner_S = new GeoNode("T-E-inner-S", ZoneType.TERMINAL, 40.4895, -80.2315, 330);
        GeoNode t_S_inner = new GeoNode("T-S-inner", ZoneType.TERMINAL, 40.4895, -80.2330, 330);
        GeoNode t_S_outer = new GeoNode("T-S-outer", ZoneType.TERMINAL, 40.4890, -80.2330, 330);
        GeoNode t_W_outer_S = new GeoNode("T-W-outer-S", ZoneType.TERMINAL, 40.4890, -80.2310, 330);
        GeoNode t_W_outer_N = new GeoNode("T-W-outer-N", ZoneType.TERMINAL, 40.4910, -80.2310, 330);

        // Circular Aerodrome
        GeoNode aCenter = new GeoNode("A-Center", ZoneType.AERODROME, 40.4900, -80.2365, 280);

        // Generate and connect nodes for the circular aerodrome outline
        List<GeoNode> aerodromeOutlineNodes = new ArrayList<>();
        for (int i = 0; i < AERODROME_OUTLINE_NODES; i++) {
            double angle = 2 * Math.PI * i / AERODROME_OUTLINE_NODES;
            double lat = aCenter.getLatitude() + AERODROME_RADIUS_DEGREES * Math.sin(angle);
            double lon = aCenter.getLongitude() + AERODROME_RADIUS_DEGREES * Math.cos(angle);
            GeoNode outlineNode = new GeoNode("A-Outline-" + i, ZoneType.AERODROME, lat, lon, 280);
            aerodromeOutlineNodes.add(outlineNode);

            // Connect the outline node to the center
            aCenter.addEdge(new GeoEdge(aCenter, outlineNode));
        }

        // Connect the outline nodes to each other to form the circle
        for (int i = 0; i < aerodromeOutlineNodes.size(); i++) {
            GeoNode currentNode = aerodromeOutlineNodes.get(i);
            GeoNode nextNode = aerodromeOutlineNodes.get((i + 1) % aerodromeOutlineNodes.size());
            currentNode.addEdge(new GeoEdge(currentNode, nextNode));
        }

        // Property line (unchanged)
        GeoNode pNW = new GeoNode("P-NW", ZoneType.PROPERTY_LINE, 40.4945, -80.2460, 285);
        GeoNode pNE = new GeoNode("P-NE", ZoneType.PROPERTY_LINE, 40.4945, -80.2290, 285);
        GeoNode pSE = new GeoNode("P-SE", ZoneType.PROPERTY_LINE, 40.4870, -80.2290, 285);
        GeoNode pSW = new GeoNode("P-SW", ZoneType.PROPERTY_LINE, 40.4870, -80.2460, 285);

        // Hotspots
        GeoNode[] hotspots = new GeoNode[30];
        for (int i = 0; i < 30; i++) {
            double lat, lon, alt;
            boolean isValid;
            do {
                lat = 40.4880 + (Math.random() * 0.007);
                lon = -80.2450 + (Math.random() * 0.015);
                alt = 300 + (Math.random() * 20);

                // Check if within property line
                boolean withinPropertyLine = (lat >= pSW.getLatitude() && lat <= pNW.getLatitude()) &&
                        (lon >= pSW.getLongitude() && lon <= pNE.getLongitude());

                // Check if within the circular aerodrome
                boolean withinAerodrome = isWithinAerodrome(lat, lon, aCenter);

                // Check if within the U-shaped terminal
                boolean withinTerminal = isWithinTerminal(lat, lon);

                if (withinTerminal) {
                    alt = 340;
                }

                // Location is valid if it's within the property line and NOT within the aerodrome
                // Additionally, if it's within the terminal, its altitude must be at least 340m
                isValid = withinPropertyLine && !withinAerodrome;
            } while (!isValid);

            hotspots[i] = new GeoNode("H" + (i + 1), ZoneType.HOTSPOT, lat, lon, alt);
        }

        // Add all nodes to the list
        nodes.addAll(List.of(t_N_outer, t_N_inner, t_E_inner_N, t_E_inner_S, t_S_inner, t_S_outer, t_W_outer_S, t_W_outer_N, aCenter, pNW, pNE, pSE, pSW));
        nodes.addAll(aerodromeOutlineNodes);
        nodes.addAll(Arrays.asList(hotspots));

        // Connect terminal nodes to form the U-shape (opening to the east)
        t_N_outer.addEdge(new GeoEdge(t_N_outer, t_W_outer_N));
        t_W_outer_N.addEdge(new GeoEdge(t_W_outer_N, t_W_outer_S));
        t_W_outer_S.addEdge(new GeoEdge(t_W_outer_S, t_S_outer));
        t_S_outer.addEdge(new GeoEdge(t_S_outer, t_S_inner));
        t_S_inner.addEdge(new GeoEdge(t_S_inner, t_E_inner_S));
        t_E_inner_S.addEdge(new GeoEdge(t_E_inner_S, t_E_inner_N));
        t_E_inner_N.addEdge(new GeoEdge(t_E_inner_N, t_N_inner));
        t_N_inner.addEdge(new GeoEdge(t_N_inner, t_N_outer));

        // Connect property line
        pNW.addEdge(new GeoEdge(pNW, pNE));
        pNE.addEdge(new GeoEdge(pNE, pSE));
        pSE.addEdge(new GeoEdge(pSE, pSW));
        pSW.addEdge(new GeoEdge(pSW, pNW));

        return nodes;
    }
}