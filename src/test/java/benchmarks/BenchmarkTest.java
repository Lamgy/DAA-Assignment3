package benchmarks;

import algorithms.*;
import graph.*;
import json.*;
import metrics.*;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkTest {
    private static final int ITERATIONS = 30;
    private static final int INNER_RUNS = 50;
    private static final int WARMUP = 5;

    private static final String[] INPUT_FILES = {
            "data/input/input.json",
            "data/input/input_extralarge.json"
    };

    private static final String OUTPUT_CSV = "data/output/benchmark_results.csv";

    @Test
    public void benchmarkMSTAlgorithms() {
        List<Result> results = new ArrayList<>();

        for (String inputPath : INPUT_FILES) {
            System.out.println("\nBenchmarking from: " + inputPath);
            try {
                List<Graph> graphs = JsonReader.readGraphs(inputPath);

                for (Graph g : graphs) {
                    System.out.printf("Graph #%d (V=%d, E=%d)%n",
                            g.getId(), g.getNodes().size(), g.getEdges().size());

                    for (int i = 0; i < WARMUP; i++) {
                        Prim.run(g);
                        Kruskal.run(g);
                    }

                    List<Double> primTimes = new ArrayList<>();
                    List<Double> kruskalTimes = new ArrayList<>();
                    List<Long> primOps = new ArrayList<>();
                    List<Long> kruskalOps = new ArrayList<>();

                    for (int i = 0; i < ITERATIONS; i++) {
                        long startPrim = System.nanoTime();
                        long totalOpsPrim = 0;
                        for (int j = 0; j < INNER_RUNS; j++) {
                            Metrics m = Prim.run(g);
                            totalOpsPrim += m.getOperations();
                        }
                        double primTime = (System.nanoTime() - startPrim) / 1_000_000.0 / INNER_RUNS;
                        primTimes.add(primTime);
                        primOps.add(totalOpsPrim / INNER_RUNS);

                        long startKruskal = System.nanoTime();
                        long totalOpsKruskal = 0;
                        for (int j = 0; j < INNER_RUNS; j++) {
                            Metrics m = Kruskal.run(g);
                            totalOpsKruskal += m.getOperations();
                        }
                        double kruskalTime = (System.nanoTime() - startKruskal) / 1_000_000.0 / INNER_RUNS;
                        kruskalTimes.add(kruskalTime);
                        kruskalOps.add(totalOpsKruskal / INNER_RUNS);
                    }

                    double primAvg = avg(primTimes);
                    double kruskalAvg = avg(kruskalTimes);
                    double primOpsAvg = avg(primOps);
                    double kruskalOpsAvg = avg(kruskalOps);

                    results.add(new Result(
                            g.getId(), g.getNodes().size(), g.getEdges().size(),
                            primAvg, kruskalAvg,
                            primOpsAvg, kruskalOpsAvg
                    ));

                    System.out.printf(Locale.US,
                            "Prim: %.4f ms (ops=%.0f) | Kruskal: %.4f ms (ops=%.0f)%n",
                            primAvg, primOpsAvg, kruskalAvg, kruskalOpsAvg);

                    assertTrue(primAvg > 0 && kruskalAvg > 0, "Invalid timing result.");
                }

            } catch (Exception e) {
                System.err.println("Error " + inputPath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        exportCSV(results);
    }

    private static double avg(List<? extends Number> list) {
        return list.stream().mapToDouble(Number::doubleValue).average().orElse(0);
    }

    private static void exportCSV(List<Result> results) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(OUTPUT_CSV))) {
            pw.println("GraphID,Nodes,Edges,PrimAvgMs,KruskalAvgMs,PrimOps,KruskalOps");
            for (Result r : results) {
                pw.printf(Locale.US,
                        "%d,%d,%d,%.4f,%.4f,%.0f,%.0f%n",
                        r.id, r.nodes, r.edges,
                        r.primAvg, r.kruskalAvg,
                        r.primOps, r.kruskalOps);
            }
            System.out.println("\nBenchmark results saved to " + OUTPUT_CSV);
        } catch (Exception e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }

    private static class Result {
        int id, nodes, edges;
        double primAvg, kruskalAvg;
        double primOps, kruskalOps;

        Result(int id, int nodes, int edges,
               double primAvg, double kruskalAvg,
               double primOps, double kruskalOps) {
            this.id = id;
            this.nodes = nodes;
            this.edges = edges;
            this.primAvg = primAvg;
            this.kruskalAvg = kruskalAvg;
            this.primOps = primOps;
            this.kruskalOps = kruskalOps;
        }
    }
}
