package main.java.game;

/**
 * Interface that all game components must implement.
 * Provides a standard way for components to register with the mediator.
 * 
 * <p>The IGameComponent interface is a fundamental part of the Mediator pattern
 * implementation in this UNO game. By implementing this interface, components can
 * be registered with the mediator and participate in coordinated interactions without
 * direct references to each other.</p>
 * 
 * <p>Components that implement this interface include:</p>
 * <ul>
 *   <li>Player - Represents a player in the game</li>
 *   <li>Deck - Manages the full set of UNO cards</li>
 *   <li>DrawPile - Manages the pile of cards players draw from</li>
 *   <li>DiscardPile - Manages the pile where played cards are placed</li>
 *   <li>ScoreTracker - Tracks and calculates player scores</li>
 * </ul>
 * 
 * <p>Each component type is identified by the {@link GameComponentType} enum.</p>
 */
public interface IGameComponent {
    
    /**
     * Sets the mediator for this component.
     * This establishes the communication channel between the component and the mediator.
     * 
     * @param mediator The mediator to set
     */
    void setMediator(IGameMediator mediator);
    
    /**
     * Gets the type of this component.
     * The component type is used by the mediator to categorize and manage
     * different types of components.
     * 
     * @return The component type
     */
    GameComponentType getComponentType();
} 