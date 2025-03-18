package main.java.cards;

import main.java.game.GameMediator;

public class NumberCard extends Card {
    
    public NumberCard(String color, int number) {
        super(color, number + "", number);
    }
    
    @Override
    public void applyEffect(GameMediator mediator) {
        // Number cards have no special effect
    }
} 