package planetwars.publicapi;

import java.util.Set;

/**
 * Information about a planet. Additional information may be available if the object also implements {@link IVisiblePlanet}.
 */
public interface IPlanet {
    /**
     * Get the id of the planet.
     * <p>
     * This is stable -- you should use it to match planets between calls to {@link IStrategy#takeTurn}.
     */
    int getId();

    /**
     * Get the connections from this planet to other planets.
     * <p>
     * {@link IEdge#getSourcePlanetId()} for each edge will be equal to {@link #getId()};
     * {@link IEdge#getDestinationPlanetId()} will be the id of each neighboring planet.
     */
    Set<IEdge> getEdges();
}
