package planetwars.core;

import planetwars.publicapi.IEvent;
import planetwars.publicapi.Owner;

final class Shuttle implements IEvent {
    private final int srcId;
    private final int destId;
    private final InternalPlayer owningPlayer;
    private final long numPeople;
    private int turnsToArrival;

    public Shuttle(int srcId, int destId, InternalPlayer owningPlayer, long numPeople, int turnsToArrival) {
        assert owningPlayer != InternalPlayer.NEUTRAL;

        this.srcId = srcId;
        this.destId = destId;
        this.owningPlayer = owningPlayer;
        this.numPeople = numPeople;
        this.turnsToArrival = turnsToArrival;
    }

    public long getNumberPeople() {
        return numPeople;
    }

    public InternalPlayer getOwningPlayer() {
        return owningPlayer;
    }

    public int getSourcePlanetId() {
        return srcId;
    }

    public int getDestinationPlanetId() {
        return destId;
    }

    public int getTurnsToArrival() {
        return turnsToArrival;
    }

    public void moveCloser() {
        if (this.turnsToArrival > 0) {
            this.turnsToArrival--;
        }
    }

    public ShuttleSnapshot getShuttleSnapshot(InternalPlayer viewer) {
        return new ShuttleSnapshot(this.srcId, this.destId, this.getOwnerFromViewer(viewer), this.numPeople, this.turnsToArrival);
    }

    public Owner getOwnerFromViewer(InternalPlayer viewer) {
        assert viewer != InternalPlayer.NEUTRAL;
        assert viewer != null;

        if (viewer == this.owningPlayer) {
            return Owner.SELF;
        } else {
            return Owner.OPPONENT;
        }
    }
}
