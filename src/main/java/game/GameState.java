package main.java.game;

/**
 * Enum representing the possible states of the game.
 */
public enum GameState {
    /**
     * Game is initialized but not yet started
     */
    INITIALIZED,
    
    /**
     * Game is in progress (players are taking turns)
     */
    IN_PROGRESS,
    
    /**
     * Current round is over (someone has won the round)
     */
    ROUND_OVER,
    
    /**
     * Game is over (someone has reached the winning score)
     */
    GAME_OVER
} 