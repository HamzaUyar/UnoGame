package main.java.game;

import java.util.ArrayList;
import java.util.List;

import main.java.cards.Card;

public class DiscardPile {
    private List<Card> cards;
    
    public DiscardPile() {
        cards = new ArrayList<>();
    }
    
    public void addCard(Card card) {
        cards.add(0, card); // Add to the top of the pile
    }
    
    public Card getTopCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(0);
    }
    
    public Card removeTopCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
    public List<Card> getCards() {
        List<Card> allExceptTop = new ArrayList<>(cards);
        if (!allExceptTop.isEmpty()) {
            allExceptTop.remove(0);
        }
        return allExceptTop;
    }
    
    public void clearCards() {
        cards.clear();
    }
} 