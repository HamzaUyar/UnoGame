package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.players.Player;

/**
 * DrawTwoCard represents a Draw Two card in UNO which forces the next player to 
 * draw two cards and lose their turn.
 * It extends ActionCard to provide specific behavior for Draw Two cards.
 */
public class DrawTwoCard extends ActionCard {

    /**
     * Constructs a new Draw Two card with the specified color and a value of 20 points.
     * 
     * @param color The color of the Draw Two card
     */
    public DrawTwoCard(String color) {
        super(color, "Draw Two", 20);
    }

    /**
     * Applies the Draw Two card effect by forcing the next player to draw two cards
     * and lose their turn.
     */
    @Override
    public void applyEffect() {
        if (mediator == null) {
            throw new IllegalStateException("Card not connected to a game mediator");
        }
        
        Player nextPlayer = mediator.getNextPlayer();
        
        // Force next player to draw 2 cards
        for (int i = 0; i < 2; i++) {
            nextPlayer.drawCard();
        }
        
        // Skip the next player's turn
        mediator.setCurrentPlayer(mediator.getNextPlayer());
        System.out.println(nextPlayer.getName() + " draws 2 cards and loses their turn!");
    }
} 