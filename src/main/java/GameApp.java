package main.java;

import main.java.game.GameMediator;
import main.java.players.Player;
import main.java.utils.ConsoleColors;
import main.java.utils.ConsoleLogger;

/**
 * GameApp serves as the entry point for the UNO game application.
 * It handles the game lifecycle and UI interactions.
 */
public class GameApp {
    private GameMediator mediator;
    
    /**
     * Constructs a new GameApp with initialized components.
     */
    public GameApp() {
        this.mediator = new GameMediator();
    }
    
    /**
     * The main method that initializes and runs the UNO game.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize console logging
        ConsoleLogger.initialize();
        
        // Create and start the game
        GameApp app = new GameApp();
        app.startGame();
        
        // Stop console logging
        ConsoleLogger.restore();
    }
    
    /**
     * Starts the game by setting up and running game rounds until completion.
     */
    public void startGame() {
        displayWelcomeMessage();
        
        // Create players
        mediator.createPlayers(4);
        
        // Start the game
        mediator.startGame();
        
        // Run the game until someone wins (reaches 500 points)
        boolean gameOver = false;
        while (!gameOver) {
            gameOver = runGameRound();
        }
        
        displayGameCompletionMessage();
    }
    
    /**
     * Displays the welcome message and game rules to the console.
     */
    private void displayWelcomeMessage() {
        System.out.println(ConsoleColors.CYAN_BOLD + "Starting UNO Game..." + ConsoleColors.RESET);
        System.out.println();
        
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
     * Displays a game completion message.
     */
    private void displayGameCompletionMessage() {
        System.out.println(ConsoleColors.formatHeader("GAME DEMO COMPLETED"));
        System.out.println(ConsoleColors.YELLOW_BOLD + "Thanks for playing UNO!" + ConsoleColors.RESET);
    }
    
    /**
     * Runs a single round of the game until someone wins the round.
     * 
     * @return True if the game is over (someone reached 500 points), false if just the round ended
     */
    private boolean runGameRound() {
        boolean roundEnded = false;
        boolean gameOver = false;
        
        while (!roundEnded) {
            Player currentPlayer = mediator.getCurrentPlayer();
            mediator.handleTurn(currentPlayer);
            
            // Check if player won the round (empty hand)
            if (currentPlayer.getHand().isEmpty()) {
                roundEnded = true;
                
                // Check if game is over (player reached 500 points)
                if (mediator.isGameOver()) {
                    gameOver = true;
                }
            }
        }
        
        return gameOver;
    }
} 