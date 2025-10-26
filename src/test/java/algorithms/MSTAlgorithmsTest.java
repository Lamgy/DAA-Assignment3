package algorithms;

import graph.*;
import metrics.Metrics;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MSTAlgorithmsTest {

    private Graph smallGraph;
    private Graph disconnectedGraph;

    @BeforeEach
    void setup() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 4),
                new Edge("A", "C", 3),
                new Edge("B", "C", 2),
                new Edge("B", "D", 5),
                new Edge("C", "D", 7),
                new Edge("C", "E", 8),
                new Edge("D", "E", 6)
        );
        smallGraph = new Graph(1, nodes, edges);

        List<String> nodes2 = Arrays.asList("A", "B", "C");
        List<Edge> edges2 = Arrays.asList(
                new Edge("A", "B", 5)
        );
        disconnectedGraph = new Graph(2, nodes2, edges2);
    }

    @Test
    void testTotalCostEquality() {
        Metrics prim = Prim.run(smallGraph);
        Metrics kruskal = Kruskal.run(smallGraph);

        assertEquals(prim.getTotalCost(), kruskal.getTotalCost(), 0.001,
                "Prim and Kruskal should yield identical MST total cost");
    }

    @Test
    void testMSTHasVMinusOneEdges() {
        Metrics prim = Prim.run(smallGraph);
        int expected = smallGraph.getNodes().size() - 1;
        assertEquals(expected, prim.getMstEdges().size(),
                "MST should contain V-1 edges");
    }

    @Test
    void testMSTIsConnected() {
        Metrics prim = Prim.run(smallGraph);
        assertTrue(isConnected(smallGraph.getNodes(), prim.getMstEdges()),
                "MST should connect all vertices");
    }

    @Test
    void testMSTIsAcyclic() {
        Metrics prim = Prim.run(smallGraph);
        assertFalse(hasCycle(prim.getMstEdges()),
                "MST must not contain cycles");
    }

    @Test
    void testDisconnectedGraphHandledGracefully() {
        Metrics prim = Prim.run(disconnectedGraph);
        Metrics kruskal = Kruskal.run(disconnectedGraph);

        assertFalse(prim.getVertices() - 1 == prim.getMstEdges().size() && prim.getTotalCost() > 0,
                "Prim should not find a valid MST for disconnected graphs");
        assertFalse(kruskal.getVertices() - 1 == kruskal.getMstEdges().size() && kruskal.getTotalCost() > 0,
                "Kruskal should not find a valid MST for disconnected graphs");
    }

    @Test
    void testExecutionTimeNonNegative() {
        Metrics prim = Prim.run(smallGraph);
        Metrics kruskal = Kruskal.run(smallGraph);

        assertTrue(prim.getExecutionTimeMs() >= 0, "Prim time must be non-negative");
        assertTrue(kruskal.getExecutionTimeMs() >= 0, "Kruskal time must be non-negative");
    }

    @Test
    void testOperationsCountNonNegative() {
        Metrics prim = Prim.run(smallGraph);
        Metrics kruskal = Kruskal.run(smallGraph);

        assertTrue(prim.getOperations() >= 0, "Prim ops must be non-negative");
        assertTrue(kruskal.getOperations() >= 0, "Kruskal ops must be non-negative");
    }

    @Test
    void testResultsReproducibility() {
        Metrics prim1 = Prim.run(smallGraph);
        Metrics prim2 = Prim.run(smallGraph);

        assertEquals(prim1.getTotalCost(), prim2.getTotalCost(), 0.0001,
                "Prim results must be reproducible");
        assertEquals(prim1.getMstEdges().size(), prim2.getMstEdges().size(),
                "Prim MST edge count should be reproducible");
    }

    private boolean isConnected(List<String> nodes, List<Edge> edges) {
        Map<String, List<String>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.getFrom()).add(e.getTo());
            adj.get(e.getTo()).add(e.getFrom());
        }

        Set<String> visited = new HashSet<>();
        dfs(nodes.get(0), adj, visited);
        return visited.size() == nodes.size();
    }

    private void dfs(String node, Map<String, List<String>> adj, Set<String> visited) {
        if (visited.contains(node)) return;
        visited.add(node);
        for (String next : adj.get(node)) dfs(next, adj, visited);
    }

    private boolean hasCycle(List<Edge> edges) {
        Map<String, String> parent = new HashMap<>();
        for (Edge e : edges) {
            String root1 = find(parent, e.getFrom());
            String root2 = find(parent, e.getTo());
            if (root1.equals(root2)) return true;
            union(parent, root1, root2);
        }
        return false;
    }

    private String find(Map<String, String> parent, String node) {
        parent.putIfAbsent(node, node);
        if (!parent.get(node).equals(node))
            parent.put(node, find(parent, parent.get(node)));
        return parent.get(node);
    }

    private void union(Map<String, String> parent, String a, String b) {
        parent.put(find(parent, a), find(parent, b));
    }
}
