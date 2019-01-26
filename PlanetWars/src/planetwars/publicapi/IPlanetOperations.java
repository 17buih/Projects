package planetwars.publicapi;

import java.util.List;
import java.util.Queue;

public interface IPlanetOperations {
    /**
     * Get an event scheduling the movement of people.
     * <p>
     * Add this event to the queue in {@link IStrategy#takeTurn(List, IPlanetOperations, Queue)} for it to take effect.
     */
    IEvent transferPeople(IPlanet from, IPlanet to, long numPeople);
}
