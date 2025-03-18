package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.utils.ConsoleColors;

/**
 * ReverseCard represents a Reverse card in UNO which changes the direction of play.
 * It extends ActionCard to provide specific behavior for Reverse cards.
 */
public class ReverseCard extends ActionCard {

    /**
     * Constructs a new Reverse card with the specified color and a value of 20 points.
     * 
     * @param color The color of the Reverse card
     */
    public ReverseCard(String color) {
        super(color, "Reverse", 20);
    }

    /**
     * Applies the Reverse card effect by switching the direction of play.
     * 
     * @param mediator The game mediator to apply effects to
     */
    @Override
    public void applyEffect(GameMediator mediator) {
        mediator.switchDirection();
        System.out.println(ConsoleColors.CYAN_BOLD + "↩️ Direction of play has been reversed! ↩️" + ConsoleColors.RESET);
    }
} 