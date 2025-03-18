package main.java.driver;

import main.java.game.GameMediator;
import main.java.players.Player;

public class GameDriver {
    
    public static void main(String[] args) {
        // Create game mediator
        GameMediator mediator = new GameMediator();
        
        // Create players (2-4)
        int numPlayers = 4; // Can be 2-4
        for (int i = 1; i <= numPlayers; i++) {
            Player player = new Player("Player" + i);
            mediator.addPlayer(player);
        }
        
        // Start the game
        mediator.startGame();
        
        // Run the game for a maximum of 20 turns (just for demonstration)
        for (int i = 0; i < 20; i++) {
            Player currentPlayer = mediator.getCurrentPlayer();
            mediator.handleTurn(currentPlayer);
            
            // Game might have ended after this turn
            if (currentPlayer.getHand().isEmpty()) {
                break;
            }
        }
        
        System.out.println("Game demo completed.");
    }
} 