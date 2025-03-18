package main.java.game;

/**
 * Interface that all game components must implement.
 * Provides a standard way for components to register with the mediator.
 */
public interface IGameComponent {
    
    /**
     * Sets the mediator for this component.
     * 
     * @param mediator The mediator to set
     */
    void setMediator(IGameMediator mediator);
    
    /**
     * Gets the type of this component.
     * 
     * @return The component type
     */
    GameComponentType getComponentType();
} 