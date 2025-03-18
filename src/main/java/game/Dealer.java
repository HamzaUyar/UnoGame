package main.java.game;

import java.util.List;

import main.java.cards.Card;
import main.java.players.Player;
import main.java.utils.ConsoleColors;

/**
 * Dealer class represents the dealer in the UNO game.
 * It is responsible for shuffling the deck and dealing cards to players.
 * Follows the Single Responsibility Principle by focusing solely on card distribution.
 */
public class Dealer {
    private final Deck deck;
    private final DrawPile drawPile;
    private final DiscardPile discardPile;
    private Player dealerPlayer;
    
    /**
     * Constructs a new Dealer with the specified game components.
     * 
     * @param deck The deck of cards to deal from
     * @param drawPile The draw pile to place remaining cards
     * @param discardPile The discard pile to start with the top card
     */
    public Dealer(Deck deck, DrawPile drawPile, DiscardPile discardPile) {
        this.deck = deck;
        this.drawPile = drawPile;
        this.discardPile = discardPile;
    }
    
    /**
     * Sets the player who will be the dealer.
     * 
     * @param player The player who will be the dealer
     */
    public void setDealerPlayer(Player player) {
        this.dealerPlayer = player;
    }
    
    /**
     * Gets the player who is the dealer.
     * 
     * @return The dealer player
     */
    public Player getDealerPlayer() {
        return dealerPlayer;
    }
    
    /**
     * Shuffles the deck, including any cards used for determining the dealer.
     */
    public void shuffleDeck() {
        if (deck.isEmpty()) {
            deck.initializeDeck();
        }
        deck.shuffle();
        System.out.println(ConsoleColors.WHITE + "Dealer shuffles the deck." + ConsoleColors.RESET);
    }
    
    /**
     * Deals 7 cards to each player in a left direction.
     * The dealer draws one card from the deck, gives it to the next player,
     * and does this until each player (including the dealer) has 7 cards.
     * 
     * @param players The list of players to deal cards to
     * @param startingPlayerIndex The index of the player to start dealing to
     */
    public void dealCards(List<Player> players, int startingPlayerIndex) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Cannot deal cards to empty player list");
        }
        
        if (dealerPlayer == null) {
            throw new IllegalStateException("Dealer player must be set before dealing cards");
        }
        
        System.out.println(ConsoleColors.formatSubHeader("DEALING CARDS"));
        System.out.println(ConsoleColors.WHITE + "Dealer (" + dealerPlayer.getName() + ") deals 7 cards to each player." + ConsoleColors.RESET);
        
        // Clear any existing cards from players' hands
        for (Player player : players) {
            player.clearHand();
        }
        
        // Deal 7 cards to each player, one at a time in a left direction
        for (int cardNum = 0; cardNum < 7; cardNum++) {
            System.out.println(ConsoleColors.CYAN + "\nDeal Round " + (cardNum + 1) + ":" + ConsoleColors.RESET);
            
            for (int i = 0; i < players.size(); i++) {
                // Calculate the player index, starting from the player after the dealer
                int playerIndex = (startingPlayerIndex + i) % players.size();
                Player player = players.get(playerIndex);
                
                // Deal one card to this player
                List<Card> dealtCard = deck.dealCards(1);
                if (!dealtCard.isEmpty()) {
                    Card card = dealtCard.get(0);
                    player.addCardToHand(card);
                    
                    // Display the current hand for this player
                    StringBuilder handStr = new StringBuilder();
                    handStr.append(ConsoleColors.WHITE_BOLD).append(player.getName()).append(": ").append(ConsoleColors.RESET);
                    
                    List<Card> hand = player.getHand();
                    for (int j = 0; j < hand.size(); j++) {
                        handStr.append(ConsoleColors.formatCard(hand.get(j).toString()));
                        if (j < hand.size() - 1) {
                            handStr.append(" ");
                        }
                    }
                    System.out.println(handStr.toString());
                }
            }
        }
        
        System.out.println(ConsoleColors.GREEN + "\nAll players have been dealt 7 cards each." + ConsoleColors.RESET);
    }
    
    /**
     * Sets up the draw and discard piles after dealing.
     * Places the remaining deck as the Draw Pile and starts the Discard Pile.
     * If the top card drawn is an Action Card, its effect is followed immediately.
     * 
     * @return The starting card placed on the discard pile
     */
    public Card setupPiles() {
        // Initialize draw pile with remaining cards
        drawPile.setCards(deck.getCards());
        
        // Draw the first card for the discard pile
        Card startingCard = drawPile.drawCard();
        
        // If first card is a Wild Draw Four, put it back and draw another
        while (startingCard.getType().equals("Wild Draw Four")) {
            System.out.println(ConsoleColors.WHITE + "First card was a Wild Draw Four. Returning to deck and drawing another." + ConsoleColors.RESET);
            drawPile.addCard(startingCard);
            drawPile.shuffle();
            startingCard = drawPile.drawCard();
        }
        
        // Place the starting card on the discard pile
        discardPile.addCard(startingCard);
        
        System.out.println(ConsoleColors.WHITE_BOLD + "Starting card: " + ConsoleColors.formatCard(startingCard.toString()) + ConsoleColors.RESET);
        
        return startingCard;
    }
} 