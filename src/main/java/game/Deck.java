package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import main.java.cards.Card;
import main.java.cards.NumberCard;
import main.java.cards.actioncards.*;

/**
 * Deck represents the complete set of cards used in the UNO game.
 * It is responsible for creating the initial set of cards and shuffling them.
 * Follows the Component role in the Mediator pattern.
 */
public class Deck implements IGameComponent {
    private static final String[] COLORS = {"Red", "Green", "Blue", "Yellow"};
    private static final int NUM_WILD_CARDS = 4;
    
    private final List<Card> cards;
    private final IGameMediator mediator;
    
    /**
     * Constructs a new, empty deck with a mediator.
     * 
     * @param mediator The game mediator
     */
    public Deck(IGameMediator mediator) {
        this.cards = new ArrayList<>();
        this.mediator = mediator;
    }
    
    /**
     * Gets the component type of this deck.
     *
     * @return The component type
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
        
        createNumberCards();
        createActionCards();
        createWildCards();
        
        // Shuffle the deck
        shuffle();
    }
    
    /**
     * Creates number cards for each color (0-9).
     * One 0 card and two of each 1-9 card per color.
     */
    private void createNumberCards() {
        for (String color : COLORS) {
            // Add one 0 card for each color
            cards.add(new NumberCard(color, 0, mediator));
            
            // Add two 1-9 cards for each color
            for (int num = 1; num <= 9; num++) {
                cards.add(new NumberCard(color, num, mediator));
                cards.add(new NumberCard(color, num, mediator));
            }
        }
    }
    
    /**
     * Creates action cards for each color.
     * Two of each action card type per color.
     */
    private void createActionCards() {
        for (String color : COLORS) {
            // Add action cards (two of each per color)
            IntStream.range(0, 2).forEach(i -> {
                cards.add(new SkipCard(color, mediator));
                cards.add(new ReverseCard(color, mediator));
                cards.add(new DrawTwoCard(color, mediator));
            });
        }
    }
    
    /**
     * Creates wild cards.
     * Four of each wild card type.
     */
    private void createWildCards() {
        IntStream.range(0, NUM_WILD_CARDS).forEach(i -> {
            cards.add(new WildCard(mediator));
            cards.add(new WildDrawFourCard(mediator));
        });
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
        int cardsToPrint = Math.min(count, cards.size());
        System.out.println("Top " + cardsToPrint + " cards in deck:");
        
        IntStream.range(0, cardsToPrint)
                .forEach(i -> System.out.println((i+1) + ". " + cards.get(i)));
        
        System.out.println();
    }
} 