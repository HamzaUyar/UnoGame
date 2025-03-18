package main.java.cards.actioncards;

import java.util.Random;

import main.java.game.GameMediator;

public class WildCard extends ActionCard {

    public WildCard() {
        super("", "Wild", 50); // No color initially
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        // Simulate player choosing a color
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        Random random = new Random();
        String chosenColor = colors[random.nextInt(colors.length)];
        
        // Set the color of the card
        this.color = chosenColor;
        
        System.out.println("Wild card played! Color changed to " + chosenColor);
    }
} 