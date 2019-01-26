package planetwars.publicapi;

import java.util.List;

/**
 * Information about a planet that is either owned by or adjacent to a planet owned by the player.
 */
public interface IVisiblePlanet extends IPlanet {
    /**
     * Get the habitability of the planet, which indicates the population growth rate.
     * <p>
     * A higher habitability indicates faster growth.
     */
    int getHabitability();

    /**
     * Get the number of people the planet can sustain.
     */
    long getSize();

    /**
     * Get the number of people currently on the planet.
     */
    long getPopulation();

    /**
     * Get the owner of the planet.
     */
    Owner getOwner();

    /**
     * True if the planet is a homeworld.
     */
    boolean isHomeworld();

    /**
     * Get a list of the shuttles currently headed for the planet.
     */
    List<IShuttle> getIncomingShuttles();
}
