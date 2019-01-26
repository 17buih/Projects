package planetwars.core;

import java.util.Collection;

interface IPlanetLookup {
    Planet lookupPlanet(int id);

    Collection<Planet> getPlanets();
}
