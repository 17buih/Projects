package planetwars.publicapi;

/**
 * Represents the movement of people from one planet to another.
 */
public interface IShuttle {
    long getNumberPeople();

    Owner getOwner();

    int getSourcePlanetId();

    int getDestinationPlanetId();

    int getTurnsToArrival();
}
