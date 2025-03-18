package main.java.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.cards.Card;

public class DrawPile {
    private List<Card> cards;
    
    public DrawPile() {
        cards = new ArrayList<>();
    }
    
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
    public void addCard(Card card) {
        cards.add(card);
    }
    
    public void setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }
    
    public List<Card> getCards() {
        return cards;
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
} 