package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public class Kruskal {

    public static Metrics run(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);

        Map<String, String> parent = new HashMap<>();
        for (String node : graph.getNodes()) parent.put(node, node);

        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0;
        long operations = 0;

        long startTime = System.nanoTime();

        for (Edge e : edges) {
            String root1 = find(parent, e.getFrom());
            String root2 = find(parent, e.getTo());
            operations++;
            if (!root1.equals(root2)) {
                mstEdges.add(e);
                totalCost += e.getWeight();
                union(parent, root1, root2);
            }
        }

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        boolean connected = mstEdges.size() == graph.getNodes().size() - 1;

        return new Metrics("Kruskal", graph.getId(), graph.getNodes().size(), graph.getEdges().size(),
                totalCost, operations, timeMs, connected, mstEdges);
    }

    private static String find(Map<String, String> parent, String node) {
        if (!parent.get(node).equals(node))
            parent.put(node, find(parent, parent.get(node)));
        return parent.get(node);
    }

    private static void union(Map<String, String> parent, String x, String y) {
        parent.put(find(parent, x), find(parent, y));
    }
}
