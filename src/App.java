import main.java.driver.GameDriver;

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
        System.out.println("Starting UNO Game...");
        GameDriver.main(args);
    }
}
