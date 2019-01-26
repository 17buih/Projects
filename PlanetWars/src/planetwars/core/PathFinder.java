package planetwars.core;

import java.util.*;

import static planetwars.core.PlanetWarsFrame.BUCKET_WIDTH;
import static planetwars.core.PlanetWarsFrame.PLANET_RADIUS;

final class PathFinder {
    private PlanetWarsFrame planetWarsFrame;

    public PathFinder(PlanetWarsFrame frame) {
        this.planetWarsFrame = frame;
    }

    private Map<Tuple3, List<Point2D>> cachedPaths = new HashMap<>();
    private Map<Tuple3, List<Point2D>> cachedStraightPaths = new HashMap<>();

    public void clearCache() {
        cachedPaths.clear();
        cachedStraightPaths.clear();
    }

    public List<Point2D> findPath(Planet src, Planet target, int time, boolean straight) {
        Tuple3 tuple3 = new Tuple3(src.getId(), target.getId(), time);

        if(straight && cachedStraightPaths.containsKey(tuple3)) {
            return cachedStraightPaths.get(tuple3);
        }

        if (!straight && cachedPaths.containsKey(tuple3)) {
            return cachedPaths.get(tuple3);
        }

        List<Point2D> path = new ArrayList<>();

        Point2D start, goal;

        // always start the search from the point closest to x = 0 regardless of who's src or target
        boolean toReverse = src.getLocation().getX() < target.getLocation().getX();
        if (src.getLocation().getX() == target.getLocation().getX()) {
            toReverse = src.getLocation().getY() < target.getLocation().getY();
        }

        if(toReverse) {
            start = new Point2D(src.getLocation());
            goal = new Point2D(target.getLocation());
        } else {
            start = new Point2D(target.getLocation());
            goal = new Point2D(src.getLocation());
        }

        if(!lineOfSight(start, goal)) {
            PriorityQueue<Entry> open = new PriorityQueue<>();
            Set<Point2D> closed = new HashSet<>();

            Map<Point2D, Double> g = new HashMap<>();
            g.put(start, 0.0);
            Map<Point2D, Point2D> parent = new HashMap<>();

            open.add(new Entry(start, g.get(start) + h(start, goal)));
            while (!open.isEmpty()) {
                Point2D s = open.poll().value;

                if (s != null && s.equals(goal)) {
                    break;
                }

                closed.add(s);

                List<Point2D> nbrs = getNeighbors(s);
                for (Point2D nbr : nbrs) {
                    if (!closed.contains(nbr)) {
                        if (!open.contains(new Entry(nbr, 0.0))) {
                            g.put(nbr, Double.POSITIVE_INFINITY);
                            parent.put(nbr, null);
                        }

                        // UpdateVertex {{{
                        double gOld = g.get(nbr);

                        // ComputeCost {{{
                        if (lineOfSight(parent.get(s), nbr)) {
                            if (g.get(parent.get(s)) + cost(parent.get(s), nbr, goal) < g.get(nbr)) {
                                parent.put(nbr, parent.get(s));
                                g.put(nbr, g.get(parent.get(s)) + cost(parent.get(s), nbr, goal));
                            }
                        } else {
                            if (g.get(s) + cost(s, nbr, goal) < g.get(nbr)) {
                                parent.put(nbr, s);
                                g.put(nbr, g.get(s) + cost(s, nbr, goal));
                            }
                        }
                        // }}}

                        if (g.get(nbr) < gOld) {
                            if (open.contains(new Entry(nbr, 0))) {
                                open.remove(new Entry(nbr, 0));
                            }

                            open.add(new Entry(nbr, g.get(nbr) + h(nbr, goal)));
                        }
                        // }}}
                    }
                }
            }

            while (parent.containsKey(goal)) {
                path.add(goal);
                goal = parent.get(goal);
            }
        }

        if (path.size() > 0) {
            path.add(start);
        } else {
            // If we have a LOS or Theta* doesn't find a path, just go from start to goal
            path.add(goal);
            path.add(start);
        }

        if(toReverse) Collections.reverse(path);
        path = bufferPath(path, time, straight);

        if(straight) cachedStraightPaths.put(tuple3, path);
        else cachedPaths.put(tuple3, path);

        return path;
    }

    private List<Point2D> bufferPath(List<Point2D> path, double time, boolean straight) {

        List<Point2D> bufferedPath = new ArrayList<>();
        Random rand = new Random();

        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D src = path.get(i);
            Point2D target = path.get(i + 1);

            double xDiff = target.getX() - src.getX();
            double yDiff = target.getY() - src.getY();

            totalDistance += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        }

        int alt = Math.max(5, rand.nextInt(10));
        int phase = Math.max(15, rand.nextInt(20));
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D src = path.get(i);
            Point2D target = path.get(i + 1);

            double xDiff = target.getX() - src.getX();
            double yDiff = target.getY() - src.getY();

            double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

            int howManyPoints = (int) ((dist / totalDistance) * time);

            double dx = xDiff / howManyPoints;
            double dy = yDiff / howManyPoints;

            for (int j = 0; j < howManyPoints; j++) {
                double x = src.getX() + (j * dx);
                double y = src.getY() + (j * dy);

                double xDist = straight ? dx : alt * -Math.sin(y / phase);
                double yDist = straight ? dy : alt * Math.cos(x / phase);

                x += xDist;
                y += yDist;
                bufferedPath.add(new Point2D(x, y));
            }
        }

        return bufferedPath;
    }

    private boolean isOccupied(double x, double y) {
        int bX = (int) (x / BUCKET_WIDTH);
        int bY = (int) (y / BUCKET_WIDTH);
        return planetWarsFrame.planetGrid[bY][bX] != null;
    }

    private class Entry implements Comparable<Entry> {
        private double key;
        private Point2D value;

        public Entry(Point2D value, double key) {
            this.key = key;
            this.value = value;
        }

        public int compareTo(Entry entry) {
            return Double.compare(this.key, entry.key);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point2D Point2Dition = ((Entry) o).value;

            return this.value.equals(Point2Dition);
        }
    }

    private List<Point2D> getNeighbors(Point2D p) {
        List<Point2D> ret = new ArrayList<>();

        int bX = (int) (p.getX() / BUCKET_WIDTH);
        int bY = (int) (p.getY() / BUCKET_WIDTH);

        for(int i = Math.max(0, bX - 1); i < Math.min(bX + 2, planetWarsFrame.planetGrid[0].length); i++) {
            for(int j = Math.max(0, bY - 1); j < Math.min(bY + 2, planetWarsFrame.planetGrid.length); j++) {
                int x = (i * BUCKET_WIDTH) + BUCKET_WIDTH / 2;
                int y = (j * BUCKET_WIDTH) + BUCKET_WIDTH / 2;

                ret.add(new Point2D(x, y));
            }
        }

        return ret;
    }


    private boolean lineOfSight(Point2D p1, Point2D p2) {
        if(p1 == null || p2 == null)
            return false;

        double x1 = p1.getX();
        double y1 = p1.getY();

        double x2 = p2.getX();
        double y2 = p2.getY();

        boolean lineOfSight = true;
        for(Planet planet : planetWarsFrame.planetWars.getPlanets()) {
            double x0 = planet.getLocation().getX();
            double y0 = planet.getLocation().getY();

            // don't do anything if we are looking at our self
            if(x0 == x1 && y0 == y1 || x0 == x2 && y0 == y2)
                continue;

            // don't do anything if outside of bounding box of p1 and p2
            if(!(Math.min(x1, x2) - PLANET_RADIUS <= x0 && x0 <= Math.max(x1, x2) + PLANET_RADIUS && Math.min(y1, y2) - PLANET_RADIUS <= y0 && y0 <= Math.max(y1, y2) + PLANET_RADIUS)) {
                continue;
            }

            // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
            double distance = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

            if(distance <= PLANET_RADIUS)
                lineOfSight = false;
        }

        return lineOfSight;
    }

    // heuristic used in Theta*
    private double h(Point2D p1, Point2D p2) {
        return cost(p1, p2, p2);
    }

    private double cost(Point2D p1, Point2D p2, Point2D goal) {
        if(isOccupied(p2.getX(), p2.getY()) && !p2.equals(goal))
            return Double.POSITIVE_INFINITY;

        // squared L2 norm
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY());
    }

    private static class Tuple3 {
        int srcId;
        int targetId;
        int time;

        public Tuple3(int srcId, int targetId, int time) {
            this.srcId = srcId;
            this.targetId = targetId;
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple3 tuple3 = (Tuple3) o;

            if (srcId != tuple3.srcId) return false;
            if (targetId != tuple3.targetId) return false;
            return time == tuple3.time;
        }

        @Override
        public int hashCode() {
            int result = srcId;
            result = 31 * result + targetId;
            result = 31 * result + time;
            return result;
        }
    }
}
