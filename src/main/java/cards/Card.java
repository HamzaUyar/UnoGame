package main.java.cards;

import main.java.game.GameMediator;

/**
 * Abstract Card class forms the base for all card types in the game.
 * Implements abstraction by defining common structure and behavior for all cards.
 * Each card has a color, type, and value.
 */
public abstract class Card {
    protected String color;
    protected String type;
    protected int value;
    protected GameMediator mediator;

    /**
     * Constructs a new card with the given color, type, and value.
     *
     * @param color The color of the card (e.g., "Red", "Blue")
     * @param type The type of the card (e.g., "1", "Skip")
     * @param value The point value of the card
     */
    public Card(String color, String type, int value) {
        this.color = color;
        this.type = type;
        this.value = value;
    }

    /**
     * Applies the effect of this card.
     * This is a template method that must be implemented by subclasses.
     * Implements polymorphism through different implementations in subclasses.
     */
    public abstract void applyEffect();

    /**
     * Sets the game mediator for this card.
     * This allows the card to interact with the game state when its effect is applied.
     *
     * @param mediator The game mediator
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Gets the color of this card.
     *
     * @return The card's color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the type of this card.
     *
     * @return The card's type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the point value of this card.
     *
     * @return The card's value
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a string representation of this card.
     * 
     * @return A string in the format "color type"
     */
    @Override
    public String toString() {
        return color + " " + type;
    }
} 