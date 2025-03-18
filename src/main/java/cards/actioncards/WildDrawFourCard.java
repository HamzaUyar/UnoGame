package main.java.cards.actioncards;

import java.util.Random;

import main.java.game.GameMediator;
import main.java.players.Player;

public class WildDrawFourCard extends ActionCard {

    public WildDrawFourCard() {
        super("", "Wild Draw Four", 50); // No color initially
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        Player currentPlayer = mediator.getCurrentPlayer();
        
        // Validate play (current player has no cards matching the color on top)
        if (!mediator.validateWildDrawFour(currentPlayer)) {
            System.out.println("Invalid Wild Draw Four play! Player has matching color card.");
            return;
        }
        
        // Simulate player choosing a color
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        Random random = new Random();
        String chosenColor = colors[random.nextInt(colors.length)];
        
        // Set the color of the card
        this.color = chosenColor;
        
        // Force next player to draw 4 cards
        Player nextPlayer = mediator.getNextPlayer();
        for (int i = 0; i < 4; i++) {
            nextPlayer.drawCard();
        }
        
        // Skip the next player's turn
        mediator.setCurrentPlayer(mediator.getNextPlayer());
        
        System.out.println("Wild Draw Four played! Color changed to " + chosenColor);
        System.out.println(nextPlayer.getName() + " draws 4 cards and loses their turn!");
    }
} 