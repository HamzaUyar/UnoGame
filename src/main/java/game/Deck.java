package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;
import main.java.cards.NumberCard;
import main.java.cards.actioncards.*;

/**
 * Deck represents the complete set of cards used in the UNO game.
 * It is responsible for creating the initial set of cards and shuffling them.
 * Implements IGameComponent interface to participate in the Mediator pattern.
 */
public class Deck implements IGameComponent {
    private List<Card> cards;
    private IGameMediator mediator;
    
    /**
     * Constructs a new, empty deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }
    
    /**
     * Sets the mediator for this component.
     * 
     * @param mediator The mediator to set
     */
    @Override
    public void setMediator(IGameMediator mediator) {
        this.mediator = mediator;
    }
    
    /**
     * Gets the type of this component.
     * 
     * @return The component type (DECK)
     */
    @Override
    public GameComponentType getComponentType() {
        return GameComponentType.DECK;
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
            
            // Add two of each 1-9 cards
            for (int value = 1; value <= 9; value++) {
                cards.add(new NumberCard(color, value));
                cards.add(new NumberCard(color, value));
            }
            
            // Add action cards (2 of each per color)
            cards.add(new SkipCard(color));
            cards.add(new SkipCard(color));
            
            cards.add(new ReverseCard(color));
            cards.add(new ReverseCard(color));
            
            cards.add(new DrawTwoCard(color));
            cards.add(new DrawTwoCard(color));
        }
        
        // Add Wild cards (4 of each)
        for (int i = 0; i < 4; i++) {
            cards.add(new WildCard());
            cards.add(new WildDrawFourCard());
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
     * 
     * @param numCards The number of cards to deal
     * @return A list of dealt cards
     * @throws IllegalStateException if there are not enough cards
     */
    public List<Card> dealCards(int numCards) {
        if (cards.size() < numCards) {
            throw new IllegalStateException("Not enough cards in the deck");
        }
        
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numCards; i++) {
            dealtCards.add(cards.remove(0));
        }
        
        return dealtCards;
    }
    
    /**
     * Returns a card to the deck.
     * 
     * @param card The card to return
     */
    public void returnCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot return null card to deck");
        }
        cards.add(card);
    }
    
    /**
     * Gets a copy of all cards in the deck.
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
     * Prints the top N cards of the deck for debugging.
     * 
     * @param count The number of cards to print
     */
    public void printTopCards(int count) {
        int toPrint = Math.min(count, cards.size());
        for (int i = 0; i < toPrint; i++) {
            System.out.println(cards.get(i));
        }
    }
} 