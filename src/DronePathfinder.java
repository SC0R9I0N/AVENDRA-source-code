import java.util.*;

public class DronePathfinder {

    // You will need a way to check if a path crosses the aerodrome.
    // This is the most complex part of the problem. A simple approximation
    // is to check if the midpoint of the line segment between two nodes
    // falls within the aerodrome circle.
    private static final double AERODROME_RADIUS_DEGREES = 0.0025;
    private static final double AERODROME_LATITUDE = 40.4900;
    private static final double AERODROME_LONGITUDE = -80.2365;

    public static List<GeoNode> findOptimalRoute(List<GeoNode> allNodes) {
        // Step 1: Filter to get only HOTSPOT nodes
        List<GeoNode> hotspotNodes = new ArrayList<>();
        for (GeoNode node : allNodes) {
            if (node.getZone() == ZoneType.HOTSPOT) {
                hotspotNodes.add(node);
            }
        }

        List<GeoNode> path = new ArrayList<>();
        Set<GeoNode> unvisited = new HashSet<>(hotspotNodes);

        // Find the hotspot node closest to the terminal to start the journey
        GeoNode startNode = findClosestHotspotToTerminal(hotspotNodes);
        if (startNode == null) return path;

        path.add(startNode);
        unvisited.remove(startNode);

        GeoNode currentNode = startNode;

        while (!unvisited.isEmpty()) {
            GeoNode nextNode = findNearestValidNode(currentNode, unvisited);
            if (nextNode == null) {
                // This indicates no valid path to the remaining hotspots exists.
                System.out.println("Could not find a valid path to the next node.");
                break;
            }
            path.add(nextNode);
            unvisited.remove(nextNode);
            currentNode = nextNode;
        }

        // Add the edge back to the start to complete the cycle, if a path was found
        if (!path.isEmpty() && path.size() > 1) {
            path.add(startNode);
        }

        return path;
    }

    /**
     * Finds the HOTSPOT node closest to the terminal's main access point.
     *
     * @param hotspotNodes The list of all hotspot nodes.
     * @return The GeoNode that is a hotspot and closest to the terminal, or null if no hotspots exist.
     */
    private static GeoNode findClosestHotspotToTerminal(List<GeoNode> hotspotNodes) {
        GeoNode closestNode = null;
        double minDistance = Double.MAX_VALUE;
        // Using the Terminal's eastern opening as a reference point
        double terminalLat = 40.4900;
        double terminalLon = -80.2315;

        for (GeoNode node : hotspotNodes) {
            double distance = calculateDistance(node.getLatitude(), node.getLongitude(), terminalLat, terminalLon);
            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node;
            }
        }
        return closestNode;
    }

    private static GeoNode findNearestValidNode(GeoNode fromNode, Set<GeoNode> unvisited) {
        GeoNode nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (GeoNode toNode : unvisited) {
            // Check if the edge crosses the aerodrome
            if (!isEdgeCrossingAerodrome(fromNode, toNode)) {
                double distance = calculateDistance(fromNode.getLatitude(), fromNode.getLongitude(),
                        toNode.getLatitude(), toNode.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = toNode;
                }
            }
        }
        return nearest;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    // Checks if the line segment between two nodes intersects the aerodrome circle.
    private static boolean isEdgeCrossingAerodrome(GeoNode n1, GeoNode n2) {
        double ax = AERODROME_LONGITUDE;
        double ay = AERODROME_LATITUDE;
        double r = AERODROME_RADIUS_DEGREES;

        // Line segment points
        double x1 = n1.getLongitude();
        double y1 = n1.getLatitude();
        double x2 = n2.getLongitude();
        double y2 = n2.getLatitude();

        // Vector from n1 to n2
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Vector from n1 to aerodrome center
        double fx = x1 - ax;
        double fy = y1 - ay;

        double a = (dx * dx) + (dy * dy);
        double b = 2 * ((fx * dx) + (fy * fy));
        double c = (fx * fx) + (fy * fy) - (r * r);

        // Use the quadratic formula discriminant to check for intersection
        double discriminant = (b * b) - 4 * (a * c);

        if (discriminant < 0) {
            return false;
        } else {
            // Check for valid t values (intersection within the line segment)
            double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);

            return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
        }
    }
}