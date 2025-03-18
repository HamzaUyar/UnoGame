package main.java.tests;

import main.java.game.Deck;
import main.java.cards.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class to verify that deck shuffling is working properly
 * and producing random results.
 */
public class ShuffleTest {
    
    public static void main(String[] args) {
        System.out.println("Running deck shuffle test...");
        
        // Test multiple shuffles
        for (int test = 1; test <= 5; test++) {
            System.out.println("\n=== Test " + test + " ===");
            
            // Create and initialize a new deck
            Deck deck = new Deck();
            deck.initializeDeck();
            
            // Print top cards before shuffle
            System.out.println("Before shuffle:");
            deck.printTopCards(4);
            
            // Shuffle deck
            System.out.println("Shuffling deck...");
            deck.shuffle();
            
            // Print top cards after shuffle
            System.out.println("After shuffle:");
            deck.printTopCards(4);
            
            // Test drawing multiple times to simulate dealer selection
            System.out.println("Simulating dealer selection (drawing 4 cards):");
            List<Card> drawnCards = new ArrayList<>();
            
            for (int i = 0; i < 4; i++) {
                List<Card> cards = deck.dealCards(1);
                if (!cards.isEmpty()) {
                    Card card = cards.get(0);
                    drawnCards.add(card);
                    System.out.println("Player " + (i+1) + " draws: " + card);
                }
            }
            
            // Return the cards
            System.out.println("\nReturning cards to deck...");
            for (Card card : drawnCards) {
                deck.returnCard(card);
            }
            
            // Shuffle again
            System.out.println("Shuffling deck again...");
            deck.shuffle();
            
            // Test drawing again to see if different
            System.out.println("Redrawing 4 cards to check randomness:");
            for (int i = 0; i < 4; i++) {
                List<Card> cards = deck.dealCards(1);
                if (!cards.isEmpty()) {
                    Card card = cards.get(0);
                    System.out.println("Player " + (i+1) + " now draws: " + card);
                }
            }
            
            System.out.println("\n");
        }
        
        System.out.println("Shuffle test complete!");
    }
} 