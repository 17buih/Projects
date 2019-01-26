package planetwars.core;

import com.paypal.digraph.parser.GraphParser;
import planetwars.publicapi.IStrategy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public final class Assets {
    private static final String STRATEGIES_DIR = "strategies/";
    private static final String GRAPHS_DIR = "graphs/";
    private static final String IMG_DIR = "img/";
    private static final String[] PLANET_IMGS = {IMG_DIR + "alpha.png", IMG_DIR + "beta.png", IMG_DIR +"neutral.png"};

    public static Image loadPlanet(InternalPlayer who) throws IOException {
        switch (who) {
            case PLAYER1:
                return ImageIO.read(new File(PLANET_IMGS[0]));
            case PLAYER2:
                return ImageIO.read(new File(PLANET_IMGS[1]));
            case NEUTRAL:
                return ImageIO.read(new File(PLANET_IMGS[2]));
            default:
                return null;
        }
    }

    public static IStrategy loadPlayer(String jar) {
        final String playerJar = STRATEGIES_DIR + jar + ".jar";
        final String STRATEGY = "planetwars.strategies.Strategy";
        try {
            ClassLoader loader = new URLClassLoader(new URL[]{new File(playerJar).toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Class<?> c = loader.loadClass(STRATEGY);

            return (IStrategy) c.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | MalformedURLException e) {
            return null;
        }
    }

    public static GraphParser loadGraph(String graph) throws FileNotFoundException {
        String filename = GRAPHS_DIR + graph + ".dot";
        return new GraphParser(new FileInputStream(filename));
    }

    public static IStrategy loadPlayer(Class<? extends IStrategy> strategyClass) {
        try {
            return strategyClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    public static String[] getStrategies() {
        String[] strategies = new File(STRATEGIES_DIR).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        assert strategies != null;
        Arrays.sort(strategies);
        return strategies;
    }

    public static String[] getGraphs() {
        String[] graphs = new File(GRAPHS_DIR).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dot");
            }
        });
        assert graphs != null;
        Arrays.sort(graphs);
        return graphs;
    }
}
