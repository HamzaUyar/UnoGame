package main.java.game;

/**
 * Enum representing the different types of game components that can be registered with the mediator.
 * 
 * <p>This enum is used to categorize components in the game, allowing the mediator to track
 * and manage different types of components separately. It's a key part of the Mediator pattern
 * implementation, enabling organized component registration and lookup.</p>
 * 
 * <p>Each component type corresponds to a specific role in the UNO game:</p>
 */
public enum GameComponentType {
    /** Represents a player in the game */
    PLAYER,
    
    /** Represents the main deck of cards */
    DECK,
    
    /** Represents the pile that players draw cards from */
    DRAW_PILE,
    
    /** Represents the pile where played cards are placed */
    DISCARD_PILE,
    
    /** Represents the component that tracks and calculates scores */
    SCORE_TRACKER,
    
    /** Represents an individual card */
    CARD
} 