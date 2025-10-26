package plots;

import graph.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.awt.geom.Line2D;
import java.util.List;

public class GraphPlotter {

    public static void saveGraphImage(Graph graph, String outputPath) {
        int width = 1200;
        int height = 900;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<String> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();

        Map<String, Point> positions = new HashMap<>();
        int n = nodes.size();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2 - 120;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            positions.put(nodes.get(i), new Point(x, y));
        }

        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.gray);
        g2.setFont(new Font("Sans", Font.PLAIN, 20));
        for (Edge e : edges) {
            Point p1 = positions.get(e.getFrom());
            Point p2 = positions.get(e.getTo());
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));

            int labelX = (p1.x + p2.x) / 2;
            int labelY = (p1.y + p2.y) / 2;
            g2.setColor(Color.black);
            g2.drawString(String.format("%.0f", e.getWeight()), labelX, labelY);
            g2.setColor(Color.gray);
        }

        for (String node : nodes) {
            Point p = positions.get(node);
            int r = 20;
            g2.setColor(Color.white);
            g2.fillOval(p.x - r / 2, p.y - r / 2, r, r);
            g2.setColor(Color.gray);
            g2.drawOval(p.x - r / 2, p.y - r / 2, r, r);
            g2.setColor(Color.black);
            g2.drawString(node, p.x - 5, p.y - 10);
        }

        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("Graph's plot saved: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
