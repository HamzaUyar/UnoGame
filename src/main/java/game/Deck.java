package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import main.java.cards.Card;
import main.java.cards.NumberCard;
import main.java.cards.actioncards.*;

/**
 * Deck represents the complete set of cards used in the UNO game.
 * It is responsible for creating the initial set of cards and shuffling them.
 */
public class Deck {
    private List<Card> cards;
    private GameMediator mediator;
    private static final String[] COLORS = {"Red", "Green", "Blue", "Yellow"};
    
    /**
     * Constructs a new, empty deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }
    
    /**
     * Sets the game mediator for this deck.
     *
     * @param mediator The game mediator
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }
    
    /**
     * Initializes the deck with a standard set of UNO cards:
     * - 76 number cards (0-9 in each color)
     * - 24 action cards (Skip, Reverse, Draw Two in each color)
     * - 8 Wild cards (4 regular Wild, 4 Wild Draw Four)
     * And shuffles the deck.
     */
    public void initializeDeck() {
        cards.clear();
        
        // Create number cards (0-9)
        // One 0 and two 1-9 of each color
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        
        for (String color : colors) {
            // Add one 0 card for each color
            cards.add(new NumberCard(color, 0));
            
            // Add two 1-9 cards for each color
            for (int num = 1; num <= 9; num++) {
                cards.add(new NumberCard(color, num));
                cards.add(new NumberCard(color, num));
            }
            
            // Add action cards (two of each per color)
            for (int i = 0; i < 2; i++) {
                cards.add(new SkipCard(color));
                cards.add(new ReverseCard(color));
                cards.add(new DrawTwoCard(color));
            }
        }
        
        // Add Wild cards
        for (int i = 0; i < 4; i++) {
            cards.add(new WildCard());
            cards.add(new WildDrawFourCard());
        }
        
        // Set the mediator for each card
        if (mediator != null) {
            for (Card card : cards) {
                card.setMediator(mediator);
            }
        }
        
        // Shuffle the deck
        shuffle();
    }
    
    /**
     * Shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    /**
     * Deals a specified number of cards from the deck.
     * Removes the cards from the deck.
     * 
     * @param numCards The number of cards to deal
     * @return A list of the dealt cards
     */
    public List<Card> dealCards(int numCards) {
        List<Card> dealtCards = new ArrayList<>();
        
        for (int i = 0; i < numCards && !cards.isEmpty(); i++) {
            dealtCards.add(cards.remove(0));
        }
        
        return dealtCards;
    }
    
    /**
     * Returns a card to the deck.
     * Useful when reshuffling discarded cards back into the deck.
     * 
     * @param card The card to return to the deck
     */
    public void returnCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }
    
    /**
     * Gets the list of cards in the deck.
     * Returns a defensive copy to maintain encapsulation.
     * 
     * @return A copy of the list of cards
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    /**
     * Checks if the deck is empty.
     * 
     * @return True if the deck has no cards, false otherwise
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
    
    /**
     * For debugging: prints the first n cards in the deck.
     * 
     * @param count The number of cards to print
     */
    public void printTopCards(int count) {
        System.out.println("Top " + Math.min(count, cards.size()) + " cards in deck:");
        for (int i = 0; i < Math.min(count, cards.size()); i++) {
            System.out.println((i+1) + ". " + cards.get(i));
        }
        System.out.println();
    }
} 