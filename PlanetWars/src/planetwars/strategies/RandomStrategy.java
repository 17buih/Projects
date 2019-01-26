package planetwars.strategies;

import planetwars.publicapi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class RandomStrategy implements IStrategy {

    private static final int POPULATION_DIVISION = 5;
    private Random random;

    public RandomStrategy() {
        random = new Random();
    }

    /**
     * Method where students can observe the state of the system and schedule events to be executed.
     *
     * @param planets          The current state of the system.
     * @param planetOperations Helper methods students can use to interact with the system.
     * @param eventsToExecute  Queue students will add to in order to schedule events.
     */
    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
        List<IVisiblePlanet> conqueredVisiblePlanets = new ArrayList<>();
        List<IVisiblePlanet> unconqueredVisiblePlanets = new ArrayList<>();
        for (IPlanet planet : planets) {
            if (planet instanceof IVisiblePlanet && ((IVisiblePlanet) planet).getOwner() == Owner.SELF) {
                conqueredVisiblePlanets.add((IVisiblePlanet) planet);
            } else if (planet instanceof IVisiblePlanet)
                unconqueredVisiblePlanets.add((IVisiblePlanet) planet);
        }
        IVisiblePlanet sourcePlanet = conqueredVisiblePlanets.get(random.nextInt(conqueredVisiblePlanets.size()));
        long transferPopulation = random.nextInt((int) sourcePlanet.getPopulation());
        IPlanet destinationPlanet = unconqueredVisiblePlanets.get(random.nextInt(unconqueredVisiblePlanets.size()));
        eventsToExecute.offer(planetOperations.transferPeople(sourcePlanet, destinationPlanet, transferPopulation));
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public boolean compete() {
        return false;
    }
}