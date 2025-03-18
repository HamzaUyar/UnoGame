package main.java.cards.actioncards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import main.java.cards.Card;
import main.java.game.GameMediator;
import main.java.players.Player;
import main.java.utils.ConsoleColors;

/**
 * WildCard represents a Wild card in UNO which allows the player to change the current color.
 * It extends ActionCard to provide special effect behavior for Wild cards.
 */
public class WildCard extends ActionCard {

    /**
     * Constructs a new Wild card with no initial color and a value of 50 points.
     */
    public WildCard() {
        super("", "Wild", 50); // No color initially
    }

    /**
     * Applies the Wild card effect by selecting a new color based on the current player's hand.
     * The color is selected to maximize the player's advantage (most common color in hand).
     */
    @Override
    public void applyEffect() {
        if (mediator == null) {
            throw new IllegalStateException("Card not connected to a game mediator");
        }
        
        Player currentPlayer = mediator.getCurrentPlayer();
        String chosenColor = selectColorBasedOnPlayerHand(currentPlayer);
        
        // Set the color of the card
        this.color = chosenColor;
        
        System.out.println(ConsoleColors.highlight("🌈 " + currentPlayer.getName() + " changes color to " + 
            ConsoleColors.formatColor(chosenColor) + "! 🌈"));
    }
    
    /**
     * Selects a color based on the player's hand.
     * Chooses the color the player has the most cards of, or randomly if tied.
     * 
     * @param player The player who played the card
     * @return The selected color
     */
    protected String selectColorBasedOnPlayerHand(Player player) {
        List<Card> hand = player.getHand();
        
        // Count cards by color
        Map<String, Integer> colorCounts = new HashMap<>();
        colorCounts.put("Red", 0);
        colorCounts.put("Green", 0);
        colorCounts.put("Blue", 0);
        colorCounts.put("Yellow", 0);
        
        // Count cards of each color
        for (Card card : hand) {
            String cardColor = card.getColor();
            if (cardColor != null && !cardColor.isEmpty() && !cardColor.equals("")) {
                colorCounts.put(cardColor, colorCounts.getOrDefault(cardColor, 0) + 1);
            }
        }
        
        // Find the color with the maximum count
        String maxColor = "Red"; // Default
        int maxCount = -1;
        boolean isTied = false;
        
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxColor = entry.getKey();
                maxCount = entry.getValue();
                isTied = false;
            } else if (entry.getValue() == maxCount && maxCount > 0) {
                isTied = true;
            }
        }
        
        // If tied or no colored cards, choose randomly
        if (isTied || maxCount == 0) {
            String[] colors = {"Red", "Green", "Blue", "Yellow"};
            // Use ThreadLocalRandom instead of new Random() for better performance
            ThreadLocalRandom random = ThreadLocalRandom.current();
            maxColor = colors[random.nextInt(colors.length)];
        }
        
        return maxColor;
    }
} 