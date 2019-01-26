package planetwars.core;


import java.util.ArrayList;
import java.util.List;

final class Move {

    private Shuttle shuttle;
    private int time;
    private InternalPlayer who;
    private List<Point2D> path;

    private Planet src;
    private Planet target;

    public Move(Planet src, Planet target, Shuttle shuttle, int time, PathFinder pathFinder) {
        this.shuttle = shuttle;
        this.time = time;
        this.who = shuttle.getOwningPlayer();

        this.src = src;
        this.target = target;

        this.path = new ArrayList<>(pathFinder.findPath(src, target, time, false));

    }

    public Planet getTarget() {
        return target;
    }

    public InternalPlayer getMoveMaker() {
        return who;
    }

    public long getNumPeople() {
        return shuttle.getNumberPeople();
    }

    public boolean hasReached() {
        return path.isEmpty();
    }

    public Point2D nextPos() {
        if (hasReached()) {
            return null;
        } else {
            return path.remove(0);
        }
    }
}
