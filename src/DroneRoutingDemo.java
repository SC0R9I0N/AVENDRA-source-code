//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  DroneRoutingDemo.java
//
//  This is the main driver class of the project. This class
//  accomplishes the following:
//      Starts the JavaFX visualization
//      Updates the visualization after button press
//      Deploy the key and its items on the visualization
//      Create the graph of GeoNodes and GeoEdges under
//          specific constraints defined therein
//
//  This file is responsible for everything the user ends up
//  seeing in the JavaFX visualization, as well as
//  maintaining the graph's nodes and edges according to
//  any method calls. This is the main file that would need
//  to be edited when surveying an airport to create the
//  optimal drone path. All coordinates would be logged here
//  to be added to a graph.
//
//  This class is limited by the shapes of various structures.
//  The calculation to determine anything related with the
//  aerodrome, property line, or terminal would need to be
//  updated based on the shape they take. The code in this
//  project assumes that:
//      The property line is a rectangle
//      The aerodrome is an oval/circle
//      The terminal is a U shape rotated 90 degrees right
//************************************************************

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

    /**
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        this.nodes = buildGraph();
        this.root = new BorderPane();

        updateVisualization();

        VBox key = createKey();
        root.setRight(key);

        Button runPathButton = new Button("Run Optimal Path");
        runPathButton.setOnAction(e -> {
            clearHotspotEdges();
            DronePathfinder.createOptimalRouteEdges(this.nodes);
            updateVisualization();
        });

        root.setBottom(runPathButton);

        Scene scene = new Scene(root, 950, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drone Routing Visualization");
        primaryStage.show();
    }

    /**
     *
     */
    private void updateVisualization() {
        Group graphGroup = GraphVisualization.render(this.nodes);
        root.setCenter(graphGroup);
    }

    /**
     *
     * @return
     */
    private VBox createKey() {
        VBox keyBox = new VBox(10);
        keyBox.setPadding(new Insets(20, 10, 10, 10));
        keyBox.setStyle("-fx-background-color: #333333;");

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

    /**
     *
     * @param label
     * @param color
     * @return
     */
    private VBox createKeyItem(String label, Color color) {
        Circle circle = new Circle(5);
        circle.setFill(color);
        Text text = new Text(label);
        text.setFill(Color.WHITE);

        VBox item = new VBox(5);
        item.getChildren().addAll(circle, text);
        return item;
    }

    /**
     *
     */
    private void clearHotspotEdges() {
        for (GeoNode node : nodes) {
            if (node.getZone() == ZoneType.HOTSPOT) {
                node.getEdges().clear();
            }
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param lat
     * @param lon
     * @param aCenter
     * @return
     */
    private boolean isWithinAerodrome(double lat, double lon, GeoNode aCenter) {
        double latDiff = lat - aCenter.getLatitude();
        double lonDiff = lon - aCenter.getLongitude();
        double distanceSquared = (latDiff * latDiff) + (lonDiff * lonDiff);
        return distanceSquared <= (AERODROME_RADIUS_DEGREES * AERODROME_RADIUS_DEGREES);
    }

    /**
     *
     * @param lat
     * @param lon
     * @return
     */
    private boolean isWithinTerminal(double lat, double lon) {
        boolean withinTopPart = (lat <= 40.4910 && lat >= 40.4905) &&
                (lon >= -80.2330 && lon <= -80.2310);
        boolean withinBottomPart = (lat <= 40.4895 && lat >= 40.4890) &&
                (lon >= -80.2330 && lon <= -80.2310);
        boolean withinVerticalPart = (lat <= 40.4910 && lat >= 40.4890) &&
                (lon >= -80.2330 && lon <= -80.2310);
        return withinTopPart || withinBottomPart || withinVerticalPart;
    }

    /**
     *
     * @return
     */
    private List<GeoNode> buildGraph() {
        List<GeoNode> nodes = new ArrayList<>();

        GeoNode t_N_outer = new GeoNode("T-N-outer", ZoneType.TERMINAL, 40.4910, -80.2330, 330);
        GeoNode t_N_inner = new GeoNode("T-N-inner", ZoneType.TERMINAL, 40.4905, -80.2330, 330);
        GeoNode t_E_inner_N = new GeoNode("T-E-inner-N", ZoneType.TERMINAL, 40.4905, -80.2315, 330);
        GeoNode t_E_inner_S = new GeoNode("T-E-inner-S", ZoneType.TERMINAL, 40.4895, -80.2315, 330);
        GeoNode t_S_inner = new GeoNode("T-S-inner", ZoneType.TERMINAL, 40.4895, -80.2330, 330);
        GeoNode t_S_outer = new GeoNode("T-S-outer", ZoneType.TERMINAL, 40.4890, -80.2330, 330);
        GeoNode t_W_outer_S = new GeoNode("T-W-outer-S", ZoneType.TERMINAL, 40.4890, -80.2310, 330);
        GeoNode t_W_outer_N = new GeoNode("T-W-outer-N", ZoneType.TERMINAL, 40.4910, -80.2310, 330);

        GeoNode aCenter = new GeoNode("A-Center", ZoneType.AERODROME, 40.4900, -80.2365, 280);

        List<GeoNode> aerodromeOutlineNodes = new ArrayList<>();
        for (int i = 0; i < AERODROME_OUTLINE_NODES; i++) {
            double angle = 2 * Math.PI * i / AERODROME_OUTLINE_NODES;
            double lat = aCenter.getLatitude() + AERODROME_RADIUS_DEGREES * Math.sin(angle);
            double lon = aCenter.getLongitude() + AERODROME_RADIUS_DEGREES * Math.cos(angle);
            GeoNode outlineNode = new GeoNode("A-Outline-" + i, ZoneType.AERODROME, lat, lon, 280);
            aerodromeOutlineNodes.add(outlineNode);

            aCenter.addEdge(new GeoEdge(aCenter, outlineNode));
        }

        for (int i = 0; i < aerodromeOutlineNodes.size(); i++) {
            GeoNode currentNode = aerodromeOutlineNodes.get(i);
            GeoNode nextNode = aerodromeOutlineNodes.get((i + 1) % aerodromeOutlineNodes.size());
            currentNode.addEdge(new GeoEdge(currentNode, nextNode));
        }

        GeoNode pNW = new GeoNode("P-NW", ZoneType.PROPERTY_LINE, 40.4945, -80.2460, 285);
        GeoNode pNE = new GeoNode("P-NE", ZoneType.PROPERTY_LINE, 40.4945, -80.2290, 285);
        GeoNode pSE = new GeoNode("P-SE", ZoneType.PROPERTY_LINE, 40.4870, -80.2290, 285);
        GeoNode pSW = new GeoNode("P-SW", ZoneType.PROPERTY_LINE, 40.4870, -80.2460, 285);

        GeoNode[] hotspots = new GeoNode[30];
        for (int i = 0; i < 30; i++) {
            double lat, lon, alt;
            boolean isValid;
            do {
                lat = 40.4880 + (Math.random() * 0.007);
                lon = -80.2450 + (Math.random() * 0.015);
                alt = 300 + (Math.random() * 20);

                boolean withinPropertyLine = (lat >= pSW.getLatitude() && lat <= pNW.getLatitude()) &&
                        (lon >= pSW.getLongitude() && lon <= pNE.getLongitude());

                boolean withinAerodrome = isWithinAerodrome(lat, lon, aCenter);

                boolean withinTerminal = isWithinTerminal(lat, lon);

                if (withinTerminal) {
                    alt = 340;
                }

                isValid = withinPropertyLine && !withinAerodrome;
            } while (!isValid);

            hotspots[i] = new GeoNode("H" + (i + 1), ZoneType.HOTSPOT, lat, lon, alt);
        }

        nodes.addAll(List.of(t_N_outer, t_N_inner, t_E_inner_N, t_E_inner_S, t_S_inner,
                t_S_outer, t_W_outer_S, t_W_outer_N, aCenter, pNW, pNE, pSE, pSW));
        nodes.addAll(aerodromeOutlineNodes);
        nodes.addAll(Arrays.asList(hotspots));

        t_N_outer.addEdge(new GeoEdge(t_N_outer, t_W_outer_N));
        t_W_outer_N.addEdge(new GeoEdge(t_W_outer_N, t_W_outer_S));
        t_W_outer_S.addEdge(new GeoEdge(t_W_outer_S, t_S_outer));
        t_S_outer.addEdge(new GeoEdge(t_S_outer, t_S_inner));
        t_S_inner.addEdge(new GeoEdge(t_S_inner, t_E_inner_S));
        t_E_inner_S.addEdge(new GeoEdge(t_E_inner_S, t_E_inner_N));
        t_E_inner_N.addEdge(new GeoEdge(t_E_inner_N, t_N_inner));
        t_N_inner.addEdge(new GeoEdge(t_N_inner, t_N_outer));

        pNW.addEdge(new GeoEdge(pNW, pNE));
        pNE.addEdge(new GeoEdge(pNE, pSE));
        pSE.addEdge(new GeoEdge(pSE, pSW));
        pSW.addEdge(new GeoEdge(pSW, pNW));

        return nodes;
    }
}