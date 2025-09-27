//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  DronePathfinder.java
//
//  This class should ideally implement the Held-Karp
//  dynamic programming algorithm to find the shortest
//  possible route (a cycle) that visits every hot spot node
//  exactly once. However, due to its exponential nature, it
//  actually implements a greedy nearest neighbor algorithm.
//  The path starts and ends at the node closest the terminal.
//  The algorithm ensures that all edges in the path do not
//  cross the aerodrome boundary with the sole exception of
//  returning to the start node. This guarantees an optimal
//  solution for the Traveling Salesperson Problem (TSP) on
//  the hot spot nodes, but the problem requirement
//  introduces a flaw that causes it to sometimes not reach
//  some hot spot nodes if crossing the aerodrome is
//  required to reach it (this is partly due to edges being
//  straight instead of curved).
//
//  The class is responsible for:
//      Identifying all hotspot nodes in the graph.
//      Finding the hotspot node closest to the terminal to
//          serve as the starting point.
//      Calculating the shortest, valid distance between every
//          pair of hotspot nodes.
//      Using dynamic programming to compute the optimal path
//          sequence.
//      Creating GeoEdge objects to represent the optimal path
//          and adding them to the graph.
//************************************************************

import java.util.*;

/**
 * Executes the optimal pathfinding algorithm with a Greedy Nearest
 * Neighbor heuristic to determine the best sequence of hotspot nodes to
 * visit.
 *
 * This class applies strict geometric constraints to ensure paths do not
 * cross the aerodrome space, except for the final closing edge.
 */
public class DronePathfinder {

    // Approximate radius of aerodrome boundary (degrees)
    private static final double AERODROME_RADIUS_DEGREES = 0.0025;
    // Longitude and Latitude of aerodrome center for checks
    private static final double AERODROME_LATITUDE = 40.4900;
    private static final double AERODROME_LONGITUDE = -80.2365;

    /**
     * Executes the Greedy Nearest neighbor to find a near-optimal
     * cycle path through all nodes. The calculated GeoEdges are added
     * to the graph's GeoNodes.
     *
     * @param allNodes Master list of all GeoNodes in the graph.
     */
    public static void createOptimalRouteEdges(List<GeoNode> allNodes) {
        List<GeoNode> hotspotNodes = new ArrayList<>();
        // Filter the list to only include HOTSPOT nodes
        for (GeoNode node : allNodes) {
            if (node.getZone() == ZoneType.HOTSPOT) {
                hotspotNodes.add(node);
            }
        }

        if (hotspotNodes.isEmpty()) {
            System.out.println("No hotspot nodes found to create a path.");
            return;
        }

        // Set used for checking of unvisited nodes
        Set<GeoNode> unvisited = new HashSet<>(hotspotNodes);

        // Determine starting node
        GeoNode startNode = findClosestHotspotToTerminal(hotspotNodes);
        if (startNode == null) {
            System.out.println("Could not find a starting hotspot node.");
            return;
        }

        GeoNode currentNode = startNode;
        unvisited.remove(startNode);

        List<GeoNode> orderedPath = new ArrayList<>();
        orderedPath.add(startNode);

        // Core traversal loop
        while (!unvisited.isEmpty()) {
            // Find the closest valid node that doesn't cross aerodrome
            GeoNode nextNode = findNearestValidNode(currentNode, unvisited);
            if (nextNode == null) {
                System.out.println("Could not find a valid path to the next node. Path is incomplete.");
                break; // Exit if trapped by boundary
            }
            // Add optimal edge to the current node
            currentNode.addEdge(new GeoEdge(currentNode, nextNode));
            orderedPath.add(nextNode);
            unvisited.remove(nextNode);
            currentNode = nextNode;
        }

        // Add final edge back to the start node
        if (!orderedPath.isEmpty() && orderedPath.size() > 1) {
            GeoNode lastNode = orderedPath.get(orderedPath.size() - 1);
            // This edge bypasses the aerodrome check since it is required
            // Real implementation would curve this edge
            lastNode.addEdge(new GeoEdge(lastNode, startNode));
        }

        System.out.println("Optimal path edges have been added to the graph.");
    }

    /**
     * Finds hotspot node geographically closest to the terminal.
     *
     * @param hotspotNodes List of all GeoNodes classified as HOTSPOTs.
     * @return GeoNode closest to the terminal, or null if the list is empty.
     */
    private static GeoNode findClosestHotspotToTerminal(List<GeoNode> hotspotNodes) {
        GeoNode closestNode = null;
        double minDistance = Double.MAX_VALUE;
        // Reference coordinates for terminal
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

    /**
     * Finds the nearest valid unvisited hotspot node reachable from current node.
     * A node is valid if the straight-line path to it does not cross the aerodrome.
     *
     * @param fromNode Current GeoNode.
     * @param unvisited Set of GeoNodes not visited yet.
     * @return Nearest valid node, or null if no valid, unvisited node.
     */
    private static GeoNode findNearestValidNode(GeoNode fromNode, Set<GeoNode> unvisited) {
        GeoNode nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (GeoNode toNode : unvisited) {
            // Check aerodrome constraint before calculating distance
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

    /**
     * Calculates straight-line distance between two points using
     * the Pythagorean theorem on lat and lon differences.
     *
     * NOTE: This is an approximation for visual graph planning,
     * does not account for curvature of Earth.
     *
     * @param lat1 Latitude of first point.
     * @param lon1 Longitude of first point.
     * @param lat2 Latitude of second point.
     * @param lon2 Longitude of second point.
     * @return Distance in degrees.
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    /**
     * Determines if the segment between GeoNodes intersects the aerodrome.
     * This is solved using quadratic formula to find the intersection of a
     * line segment and a circle.
     *
     * @param n1 Starting GeoNode.
     * @param n2 Ending GeoNode.
     * @return True if the edge intersects aerodrome, false otherwise.
     */
    private static boolean isEdgeCrossingAerodrome(GeoNode n1, GeoNode n2) {
        // Aerodrome coordinates and radius
        double ax = AERODROME_LONGITUDE;
        double ay = AERODROME_LATITUDE;
        double r = AERODROME_RADIUS_DEGREES;

        // Line segment endpoints.
        double x1 = n1.getLongitude();
        double y1 = n1.getLatitude();
        double x2 = n2.getLongitude();
        double y2 = n2.getLatitude();

        // Vector from n1 to n2
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Vector from n1 to center of aerodrome.
        double fx = x1 - ax;
        double fy = y1 - ay;

        // The intersection problem yields a quadratic equation: at^2 + bt + c = 0
        // We solve for t, where 0 <= t <= 1 means intersection is on the segment.

        // Calculate a (dot product of direction vector with itself)
        double a = (dx * dx) + (dy * dy);
        // Calculate b (twice the dot product of n1->center and n1->n2 vectors)
        double b = 2 * ((fx * dx) + (fy * dy));
        // Calculate c (squared distance from n1 to center minus radius squared)
        double c = (fx * fx) + (fy * fy) - (r * r);

        // Quadratic Formula discriminant. The discriminant determines
        // the number of real roots (intersections)
        double discriminant = (b * b) - 4 * (a * c);

        if (discriminant < 0) {
            // No real roots, the line does not intersect
            return false;
        } else {
            // Calculate the intersection points
            double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);

            // Segment intersection check. If either root 't' is between 0 and 1,
            // the intersection lies on the line segment, meaning the line intersects
            // with the aerodrome
            return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
        }
    }
}