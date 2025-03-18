package main.java.cards.actioncards;

import java.util.List;

import main.java.cards.Card;
import main.java.game.GameMediator;
import main.java.players.Player;
import main.java.utils.ConsoleColors;

public class WildDrawFourCard extends WildCard {

    public WildDrawFourCard() {
        super();
        this.type = "Wild Draw Four"; // Override type from parent class
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        Player currentPlayer = mediator.getCurrentPlayer();
        
        // Validate play (current player has no cards matching the color on top)
        if (!mediator.validateWildDrawFour(currentPlayer)) {
            System.out.println(ConsoleColors.RED_BOLD + "Invalid Wild Draw Four play! Player has matching color card." + ConsoleColors.RESET);
            return;
        }
        
        // Select color using the parent class method (most frequent in hand)
        String chosenColor = selectColorBasedOnPlayerHand(currentPlayer);
        
        // Set the color of the card
        this.color = chosenColor;
        
        // Display color change
        System.out.println(ConsoleColors.highlight("🌈➕ " + currentPlayer.getName() + " changes color to " + 
            chosenColor + " and next player draws 4 cards! 🌈➕"));
                
        // Next player effects handled by GameMediator
    }
} 