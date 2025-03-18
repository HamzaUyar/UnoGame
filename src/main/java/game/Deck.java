package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;
import main.java.cards.NumberCard;
import main.java.cards.actioncards.*;

/**
 * Deck represents the initial set of cards in an UNO game.
 * It is responsible for creating and managing all cards at the start of the game.
 * Follows the Factory pattern for creating different types of cards.
 */
public class Deck {
    private List<Card> cards;
    private static final String[] COLORS = {"Red", "Green", "Blue", "Yellow"};
    
    /**
     * Constructs a new, empty deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }
    
    /**
     * Initializes the deck with all UNO cards according to standard UNO rules.
     * Creates number cards, action cards, and wild cards.
     */
    public void initializeDeck() {
        // Add number cards (0-9) for each color
        for (String color : COLORS) {
            // Add one 0 card for each color
            cards.add(new NumberCard(color, 0));
            
            // Add two of each number 1-9 for each color
            for (int i = 1; i <= 9; i++) {
                cards.add(new NumberCard(color, i));
                cards.add(new NumberCard(color, i));
            }
        }
        
        // Add action cards for each color
        for (String color : COLORS) {
            // Add two Skip cards for each color
            cards.add(new SkipCard(color));
            cards.add(new SkipCard(color));
            
            // Add two Reverse cards for each color
            cards.add(new ReverseCard(color));
            cards.add(new ReverseCard(color));
            
            // Add two Draw Two cards for each color
            cards.add(new DrawTwoCard(color));
            cards.add(new DrawTwoCard(color));
        }
        
        // Add 4 Wild cards
        for (int i = 0; i < 4; i++) {
            cards.add(new WildCard());
        }
        
        // Add 4 Wild Draw Four cards
        for (int i = 0; i < 4; i++) {
            cards.add(new WildDrawFourCard());
        }
        
        // Add 1 Shuffle Hands card (custom card)
        cards.add(new ShuffleHandsCard());
    }
    
    /**
     * Shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    /**
     * Deals a specified number of cards from the deck.
     * 
     * @param count The number of cards to deal
     * @return A list containing the dealt cards
     */
    public List<Card> dealCards(int count) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < count && !cards.isEmpty(); i++) {
            dealtCards.add(cards.remove(0));
        }
        return dealtCards;
    }
    
    /**
     * Gets the cards remaining in the deck.
     * Returns a defensive copy to maintain encapsulation.
     * 
     * @return A copy of the cards list
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    /**
     * Checks if the deck is empty.
     * 
     * @return True if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /**
     * Gets the number of cards in the deck.
     * 
     * @return The number of cards
     */
    public int size() {
        return cards.size();
    }
} 