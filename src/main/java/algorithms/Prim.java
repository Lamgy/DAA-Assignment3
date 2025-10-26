package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public class Prim {

    public static Metrics run(Graph graph) {
        Map<String, List<Edge>> adj = graph.getAdjacencyList();
        Set<String> visited = new HashSet<>();
        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0;
        long operations = 0;

        String start = graph.getNodes().get(0);
        visited.add(start);
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
        pq.addAll(adj.get(start));

        long startTime = System.nanoTime();

        while (!pq.isEmpty() && mstEdges.size() < graph.getNodes().size() - 1) {
            Edge edge = pq.poll();
            operations++;
            if (visited.contains(edge.getTo())) continue;
            visited.add(edge.getTo());
            mstEdges.add(edge);
            totalCost += edge.getWeight();
            for (Edge next : adj.get(edge.getTo())) {
                if (!visited.contains(next.getTo())) pq.add(next);
            }
        }

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        boolean connected = mstEdges.size() == graph.getNodes().size() - 1;

        return new Metrics("Prim", graph.getId(), graph.getNodes().size(), graph.getEdges().size(),
                totalCost, operations, timeMs, connected, mstEdges);
    }
}
