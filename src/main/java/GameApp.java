package main.java;

import main.java.game.GameMediator;
import main.java.game.IGameMediator;
import main.java.players.Player;
import main.java.ui.GameUI;
import main.java.utils.ConsoleLogger;

/**
 * GameApp serves as the entry point for the UNO game application.
 * It handles the game lifecycle and UI interactions.
 */
public class GameApp {
    private IGameMediator mediator;
    private GameUI ui;
    
    /**
     * Constructs a new GameApp with initialized components.
     */
    public GameApp() {
        this.mediator = new GameMediator();
        this.ui = new GameUI();
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
        ui.displayWelcomeMessage();
        
        // Create players
        mediator.createPlayers(4);
        
        // Start the game
        mediator.startGame();
        
        // Run the game until someone wins (reaches 500 points)
        boolean gameOver = false;
        while (!gameOver) {
            gameOver = runGameRound();
        }
        
        ui.displayGameCompletionMessage();
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