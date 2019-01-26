package planetwars.core;

import planetwars.publicapi.IEdge;

final class Edge implements IEdge {
    private final int sourcePlanetId;
    private final int destinationPlanetId;
    private final int length;

    public Edge(int sourcePlanetId, int destinationPlanetId, int length) {
        this.sourcePlanetId = sourcePlanetId;
        this.destinationPlanetId = destinationPlanetId;
        this.length = length;
    }

    @Override
    public int getSourcePlanetId() {
        return this.sourcePlanetId;
    }

    @Override
    public int getDestinationPlanetId() {
        return this.destinationPlanetId;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (sourcePlanetId != edge.sourcePlanetId) return false;
        if (destinationPlanetId != edge.destinationPlanetId) return false;
        return length == edge.length;
    }

    @Override
    public int hashCode() {
        int result = sourcePlanetId;
        result = 31 * result + destinationPlanetId;
        result = 31 * result + length;
        return result;
    }
}
