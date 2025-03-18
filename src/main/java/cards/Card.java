package main.java.cards;

import main.java.game.GameMediator;

public abstract class Card {
    protected String color;
    protected String type;
    protected int value;

    public Card(String color, String type, int value) {
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public abstract void applyEffect(GameMediator mediator);

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
} 