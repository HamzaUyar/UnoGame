package main.java.ui;

import java.util.List;
import main.java.cards.Card;
import main.java.utils.ConsoleColors;

/**
 * Handles all user interface presentation for the UNO game.
 * This class centralizes UI concerns, separating them from game logic.
 */
public class GameUI {
    
    /**
     * Displays the welcome message and game rules to the console.
     */
    public void displayWelcomeMessage() {
        System.out.println();
        // Create a fancy UNO title with rainbow effect
        String unoTitle = ConsoleColors.rainbow("  U N O  ");
        printFancyBanner(unoTitle, ConsoleColors.CYAN_BOLD_BRIGHT);
        
        System.out.println();
        
        System.out.println(ConsoleColors.formatHeader(ConsoleColors.RED_BOLD_BRIGHT + "U" + 
                                                     ConsoleColors.GREEN_BOLD_BRIGHT + "N" + 
                                                     ConsoleColors.BLUE_BOLD_BRIGHT + "O" + 
                                                     ConsoleColors.RESET + 
                                                     ConsoleColors.YELLOW_BOLD_BRIGHT + " GAME SIMULATOR"));
        
        System.out.println(ConsoleColors.WHITE_BOLD + "Game Rules:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.WHITE + "• Match cards by color or number");
        System.out.println("• Action cards: Skip, Reverse, Draw Two, Wild, Wild Draw Four");
        System.out.println("• First player to get rid of all cards wins the round");
        System.out.println("• Winner gets points equal to the sum of opponents' card values");
        System.out.println("• First player to reach 500 points wins the game!" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.SHORT_DIVIDER);
    }
    
    /**
     * Displays a game completion message.
     */
    public void displayGameCompletionMessage() {
        System.out.println(ConsoleColors.formatHeader("GAME COMPLETED"));
        
        String thankYouMessage = "Thanks for playing UNO!";
        printFancyBanner(thankYouMessage, ConsoleColors.YELLOW_BOLD_BRIGHT);
    }
    
    /**
     * Creates a fancy banner with the text centered
     * 
     * @param text Text to display in the banner
     * @param color Color for the banner
     */
    private void printFancyBanner(String text, String color) {
        int bannerWidth = 60;
        int padding = (bannerWidth - text.length()) / 2;
        
        StringBuilder banner = new StringBuilder();
        banner.append("\n").append(color);
        
        // Top border with stars
        for (int i = 0; i < bannerWidth; i++) {
            banner.append("*");
        }
        
        // Empty line
        banner.append("\n*");
        for (int i = 0; i < bannerWidth - 2; i++) {
            banner.append(" ");
        }
        banner.append("*");
        
        // Text line
        banner.append("\n*");
        for (int i = 0; i < padding; i++) {
            banner.append(" ");
        }
        banner.append(text);
        for (int i = 0; i < padding - (text.length() % 2 == 0 ? 0 : 1); i++) {
            banner.append(" ");
        }
        banner.append("*");
        
        // Empty line
        banner.append("\n*");
        for (int i = 0; i < bannerWidth - 2; i++) {
            banner.append(" ");
        }
        banner.append("*");
        
        // Bottom border with stars
        banner.append("\n");
        for (int i = 0; i < bannerWidth; i++) {
            banner.append("*");
        }
        
        banner.append(ConsoleColors.RESET);
        System.out.println(banner.toString());
    }
    
    /**
     * Displays the round header.
     * 
     * @param roundNumber The current round number
     */
    public void displayRoundHeader(int roundNumber) {
        System.out.println(ConsoleColors.formatHeader("UNO GAME - ROUND " + roundNumber));
    }
    
    /**
     * Displays the game setup complete message.
     * 
     * @param dealerName The name of the dealer/starting player
     */
    public void displayGameSetupComplete(String dealerName) {
        System.out.println(ConsoleColors.formatSubHeader("GAME SETUP COMPLETE"));
        System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT + "Dealer/Starting player: " + 
                          ConsoleColors.formatPlayerName(dealerName));
    }
    
    /**
     * Displays the determining dealer message.
     */
    public void displayDeterminingDealerHeader() {
        System.out.println(ConsoleColors.formatSubHeader("DETERMINING DEALER"));
        System.out.println(ConsoleColors.WHITE_BRIGHT + "Each player draws a card; highest card value becomes the dealer." + ConsoleColors.RESET);
    }
    
    /**
     * Displays a player drawing a card during dealer determination.
     * 
     * @param playerName The name of the player
     * @param cardDescription The description of the card drawn
     */
    public void displayPlayerDrawingCard(String playerName, String cardDescription) {
        System.out.println(ConsoleColors.formatPlayerName(playerName) + " draws " + ConsoleColors.formatCard(cardDescription));
    }
    
    /**
     * Displays the dealer selection result.
     * 
     * @param dealerName The name of the selected dealer
     * @param cardDescription The description of the highest card
     */
    public void displayDealerSelected(String dealerName, String cardDescription) {
        System.out.println(ConsoleColors.highlight(ConsoleColors.formatPlayerName(dealerName) + 
            " has the highest card: " + ConsoleColors.formatCard(cardDescription) + 
            " and will be the dealer!"));
    }
    
    /**
     * Displays a message about cards being returned to the deck.
     */
    public void displayCardsReturnedToDeck() {
        System.out.println(ConsoleColors.WHITE_BRIGHT + "Cards returned to deck for shuffling." + ConsoleColors.RESET);
    }
    
    /**
     * Displays a message about the deck being shuffled.
     */
    public void displayDeckShuffled() {
        System.out.println(ConsoleColors.WHITE_BRIGHT + "Dealer shuffles the deck." + ConsoleColors.RESET);
    }
    
    /**
     * Displays the dealing cards header.
     * 
     * @param dealerName The name of the dealer
     */
    public void displayDealingCardsHeader(String dealerName) {
        System.out.println(ConsoleColors.formatSubHeader("DEALING CARDS"));
        System.out.println(ConsoleColors.WHITE_BRIGHT + "Dealer (" + 
                          ConsoleColors.formatPlayerName(dealerName) + ") deals 7 cards to each player." + 
                          ConsoleColors.RESET);
    }
    
    /**
     * Displays a deal round header.
     * 
     * @param roundNum The dealing round number
     */
    public void displayDealRoundHeader(int roundNum) {
        System.out.println(ConsoleColors.CYAN_BRIGHT + "\n🎴 Deal Round " + roundNum + ":" + ConsoleColors.RESET);
    }
    
    /**
     * Displays a player's hand.
     * 
     * @param playerName The name of the player
     * @param hand The player's hand of cards
     */
    public void displayPlayerHand(String playerName, List<Card> hand) {
        StringBuilder handStr = new StringBuilder();
        handStr.append(ConsoleColors.formatPlayerName(playerName)).append(": ");
        
        for (int j = 0; j < hand.size(); j++) {
            handStr.append(ConsoleColors.formatCard(hand.get(j).toString()));
            if (j < hand.size() - 1) {
                handStr.append(" ");
            }
        }
        System.out.println(handStr.toString());
    }
    
    /**
     * Displays a message that all players have been dealt cards.
     */
    public void displayAllPlayersDealt() {
        System.out.println(ConsoleColors.GREEN_BRIGHT + "\n✓ All players have been dealt 7 cards each." + ConsoleColors.RESET);
    }
    
    /**
     * Displays the starting card.
     * 
     * @param cardDescription The description of the starting card
     */
    public void displayStartingCard(String cardDescription) {
        System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT + "Starting card: " + 
                          ConsoleColors.formatCard(cardDescription) + ConsoleColors.RESET);
    }
    
    /**
     * Displays a player's turn header.
     * 
     * @param playerName The name of the player whose turn it is
     */
    public void displayPlayerTurnHeader(String playerName) {
        System.out.println(ConsoleColors.formatSubHeader("🎮 " + playerName + "'S TURN"));
    }
    
    /**
     * Displays the top card.
     * 
     * @param cardDescription The description of the top card
     */
    public void displayTopCard(String cardDescription) {
        System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT + "Top card: " + 
                          ConsoleColors.formatCard(cardDescription) + ConsoleColors.RESET);
    }
    
    /**
     * Displays a player playing a card.
     * 
     * @param playerName The name of the player
     * @param cardDescription The description of the card being played
     */
    public void displayPlayerPlayingCard(String playerName, String cardDescription) {
        System.out.println(ConsoleColors.highlight(ConsoleColors.formatPlayerName(playerName) + " plays " + 
                                                 ConsoleColors.formatCard(cardDescription)));
    }
    
    /**
     * Displays a message that a player has no playable cards and must draw.
     * 
     * @param playerName The name of the player
     */
    public void displayPlayerDrawingCardOnTurn(String playerName) {
        System.out.println(ConsoleColors.WHITE_BRIGHT + 
                          ConsoleColors.formatPlayerName(playerName) + 
                          " has no playable cards and draws a card" + ConsoleColors.RESET);
    }
    
    /**
     * Displays a player playing a drawn card.
     * 
     * @param playerName The name of the player
     * @param cardDescription The description of the drawn card
     */
    public void displayPlayerPlayingDrawnCard(String playerName, String cardDescription) {
        System.out.println(ConsoleColors.highlight(ConsoleColors.formatPlayerName(playerName) + 
                                                 " plays drawn card: " + 
                                                 ConsoleColors.formatCard(cardDescription)));
    }
    
    /**
     * Displays a message that a drawn card cannot be played.
     */
    public void displayDrawnCardCannotBePlayed() {
        System.out.println(ConsoleColors.WHITE_BRIGHT + "⛔ Drawn card cannot be played. End turn." + ConsoleColors.RESET);
    }
    
    /**
     * Displays a message about how many cards a player has left.
     * 
     * @param playerName The name of the player
     * @param cardCount The number of cards the player has left
     */
    public void displayPlayerCardCount(String playerName, int cardCount) {
        String cardEmoji = cardCount == 1 ? "⚠️ " : "🎴 ";
        System.out.println(ConsoleColors.WHITE_BRIGHT + cardEmoji + 
                          ConsoleColors.formatPlayerName(playerName) + " has " + 
                          ConsoleColors.YELLOW_BOLD_BRIGHT + cardCount + 
                          ConsoleColors.WHITE_BRIGHT + " card" + (cardCount == 1 ? "" : "s") + " left" + 
                          ConsoleColors.RESET);
        
        // Add UNO warning if player has only one card
        if (cardCount == 1) {
            System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "   ⚠️  UNO!  ⚠️" + ConsoleColors.RESET);
        }
    }
    
    /**
     * Displays a message that a player has won the round.
     * 
     * @param playerName The name of the player who won
     */
    public void displayPlayerWinsRound(String playerName) {
        String message = "🎉 " + playerName + " WINS THE ROUND! 🎉";
        System.out.println(ConsoleColors.formatHeader(ConsoleColors.YELLOW_BOLD_BRIGHT + message));
    }
    
    /**
     * Displays a message that a player has won the game.
     * 
     * @param playerName The name of the player who won
     * @param score The player's final score
     */
    public void displayPlayerWinsGame(String playerName, int score) {
        String winMessage = "🏆 " + playerName + " WINS THE GAME WITH " + score + " POINTS! 🏆";
        System.out.println(ConsoleColors.formatHeader(ConsoleColors.YELLOW_BOLD_BRIGHT + winMessage));
        
        // Display a trophy ASCII art
        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT);
        System.out.println("       ___________      ");
        System.out.println("      '._==_==_=_.'     ");
        System.out.println("      .-\\:      /-.    ");
        System.out.println("     | (|:.     |) |    ");
        System.out.println("      '-|:.     |-'     ");
        System.out.println("        \\::.    /      ");
        System.out.println("         '::. .'        ");
        System.out.println("           ) (          ");
        System.out.println("         _.' '._        ");
        System.out.println("        '-------'       " + ConsoleColors.RESET);
    }
    
    /**
     * Displays a message about preparing for the next round.
     * 
     * @param roundNumber The upcoming round number
     */
    public void displayPreparingForNextRound(int roundNumber) {
        System.out.println(ConsoleColors.CYAN_BOLD + "\nPreparing for round " + 
                           roundNumber + "...\n" + ConsoleColors.RESET);
    }
    
    /**
     * Displays the header for applying a card effect.
     */
    public void displayApplyingCardEffectHeader() {
        System.out.println(ConsoleColors.formatSubHeader("APPLYING CARD EFFECT"));
    }
    
    /**
     * Displays a message about applying a card's effect.
     * 
     * @param cardDescription The description of the card whose effect is being applied
     */
    public void displayApplyingCardEffect(String cardDescription) {
        System.out.println(ConsoleColors.WHITE + "Applying effect of " + 
                           ConsoleColors.formatCard(cardDescription) + ConsoleColors.RESET);
    }
    
    /**
     * Displays a message about replenishing the draw pile.
     */
    public void displayReplenishingDrawPile() {
        System.out.println(ConsoleColors.CYAN_BOLD + "Draw pile empty! Reshuffling discard pile..." + ConsoleColors.RESET);
    }
    
    /**
     * Displays a message that the discard pile has been reshuffled.
     */
    public void displayDiscardPileReshuffled() {
        System.out.println(ConsoleColors.CYAN_BOLD + "Discard pile reshuffled and added to draw pile." + ConsoleColors.RESET);
    }
    
    /**
     * Displays a warning message about no cards left in the deck.
     */
    public void displayNoCardsLeftWarning() {
        System.out.println(ConsoleColors.RED_BOLD + 
                          "Warning: No cards left in deck or discard pile! Creating a fallback Wild card." + 
                          ConsoleColors.RESET);
    }
    
    /**
     * Displays the player's detailed hand.
     * 
     * @param playerName The name of the player
     * @param hand The player's hand of cards
     */
    public void displayDetailedPlayerHand(String playerName, List<Card> hand) {
        StringBuilder handStr = new StringBuilder();
        handStr.append(ConsoleColors.WHITE_BOLD).append(playerName).append("'s hand: ").append(ConsoleColors.RESET);
        
        for (int i = 0; i < hand.size(); i++) {
            handStr.append(ConsoleColors.formatCard(hand.get(i).toString()));
            if (i < hand.size() - 1) {
                handStr.append(" ");
            }
        }
        System.out.println(handStr.toString());
    }
    
    /**
     * Displays a message indicating that a dealer has been selected.
     * 
     * @param dealerName The name of the dealer
     */
    public void displayDealerSelectedMessage(String dealerName) {
        System.out.println(ConsoleColors.CYAN_BRIGHT + "✓ " + dealerName + " will be the dealer." + ConsoleColors.RESET);
        System.out.println(ConsoleColors.SHORT_DIVIDER);
    }
} 