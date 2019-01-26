package planetwars.core;

import planetwars.publicapi.IEdge;
import planetwars.publicapi.IPlanet;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the state of a planet at a single moment; the data members will not be updated as the state of the planet changes.
 * <p>
 * This is a distinct class from {@link VisiblePlanetSnapshot} so that students cannot cast between them to get more information.
 */
final class PlanetSnapshot implements IPlanet {
    private int id;
    private Set<IEdge> edges;

    public PlanetSnapshot(int id, Set<IEdge> edges) {
        this.id = id;
        this.edges = edges;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Set<IEdge> getEdges() {
        return new HashSet<>(edges);
    }
}
