package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;
import main.java.cards.NumberCard;
import main.java.cards.actioncards.*;

public class Deck {
    private List<Card> cards;
    
    public Deck() {
        cards = new ArrayList<>();
    }
    
    public void initializeDeck() {
        // Create colors
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        
        // Add number cards (0-9) for each color
        for (String color : colors) {
            // Add one 0 card for each color
            cards.add(new NumberCard(color, 0));
            
            // Add two of each number 1-9 for each color
            for (int i = 1; i <= 9; i++) {
                cards.add(new NumberCard(color, i));
                cards.add(new NumberCard(color, i));
            }
        }
        
        // Add action cards for each color
        for (String color : colors) {
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
        
        // Add 1 Shuffle Hands card
        cards.add(new ShuffleHandsCard());
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public List<Card> dealCards(int count) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < count && !cards.isEmpty(); i++) {
            dealtCards.add(cards.remove(0));
        }
        return dealtCards;
    }
    
    public List<Card> getCards() {
        return cards;
    }
} 