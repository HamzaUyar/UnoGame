package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;

/**
 * DrawPile represents the pile of cards that players draw from during the game.
 * It encapsulates the management of cards available for drawing.
 */
public class DrawPile {
    private List<Card> cards;
    private GameMediator mediator;
    
    /**
     * Constructs a new, empty draw pile.
     */
    public DrawPile() {
        this.cards = new ArrayList<>();
    }
    
    /**
     * Sets the game mediator for this draw pile.
     *
     * @param mediator The game mediator
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }
    
    /**
     * Draws a card from the top of the pile.
     * 
     * @return The drawn card, or null if the pile is empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
    /**
     * Adds a card to the bottom of the draw pile.
     * 
     * @param card The card to add
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to draw pile");
        }
        cards.add(card);
    }
    
    /**
     * Sets the cards in the draw pile to the provided list.
     * Makes a defensive copy to maintain encapsulation.
     * 
     * @param cards The new list of cards
     * @throws IllegalArgumentException if the cards list is null
     */
    public void setCards(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Cards list cannot be null");
        }
        this.cards = new ArrayList<>(cards);
    }
    
    /**
     * Gets the list of cards in the draw pile.
     * Returns a defensive copy to maintain encapsulation.
     * 
     * @return A copy of the cards list
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    /**
     * Checks if the draw pile is empty.
     * 
     * @return True if the pile is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /**
     * Shuffles the cards in the draw pile.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
} 