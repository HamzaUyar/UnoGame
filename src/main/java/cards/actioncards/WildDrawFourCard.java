package main.java.cards.actioncards;

import main.java.players.Player;
import main.java.utils.ConsoleColors;

/**
 * WildDrawFourCard represents a Wild Draw Four card in UNO.
 * It allows the player to change the current color and forces the next player to draw 4 cards.
 * It extends WildCard to leverage the color selection logic.
 */
public class WildDrawFourCard extends WildCard {

    /**
     * Constructs a new Wild Draw Four card with no initial color and a value of 50 points.
     * Overrides the type from the parent WildCard class.
     */
    public WildDrawFourCard() {
        super();
        this.type = "Wild Draw Four"; // Override type from parent class
    }

    /**
     * Applies the Wild Draw Four card effect by changing the color and setting up
     * the next player to draw 4 cards and lose their turn.
     * First validates that the play is legal according to official UNO rules.
     */
    @Override
    public void applyEffect() {
        if (mediator == null) {
            throw new IllegalStateException("Card not connected to a game mediator");
        }
        
        Player currentPlayer = mediator.getCurrentPlayer();
        
        // Validate play (current player has no cards matching the color on top)
        if (!mediator.validateWildDrawFour(currentPlayer)) {
            System.out.println(ConsoleColors.RED_BOLD + "⚠️ Invalid Wild Draw Four play! Player has matching color card. ⚠️" + ConsoleColors.RESET);
            return;
        }
        
        // Select color using the parent class method (most frequent in hand)
        String chosenColor = selectColorBasedOnPlayerHand(currentPlayer);
        
        // Set the color of the card
        this.color = chosenColor;
        
        // Display color change
        System.out.println(ConsoleColors.highlight("🌈➕ " + currentPlayer.getName() + " changes color to " + 
            ConsoleColors.formatColor(chosenColor) + " and next player draws 4 cards! 🌈➕"));
                
        // Next player effects handled by GameMediator
    }
} 