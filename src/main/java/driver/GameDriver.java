package main.java.driver;

import main.java.game.GameMediator;
import main.java.players.Player;
import main.java.utils.ConsoleColors;

/**
 * GameDriver serves as the entry point for the UNO game application.
 * It follows the Facade pattern by providing a simplified interface to the complex game system.
 */
public class GameDriver {
    
    /**
     * The main method that initializes and runs the UNO game.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        GameDriver driver = new GameDriver();
        driver.runGame();
    }
    
    /**
     * Initializes and runs a demo UNO game with 4 players.
     */
    public void runGame() {
        printWelcomeMessage();
        
        // Create game mediator
        GameMediator mediator = createGameMediator();
        
        // Add players
        addPlayers(mediator, 4);
        
        // Start the game
        mediator.startGame();
        
        // Run the game for a maximum of 20 turns (just for demonstration)
        runGameTurns(mediator, 20);
        
        printGameCompletionMessage();
    }
    
    /**
     * Prints a welcome message and game rules to the console.
     */
    private void printWelcomeMessage() {
        System.out.println(ConsoleColors.formatHeader(ConsoleColors.RED_BOLD + "U" + 
                                                      ConsoleColors.GREEN_BOLD + "N" + 
                                                      ConsoleColors.BLUE_BOLD + "O" + 
                                                      ConsoleColors.RESET + 
                                                      ConsoleColors.YELLOW_BOLD + " GAME SIMULATOR"));
        
        System.out.println(ConsoleColors.WHITE_BOLD + "Game Rules:" + ConsoleColors.RESET);
        System.out.println("• Match cards by color or number");
        System.out.println("• Action cards: Skip, Reverse, Draw Two, Wild, Wild Draw Four");
        System.out.println("• First player to get rid of all cards wins the round");
        System.out.println("• Winner gets points equal to the sum of opponents' card values");
        System.out.println("• First player to reach 500 points wins the game!");
        System.out.println(ConsoleColors.SHORT_DIVIDER);
    }
    
    /**
     * Prints a game completion message.
     */
    private void printGameCompletionMessage() {
        System.out.println(ConsoleColors.formatHeader("GAME DEMO COMPLETED"));
        System.out.println(ConsoleColors.YELLOW_BOLD + "Thanks for playing UNO!" + ConsoleColors.RESET);
    }
    
    /**
     * Creates and initializes a GameMediator instance.
     * 
     * @return The initialized GameMediator
     */
    private GameMediator createGameMediator() {
        return new GameMediator();
    }
    
    /**
     * Adds a specified number of players to the game.
     * 
     * @param mediator The game mediator
     * @param numPlayers The number of players to add
     */
    private void addPlayers(GameMediator mediator, int numPlayers) {
        for (int i = 1; i <= numPlayers; i++) {
            Player player = new Player("Player" + i);
            mediator.addPlayer(player);
        }
    }
    
    /**
     * Runs the game for a specified maximum number of turns.
     * 
     * @param mediator The game mediator
     * @param maxTurns The maximum number of turns to run
     */
    private void runGameTurns(GameMediator mediator, int maxTurns) {
        for (int i = 0; i < maxTurns; i++) {
            Player currentPlayer = mediator.getCurrentPlayer();
            mediator.handleTurn(currentPlayer);
            
            // Game might have ended after this turn
            if (currentPlayer.getHand().isEmpty()) {
                break;
            }
        }
    }
} 