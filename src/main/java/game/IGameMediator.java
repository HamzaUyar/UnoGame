package main.java.game;

import main.java.cards.Card;
import main.java.players.Player;
import java.util.List;

/**
 * Interface defining the Mediator pattern contract for UNO game coordination.
 * Responsible for coordinating interactions between game components without them
 * having to refer to each other directly.
 */
public interface IGameMediator {
    
    /**
     * Registers a game component with the mediator.
     * 
     * @param component The component to register
     */
    void registerComponent(IGameComponent component);
    
    /**
     * Starts a new game.
     */
    void startGame();
    
    /**
     * Handles a player's turn.
     * 
     * @param player The player whose turn it is
     */
    void handleTurn(Player player);
    
    /**
     * Ends the current round and processes results.
     * 
     * @param winner The player who won the round
     */
    void endRound(Player player);
    
    /**
     * Creates and adds a specified number of players to the game.
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
     * 
     * @return The next player
     */
    Player getNextPlayer();
    
    /**
     * Switches the direction of play.
     */
    void switchDirection();
    
    /**
     * Allows a player to draw a card.
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
     */
    void redistributeHands();
    
    /**
     * Validates whether a Wild Draw Four play is legal.
     * 
     * @param player The player who played the card
     * @return True if the play is valid, false otherwise
     */
    boolean validateWildDrawFour(Player player);
    
    /**
     * Checks if the game is over.
     * 
     * @return True if the game is over, false otherwise
     */
    boolean isGameOver();
} 