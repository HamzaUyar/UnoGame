package main.java.game;

/**
 * Interface that all game components must implement.
 * Components receive their mediator through constructor injection.
 */
public interface IGameComponent {
    /**
     * Gets the type of this component.
     * 
     * @return The component type
     */
    GameComponentType getComponentType();
} 