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
    }
    
    /**
     * Starts a new game.
     * Initializes the deck, deals cards to players, and sets up the discard pile.
     */
    public void startGame() {
        this.gameState = GameState.IN_PROGRESS;
        
        System.out.println(ConsoleColors.formatHeader("UNO GAME - ROUND " + roundNumber));
        
        // 1. Initialize deck
        deck.initializeDeck();
        deck.shuffle();
        
        // 2. Use Deck to determine dealer (highest card)
        determineStartingPlayer();
        
        // 3. Deal cards to players (7 each)
        for (Player player : players) {
            List<Card> dealtCards = deck.dealCards(7);
            player.setHand(dealtCards);
        }
        
        // 4. Initialize draw pile with remaining cards
        drawPile.setCards(deck.getCards());
        
        // 5. Set up first card in discard pile
        Card startingCard = drawPile.drawCard();
        // If first card is a Wild Draw Four, put it back and draw another
        while (startingCard.getType().equals("Wild Draw Four")) {
            drawPile.addCard(startingCard);
            drawPile.shuffle();
            startingCard = drawPile.drawCard();
        }
        discardPile.addCard(startingCard);
        
        // 6. Apply starting card effect if it's an action card
        if (!startingCard.getType().matches("\\d+")) {
            applyCardEffect(startingCard);
        }
        
        // 7. Print game setup
        System.out.println(ConsoleColors.formatSubHeader("GAME SETUP"));
        System.out.println(ConsoleColors.WHITE_BOLD + "Starting player/dealer: " + ConsoleColors.YELLOW_BOLD + currentPlayer.getName() + ConsoleColors.RESET);
        System.out.println(ConsoleColors.WHITE_BOLD + "Starting card: " + ConsoleColors.formatCard(startingCard.toString()) + ConsoleColors.RESET);
        
        System.out.println(ConsoleColors.formatSubHeader("PLAYER HANDS"));
        for (Player player : players) {
            printPlayerHand(player);
        }
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
        
        System.out.println(ConsoleColors.formatSubHeader("DETERMINING STARTING PLAYER"));
        System.out.println(ConsoleColors.WHITE + "Each player draws a card; highest card value starts the game." + ConsoleColors.RESET);
        
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
        
        // Set the starting player
        currentPlayer = startingPlayer;
        
        Card highestCard = drawnCards.get(startingPlayer);
        System.out.println(ConsoleColors.highlight(startingPlayer.getName() + 
            " has the highest card: " + ConsoleColors.formatCard(highestCard.toString()) + 
            " and will start the game as dealer!"));
        
        // Return the drawn cards to the deck
        List<Card> cardsToReturn = new ArrayList<>(drawnCards.values());
        for (Card card : cardsToReturn) {
            deck.returnCard(card);
        }
        
        // Reshuffle the deck
        deck.shuffle();
        System.out.println(ConsoleColors.WHITE + "Cards returned to deck and reshuffled." + ConsoleColors.RESET);
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
        card.applyEffect(this);
    }
    
    /**
     * Replenishes the draw pile if it's empty by shuffling the discard pile.
     */
    private void replenishDrawPile() {
        if (drawPile.isEmpty()) {
            System.out.println(ConsoleColors.PURPLE_BOLD + "Draw pile is empty. Shuffling discard pile." + ConsoleColors.RESET);
            Card topCard = discardPile.removeTopCard();
            drawPile.setCards(discardPile.getCards());
            discardPile.clearCards();
            discardPile.addCard(topCard);
            drawPile.shuffle();
        }
    }
    
    /**
     * Ends the current round, updates scores, and checks for game end.
     * 
     * @param winner The player who won the round
     */
    private void endRound(Player winner) {
        this.gameState = GameState.ROUND_ENDED;
        
        scoreTracker.updateScores(winner, players);
        scoreTracker.logRoundToCSV(roundNumber);
        
        // Check if winner has 500 or more points
        if (scoreTracker.getScore(winner) >= 500) {
            System.out.println(ConsoleColors.formatHeader(ConsoleColors.YELLOW_BOLD + "🏆 GAME OVER! " + winner.getName() + 
                " has won the game with " + scoreTracker.getScore(winner) + " points! 🏆"));
            this.gameState = GameState.GAME_OVER;
        } else {
            System.out.println(ConsoleColors.highlight("Starting a new round..."));
            this.gameState = GameState.IN_PROGRESS;
            roundNumber++;
            startGame();
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
        return player.getHand().stream()
            .noneMatch(card -> card.getColor().equals(discardPile.getTopCard().getColor()));
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
     * Enum representing the possible states of the game.
     */
    private enum GameState {
        INITIALIZED,
        IN_PROGRESS,
        ROUND_ENDED,
        GAME_OVER
    }
} 