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
        // Clear any existing cards first
        cards.clear();
        
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
        
        // Always shuffle after initialization
        shuffle();
    }
    
    /**
     * Shuffles the cards in the deck using multiple randomization techniques.
     * This ensures true randomness in card order.
     */
    public void shuffle() {
        if (cards.isEmpty()) {
            return;
        }
        
        // 1. Use ThreadLocalRandom for better randomization
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // 2. Fisher-Yates shuffle algorithm
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap cards[i] with cards[j]
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
        
        // 3. Also use Collections.shuffle with a new random seed
        Collections.shuffle(cards, new Random(System.nanoTime()));
        
        // 4. Cut the deck at a random point (another common shuffling technique)
        int cutPoint = random.nextInt(cards.size());
        List<Card> topHalf = new ArrayList<>(cards.subList(0, cutPoint));
        cards.subList(0, cutPoint).clear();
        cards.addAll(topHalf);
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
     * Returns a card to the deck.
     * Used when returning cards used to determine starting player.
     * 
     * @param card The card to return to the deck
     */
    public void returnCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
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