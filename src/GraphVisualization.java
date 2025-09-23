import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.List;


public class GraphVisualization {
    public static Group render(List<GeoNode> nodes) {
        Group root = new Group();

        // step 1: compute bounds of graph
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

        for (GeoNode node : nodes) {
            minLat = Math.min(minLat, node.getLatitude());
            maxLat = Math.max(maxLat, node.getLatitude());
            minLon = Math.min(minLon, node.getLongitude());
            maxLon = Math.max(maxLon, node.getLongitude());
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

        // step 4: draw nodes
        for (GeoNode node : nodes) {
            double x = ((node.getLongitude() - minLon) / (maxLon - minLon)) * canvasWidth;
            double y = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;

            Circle circle = new Circle(x, y, 5);
            circle.setFill(switch (node.getZone()) {
                case TERMINAL -> Color.RED;
                case AERODROME -> Color.BLUE;
                case PROPERTY_LINE -> Color.GREEN;
                case HOTSPOT -> Color.YELLOW;
            });
            root.getChildren().add(circle);
        }

        // step 5: draw edges
        for (GeoNode node : nodes) {
            for (GeoEdge edge : node.getEdges()) {
                GeoNode target = edge.getTarget();
                double x1 = ((node.getLongitude() - minLon) / (maxLon - minLon)) * canvasWidth;
                double y1 = ((maxLat - node.getLatitude()) / (maxLat - minLat)) * canvasHeight;
                double x2 = ((edge.getTarget().getLongitude() - minLon) / (maxLon - minLon)) * canvasWidth;
                double y2 = ((maxLat - edge.getTarget().getLatitude()) / (maxLat - minLat)) * canvasHeight;


                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(switch (node.getZone()) {
                    case TERMINAL -> Color.RED;
                    case AERODROME -> Color.BLUE;
                    case PROPERTY_LINE -> Color.GREEN;
                    case HOTSPOT -> Color.WHITE;
                });
                line.getStrokeDashArray().addAll(5.0, 5.0);
                root.getChildren().add(line);
            }
        }

        return root;
    }
}
