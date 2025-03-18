package main.java.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.cards.Card;
import main.java.players.Player;
import main.java.utils.ConsoleColors;
import main.java.utils.ScoreTracker;

/**
 * GameMediator class implements the Mediator design pattern to coordinate 
 * interactions between game components without them having to refer to each other.
 * It encapsulates the game state and rules.
 */
public class GameMediator {
    private List<Player> players;
    private Player currentPlayer;
    private boolean isClockwise;
    private Deck deck;
    private DrawPile drawPile;
    private DiscardPile discardPile;
    private ScoreTracker scoreTracker;
    private GameState gameState;
    private int roundNumber = 1;
    private Dealer dealer;
    
    /**
     * Creates a new GameMediator instance with initialized components.
     */
    public GameMediator() {
        this.players = new ArrayList<>();
        this.isClockwise = true;
        this.deck = new Deck();
        this.drawPile = new DrawPile();
        this.discardPile = new DiscardPile();
        this.scoreTracker = new ScoreTracker();
        this.gameState = GameState.INITIALIZED;
        this.dealer = new Dealer(deck, drawPile, discardPile);
    }
    
    /**
     * Starts a new game.
     * Initializes the deck, deals cards to players, and sets up the discard pile.
     */
    public void startGame() {
        this.gameState = GameState.IN_PROGRESS;
        
        System.out.println(ConsoleColors.formatHeader("UNO GAME - ROUND " + roundNumber));
        
        // Start tracking the new round
        scoreTracker.startNewRound();
        
        // 1. Initialize deck
        deck.initializeDeck();
        
        // 2. Use Deck to determine dealer (highest card)
        determineStartingPlayer();
        
        // 3. Have dealer shuffle the deck
        dealer.shuffleDeck();
        
        // 4. Have dealer deal cards to players (7 each)
        int dealerIndex = players.indexOf(currentPlayer);
        dealer.dealCards(players, (dealerIndex + 1) % players.size()); // Start dealing to the left of the dealer
        
        // 5. Have dealer set up the draw and discard piles
        Card startingCard = dealer.setupPiles();
        
        // 6. Apply starting card effect if it's an action card
        if (!startingCard.getType().matches("\\d+")) {
            applyCardEffect(startingCard);
        }
        
        // 7. Print game setup
        System.out.println(ConsoleColors.formatSubHeader("GAME SETUP COMPLETE"));
        System.out.println(ConsoleColors.WHITE_BOLD + "Dealer/Starting player: " + ConsoleColors.YELLOW_BOLD + currentPlayer.getName() + ConsoleColors.RESET);
    }
    
    /**
     * Determines the starting player by having each player draw a card.
     * The player with the highest value card becomes the dealer/starting player.
     */
    private void determineStartingPlayer() {
        // First make sure we have a deck ready
        if (deck.isEmpty()) {
            deck.initializeDeck();
            deck.shuffle();
        }
        
        System.out.println(ConsoleColors.formatSubHeader("DETERMINING DEALER"));
        System.out.println(ConsoleColors.WHITE + "Each player draws a card; highest card value becomes the dealer." + ConsoleColors.RESET);
        
        // Create a map to store player -> card drawn
        Map<Player, Card> drawnCards = new HashMap<>();
        
        // Each player draws a card
        for (Player player : players) {
            Card drawnCard = deck.dealCards(1).get(0);
            drawnCards.put(player, drawnCard);
            System.out.println(player.getName() + " draws " + ConsoleColors.formatCard(drawnCard.toString()));
        }
        
        // Find the player with the highest card value
        Player startingPlayer = drawnCards.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getValue()))
                .map(Map.Entry::getKey)
                .orElse(players.get(0)); // Fallback to first player if there's a problem
        
        // Set the starting player and dealer
        currentPlayer = startingPlayer;
        dealer.setDealerPlayer(startingPlayer);
        
        Card highestCard = drawnCards.get(startingPlayer);
        System.out.println(ConsoleColors.highlight(startingPlayer.getName() + 
            " has the highest card: " + ConsoleColors.formatCard(highestCard.toString()) + 
            " and will be the dealer!"));
        
        // Return the drawn cards to the deck
        List<Card> cardsToReturn = new ArrayList<>(drawnCards.values());
        for (Card card : cardsToReturn) {
            deck.returnCard(card);
        }
        
        // Note: The dealer will shuffle the deck after this
        System.out.println(ConsoleColors.WHITE + "Cards returned to deck for shuffling." + ConsoleColors.RESET);
    }
    
    /**
     * Prints a player's hand in a nicely formatted way
     */
    private void printPlayerHand(Player player) {
        StringBuilder handStr = new StringBuilder();
        handStr.append(ConsoleColors.WHITE_BOLD).append(player.getName()).append("'s hand: ").append(ConsoleColors.RESET);
        
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            handStr.append(ConsoleColors.formatCard(hand.get(i).toString()));
            if (i < hand.size() - 1) {
                handStr.append(" ");
            }
        }
        System.out.println(handStr.toString());
    }
    
    /**
     * Handles a player's turn.
     * Manages card play, drawing, effects, and checking for round end.
     * 
     * @param player The player whose turn it is
     */
    public void handleTurn(Player player) {
        if (this.gameState != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Cannot handle turn when game is not in progress");
        }
        
        System.out.println(ConsoleColors.formatSubHeader(player.getName() + "'S TURN"));
        
        // 1. Check if player has playable cards
        Card topCard = discardPile.getTopCard();
        System.out.println(ConsoleColors.WHITE_BOLD + "Top card: " + ConsoleColors.formatCard(topCard.toString()) + ConsoleColors.RESET);
        
        // Print current player's hand
        printPlayerHand(player);
        
        Card selectedCard = player.selectPlayableCard(topCard);
        
        if (selectedCard != null) {
            // Player plays a card
            System.out.println(ConsoleColors.highlight(player.getName() + " plays " + ConsoleColors.formatCard(selectedCard.toString())));
            player.playCard(selectedCard);
            discardPile.addCard(selectedCard);
            
            // Track that a card was played
            scoreTracker.recordCardPlayed();
            
            applyCardEffect(selectedCard);
        } else {
            // Player must draw a card
            System.out.println(ConsoleColors.WHITE + player.getName() + " has no playable cards and draws a card" + ConsoleColors.RESET);
            Card drawnCard = drawPile.drawCard();
            player.addCardToHand(drawnCard);
            
            // Check if drawn card is playable
            if (isPlayable(drawnCard, topCard)) {
                System.out.println(ConsoleColors.highlight(player.getName() + " plays drawn card: " + ConsoleColors.formatCard(drawnCard.toString())));
                player.playCard(drawnCard);
                discardPile.addCard(drawnCard);
                
                // Track that a card was played
                scoreTracker.recordCardPlayed();
                
                applyCardEffect(drawnCard);
            } else {
                System.out.println(ConsoleColors.WHITE + "Drawn card cannot be played. End turn." + ConsoleColors.RESET);
            }
        }
        
        // Check if player has won
        if (player.getHand().isEmpty()) {
            System.out.println(ConsoleColors.formatHeader(ConsoleColors.YELLOW_BOLD + "🎉 " + player.getName() + " WINS THE ROUND! 🎉"));
            endRound(player);
            return;
        } else {
            System.out.println(ConsoleColors.WHITE + player.getName() + " has " + 
                ConsoleColors.YELLOW_BOLD + player.getHand().size() + ConsoleColors.WHITE + " cards left" + ConsoleColors.RESET);
        }
        
        // Move to next player
        currentPlayer = getNextPlayer();
    }
    
    /**
     * Applies the effect of the given card.
     * 
     * @param card The card whose effect to apply
     */
    private void applyCardEffect(Card card) {
        if (card.getType().matches("\\d+")) {
            // Number card, no effect
            return;
        }
        
        System.out.println(ConsoleColors.formatSubHeader("APPLYING CARD EFFECT"));
        System.out.println(ConsoleColors.WHITE + "Applying effect of " + ConsoleColors.formatCard(card.toString()) + ConsoleColors.RESET);
        
        // Apply effect based on card type
        switch (card.getType()) {
            case "Skip":
                System.out.println(ConsoleColors.highlight("🚫 " + getNextPlayer().getName() + "'s turn is skipped! 🚫"));
                currentPlayer = getNextPlayer(); // Skip the next player
                break;
                
            case "Reverse":
                isClockwise = !isClockwise;
                System.out.println(ConsoleColors.highlight("↩️ Direction reversed! Now playing " + 
                    (isClockwise ? "clockwise" : "counter-clockwise") + " ↩️"));
                break;
                
            case "Draw Two":
                Player nextPlayer = getNextPlayer();
                System.out.println(ConsoleColors.highlight("➕ " + nextPlayer.getName() + " must draw 2 cards! ➕"));
                for (int i = 0; i < 2; i++) {
                    Card drawnCard = requestDraw();
                    nextPlayer.addCardToHand(drawnCard);
                    System.out.println(ConsoleColors.WHITE + nextPlayer.getName() + " draws " + 
                        ConsoleColors.formatCard(drawnCard.toString()) + ConsoleColors.RESET);
                }
                System.out.println(ConsoleColors.WHITE + nextPlayer.getName() + " has " + 
                    ConsoleColors.YELLOW_BOLD + nextPlayer.getHand().size() + 
                    ConsoleColors.WHITE + " cards left" + ConsoleColors.RESET);
                currentPlayer = getNextPlayer(); // Skip their turn
                break;
                
            case "Wild":
                // Handle Wild card special case for game start
                if (gameState == GameState.IN_PROGRESS && discardPile.size() == 1) {
                    // This is the starting card
                    Player leftOfDealer = getPlayerLeftOfDealer();
                    System.out.println(ConsoleColors.highlight("🌈 Starting card is Wild! " + 
                        leftOfDealer.getName() + " (left of dealer) chooses the starting color! 🌈"));
                    
                    // Let player choose color (use the most frequent in their hand)
                    card.applyEffect(this);
                }
                else {
                    // Normal Wild card play during the game
                    card.applyEffect(this);
                }
                break;
                
            case "Wild Draw Four":
                // For Wild Draw Four, first apply its color change effect
                card.applyEffect(this);
                
                // Then handle the draw and skip effects
                nextPlayer = getNextPlayer();
                for (int i = 0; i < 4; i++) {
                    Card drawnCard = requestDraw();
                    nextPlayer.addCardToHand(drawnCard);
                    System.out.println(ConsoleColors.WHITE + nextPlayer.getName() + " draws " + 
                        ConsoleColors.formatCard(drawnCard.toString()) + ConsoleColors.RESET);
                }
                System.out.println(ConsoleColors.WHITE + nextPlayer.getName() + " has " + 
                    ConsoleColors.YELLOW_BOLD + nextPlayer.getHand().size() + 
                    ConsoleColors.WHITE + " cards left" + ConsoleColors.RESET);
                currentPlayer = getNextPlayer(); // Skip their turn
                break;
                
            case "Shuffle Hands":
                System.out.println(ConsoleColors.highlight("🔄 All hands will be shuffled and redistributed! 🔄"));
                redistributeHands();
                break;
                
            default:
                System.out.println(ConsoleColors.RED + "Unknown card type: " + card.getType() + ConsoleColors.RESET);
                break;
        }
    }
    
    /**
     * Ends the current round and updates scores.
     * 
     * @param winner The player who won the round
     */
    private void endRound(Player winner) {
        this.gameState = GameState.ROUND_OVER;
        
        // Update scores using the existing method in ScoreTracker
        scoreTracker.updateScores(winner, players);
        
        // Log round results to CSV
        scoreTracker.logRoundToCSV(roundNumber);
        
        // Reset for next round or end game
        roundNumber++;
        
        // Check if game should end
        if (scoreTracker.checkWinCondition()) {
            this.gameState = GameState.GAME_OVER;
            System.out.println(ConsoleColors.formatHeader(ConsoleColors.YELLOW_BOLD + "🏆 GAME OVER! " + 
                winner.getName() + " has won the game with " + scoreTracker.getScore(winner) + " points! 🏆"));
        } else {
            // Prepare for next round
            System.out.println(ConsoleColors.highlight("Starting a new round..."));
            startGame();
        }
    }
    
    /**
     * Calculates the point value of a player's hand.
     * 
     * @param player The player whose hand to calculate
     * @return The total point value of the hand
     */
    private int calculateHandValue(Player player) {
        int total = 0;
        for (Card card : player.getHand()) {
            total += card.getValue();
        }
        return total;
    }
    
    /**
     * Replenishes the draw pile with cards from the discard pile if needed.
     */
    private void replenishDrawPile() {
        // If draw pile is empty, take all cards from discard pile except the top card
        if (drawPile.isEmpty() && !discardPile.isEmpty() && discardPile.size() > 1) {
            System.out.println(ConsoleColors.WHITE + "Draw pile is empty. Shuffling discard pile to create new draw pile." + ConsoleColors.RESET);
            
            // Get all cards except the top card from discard pile
            List<Card> discardedCards = new ArrayList<>(discardPile.getCards());
            
            // Shuffle the cards and put them in the draw pile
            java.util.Collections.shuffle(discardedCards);
            for (Card card : discardedCards) {
                drawPile.addCard(card);
            }
            
            // Remove all cards except the top card from discard pile
            Card topCard = discardPile.getTopCard();
            discardPile.clearCards();
            discardPile.addCard(topCard);
        }
    }
    
    /**
     * Switches the direction of play.
     */
    public void switchDirection() {
        isClockwise = !isClockwise;
        System.out.println(ConsoleColors.CYAN_BOLD + "⟲ Direction switched! Now playing " + 
            (isClockwise ? "clockwise" : "counter-clockwise") + " ⟳" + ConsoleColors.RESET);
    }
    
    /**
     * Gets the next player in turn order.
     * 
     * @return The next player
     */
    public Player getNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex;
        
        if (isClockwise) {
            nextIndex = (currentIndex + 1) % players.size();
        } else {
            nextIndex = (currentIndex - 1 + players.size()) % players.size();
        }
        
        return players.get(nextIndex);
    }
    
    /**
     * Adds a player to the game.
     * 
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (this.gameState != GameState.INITIALIZED) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        players.add(player);
        player.setMediator(this);
    }
    
    /**
     * Checks if a card is playable on the current top card.
     * 
     * @param card The card to check
     * @param topCard The top card on the discard pile
     * @return True if the card is playable, false otherwise
     */
    private boolean isPlayable(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) || 
               card.getType().equals(topCard.getType()) ||
               card.getType().equals("Wild") ||
               card.getType().equals("Wild Draw Four");
    }
    
    /**
     * Draws a card from the draw pile, replenishing if necessary.
     * 
     * @return The drawn card
     */
    public Card requestDraw() {
        replenishDrawPile();
        return drawPile.drawCard();
    }
    
    /**
     * Gets the current player.
     * 
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Sets the current player.
     * 
     * @param player The new current player
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    /**
     * Checks whether the direction of play is clockwise.
     * 
     * @return True if clockwise, false otherwise
     */
    public boolean isClockwise() {
        return isClockwise;
    }
    
    /**
     * Gets the list of players.
     * 
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players); // Return a defensive copy
    }
    
    /**
     * Validates a Wild Draw Four play.
     * According to rules, player must not have any cards matching the color on top.
     * 
     * @param player The player to validate
     * @return True if the play is valid, false otherwise
     */
    public boolean validateWildDrawFour(Player player) {
        Card topCard = discardPile.getTopCard();
        
        // If there's no top card yet, it's valid
        if (topCard == null) {
            return true;
        }
        
        // Get the color of the top card
        String topColor = topCard.getColor();
        
        // Wild Draw Four is valid if player has no cards matching the top card's color
        boolean hasCardOfMatchingColor = player.getHand().stream()
            .anyMatch(card -> card.getColor().equals(topColor));
            
        return !hasCardOfMatchingColor;
    }
    
    /**
     * Redistributes all cards among players after shuffling.
     * Used by the Shuffle Hands card.
     */
    public void redistributeHands() {
        List<Card> allCards = new ArrayList<>();
        for (Player player : players) {
            allCards.addAll(player.getHand());
            player.clearHand();
        }
        
        java.util.Collections.shuffle(allCards);
        
        int cardsPerPlayer = allCards.size() / players.size();
        int remainingCards = allCards.size() % players.size();
        
        int cardIndex = 0;
        for (Player player : players) {
            int cardsForThisPlayer = cardsPerPlayer;
            if (remainingCards > 0) {
                cardsForThisPlayer++;
                remainingCards--;
            }
            
            for (int i = 0; i < cardsForThisPlayer; i++) {
                player.addCardToHand(allCards.get(cardIndex++));
            }
        }
        
        System.out.println(ConsoleColors.PURPLE_BOLD + "🔄 All hands have been shuffled and redistributed! 🔄" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.formatSubHeader("NEW PLAYER HANDS"));
        for (Player player : players) {
            printPlayerHand(player);
        }
    }
    
    /**
     * Gets the player to the left of the dealer.
     * 
     * @return The player to the left of the dealer
     */
    private Player getPlayerLeftOfDealer() {
        int dealerIndex = players.indexOf(dealer.getDealerPlayer());
        return players.get((dealerIndex + 1) % players.size());
    }
    
    /**
     * Checks if the game is over (a player has reached 500 points).
     * 
     * @return True if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return this.gameState == GameState.GAME_OVER;
    }
    
    /**
     * Enum representing the possible states of the game.
     */
    private enum GameState {
        INITIALIZED,
        IN_PROGRESS,
        ROUND_OVER,
        GAME_OVER
    }
} 