package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;

/**
 * DiscardPile represents the pile where players place their played cards.
 * It encapsulates the management of discarded cards during the game.
 */
public class DiscardPile {
    private List<Card> cards;
    
    /**
     * Constructs a new, empty discard pile.
     */
    public DiscardPile() {
        this.cards = new ArrayList<>();
    }
    
    /**
     * Adds a card to the top of the discard pile.
     * 
     * @param card The card to add
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to discard pile");
        }
        cards.add(0, card); // Add to the top of the pile
    }
    
    /**
     * Gets the top card of the discard pile without removing it.
     * 
     * @return The top card, or null if the pile is empty
     */
    public Card getTopCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(0);
    }
    
    /**
     * Removes and returns the top card from the discard pile.
     * 
     * @return The top card, or null if the pile is empty
     */
    public Card removeTopCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
    /**
     * Gets all cards except the top card from the discard pile.
     * Returns a defensive copy to maintain encapsulation.
     * 
     * @return A list of all cards except the top card
     */
    public List<Card> getCards() {
        List<Card> allExceptTop = new ArrayList<>(cards);
        if (!allExceptTop.isEmpty()) {
            allExceptTop.remove(0);
        }
        return Collections.unmodifiableList(allExceptTop);
    }
    
    /**
     * Clears all cards from the discard pile.
     */
    public void clearCards() {
        cards.clear();
    }
    
    /**
     * Checks if the discard pile is empty.
     * 
     * @return True if the pile is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /**
     * Gets the number of cards in the discard pile.
     * 
     * @return The number of cards
     */
    public int size() {
        return cards.size();
    }
} 