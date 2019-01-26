package planetwars.publicapi;

import planetwars.core.Assets;
import planetwars.core.PlanetWars;
import planetwars.strategies.*;

import java.io.FileNotFoundException;
import java.util.*;

public class StrategyRanker {
    public static final int NUM_TRIALS = 10;

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, FileNotFoundException {
        // List of all strategies to compare
        List<IStrategyProvider> strategyProviders = new ArrayList<>();
//         strategyProviders.add(new ClassStrategyProvider(RandomStrategy.class));
        strategyProviders.add(new ClassStrategyProvider(Strategy.class));
        strategyProviders.add(new JarStrategyProvider("Strategy"));
//         strategyProviders.add(new ClassStrategyProvider(NoOpStrategy.class));
//         strategyProviders.add(new JarStrategyProvider("AI1_obf"));
//         strategyProviders.add(new JarStrategyProvider("AI2_obf"
// ));
//        strategyProviders.add(new JarStrategyProvider("AI3_obf"));

        List<String> graphs = new ArrayList<>();
        graphs.add("rings");
        graphs.add("k4");
        for (int i = 0; i < 100; i++) {
            graphs.add("graph_" + i);
        }

        Map<IStrategyProvider, Integer> wins = new HashMap<>();
        Map<IStrategyProvider, Map<String, Integer>> winsPerMap = new HashMap<>();
        for (IStrategyProvider strategyProvider : strategyProviders) {
            wins.put(strategyProvider, 0);

            winsPerMap.put(strategyProvider, new HashMap<>());
            for (String graph : graphs) {
                winsPerMap.get(strategyProvider).put(graph, 0);
            }
        }

        for (int i = 0; i < strategyProviders.size(); i++) {
            for (int j = i + 1; j < strategyProviders.size(); j++) {
                if (i == j) {
                    continue;
                }

                for (String graph : graphs) {
                    for (int trial = 0; trial < NUM_TRIALS; trial++) {
                        IStrategyProvider strategyProvider1;
                        IStrategyProvider strategyProvider2;
                        if (trial < NUM_TRIALS / 2) {
                            strategyProvider1 = strategyProviders.get(i);
                            strategyProvider2 = strategyProviders.get(j);
                        } else {
                            strategyProvider1 = strategyProviders.get(j);
                            strategyProvider2 = strategyProviders.get(i);
                        }

                        IStrategy strategy1 = strategyProvider1.newInstance();
                        IStrategy strategy2 = strategyProvider2.newInstance();

                        PlanetWars planetWars = new PlanetWars(strategy1, strategy2, graph, false);
                        IStrategy winner = planetWars.runToCompletion(10000);
                        if (winner == strategy1) {
                            wins.put(strategyProvider1, wins.get(strategyProvider1) + 1);
                            winsPerMap.get(strategyProvider1).put(graph, winsPerMap.get(strategyProvider1).get(graph) + 1);
                        } else if (winner == strategy2) {
                            wins.put(strategyProvider2, wins.get(strategyProvider2) + 1);
                            winsPerMap.get(strategyProvider2).put(graph, winsPerMap.get(strategyProvider2).get(graph) + 1);
                        } else {
                            // No one won, neither player gets a point
                        }
                    }
                }
            }
        }

        System.out.println("Rankings:");
        List<Map.Entry<IStrategyProvider, Integer>> winsPairs = new ArrayList<>(wins.entrySet());
        winsPairs.sort(new Comparator<Map.Entry<IStrategyProvider, Integer>>() {
            @Override
            public int compare(Map.Entry<IStrategyProvider, Integer> e1, Map.Entry<IStrategyProvider, Integer> e2) {
                return -1 * e1.getValue().compareTo(e2.getValue());
            }
        });

        for (Map.Entry<IStrategyProvider, Integer> entry : winsPairs) {
            System.out.println(String.format("Strategy: %s, Wins: %d", entry.getKey().newInstance().getName(), entry.getValue()));
        }

        System.out.println();
        System.out.println("Wins per Map");
        List<Map.Entry<IStrategyProvider, Map<String, Integer>>> winsPerMapPairs = new ArrayList<>(winsPerMap.entrySet());
        for (Map.Entry<IStrategyProvider, Map<String, Integer>> entry : winsPerMapPairs) {
            List<Map.Entry<String, Integer>> winsPerMapInnerPairs = new ArrayList<>(entry.getValue().entrySet());
            winsPerMapInnerPairs.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                    return -1 * e1.getValue().compareTo(e2.getValue());
                }
            });
            System.out.println(String.format("Strategy: %s, Wins: %s", entry.getKey().newInstance().getName(), winsPerMapInnerPairs));
        }
    }

    /**
     * Interface for getting an instance of a strategy, without knowing where it came from.
     */
    private interface IStrategyProvider {
        IStrategy newInstance() throws IllegalAccessException, InstantiationException;
    }

    /**
     * Class providing instances of a strategy which can be referred to directly, e.g. {@code MyStrategy.class}
     */
    private static class ClassStrategyProvider implements IStrategyProvider {
        private Class<? extends IStrategy> strategyClass;

        public ClassStrategyProvider(Class<? extends IStrategy> strategyClass) {
            this.strategyClass = strategyClass;
        }

        @Override
        public IStrategy newInstance() throws IllegalAccessException, InstantiationException {
            return strategyClass.newInstance();
        }
    }

    /**
     * Class providing instances of a strategy which has been jarred up, e.g. {@code "AI1_obf"}
     */
    private static class JarStrategyProvider implements IStrategyProvider {
        private String jar;

        public JarStrategyProvider(String jar) {
            this.jar = jar;
        }

        @Override
        public IStrategy newInstance() throws IllegalAccessException, InstantiationException {
            IStrategy instance = Assets.loadPlayer(jar);
            if (instance == null) {
                throw new InstantiationException("Failed to instantiate " + jar);
            }
            return instance;
        }
    }
}
