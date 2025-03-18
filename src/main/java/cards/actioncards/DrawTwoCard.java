package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.players.Player;

public class DrawTwoCard extends ActionCard {

    public DrawTwoCard(String color) {
        super(color, "Draw Two", 20);
    }

    @Override
    public void applyEffect(GameMediator mediator) {
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