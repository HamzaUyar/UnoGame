package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.players.Player;
import main.java.utils.ConsoleColors;

/**
 * SkipCard represents a Skip card in UNO which causes the next player to lose their turn.
 * It extends ActionCard to provide specific behavior for Skip cards.
 */
public class SkipCard extends ActionCard {

    /**
     * Constructs a new Skip card with the specified color and a value of 20 points.
     * 
     * @param color The color of the Skip card
     */
    public SkipCard(String color) {
        super(color, "Skip", 20);
    }

    /**
     * Applies the Skip card effect by setting the current player to the player after the next player,
     * effectively skipping one player's turn.
     * 
     * @param mediator The game mediator to apply effects to
     */
    @Override
    public void applyEffect(GameMediator mediator) {
        Player skippedPlayer = mediator.getNextPlayer();
        mediator.setCurrentPlayer(mediator.getNextPlayer());
        System.out.println(ConsoleColors.YELLOW_BOLD + "🚫 " + skippedPlayer.getName() + "'s turn has been skipped! 🚫" + ConsoleColors.RESET);
    }
} 