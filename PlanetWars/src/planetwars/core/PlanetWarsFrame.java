package planetwars.core;

import planetwars.publicapi.IStrategy;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static planetwars.core.InternalPlayer.PLAYER1;

final class PlanetWarsFrame extends AnimationFrame {
    static final int BUCKET_WIDTH = 80;
    static final int PLANET_RADIUS = 40;
    static final int FRAMES_PER_TURN = 30;
    static final int EDGE_POINT_SPACING = 10;

    private final int NUM_MOBILE_STARS = 50;
    private final int NUM_STATIC_STARS = 200;

    PlanetWars planetWars;
    Planet[][] planetGrid;
    private PathFinder pathFinder;
    private List<Move> moves;
    private List<List<Point2D>> edgePaths;
    private List<Point2D> stationaryStars;
    private List<Point2D> mobileStars;

    private int frameCount = 0;

    public PlanetWarsFrame(int w, int h, String name, PlanetWars planetWars) {
        super(w, h, name);
        this.planetWars = planetWars;
        planetWars.setObserver(this);

        this.planetGrid = new Planet[h / BUCKET_WIDTH][w / BUCKET_WIDTH];
        for (Planet planet : planetWars.getPlanets()) {
            double y, x;
            Point2D location = planet.getLocation();
            this.planetGrid[(int) location.getY()][(int) location.getX()] = planet;

            // Need to center the planet in the bucket for PathFinder
            // and needs to be undiscretized so that we can buffer the path
            x = (location.getX() * BUCKET_WIDTH) + BUCKET_WIDTH / 2;
            y = (location.getY() * BUCKET_WIDTH) + BUCKET_WIDTH / 2;
            planet.setLocation(new Point2D(x, y));
        }

        this.pathFinder = new PathFinder(this);
        this.moves = new ArrayList<>();

        // Generate lines to represent the edges
        this.edgePaths = new ArrayList<>();
        for (Planet planet1 : planetWars.getPlanets()) {
            Set<Planet> neighbors = planet1.getNeighboringPlanets();
            for (Planet planet2 : neighbors) {
                if (planet1.getId() >= planet2.getId()) {
                    continue;
                }

                Point2D planet1Location = planet1.getLocation();
                Point2D planet2Location = planet2.getLocation();
                double xdiff = planet1Location.getX() - planet2Location.getX();
                double ydiff = planet1Location.getY() - planet2Location.getY();
                int distance = (int) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
                // The time here just indicates the number of points, so it controls the granularity of the dotted line
                // We'll use the Euclidean distance for aesthetics, divided by a scale factor so there are gaps between points
                List<Point2D> edgePath = this.pathFinder.findPath(planet1, planet2, distance / EDGE_POINT_SPACING, true);
                this.edgePaths.add(edgePath);
            }
        }

        stationaryStars = this.generateStars(NUM_MOBILE_STARS);
        mobileStars = this.generateStars(NUM_STATIC_STARS);
    }

    @Override
    public void action() {
        frameCount = (frameCount + 1) % FRAMES_PER_TURN;
        if (frameCount == 0) {
            planetWars.gameTick();
        }

        moveStars();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw edge paths, stars before planets so the planets are on top
        this.drawEdgePaths(g);
        this.drawStars(g);

        this.drawPlanets(g);

        // Draw moves last so they're always visible
        this.drawMoves(g);


        if (planetWars.isOver()) {
            InternalPlayer winner = planetWars.getWinner();
            Color color;
            String winnerText;
            switch (winner) {
                case PLAYER1:
                    color = Color.RED;
                    winnerText = "Player One Wins";
                    break;
                case PLAYER2:
                    color = Color.BLUE;
                    winnerText = "Player Two Wins";
                    break;
                default:
                    color = Color.GREEN;
                    winnerText = "INVALID STATE";
            }
            g.setColor(color);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
            g.drawString(winnerText, 250, 450);
        }
    }

    private int getX(Planet planet) {
        return (int) ((planet.getLocation().getX()));
    }

    private int getY(Planet planet) {
        return (int) ((planet.getLocation().getY()));
    }

    private void drawPlanets(Graphics g) {
        for (Planet planet : planetWars.getPlanets()) {
            InternalPlayer owner = planet.getOwningPlayer();
            Image img;

            try {
                if (owner == PLAYER1) {
                    img = Assets.loadPlanet(PLAYER1);
                } else if (owner == InternalPlayer.PLAYER2) {
                    img = Assets.loadPlanet(InternalPlayer.PLAYER2);
                } else {
                    img = Assets.loadPlanet(InternalPlayer.NEUTRAL);
                }
                g.drawImage(img, getX(planet) - PLANET_RADIUS, getY(planet) - PLANET_RADIUS, null);

                FontMetrics fm = g.getFontMetrics();

                // h4x
                String id = String.valueOf(planet.getId());
                String population = String.valueOf(planet.getPopulation());
                String[] details = {id, population};

                int multiplier = 1;
                for (String detail : details) {
                    double textWidth = fm.getStringBounds(detail, g).getWidth();
                    g.setColor(Color.WHITE);
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    g.drawString(detail, (int) (getX(planet) - textWidth / 2),
                            getY(planet) + fm.getMaxAdvance() / (multiplier));
                    multiplier *= 2;
                }
            } catch (IOException e) {

            }
        }
    }

    private void drawEdgePaths(Graphics g) {
        g.setColor(Color.WHITE);
        for (List<Point2D> edgePath : this.edgePaths) {
            Iterator<Point2D> it = edgePath.iterator();
            while (it.hasNext()) {
                // Get two points and draw a line between them
                Point2D p1 = it.next();
                if (!it.hasNext()) {
                    break;
                }
                Point2D p2 = it.next();

                g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
            }
        }
    }

    private void drawStars(Graphics g) {
        for (Point2D star : stationaryStars) {
            g.setColor(Color.WHITE);
            g.fillOval((int) star.getX(), (int) star.getY(), 2, 2);
        }

        for (Point2D star : mobileStars) {
            g.setColor(Color.WHITE);
            g.fillOval((int) star.getX(), (int) star.getY(), 2, 2);
        }
    }

    private void drawMoves(Graphics g) {
        Iterator<Move> it = moves.iterator();
        while (it.hasNext()) {
            Move move = it.next();
            Point2D pos = move.nextPos();
            InternalPlayer player = move.getMoveMaker();
            Color color;
            if (player == PLAYER1) {
                color = Color.RED;
            } else {
                color = Color.BLUE;
            }
            FontMetrics fm = g.getFontMetrics();
            String numPeople = String.valueOf(move.getNumPeople());
            double textWidth = fm.getStringBounds(numPeople, g).getWidth();

            double moveRadius = textWidth + 10;

            g.setColor(color);
            assert pos != null;
            g.fillOval((int) (pos.getX() - moveRadius / 2), (int) (pos.getY() - moveRadius / 2), (int) moveRadius, (int) moveRadius);
            g.setColor(Color.WHITE);
            g.drawOval((int) (pos.getX() - moveRadius / 2), (int) (pos.getY() - moveRadius / 2), (int) moveRadius, (int) moveRadius);

            g.setColor(Color.WHITE);
            g.drawString(numPeople, (int) (pos.getX() - textWidth / 2),
                    (int) pos.getY() + fm.getMaxAdvance() / 4);

            if (move.hasReached()) {
                it.remove();
            }
        }
    }

    private void moveStars() {
        for (Point2D mobileStar : mobileStars) {
            double newX = mobileStar.getX() + 0.3;
            if (newX >= GameWindow.PLANET_WARS_WIDTH) {
                newX -= GameWindow.PLANET_WARS_WIDTH;
            }
            mobileStar.setPos(newX, mobileStar.getY());
        }
    }

    public List<Point2D> generateStars(int howMany) {
        List<Point2D> stars = new ArrayList<>();

        Random rand = new Random();
        for (int i = 0; i < howMany; i++) {
            stars.add(new Point2D(rand.nextInt(GameWindow.PLANET_WARS_WIDTH), rand.nextInt(GameWindow.PLANET_WARS_HEIGHT)));
        }
        return stars;
    }

    public void notifyNewShuttle(Shuttle shuttle) {
        Planet src = this.planetWars.lookupPlanet(shuttle.getSourcePlanetId());
        Planet dest = this.planetWars.lookupPlanet(shuttle.getDestinationPlanetId());
        Move move = new Move(src, dest, shuttle, shuttle.getTurnsToArrival() * FRAMES_PER_TURN, this.pathFinder);
        this.moves.add(move);
    }

    public void setPlayer1(IStrategy player) {
        this.planetWars.setPlayer1(player);
    }

    public void setPlayer2(IStrategy player) {
        this.planetWars.setPlayer2(player);
    }
}
