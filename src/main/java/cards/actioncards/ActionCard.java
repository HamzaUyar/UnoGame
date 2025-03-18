package main.java.cards.actioncards;

import main.java.cards.Card;
import main.java.game.GameMediator;

/**
 * ActionCard is the abstract base class for all cards with special effects.
 * It extends Card and represents a higher level of abstraction for action cards.
 * This class follows the Template Method design pattern.
 */
public abstract class ActionCard extends Card {
    
    /**
     * Constructs a new ActionCard with the given color, type, and value.
     * 
     * @param color The color of the card
     * @param type The type/name of the action card
     * @param value The point value of the card
     */
    public ActionCard(String color, String type, int value) {
        super(color, type, value);
    }
    
    /**
     * Each ActionCard must implement this method to apply its specific effect to the game.
     * This achieves polymorphism through different implementations in subclasses.
     */
    @Override
    public abstract void applyEffect();
} 