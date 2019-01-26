package planetwars.strategies;
import planetwars.publicapi.*;
import java.util.*;

/**
 * Author: Xiaoxuan Zhang 5375317
 * Author: Wiley Bui 5368469
*/


public class Strategy implements IStrategy {
    private HashMap<IVisiblePlanet, Boolean> seekHelpPlanets = new HashMap<>(); //our planets that need of help
    private boolean hasEnemyNeighbor = false;

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
        List<IVisiblePlanet> powerfulPlanets = new ArrayList<>(); //lots of population were added to these planets which are close to OPPONENT

        for (IPlanet planet : planets) {
            if (planet instanceof IVisiblePlanet && ((IVisiblePlanet) planet).getOwner() == Owner.SELF) {
                conqueredVisiblePlanets.add((IVisiblePlanet) planet);
            } else if (planet instanceof IVisiblePlanet) {
                unconqueredVisiblePlanets.add((IVisiblePlanet) planet);
            }
        }

        for (IVisiblePlanet planet: conqueredVisiblePlanets) {
            hasEnemyNeighbor    = false;
            long tempPopulation = ((IVisiblePlanet) planet).getPopulation();
            IVisiblePlanet sourcePlanet = (IVisiblePlanet) planet;
            Queue<IVisiblePlanet> sortedNeighborDistances = getAllNeighborsShortestDistance(planet, planets);

            if (hasEnemyNeighbor) {
                //planets need to seek help if close to an enemy
                seekHelpPlanets.put(sourcePlanet, true);
            }

            for (IVisiblePlanet neighbor: sortedNeighborDistances) {
                //for loop looks for neutral planets to attack them ASAP
                if (tempPopulation > 1 && neighbor.getOwner() == Owner.NEUTRAL) {
                    long myTemp = (long) (tempPopulation/2);
                    tempPopulation -= myTemp;
                    eventsToExecute.offer(planetOperations.transferPeople(sourcePlanet, neighbor, myTemp));
                }
            }

            while (!sortedNeighborDistances.isEmpty()) {
                //attack the opponent when our population is greater than enemy * 1.2
                long myMaxSize = sourcePlanet.getSize();
                long enemyPopulation = sortedNeighborDistances.peek().getPopulation();

                if (sortedNeighborDistances.peek().getOwner() == Owner.OPPONENT) {
                    int max = (int) Math.max(3, enemyPopulation * 1.2);
                    long myTemp = (long) Math.max(max, sourcePlanet.getPopulation()/1.2);

                    if ((tempPopulation > enemyPopulation * 1.3) && (tempPopulation - myTemp > 1)) { //attack the opponent based on size
                        tempPopulation -= myTemp;
                        powerfulPlanets.add(sortedNeighborDistances.peek());
                        eventsToExecute.offer(planetOperations.transferPeople(sourcePlanet, sortedNeighborDistances.peek(), myTemp));
                    }
                }
                sortedNeighborDistances.remove();
            }
        }

        for (IVisiblePlanet seekingPlanet: seekHelpPlanets.keySet()) {
            //planets that need help for loop -> our neighbors are our own team = send to that need-help planet 
            if (seekHelpPlanets.get(seekingPlanet) == true) {
                for (IVisiblePlanet conquerPlanet : conqueredVisiblePlanets) {
                    if (conquerPlanet.getPopulation() < 6 || getEnemyNeighbors(conquerPlanet, planets).size() > 1) {
                        continue;
                    }
                    else if (conquerPlanet.getPopulation() > conquerPlanet.getSize() * 0.5) {
                        for (IEdge edge : conquerPlanet.getEdges()) {
                            if (seekingPlanet.getId() == edge.getDestinationPlanetId() && seekingPlanet.getSize() > seekingPlanet.getPopulation()) {
                                eventsToExecute.offer(planetOperations.transferPeople(conquerPlanet, seekingPlanet, conquerPlanet.getPopulation()/2));
                                break;
                            }
                        }
                    }
                }
                seekHelpPlanets.remove(seekingPlanet);
            }
        }

        for(IVisiblePlanet thankfulPlanet: powerfulPlanets) {
            //for every planets that receives from our planets
            List<IVisiblePlanet> myEnemies = getEnemyNeighbors(((IPlanet) thankfulPlanet), planets);
            for (IVisiblePlanet enemyPlanet : myEnemies) {
                eventsToExecute.offer(planetOperations.transferPeople(thankfulPlanet, enemyPlanet, thankfulPlanet.getPopulation()/2));
            }
        }
    }

     /**
     * Method that gets all the enemies from source planet
     * @param planets   List that contained all the planets in the map
     * @param myPlanet  Source planet
     * @return          list of IVisible enemies
     */
    public List<IVisiblePlanet> getEnemyNeighbors(IPlanet myPlanet, List<IPlanet> planets) {
        //get all enemies from source planet
        List<IVisiblePlanet> myEnemies = new ArrayList<>();
        Set<IEdge> myEdges = myPlanet.getEdges();
        for (IEdge edge : myEdges) {
            for (IPlanet planet : planets) {
                if (planet.getId() == edge.getDestinationPlanetId()) {
                    if (((IVisiblePlanet) planet).getOwner() == Owner.OPPONENT) { //neighbor Enemies
                        myEnemies.add((IVisiblePlanet) planet);
                    }
                }
            }
        }
        return myEnemies;
    }

     /**
     * Method that gets all neighbor planets of the source planet and return a sorted Queue base on
     * the distance of each neighbors, from low to high.
     * @param planets   List that contained all the planets in the map
     * @param myPlanet  Source planet
     * @return          Queue of IVisiblePlanet from smallest distance to highest
     */
    public Queue<IVisiblePlanet> getAllNeighborsShortestDistance(IPlanet myPlanet, List<IPlanet> planets) {
        HashMap<IVisiblePlanet, Integer> myNeighbors = new HashMap<>(); //storing IVisiblePlanet as key by length as value
        int index = 0;
        Set<IEdge> myEdges = myPlanet.getEdges();
        int[] orderedDistanceArr = new int[myEdges.size()]; //allocate the spaces for an int array
        Queue<IVisiblePlanet> distanceQueue = new LinkedList<>(); //queue sorted by distance

        for (IEdge edge : myEdges) {
            for (IPlanet planet : planets) {
                if (planet.getId() == edge.getDestinationPlanetId()) {
                    int myLength = edge.getLength();
                    myNeighbors.put((IVisiblePlanet) planet, myLength);
                    orderedDistanceArr[index++] = myLength; //put length into incremented array
                    if (((IVisiblePlanet) planet).getOwner() == Owner.OPPONENT) {
                        hasEnemyNeighbor = true;
                    }
                }
            }
        }

        index = 0;
        Arrays.sort(orderedDistanceArr);
        while (index < myEdges.size()) { //add into queue based on sorted distance from orderedDistanceArr
            for (IVisiblePlanet neighborPlanet : myNeighbors.keySet()) {
                if (myNeighbors.get(neighborPlanet) == orderedDistanceArr[index++]) {
                    distanceQueue.add(neighborPlanet);
                }
            }
        }
        return distanceQueue;
    }


    @Override
    public String getName() {
        return "Dustin & Wiley's Strategies";
    }

    @Override
    public boolean compete() {
        return false;
    }
}
