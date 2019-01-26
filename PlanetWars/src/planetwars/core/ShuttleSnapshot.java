package planetwars.core;

import planetwars.publicapi.IShuttle;
import planetwars.publicapi.Owner;

/**
 * Represents a Shuttle at a distinct moment in time.
 */
final class ShuttleSnapshot implements IShuttle {
    private final int srcId;
    private final int destId;
    private final Owner owner;
    private final long numPeople;
    private final int turnsToArrival;

    public ShuttleSnapshot(int srcId, int destId, Owner owner, long numPeople, int turnsToArrival) {
        this.srcId = srcId;
        this.destId = destId;
        this.owner = owner;
        this.numPeople = numPeople;
        this.turnsToArrival = turnsToArrival;
    }

    @Override
    public long getNumberPeople() {
        return numPeople;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public int getSourcePlanetId() {
        return srcId;
    }

    @Override
    public int getDestinationPlanetId() {
        return destId;
    }

    @Override
    public int getTurnsToArrival() {
        return turnsToArrival;
    }
}
