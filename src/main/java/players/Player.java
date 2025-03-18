package main.java.players;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.java.cards.Card;
import main.java.game.GameMediator;

public class Player {
    private String name;
    private List<Card> hand;
    private GameMediator mediator;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void playCard(Card card) {
        hand.remove(card);
    }

    public void drawCard() {
        Card drawnCard = mediator.requestDraw();
        hand.add(drawnCard);
        System.out.println(name + " drew " + drawnCard);
    }

    public Card selectPlayableCard(Card topCard) {
        // Find all playable cards
        List<Card> playableCards = hand.stream()
                .filter(card -> isPlayable(card, topCard))
                .collect(Collectors.toList());

        if (playableCards.isEmpty()) {
            return null;
        }

        // Select the card with the highest value
        return playableCards.stream()
                .max((c1, c2) -> Integer.compare(c1.getValue(), c2.getValue()))
                .orElse(playableCards.get(0));
    }

    private boolean isPlayable(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) || 
               card.getType().equals(topCard.getType()) ||
               card.getType().equals("Wild") ||
               card.getType().equals("Wild Draw Four");
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }
} 