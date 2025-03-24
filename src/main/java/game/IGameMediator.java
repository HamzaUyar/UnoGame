package main.java.game;

import main.java.cards.Card;
import main.java.players.Player;
import java.util.List;

/**
 * Interface defining the Mediator pattern contract for UNO game coordination.
 * Responsible for coordinating interactions between game components without them
 * having to refer to each other directly.
 * 
 * <p>The IGameMediator interface is the core of the Mediator design pattern implementation
 * in this UNO game. It centralizes the complex communications between different game components,
 * reducing coupling and making the system more maintainable.</p>
 * 
 * <p>This mediator handles all aspects of game flow including:</p>
 * <ul>
 *   <li>Player management - adding, removing, and tracking players</li>
 *   <li>Game state - managing the progression of the game through different phases</li>
 *   <li>Card interactions - validating and executing card plays</li>
 *   <li>Turn management - tracking whose turn it is and managing turn order</li>
 *   <li>Component registration - allowing components to register themselves with the mediator</li>
 * </ul>
 * 
 * <p>By implementing this interface, a mediator can ensure that all game components
 * communicate through a central point rather than directly with each other, promoting
 * loose coupling and modular design.</p>
 */
public interface IGameMediator {
    
    /**
     * Registers a game component with the mediator.
     * This is a critical method for the Mediator pattern as it establishes
     * the connection between the mediator and its controlled components.
     * 
     * @param component The component to register
     */
    void registerComponent(IGameComponent component);
    
    /**
     * Starts a new game.
     * Initializes all necessary components and game state to begin gameplay.
     */
    void startGame();
    
    /**
     * Handles a player's turn.
     * Coordinates all actions that happen during a player's turn,
     * including drawing cards, playing cards, and applying card effects.
     * 
     * @param player The player whose turn it is
     */
    void handleTurn(Player player);
    
    /**
     * Ends the current round and processes results.
     * Calculates scores, updates game state, and prepares for the next round
     * or ends the game if a player has reached the winning score.
     * 
     * @param winner The player who won the round
     */
    void endRound(Player player);
    
    /**
     * Creates and adds a specified number of players to the game.
     * Handles player initialization and setup.
     * 
     * @param numPlayers The number of players to create
     */
    void createPlayers(int numPlayers);
    
    /**
     * Adds a player to the game.
     * 
     * @param player The player to add
     */
    void addPlayer(Player player);
    
    /**
     * Gets the next player in turn order.
     * Takes into account the current direction of play (clockwise or counter-clockwise).
     * 
     * @return The next player
     */
    Player getNextPlayer();
    
    /**
     * Switches the direction of play.
     * Typically triggered by a Reverse card.
     */
    void switchDirection();
    
    /**
     * Allows a player to draw a card.
     * Mediates the interaction between the player and the draw pile.
     * 
     * @return The drawn card
     */
    Card requestDraw();
    
    /**
     * Gets all players in the game.
     * 
     * @return A copy of the list of players
     */
    List<Player> getPlayers();
    
    /**
     * Gets the current player.
     * 
     * @return The current player
     */
    Player getCurrentPlayer();
    
    /**
     * Sets the current player.
     * 
     * @param player The new current player
     */
    void setCurrentPlayer(Player player);
    
    /**
     * Redistributes all cards between players.
     * Typically used with a Shuffle Hands card effect.
     */
    void redistributeHands();
    
    /**
     * Validates whether a Wild Draw Four play is legal.
     * According to UNO rules, a Wild Draw Four can only be played if the player
     * has no matching color cards in their hand.
     * 
     * @param player The player who played the card
     * @return True if the play is valid, false otherwise
     */
    boolean validateWildDrawFour(Player player);
    
    /**
     * Checks if the game is over.
     * In UNO, the game is typically over when a player reaches 500 points.
     * 
     * @return True if the game is over, false otherwise
     */
    boolean isGameOver();
} 