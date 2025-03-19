package main.java.cards;

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
     * Number cards have no special effect when played.
     * This is a concrete implementation of the abstract method from Card.
     */
    @Override
    public void applyEffect() {
        // Number cards have no special effect
    }
} 