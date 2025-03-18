package main.java.cards.actioncards;

import main.java.game.GameMediator;

public class ReverseCard extends ActionCard {

    public ReverseCard(String color) {
        super(color, "Reverse", 20);
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        mediator.switchDirection();
        System.out.println("Direction of play has been reversed!");
    }
} 