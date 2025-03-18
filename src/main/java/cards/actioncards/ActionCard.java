package main.java.cards.actioncards;

import main.java.cards.Card;
import main.java.game.GameMediator;

public abstract class ActionCard extends Card {
    
    public ActionCard(String color, String type, int value) {
        super(color, type, value);
    }
    
    @Override
    public abstract void applyEffect(GameMediator mediator);
} 