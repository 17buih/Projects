package planetwars.core;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class SystemLoader {
    static Map<Integer, Planet> load(String graph, IPlanetLookup planetLookup) throws FileNotFoundException {
        GraphParser parser = Assets.loadGraph(graph);
        Map<Integer, Planet> planetsMap = new HashMap<>();

        for (Map.Entry<String, GraphNode> entry : parser.getNodes().entrySet()) {
            GraphNode node = entry.getValue();
            int id = Integer.parseInt(entry.getKey());
            int habitability = Integer.parseInt((String) node.getAttribute("habit"));
            int size = Integer.parseInt((String) node.getAttribute("size"));
            int x = Integer.parseInt((String) node.getAttribute("x"));
            int y = Integer.parseInt((String) node.getAttribute("y"));

            boolean isHomeworld;
            InternalPlayer owningPlayer;
            String base = (String) node.getAttribute("base");
            if (base == null) {
                isHomeworld = false;
                owningPlayer = InternalPlayer.NEUTRAL;
            } else {
                isHomeworld = true;
                switch (base) {
                    case "1":
                        owningPlayer = InternalPlayer.PLAYER1;
                        break;
                    case "2":
                        owningPlayer = InternalPlayer.PLAYER2;
                        break;
                    default:
                        throw new IllegalArgumentException("Base must be either 1 or 2");
                }
            }

            Planet planet = new Planet(id, habitability, size, owningPlayer, isHomeworld, planetLookup);
            if (isHomeworld) {
                planet.setPopulation(1);
            }
            planet.setLocation(new Point2D(x, y));

            planetsMap.put(id, planet);
        }

        for (Map.Entry<String, GraphEdge> entry : parser.getEdges().entrySet()) {
            GraphEdge edge = entry.getValue();
            int id1 = Integer.parseInt(edge.getNode1().getId());
            int id2 = Integer.parseInt(edge.getNode2().getId());
            int distance = Integer.parseInt((String) edge.getAttribute("weight"));

            Planet planet1 = planetsMap.get(id1);
            Planet planet2 = planetsMap.get(id2);
            planet1.addEdge(planet2, distance);
        }

        return planetsMap;
    }

    public static void save(String filename, Collection<Planet> planets) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static void main(String[] args) throws FileNotFoundException {
        Map<Integer, Planet> planetMap = load("graph", null);
        System.out.println();
    }
}
