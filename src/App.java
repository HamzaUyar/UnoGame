import main.java.driver.GameDriver;
import main.java.utils.ConsoleColors;

/**
 * Main application class that serves as the entry point for the UNO game.
 */
public class App {
    /**
     * Main method that starts the UNO game.
     * 
     * @param args Command line arguments (not used)
     * @throws Exception If an error occurs during game execution
     */
    public static void main(String[] args) throws Exception {
        System.out.println(ConsoleColors.CYAN_BOLD + "Starting UNO Game..." + ConsoleColors.RESET);
        System.out.println();
        GameDriver.main(args);
    }
}
