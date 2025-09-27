//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  GraphVisualization.java
//
//  This driver class implements the code and methods
//  necessary for building the JavaFX visualization for the
//  graph defined in DroneRoutingDemo.java. This class
//  contains a single method, render(), that is responsible
//  for defining the bounds of the visualization window,
//  define mouse events, scale the graph to properly fit in
//  the window, and properly draw the graph. The one issue
//  with this class is that it is specifically designed
//  to accurately draw this graph with these longitude and
//  latitude values. The use of abs(node.getLongitude()) and
//  abs(node.getLatitude()) will not properly work with all
//  inputs of Longitude and Latitude.
//************************************************************

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.List;
import javafx.scene.text.Text;
import static java.lang.Math.abs;

/**
 * Handles the graphical rendering of the GeoNode graph using JavaFX.
 * It is responsible for scaling the geographic coordinates to screen
 * coordinates, drawing all nodes/edges, and handling interactive
 * display of node data.
 */
public class GraphVisualization {

    /**
     * Renders the visualization into a JavaFX Group object.
     *
     * @param nodes List of all GeoNodes to be visualized.
     * @return JavaFX Group object containing all graphical elements.
     */
    public static Group render(List<GeoNode> nodes) {
        Group root = new Group();

        // Determine min/max extent of map data
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

        // Compute bounds of graph
        for (GeoNode node : nodes) {
            // WARNING: use of Math.abs() is incorrect for general map data
            // and is specifically used for this implementation
            minLat = Math.min(minLat, abs(node.getLatitude()));
            maxLat = Math.max(maxLat, abs(node.getLatitude()));
            minLon = Math.min(minLon, abs(node.getLongitude()));
            maxLon = Math.max(maxLon, abs(node.getLongitude()));
        }

        // Add a 10% buffer to the bounds for padding
        double latBuffer = (maxLat - minLat) * 0.1;
        double lonBuffer = (maxLon - minLon) * 0.1;
        minLat -= latBuffer;
        maxLat += latBuffer;
        minLon -= lonBuffer;
        maxLon += lonBuffer;

        // Define fixed canvas size for graph rendering
        double canvasWidth = 800;
        double canvasHeight = 600;

        // Create Text node to display altitude/ID data
        Text altitudeText = new Text();
        altitudeText.setVisible(false);
        altitudeText.setFill(Color.BLACK);

        // Draw nodes and set up interactivity
        for (GeoNode node : nodes) {
            // Calculates X and Y positions relative to canvas width and height respectively
            // WARNING: Math.abs() again incorrect for general map data
            double x = ((abs(node.getLongitude()) - minLon) / (maxLon - minLon) * canvasWidth);
            double y = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;

            // Set node color based on zone type
            Circle circle = new Circle(x, y, 5);
            circle.setFill(switch (node.getZone()) {
                case TERMINAL -> Color.MEDIUMPURPLE;
                case AERODROME -> Color.BLUE;
                case PROPERTY_LINE -> Color.HOTPINK;
                case HOTSPOT -> Color.LIMEGREEN;
            });

            // Mouse Enter event: display altitude and ID
            circle.setOnMouseEntered(e -> {
                altitudeText.setText(String.format("ID: %s, Alt: %.1fm", node.getId(), node.getAltitude()));

                // Position slightly to the left and above the node
                altitudeText.setX(x - 50);
                altitudeText.setY(y - 15);
                altitudeText.setVisible(true);
            });

            // Mouse Exit event: hide altitude and ID
            circle.setOnMouseExited(e -> {
                altitudeText.setVisible(false);
            });

            root.getChildren().add(circle);
        }

        // Draw Edges
        for (GeoNode node : nodes) {
            for (GeoEdge edge : node.getEdges()) {
                GeoNode target = edge.getTarget();

                // Calculate starting point
                double x1 = ((abs(node.getLongitude()) - minLon) / (maxLon - minLon)) * canvasWidth;
                double y1 = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;

                // Calculate ending point
                double x2 = ((abs(edge.getTarget().getLongitude()) - minLon) / (maxLon - minLon)) * canvasWidth;
                double y2 = ((maxLat - edge.getTarget().getLatitude()) / (maxLat - minLat)) * canvasHeight;

                // Set edge color based on origin zone
                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(switch (node.getZone()) {
                    case TERMINAL -> Color.MEDIUMPURPLE;
                    case AERODROME -> Color.BLUE;
                    case PROPERTY_LINE -> Color.HOTPINK;
                    case HOTSPOT -> Color.GOLD;
                });
                // Dashed line
                line.getStrokeDashArray().addAll(5.0, 5.0);
                root.getChildren().add(line);
            }
        }

        // Add altitude text last to ensure it is rendered on top of other elements
        root.getChildren().add(altitudeText);

        return root;
    }
}