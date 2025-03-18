package main.java.cards.actioncards;

import main.java.game.GameMediator;

public class ShuffleHandsCard extends ActionCard {

    public ShuffleHandsCard() {
        super("", "Shuffle Hands", 50); // Special card, no specific color
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        System.out.println("Shuffle Hands card played! All hands will be collected, shuffled, and redistributed.");
        mediator.redistributeHands();
    }
} 