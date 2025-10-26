package json;

import com.google.gson.*;
import graph.Graph;
import graph.Edge;

import java.io.FileReader;
import java.util.*;

public class JsonReader {

    public static List<Graph> readGraphs(String path) throws Exception {
        List<Graph> graphs = new ArrayList<>();

        try (FileReader reader = new FileReader(path)) {
            JsonElement root = JsonParser.parseReader(reader);

            JsonArray graphsArray;
            if (root.isJsonObject() && root.getAsJsonObject().has("graphs")) {
                graphsArray = root.getAsJsonObject().getAsJsonArray("graphs");
            } else if (root.isJsonArray()) {
                graphsArray = root.getAsJsonArray();
            } else {
                throw new IllegalArgumentException("Invalid JSON structure: expected object with 'graphs' or array.");
            }

            for (JsonElement gElem : graphsArray) {
                JsonObject gObj = gElem.getAsJsonObject();

                int id = gObj.get("id").getAsInt();
                JsonArray nodesArray = gObj.getAsJsonArray("nodes");
                List<String> nodes = new ArrayList<>();
                for (JsonElement n : nodesArray)
                    nodes.add(n.getAsString());

                JsonArray edgesArray = gObj.getAsJsonArray("edges");
                List<Edge> edges = new ArrayList<>();
                for (JsonElement e : edgesArray) {
                    JsonObject edgeObj = e.getAsJsonObject();
                    String from = edgeObj.get("from").getAsString();
                    String to = edgeObj.get("to").getAsString();
                    double weight = edgeObj.get("weight").getAsDouble();
                    edges.add(new Edge(from, to, weight));
                }

                Graph g = new Graph(id, nodes, edges);
                graphs.add(g);
            }
        }

        return graphs;
    }
}
