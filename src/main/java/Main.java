import algorithms.*;
import graph.*;
import json.*;
import metrics.*;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        String[] inputFiles = {
                "data/input/input.json",
                "data/input/input_extralarge.json"
        };

        String outputJson = "data/output/output.json";
        String csvPath = "data/output/result.csv";

        List<Metrics> primResults = new ArrayList<>();
        List<Metrics> kruskalResults = new ArrayList<>();

        for (String inputPath : inputFiles) {
            try {
                System.out.println("Reading " + inputPath);
                List<Graph> graphs = JsonReader.readGraphs(inputPath);

                for (Graph g : graphs) {
                    Metrics primM = Prim.run(g);
                    Metrics kruskalM = Kruskal.run(g);
                    primResults.add(primM);
                    kruskalResults.add(kruskalM);
                }

            } catch (Exception e) {
                System.err.println("Error " + inputPath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            JsonWriter.writeResults(primResults, kruskalResults, outputJson);

            List<Metrics> all = new ArrayList<>();
            all.addAll(primResults);
            all.addAll(kruskalResults);
            Metrics.exportToCSV(all, csvPath);

            System.out.println("Output saved to " + outputJson);
            System.out.println("Metrics saved to " + csvPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
