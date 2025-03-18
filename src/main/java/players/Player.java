package main.java.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import main.java.cards.Card;
import main.java.game.GameMediator;
import main.java.utils.ConsoleColors;

/**
 * Player class represents a player in the UNO game.
 * It encapsulates the player's state (name, hand) and behavior (playing cards).
 * Follows the Mediator pattern by interacting with the game through GameMediator.
 */
public class Player {
    private final String name;
    private List<Card> hand;
    private GameMediator mediator;

    /**
     * Constructs a new Player with the given name.
     * 
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Plays a card from the player's hand.
     * 
     * @param card The card to play
     */
    public void playCard(Card card) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Player doesn't have this card in hand");
        }
        hand.remove(card);
    }

    /**
     * Draws a card from the draw pile (via the mediator).
     */
    public void drawCard() {
        if (mediator == null) {
            throw new IllegalStateException("Player is not connected to a game mediator");
        }
        Card drawnCard = mediator.requestDraw();
        hand.add(drawnCard);
        System.out.println(ConsoleColors.WHITE + name + " drew " + ConsoleColors.formatCard(drawnCard.toString()) + ConsoleColors.RESET);
    }

    /**
     * Selects a playable card from the player's hand.
     * Implements strategy for card selection.
     * 
     * @param topCard The current top card on the discard pile
     * @return The selected card, or null if no playable card exists
     */
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

    /**
     * Determines if a card is playable on the current top card.
     * 
     * @param card The card to check
     * @param topCard The top card on the discard pile
     * @return True if the card is playable, false otherwise
     */
    private boolean isPlayable(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) || 
               card.getType().equals(topCard.getType()) ||
               card.getType().equals("Wild") ||
               card.getType().equals("Wild Draw Four");
    }

    /**
     * Gets the player's name.
     * 
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's hand.
     * Returns an unmodifiable view of the hand to enforce encapsulation.
     * 
     * @return An unmodifiable view of the player's hand
     */
    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Sets the player's hand to the provided list of cards.
     * Makes a defensive copy to maintain encapsulation.
     * 
     * @param hand The new hand
     */
    public void setHand(List<Card> hand) {
        this.hand = new ArrayList<>(hand);
    }

    /**
     * Adds a card to the player's hand.
     * 
     * @param card The card to add
     */
    public void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to hand");
        }
        hand.add(card);
    }

    /**
     * Clears all cards from the player's hand.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Sets the game mediator for this player.
     * 
     * @param mediator The game mediator
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }
} 