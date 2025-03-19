package main.java.players;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

import main.java.cards.Card;
import main.java.game.IGameMediator;
import main.java.utils.ConsoleColors;

/**
 * Player class represents a player in the UNO game.
 * It encapsulates the player's state (name, hand) and behavior (playing cards).
 * Follows the Mediator pattern by interacting with the game through IGameMediator.
 */
public class Player {
    private final String name;
    private List<Card> hand;
    private final IGameMediator mediator;
    private boolean isDealer;

    /**
     * Constructs a new Player with the given name and mediator.
     * 
     * @param name The player's name
     * @param mediator The game mediator
     */
    public Player(String name, IGameMediator mediator) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.mediator = mediator;
        this.isDealer = false;
    }

    /**
     * Plays a card from the player's hand.
     * 
     * @param card The card to play
     * @throws IllegalArgumentException if the card is not in the player's hand
     */
    public void playCard(Card card) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Player doesn't have this card in hand");
        }
        hand.remove(card);
    }

    /**
     * Draws a card from the draw pile (via the mediator).
     * 
     * @throws IllegalStateException if the player is not connected to a mediator
     */
    public void drawCard() {
        ensureMediatorConnected();
        Card drawnCard = mediator.requestDraw();
        hand.add(drawnCard);
        System.out.println(ConsoleColors.WHITE + name + " drew " + ConsoleColors.formatCard(drawnCard.toString()) + ConsoleColors.RESET);
    }
    
    /**
     * Ensures the player is connected to a mediator.
     * 
     * @throws IllegalStateException if the player is not connected to a mediator
     */
    private void ensureMediatorConnected() {
        if (mediator == null) {
            throw new IllegalStateException("Player is not connected to a game mediator");
        }
    }

    /**
     * Selects a playable card from the player's hand.
     * Implements strategy for card selection.
     * 
     * @param topCard The current top card on the discard pile
     * @return The selected card, or null if no playable card exists
     */
    public Card selectPlayableCard(Card topCard) {
        // Try to play a wild card with some probability
        Card wildCard = selectWildCard();
        if (wildCard != null) {
            return wildCard;
        }
        
        // Find regular playable cards
        List<Card> regularPlayableCards = findRegularPlayableCards(topCard);
        if (!regularPlayableCards.isEmpty()) {
            return selectBestRegularCard(regularPlayableCards);
        }
        
        // Check if we can play a Wild Draw Four card
        Card wildDrawFour = selectWildDrawFour(topCard);
        if (wildDrawFour != null) {
            return wildDrawFour;
        }
        
        // No playable cards
        return null;
    }
    
    /**
     * Tries to select a Wild card with 30% probability.
     * 
     * @return A Wild card or null
     */
    private Card selectWildCard() {
        List<Card> wildCards = hand.stream()
                .filter(card -> card.getType().equals("Wild"))
                .collect(Collectors.toList());
        
        // Play a Wild card with 30% probability if available
        if (!wildCards.isEmpty() && ThreadLocalRandom.current().nextDouble() < 0.3) {
            return wildCards.get(0);
        }
        
        return null;
    }
    
    /**
     * Finds all regular (non-Wild Draw Four) playable cards.
     * 
     * @param topCard The current top card
     * @return List of playable cards
     */
    private List<Card> findRegularPlayableCards(Card topCard) {
        return hand.stream()
                .filter(card -> !card.getType().equals("Wild Draw Four") && isPlayable(card, topCard))
                .collect(Collectors.toList());
    }
    
    /**
     * Selects the best card from regular playable cards based on value.
     * 
     * @param playableCards List of playable cards
     * @return The selected card
     */
    private Card selectBestRegularCard(List<Card> playableCards) {
        return playableCards.stream()
                .max((c1, c2) -> Integer.compare(c1.getValue(), c2.getValue()))
                .orElse(playableCards.get(0));
    }
    
    /**
     * Tries to select a Wild Draw Four card if valid to play.
     * 
     * @param topCard The current top card
     * @return A Wild Draw Four card or null
     */
    private Card selectWildDrawFour(Card topCard) {
        List<Card> wildDrawFourCards = hand.stream()
                .filter(card -> card.getType().equals("Wild Draw Four"))
                .collect(Collectors.toList());
                
        if (!wildDrawFourCards.isEmpty()) {
            // Check if player has any card matching the top card color
            boolean hasMatchingColor = hand.stream()
                .anyMatch(card -> card.getColor().equals(topCard.getColor()));
                
            // Can only play Wild Draw Four if no matching color
            if (!hasMatchingColor) {
                return wildDrawFourCards.get(0);
            }
        }
        
        return null;
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
     * Returns a defensive copy to prevent external modification.
     * 
     * @return A copy of the player's hand
     */
    public List<Card> getHand() {
        return new ArrayList<>(hand);
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
     * @throws IllegalArgumentException if the card is null
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
     * Calculates the total point value of all cards in the player's hand.
     * 
     * @return The sum of all card values in the hand
     */
    public int calculateHandValue() {
        return hand.stream()
                .mapToInt(Card::getValue)
                .sum();
    }
    
    /**
     * Sets this player as the dealer or removes dealer status.
     * 
     * @param isDealer True to set as dealer, false to remove dealer status
     */
    public void setAsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }
    
    /**
     * Checks if this player is the dealer.
     * 
     * @return True if this player is the dealer, false otherwise
     */
    public boolean isDealer() {
        return isDealer;
    }
} 