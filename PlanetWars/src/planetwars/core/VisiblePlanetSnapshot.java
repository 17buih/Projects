package planetwars.core;

import planetwars.publicapi.IEdge;
import planetwars.publicapi.IShuttle;
import planetwars.publicapi.IVisiblePlanet;
import planetwars.publicapi.Owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class representing the state of a visible planet at a single moment; the data members will not be updated as the state of the planet changes.
 * <p>
 * This is a distinct class from {@link PlanetSnapshot} so that students cannot cast between them to get more information.
 */
final class VisiblePlanetSnapshot implements IVisiblePlanet {
    private int id;
    private Set<IEdge> edges;
    private int habitability;
    private long size;
    private long population;
    private Owner owner;
    private boolean isHomeworld;
    private List<IShuttle> incomingShuttles;

    public VisiblePlanetSnapshot(int id, Set<IEdge> edges, int habitability, long size, long population, Owner owner, boolean isHomeworld, List<IShuttle> incomingShuttles) {
        this.id = id;
        this.edges = edges;
        this.habitability = habitability;
        this.size = size;
        this.population = population;
        this.owner = owner;
        this.isHomeworld = isHomeworld;
        this.incomingShuttles = incomingShuttles;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Set<IEdge> getEdges() {
        // TODO: Return a copy here so they can't modify the set
        return edges;
    }

    @Override
    public int getHabitability() {
        return habitability;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getPopulation() {
        return population;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public boolean isHomeworld() {
        return isHomeworld;
    }

    @Override
    public List<IShuttle> getIncomingShuttles() {
        return new ArrayList<>(this.incomingShuttles);
    }
}
