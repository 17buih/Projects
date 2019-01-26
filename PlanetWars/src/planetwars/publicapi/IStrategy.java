package planetwars.publicapi;

import java.util.List;
import java.util.Queue;

public interface IStrategy {
    /**
     * Method where students can observe the state of the system and schedule events to be executed.
     *
     * @param planets          The current state of the system.
     * @param planetOperations Helper methods students can use to interact with the system.
     * @param eventsToExecute  Queue students will add to in order to schedule events.
     */
    void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute);

    String getName();

    boolean compete();
}
