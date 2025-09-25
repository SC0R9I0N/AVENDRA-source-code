import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.List;
import javafx.scene.text.Text;
import static java.lang.Math.abs;


public class GraphVisualization {
    public static Group render(List<GeoNode> nodes) {
        Group root = new Group();

        // step 1: compute bounds of graph
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

        for (GeoNode node : nodes) {
            minLat = Math.min(minLat, abs(node.getLatitude()));
            maxLat = Math.max(maxLat, abs(node.getLatitude()));
            minLon = Math.min(minLon, abs(node.getLongitude()));
            maxLon = Math.max(maxLon, abs(node.getLongitude()));
        }

        // step 2: add buffer to clearly see property line
        double latBuffer = (maxLat - minLat) * 0.1;
        double lonBuffer = (maxLon - minLon) * 0.1;
        minLat -= latBuffer;
        maxLat += latBuffer;
        minLon -= lonBuffer;
        maxLon += lonBuffer;

        // step 3: define canvas size
        double canvasWidth = 800;
        double canvasHeight = 600;

        // Create a single Text node to display the altitude
        Text altitudeText = new Text();
        altitudeText.setVisible(false);
        altitudeText.setFill(Color.BLACK);

        // step 4: draw nodes
        for (GeoNode node : nodes) {
            double x = ((abs(node.getLongitude()) - minLon) / (maxLon - minLon) * canvasWidth);
            double y = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;

            Circle circle = new Circle(x, y, 5);
            circle.setFill(switch (node.getZone()) {
                case TERMINAL -> Color.MEDIUMPURPLE;
                case AERODROME -> Color.BLUE;
                case PROPERTY_LINE -> Color.HOTPINK;
                case HOTSPOT -> Color.LIMEGREEN;
            });

            // Add event handlers for mouse hover
            circle.setOnMouseEntered(e -> {
                // Set the text to show both ID and Altitude
                altitudeText.setText(String.format("ID: %s, Alt: %.1fm", node.getId(), node.getAltitude()));

                // Position text slightly to the left and above the node
                altitudeText.setX(x - 50);
                altitudeText.setY(y - 15);
                altitudeText.setVisible(true);
            });

            circle.setOnMouseExited(e -> {
                altitudeText.setVisible(false);
            });

            root.getChildren().add(circle);
        }

        // step 5: draw edges
        for (GeoNode node : nodes) {
            for (GeoEdge edge : node.getEdges()) {
                GeoNode target = edge.getTarget();
                double x1 = ((abs(node.getLongitude()) - minLon) / (maxLon - minLon)) * canvasWidth;
                double y1 = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;
                double x2 = ((abs(edge.getTarget().getLongitude()) - minLon) / (maxLon - minLon)) * canvasWidth;
                double y2 = ((maxLat - edge.getTarget().getLatitude()) / (maxLat - minLat)) * canvasHeight;


                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(switch (node.getZone()) {
                    case TERMINAL -> Color.MEDIUMPURPLE;
                    case AERODROME -> Color.BLUE;
                    case PROPERTY_LINE -> Color.HOTPINK;
                    case HOTSPOT -> Color.WHITE;
                });
                line.getStrokeDashArray().addAll(5.0, 5.0);
                root.getChildren().add(line);
            }
        }

        root.getChildren().add(altitudeText);

        return root;
    }
}
