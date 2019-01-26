package planetwars.core;

import planetwars.publicapi.*;

final class PlanetOperations implements IPlanetOperations {
    private IPlanetLookup planetLookup;
    private InternalPlayer player;

    public PlanetOperations(IPlanetLookup planetLookup, InternalPlayer player) {
        this.planetLookup = planetLookup;
        this.player = player;
    }

    @Override
    public IEvent transferPeople(IPlanet from, IPlanet to, long numPeople) {
        int distance = -1;
        for (IEdge edge : from.getEdges()) {
            if (edge.getDestinationPlanetId() == to.getId()) {
                distance = edge.getLength();
            }
        }
        return new Shuttle(from.getId(), to.getId(), this.player, numPeople, distance);
    }
}
