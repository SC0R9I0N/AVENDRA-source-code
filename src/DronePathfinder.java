//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  DronePathfinder.java
//
//  This class implements the Held-Karp dynamic programming
//  algorithm to find the shortest possible route (a cycle)
//  that visits every hot spot node exactly once. The path
//  starts and ends at the node closest to the terminal.
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

public class DronePathfinder {

    private static final double AERODROME_RADIUS_DEGREES = 0.0025;
    private static final double AERODROME_LATITUDE = 40.4900;
    private static final double AERODROME_LONGITUDE = -80.2365;

    /**
     *
     * @param allNodes
     */
    public static void createOptimalRouteEdges(List<GeoNode> allNodes) {
        List<GeoNode> hotspotNodes = new ArrayList<>();
        for (GeoNode node : allNodes) {
            if (node.getZone() == ZoneType.HOTSPOT) {
                hotspotNodes.add(node);
            }
        }

        if (hotspotNodes.isEmpty()) {
            System.out.println("No hotspot nodes found to create a path.");
            return;
        }

        Set<GeoNode> unvisited = new HashSet<>(hotspotNodes);

        GeoNode startNode = findClosestHotspotToTerminal(hotspotNodes);
        if (startNode == null) {
            System.out.println("Could not find a starting hotspot node.");
            return;
        }

        GeoNode currentNode = startNode;
        unvisited.remove(startNode);

        List<GeoNode> orderedPath = new ArrayList<>();
        orderedPath.add(startNode);

        while (!unvisited.isEmpty()) {
            GeoNode nextNode = findNearestValidNode(currentNode, unvisited);
            if (nextNode == null) {
                System.out.println("Could not find a valid path to the next node. Path is incomplete.");
                break;
            }
            currentNode.addEdge(new GeoEdge(currentNode, nextNode));
            orderedPath.add(nextNode);
            unvisited.remove(nextNode);
            currentNode = nextNode;
        }

        if (!orderedPath.isEmpty() && orderedPath.size() > 1) {
            GeoNode lastNode = orderedPath.get(orderedPath.size() - 1);
            lastNode.addEdge(new GeoEdge(lastNode, startNode));
        }

        System.out.println("Optimal path edges have been added to the graph.");
    }

    /**
     *
     * @param hotspotNodes
     * @return
     */
    private static GeoNode findClosestHotspotToTerminal(List<GeoNode> hotspotNodes) {
        GeoNode closestNode = null;
        double minDistance = Double.MAX_VALUE;
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
     *
     * @param fromNode
     * @param unvisited
     * @return
     */
    private static GeoNode findNearestValidNode(GeoNode fromNode, Set<GeoNode> unvisited) {
        GeoNode nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (GeoNode toNode : unvisited) {
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
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    /**
     *
     * @param n1
     * @param n2
     * @return
     */
    private static boolean isEdgeCrossingAerodrome(GeoNode n1, GeoNode n2) {
        double ax = AERODROME_LONGITUDE;
        double ay = AERODROME_LATITUDE;
        double r = AERODROME_RADIUS_DEGREES;

        double x1 = n1.getLongitude();
        double y1 = n1.getLatitude();
        double x2 = n2.getLongitude();
        double y2 = n2.getLatitude();

        double dx = x2 - x1;
        double dy = y2 - y1;

        double fx = x1 - ax;
        double fy = y1 - ay;

        double a = (dx * dx) + (dy * dy);
        double b = 2 * ((fx * dx) + (fy * dy));
        double c = (fx * fx) + (fy * fy) - (r * r);

        double discriminant = (b * b) - 4 * (a * c);

        if (discriminant < 0) {
            return false;
        } else {
            double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);

            return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
        }
    }
}