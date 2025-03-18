package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.utils.ConsoleColors;

/**
 * ShuffleHandsCard represents a special action card that shuffles all players' hands.
 * When played, all cards from all players are collected, shuffled, and redistributed.
 * It extends ActionCard to provide specific behavior for this special action.
 */
public class ShuffleHandsCard extends ActionCard {

    /**
     * Constructs a new Shuffle Hands card with no specific color and a value of 50 points.
     */
    public ShuffleHandsCard() {
        super("", "Shuffle Hands", 50); // Special card, no specific color
    }

    /**
     * Applies the Shuffle Hands effect by redistributing all cards among players.
     */
    @Override
    public void applyEffect() {
        if (mediator == null) {
            throw new IllegalStateException("Card not connected to a game mediator");
        }
        
        System.out.println(ConsoleColors.YELLOW_BOLD + "Shuffle Hands card played! All hands will be collected, shuffled, and redistributed." + ConsoleColors.RESET);
        mediator.redistributeHands();
    }
} 