package json;

import com.google.gson.*;
import graph.*;
import metrics.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonWriter {

    public static void writeResults(List<Metrics> primResults, List<Metrics> kruskalResults, String outputPath)
            throws IOException {

        JsonObject root = new JsonObject();
        JsonArray results = new JsonArray();

        for (int i = 0; i < primResults.size(); i++) {
            Metrics prim = primResults.get(i);
            Metrics kruskal = kruskalResults.get(i);

            JsonObject item = new JsonObject();
            item.addProperty("graph_id", prim.getGraphId());

            JsonObject stats = new JsonObject();
            stats.addProperty("vertices", prim.getVertices());
            stats.addProperty("edges", prim.getEdges());
            item.add("input_stats", stats);

            item.add("prim", buildAlgorithmBlock(prim));
            item.add("kruskal", buildAlgorithmBlock(kruskal));

            results.add(item);
        }

        root.add("results", results);

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(formatJson(root, 0));
        }
    }

    private static JsonObject buildAlgorithmBlock(Metrics m) {
        JsonObject obj = new JsonObject();
        JsonArray edges = new JsonArray();

        for (Edge e : m.getMstEdges()) {
            JsonObject edge = new JsonObject();
            edge.addProperty("from", e.getFrom());
            edge.addProperty("to", e.getTo());
            edge.addProperty("weight", e.getWeight());
            edges.add(edge);
        }

        obj.add("mst_edges", edges);
        obj.addProperty("total_cost", m.getTotalCost());
        obj.addProperty("operations_count", m.getOperations());
        obj.addProperty("execution_time_ms", m.getExecutionTimeMs());
        return obj;
    }

    private static String formatJson(JsonElement el, int indent) {
        StringBuilder sb = new StringBuilder();
        String pad = "  ".repeat(indent);

        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            sb.append("{\n");
            int i = 0;
            for (var e : obj.entrySet()) {
                sb.append(pad).append("  \"").append(e.getKey()).append("\": ");
                sb.append(formatJson(e.getValue(), indent + 1));
                if (++i < obj.size()) sb.append(",");
                sb.append("\n");
            }
            sb.append(pad).append("}");
        } else if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            if (!arr.isEmpty() && arr.get(0).isJsonObject() && isSmallObject(arr.get(0).getAsJsonObject())) {
                sb.append("[");
                for (int j = 0; j < arr.size(); j++) {
                    JsonObject o = arr.get(j).getAsJsonObject();
                    sb.append("{");
                    int k = 0;
                    for (var f : o.entrySet()) {
                        sb.append("\"").append(f.getKey()).append("\":").append(f.getValue());
                        if (++k < o.size()) sb.append(", ");
                    }
                    sb.append("}");
                    if (j < arr.size() - 1) sb.append(",\n").append(pad).append("  ");
                }
                sb.append("]");
            } else {
                sb.append("[\n");
                for (int j = 0; j < arr.size(); j++) {
                    sb.append(pad).append("  ").append(formatJson(arr.get(j), indent + 1));
                    if (j < arr.size() - 1) sb.append(",");
                    sb.append("\n");
                }
                sb.append(pad).append("]");
            }
        } else {
            sb.append(el.toString());
        }
        return sb.toString();
    }

    private static boolean isSmallObject(JsonObject obj) {
        return obj.entrySet().size() <= 3;
    }
}
