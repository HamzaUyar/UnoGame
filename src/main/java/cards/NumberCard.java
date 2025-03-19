package main.java.cards;

import main.java.game.IGameMediator;

/**
 * NumberCard represents the number cards in the UNO deck.
 * It extends the abstract Card class and provides a concrete implementation 
 * with no special effects.
 */
public class NumberCard extends Card {
    
    /**
     * Constructs a new NumberCard with the specified color and number.
     * The number is used as both the type and value of the card.
     * 
     * @param color The color of the card
     * @param number The number on the card (0-9)
     */
    public NumberCard(String color, int number) {
        super(color, number + "", number);
    }
    
    /**
     * Constructs a new NumberCard with the specified color, number, and mediator.
     * The number is used as both the type and value of the card.
     * 
     * @param color The color of the card
     * @param number The number on the card (0-9)
     * @param mediator The game mediator
     */
    public NumberCard(String color, int number, IGameMediator mediator) {
        super(color, number + "", number, mediator);
    }
    
    /**
     * Number cards have no special effect when played.
     * This is a concrete implementation of the abstract method from Card.
     */
    @Override
    public void applyEffect() {
        // Number cards have no special effect
    }
} 