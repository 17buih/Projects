package planetwars.publicapi;

import planetwars.core.GameWindow;
import planetwars.strategies.Strategy;
import planetwars.strategies.NoOpStrategy;
import planetwars.strategies.RandomStrategy;

import javax.swing.*;
import java.io.FileNotFoundException;

public class Driver {
    public static void main(String[] args) {
        try {
            // You can either instantiate the GameWindow by specifying two strategy classes directly, or by
            // using the default constructor and picking the strategies at run time
            // GameWindow window = new GameWindow();
            // false means you are player2. Player2 will be BLUE in the graphics.
            GameWindow window = new GameWindow();

            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
            window.setResizable(false);
            window.setLocationRelativeTo(null);
        } catch (FileNotFoundException e) {
            System.out.println("Sorry the game could not start");
            e.printStackTrace();
        }
    }
}
