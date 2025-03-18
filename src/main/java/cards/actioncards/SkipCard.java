package main.java.cards.actioncards;

import main.java.game.GameMediator;
import main.java.players.Player;

public class SkipCard extends ActionCard {

    public SkipCard(String color) {
        super(color, "Skip", 20);
    }

    @Override
    public void applyEffect(GameMediator mediator) {
        Player skippedPlayer = mediator.getNextPlayer();
        mediator.setCurrentPlayer(mediator.getNextPlayer());
        System.out.println(skippedPlayer.getName() + "'s turn has been skipped!");
    }
} 