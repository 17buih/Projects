package planetwars.strategies;

import planetwars.publicapi.IEvent;
import planetwars.publicapi.IPlanet;
import planetwars.publicapi.IPlanetOperations;
import planetwars.publicapi.IStrategy;

import java.util.List;
import java.util.Queue;

public class NoOpStrategy implements IStrategy {
    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {}

    @Override
    public String getName() {
        return "Dummy";
    }

    @Override
    public boolean compete() {
        return false;
    }
}
