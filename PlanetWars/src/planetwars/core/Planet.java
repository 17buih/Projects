package planetwars.core;

import planetwars.publicapi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Planet {
    private int id;
    private Set<Edge> edges;
    private int habitability;
    private long size;
    private long population;
    private InternalPlayer owningPlayer;
    private boolean isHomeworld;
    private List<Shuttle> incomingShuttles;
    private Point2D location;

    private IPlanetLookup planetLookup;

    public Planet(int id, int habitability, long size, InternalPlayer owningPlayer, boolean isHomeworld, IPlanetLookup planetLookup) {
        assert owningPlayer != null;

        this.id = id;
        this.habitability = habitability;
        this.size = size;
        this.owningPlayer = owningPlayer;
        this.isHomeworld = isHomeworld;

        this.population = 0;
        this.incomingShuttles = new ArrayList<>();
        this.edges = new HashSet<>();
        this.planetLookup = planetLookup;
    }

    public IPlanet getPlanetSnapshot(InternalPlayer viewer) {
        return new PlanetSnapshot(this.id, this.getIEdges());
    }

    public IVisiblePlanet getVisiblePlanetSnapshot(InternalPlayer viewer) {
        return new VisiblePlanetSnapshot(
                this.id,
                this.getIEdges(),
                this.habitability,
                this.size,
                this.population,
                this.getOwnerFromViewer(viewer),
                this.isHomeworld,
                this.getIncomingIShuttles(viewer)
        );
    }

    public Owner getOwnerFromViewer(InternalPlayer viewer) {
        assert viewer != InternalPlayer.NEUTRAL;
        assert viewer != null;
        if (this.owningPlayer == InternalPlayer.NEUTRAL) {
            return Owner.NEUTRAL;
        } else if (viewer == this.owningPlayer) {
            return Owner.SELF;
        } else {
            return Owner.OPPONENT;
        }
    }

    public InternalPlayer getOwningPlayer() {
        return owningPlayer;
    }

    public Set<IEdge> getIEdges() {
        Set<IEdge> iedges = new HashSet<>(this.edges.size());
        iedges.addAll(this.edges);
        return iedges;
    }

    public Set<Planet> getNeighboringPlanets() {
        Set<Planet> neighbors = new HashSet<>();

        for (Edge edge : this.edges) {
            int neighborId = edge.getDestinationPlanetId();
            Planet neighbor = this.planetLookup.lookupPlanet(neighborId);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    /**
     * Checks that a shuttle represents a valid move. If it does, depart the shuttle from this planet and return true.
     *
     * @return True if the shuttle has successfully left the planet
     */
    public boolean checkAndLaunchShuttle(Shuttle shuttle) {
        // Check that the player owning the planet is making the transaction
        if (shuttle.getOwningPlayer() != this.owningPlayer) {
            return false;
        }

        // Check that enough population exists to support the transaction, and that the shuttle population is positive
        if (shuttle.getNumberPeople() > this.population || shuttle.getNumberPeople() <= 0) {
            return false;
        }

        // Check that the destination planet isn't the same as the current one
        if (shuttle.getDestinationPlanetId() == shuttle.getSourcePlanetId()) {
            return false;
        }

        // Check that an edge exists
        Set<Edge> sourceEdges = planetLookup.lookupPlanet(shuttle.getSourcePlanetId()).edges;
        boolean found = false;
        for (Edge edge : sourceEdges) {
            if (edge.getDestinationPlanetId() == shuttle.getDestinationPlanetId()) {
                found = true;
            }
        }
        if (!found) {
            return false;
        }

        this.population -= shuttle.getNumberPeople();
        if (population == 0) {
            this.owningPlayer = InternalPlayer.NEUTRAL;
        }

        return true;
    }

    public void addIncomingShuttle(Shuttle shuttle) {
        this.incomingShuttles.add(shuttle);
    }

    public List<IShuttle> getIncomingIShuttles(InternalPlayer viewer) {
        List<IShuttle> incomingIShuttles = new ArrayList<>(this.incomingShuttles.size());
        for (Shuttle shuttle : this.incomingShuttles) {
            incomingIShuttles.add(shuttle.getShuttleSnapshot(viewer));
        }
        return incomingIShuttles;
    }

    public void addEdge(Planet neighbor, int distance) {
        this.edges.add(new Edge(this.id, neighbor.id, distance));
        neighbor.edges.add(new Edge(neighbor.id, this.id, distance));
    }

    public int getId() {
        return id;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public void grow() {
        if (this.population > size) {
            return;
        }

        double populationScaleFactor = 1. + (this.habitability / 100.);
        this.population = (long) Math.min(this.size, Math.ceil(this.population * populationScaleFactor));
    }

    public void processShuttles() {
        if (this.owningPlayer == InternalPlayer.NEUTRAL) {
            // If the planet is currently neutral, the person who is landing more troops gets it,
            // with the population being the difference in arriving people. Neither player gets a
            // defending bonus.
            List <Shuttle> player1ShuttlesArrived = new ArrayList<>();
            List <Shuttle> player2ShuttlesArrived = new ArrayList<>();

            // Split shuttles based on owning player
            for (Shuttle shuttle : this.incomingShuttles) {
                shuttle.moveCloser();
                if (shuttle.getTurnsToArrival() == 0) {
                    if (shuttle.getOwningPlayer() == InternalPlayer.PLAYER1) {
                        player1ShuttlesArrived.add(shuttle);
                    } else {
                        player2ShuttlesArrived.add(shuttle);
                    }
                }
            }

            // Total up population for each player
            int player1Pop = 0;
            int player2Pop = 0;
            for (Shuttle shuttle : player1ShuttlesArrived) {
                player1Pop += shuttle.getNumberPeople();
            }
            for (Shuttle shuttle : player2ShuttlesArrived) {
                player2Pop += shuttle.getNumberPeople();
            }

            // Assign ownership to the person with more people landing
            this.population = Math.abs(player1Pop - player2Pop);
            if (player1Pop > player2Pop) {
                this.owningPlayer = InternalPlayer.PLAYER1;
            } else if (player1Pop < player2Pop) {
                this.owningPlayer = InternalPlayer.PLAYER2;
            } else {
                assert this.owningPlayer == InternalPlayer.NEUTRAL;
            }

            this.incomingShuttles.removeAll(player1ShuttlesArrived);
            this.incomingShuttles.removeAll(player2ShuttlesArrived);
        } else {
            // Someone owns the planet; we'll land all friendly shuttles first, and then all
            // hostile shuttles. The owning player gets a defensive bonus.
            List<Shuttle> friendlyShuttlesArrived = new ArrayList<>();
            List<Shuttle> hostileShuttlesArrived = new ArrayList<>();

            // Pull out the friendly and hostile shuttles
            for (Shuttle shuttle : this.incomingShuttles) {
                shuttle.moveCloser();
                if (shuttle.getTurnsToArrival() == 0) {
                    if (shuttle.getOwningPlayer() == this.owningPlayer) {
                        friendlyShuttlesArrived.add(shuttle);
                    } else {
                        hostileShuttlesArrived.add(shuttle);
                    }
                }
            }

            // Land all friendly shuttles
            for (Shuttle shuttle : friendlyShuttlesArrived) {
                if (shuttle.getOwningPlayer() == this.owningPlayer) {
                    this.population += shuttle.getNumberPeople();
                }
            }

            if (hostileShuttlesArrived.size() > 0) {
                long attackingPopulation = 0;
                InternalPlayer attackingPlayer = hostileShuttlesArrived.get(0).getOwningPlayer();

                // Calculate the strength of the incoming attack
                for (Shuttle shuttle : hostileShuttlesArrived) {
                    attackingPopulation += shuttle.getNumberPeople();
                }

                // The defending player gets a bonus
                long effectivePopulation = (long) (1.1 * this.population);
                if (effectivePopulation < attackingPopulation) {
                    this.owningPlayer = attackingPlayer;
                    this.population = attackingPopulation - effectivePopulation;
                } else if (effectivePopulation == attackingPopulation) {
                    this.owningPlayer = InternalPlayer.NEUTRAL;
                    this.population = 0;
                } else {
                    this.population = Math.min(this.population, effectivePopulation - attackingPopulation);
                }
            }

            this.incomingShuttles.removeAll(friendlyShuttlesArrived);
            this.incomingShuttles.removeAll(hostileShuttlesArrived);
        }
        assert this.population >= 0;
    }

    public void shrink() {
        if (this.population <= this.size) {
            return;
        }

        long difference = this.population - this.size;
        long killedOff = (long) Math.ceil(difference * 0.1);
        this.population -= killedOff;
        assert this.population >= this.size;
    }

    @Override
    public String toString() {
        return String.format("{Planet: %d, Owner: %s, Pop: %d}", this.id, this.owningPlayer, this.population);
    }
}
