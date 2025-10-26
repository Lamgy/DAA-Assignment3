package metrics;

import graph.Edge;
import java.io.*;
import java.util.*;

public class Metrics {
    private String algorithm;
    private int graphId;
    private int vertices;
    private int edges;
    private double totalCost;
    private long operations;
    private double executionTimeMs;
    private boolean connected;
    private List<Edge> mstEdges;

    public Metrics(String algorithm, int graphId, int vertices, int edges,
                   double totalCost, long operations, double executionTimeMs,
                   boolean connected, List<Edge> mstEdges) {
        this.algorithm = algorithm;
        this.graphId = graphId;
        this.vertices = vertices;
        this.edges = edges;
        this.totalCost = totalCost;
        this.operations = operations;
        this.executionTimeMs = executionTimeMs;
        this.connected = connected;
        this.mstEdges = mstEdges;
    }

    public String getAlgorithm() { return algorithm; }
    public int getGraphId() { return graphId; }
    public double getTotalCost() { return totalCost; }
    public long getOperations() { return operations; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public int getVertices() { return vertices; }
    public int getEdges() { return edges; }
    public List<Edge> getMstEdges() { return mstEdges; }

    public static void exportToCSV(List<Metrics> metricsList, String csvPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("algorithm,graph_id,vertices,edges,total_cost,operations,execution_time_ms");
            for (Metrics m : metricsList) {
                writer.write(String.format(java.util.Locale.US, "%s,%d,%d,%d,%.2f,%d,%.3f\n",
                        m.algorithm,
                        m.graphId,
                        m.vertices,
                        m.edges,
                        m.totalCost,
                        m.operations,
                        m.executionTimeMs
                ));
            }
        }
    }
}
