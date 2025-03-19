package main.java.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

import main.java.cards.Card;
import main.java.players.Player;
import main.java.ui.GameUI;
import main.java.utils.ConsoleColors;
import main.java.utils.ScoreManager;

/**
 * GameMediator class implements the Mediator design pattern to coordinate 
 * interactions between game components without them having to refer to each other.
 * It encapsulates the game state and rules.
 */
public class GameMediator implements IGameMediator {
    private List<Player> players;
    private Player currentPlayer;
    private boolean isClockwise;
    private Deck deck;
    private DrawPile drawPile;
    private DiscardPile discardPile;
    private ScoreManager scoreManager;
    private GameState gameState;
    private int roundNumber = 1;
    private int dealerIndex;
    private GameUI ui;
    
    /**
     * Creates a new GameMediator instance with initialized components.
     * All components receive a reference to this mediator in their constructors.
     */
    public GameMediator() {
        this.players = new ArrayList<>();
        this.isClockwise = true;
        this.scoreManager = new ScoreManager();
        this.gameState = GameState.INITIALIZED;
        this.dealerIndex = 0;
        this.ui = new GameUI();
        
        // Create components with mediator reference
        this.deck = new Deck(this);
        this.drawPile = new DrawPile(this);
        this.discardPile = new DiscardPile(this);
    }
    
    /**
     * Starts a new game.
     * Initializes the deck, deals cards to players, and sets up the discard pile.
     */
    @Override
    public void startGame() {
        this.gameState = GameState.IN_PROGRESS;
        
        ui.displayRoundHeader(roundNumber);
        
        // Start tracking the new round
        scoreManager.startNewRound();
        
        setupGameRound();
    }
    
    /**
     * Sets up a game round with all necessary components and initial state.
     */
    private void setupGameRound() {
        // 1. Initialize deck
        deck.initializeDeck();
        
        // 2. Determine starting player (dealer)
        determineStartingPlayer();
        
        // 3. Deal cards to players (7 each)
        dealCards();
        
        // 4. Set up the draw and discard piles
        Card startingCard = setupPiles();
        
        // 5. Apply starting card effect if it's an action card
        if (!startingCard.getType().matches("\\d+")) {
            applyCardEffect(startingCard);
        }
        
        // 6. Print game setup
        ui.displayGameSetupComplete(currentPlayer.getName());
    }
    
    /**
     * Determines the starting player by having each player draw a card.
     * The player with the highest value card becomes the dealer/starting player.
     */
    private void determineStartingPlayer() {
        // Make sure we have a deck ready and properly shuffled
        deck.initializeDeck();
        
        ui.displayDeterminingDealerHeader();
        
        // Create a map to store player -> card drawn
        Map<Player, Card> drawnCards = new HashMap<>();
        
        // Each player draws a card
        for (Player player : players) {
            Card drawnCard = deck.dealCards(1).get(0);
            drawnCards.put(player, drawnCard);
            ui.displayPlayerDrawingCard(player.getName(), drawnCard.toString());
        }
        
        // Find the player with the highest card value
        Player startingPlayer = findHighestCardPlayer(drawnCards);
        
        // Set the starting player and dealer
        currentPlayer = startingPlayer;
        dealerIndex = players.indexOf(startingPlayer);
        
        // Set dealer status for the starting player
        players.forEach(player -> player.setAsDealer(player == startingPlayer));
        
        Card highestCard = drawnCards.get(startingPlayer);
        ui.displayDealerSelected(startingPlayer.getName(), highestCard.toString());
    }
    
    private Player findHighestCardPlayer(Map<Player, Card> drawnCards) {
        return drawnCards.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getValue()))
                .map(Map.Entry::getKey)
                .orElse(players.get(0)); // Fallback to first player if there's a problem
    }
    
    /**
     * Deals cards to all players at the start of a game.
     * Each player receives 7 cards.
     */
    private void dealCards() {
        ui.displayDealingCardsHeader(currentPlayer.getName());
        
        // Deal 7 cards to each player
        for (int i = 0; i < 7; i++) {
            for (Player player : players) {
                List<Card> cards = deck.dealCards(1);
                player.addCardToHand(cards.get(0));
            }
        }
        
        // Print each player's hand
        players.forEach(this::printPlayerHand);
        
        ui.displayAllPlayersDealt();
    }
    
    /**
     * Sets up the draw and discard piles at the start of a game.
     * 
     * @return The top card of the discard pile
     */
    private Card setupPiles() {
        // Move all remaining cards to the draw pile
        List<Card> remainingCards = deck.getCards();
        remainingCards.forEach(drawPile::addCard);
        
        // Draw one card from the draw pile for the discard pile
        Card startingCard = drawPile.drawCard();
        discardPile.addCard(startingCard);
        
        ui.displayStartingCard(startingCard.toString());
        
        return startingCard;
    }
    
    /**
     * Prints a player's hand to the console.
     * 
     * @param player The player whose hand to print
     */
    private void printPlayerHand(Player player) {
        ui.displayDetailedPlayerHand(player.getName(), player.getHand());
    }
    
    /**
     * Handles a player's turn.
     * Manages card play, drawing, effects, and checking for round end.
     * 
     * @param player The player whose turn it is
     */
    @Override
    public void handleTurn(Player player) {
        validateGameInProgress();
        
        ui.displayPlayerTurnHeader(player.getName());
        
        // Get top card and display game state
        Card topCard = discardPile.getTopCard();
        ui.displayTopCard(topCard.toString());
        printPlayerHand(player);
        
        // Player selects a card to play
        Card selectedCard = player.selectPlayableCard(topCard);
        
        if (selectedCard != null) {
            playSelectedCard(player, selectedCard);
        } else {
            handleDrawingCard(player, topCard);
        }
        
        // Check if player has won
        if (player.getHand().isEmpty()) {
            ui.displayPlayerWinsRound(player.getName());
            endRound(player);
        } else {
            // Move to the next player
            currentPlayer = getNextPlayer();
        }
    }
    
    private void validateGameInProgress() {
        if (this.gameState != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Cannot handle turn when game is not in progress");
        }
    }
    
    /**
     * Handles playing a selected card
     * 
     * @param player The player playing the card
     * @param selectedCard The card being played
     */
    private void playSelectedCard(Player player, Card selectedCard) {
        ui.displayPlayerPlayingCard(player.getName(), selectedCard.toString());
        player.playCard(selectedCard);
        discardPile.addCard(selectedCard);
        
        // Track that a card was played
        scoreManager.recordCardPlayed();
        
        applyCardEffect(selectedCard);
    }
    
    /**
     * Handles drawing a card when no playable cards exist
     * 
     * @param player The player drawing the card
     * @param topCard The current top card
     */
    private void handleDrawingCard(Player player, Card topCard) {
        ui.displayPlayerDrawingCardOnTurn(player.getName());
        Card drawnCard = drawPile.drawCard();
        player.addCardToHand(drawnCard);
        
        // Check if drawn card is playable
        if (isPlayable(drawnCard, topCard)) {
            ui.displayPlayerPlayingDrawnCard(player.getName(), drawnCard.toString());
            player.playCard(drawnCard);
            discardPile.addCard(drawnCard);
            
            // Track that a card was played
            scoreManager.recordCardPlayed();
            
            applyCardEffect(drawnCard);
        } else {
            ui.displayDrawnCardCannotBePlayed();
        }
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
        
        ui.displayApplyingCardEffectHeader();
        ui.displayApplyingCardEffect(card.toString());
        
        card.applyEffect();
    }
    
    /**
     * Replenishes the draw pile from the discard pile when it runs out of cards.
     * Keeps the top card of the discard pile and shuffles the rest.
     */
    private void replenishDrawPile() {
        if (gameState == GameState.IN_PROGRESS && discardPile.size() > 1) {
            ui.displayReplenishingDrawPile();
            
            // Keep the top card
            Card topCard = discardPile.getTopCard();
            
            // Get all other cards
            List<Card> cards = discardPile.getCards();
            
            // Clear the discard pile
            discardPile.clearCards();
            
            // Add back the top card
            discardPile.addCard(topCard);
            
            // Add the rest to the draw pile
            drawPile.setCards(cards);
            
            // Shuffle the draw pile
            drawPile.shuffle();
            
            ui.displayDiscardPileReshuffled();
        }
    }
    
    /**
     * Ends the current round, calculates scores, and prepares for the next round.
     * 
     * @param winner The player who won the round
     */
    @Override
    public void endRound(Player winner) {
        this.gameState = GameState.ROUND_OVER;
        
        // Update scores
        scoreManager.updateScores(winner, players);
        
        // Log the round to CSV
        scoreManager.logRoundToCSV(roundNumber);
        
        // Check if game over (someone reached 500 points)
        if (isGameWon()) {
            handleGameOver(winner);
        } else {
            prepareNextRound();
        }
    }
    
    /**
     * Handles game over state
     * 
     * @param lastRoundWinner The winner of the last round
     */
    private void handleGameOver(Player lastRoundWinner) {
        this.gameState = GameState.GAME_OVER;
        
        // Find the winner
        Player gameWinner = players.stream()
                .max(Comparator.comparing(player -> scoreManager.getScore(player)))
                .orElse(lastRoundWinner);
        
        ui.displayPlayerWinsGame(gameWinner.getName(), scoreManager.getScore(gameWinner));
    }
    
    /**
     * Prepares for the next round of play
     */
    private void prepareNextRound() {
        // Prepare for next round
        roundNumber++;
        ui.displayPreparingForNextRound(roundNumber);
        
        // Move dealer to the next player for the new round
        dealerIndex = (dealerIndex + 1) % players.size();
        currentPlayer = players.get(dealerIndex);
        
        // Update dealer status for all players
        for (Player player : players) {
            player.setAsDealer(player == currentPlayer);
        }
        
        // Start new round
        startGame();
    }
    
    /**
     * Checks if any player has won the game by reaching the winning score.
     * 
     * @return True if a player has reached the winning score, false otherwise
     */
    private boolean isGameWon() {
        return players.stream()
                .anyMatch(player -> scoreManager.getScore(player) >= 500);
    }
    
    /**
     * Shuffles all players' hands together and redistributes them.
     * Used by the Shuffle Hands card.
     */
    @Override
    public void redistributeHands() {
        // Gather all cards
        List<Card> allCards = new ArrayList<>();
        for (Player player : players) {
            List<Card> hand = new ArrayList<>(player.getHand());
            for (Card card : hand) {
                player.playCard(card);
                allCards.add(card);
            }
        }
        
        // Shuffle all cards
        ThreadLocalRandom.current().nextInt(); // Warm up the RNG
        for (int i = allCards.size() - 1; i > 0; i--) {
            int index = ThreadLocalRandom.current().nextInt(i + 1);
            // Swap
            Card temp = allCards.get(index);
            allCards.set(index, allCards.get(i));
            allCards.set(i, temp);
        }
        
        // Redistribute cards evenly
        int cardsPerPlayer = allCards.size() / players.size();
        int remainder = allCards.size() % players.size();
        
        int cardIndex = 0;
        for (Player player : players) {
            // Calculate cards for this player
            int playerCards = cardsPerPlayer;
            if (remainder > 0) {
                playerCards++;
                remainder--;
            }
            
            // Give cards to the player
            for (int i = 0; i < playerCards && cardIndex < allCards.size(); i++) {
                Card card = allCards.get(cardIndex++);
                player.addCardToHand(card);
            }
            
            System.out.println(player.getName() + " now has " + player.getHand().size() + " cards");
        }
    }
    
    /**
     * Determines if a card is playable on the current top card.
     * 
     * @param card The card to check
     * @param topCard The current top card
     * @return True if the card is playable, false otherwise
     */
    private boolean isPlayable(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) || 
               card.getType().equals(topCard.getType()) ||
               card.getColor().equals("Wild");
    }
    
    /**
     * Checks if the game is over (any player has reached 500 points).
     * 
     * @return True if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver() {
        return isGameWon();
    }
    
    /**
     * Creates and initializes players for the game.
     * 
     * @param numPlayers The number of players to create
     */
    @Override
    public void createPlayers(int numPlayers) {
        players.clear();
        
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i + 1), this);
            addPlayer(player);
        }
    }
    
    /**
     * Adds a player to the game.
     * 
     * @param player The player to add
     */
    @Override
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    /**
     * Gets the next player in the sequence.
     * Handles direction changes (clockwise/counter-clockwise).
     * 
     * @return The next player
     */
    @Override
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
     * Switches the direction of play.
     */
    @Override
    public void switchDirection() {
        isClockwise = !isClockwise;
        System.out.println(ConsoleColors.YELLOW + "Direction changed to " + 
                          (isClockwise ? "clockwise" : "counter-clockwise") + 
                          ConsoleColors.RESET);
    }
    
    /**
     * Allows a player to draw a card.
     * 
     * @return The drawn card
     */
    @Override
    public Card requestDraw() {
        // Check if the draw pile is empty
        if (drawPile.isEmpty()) {
            replenishDrawPile();
        }
        
        return drawPile.drawCard();
    }
    
    /**
     * Gets all players in the game.
     * 
     * @return A defensive copy of the list of players
     */
    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Gets the current player.
     * 
     * @return The current player
     */
    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Sets the current player.
     * 
     * @param player The player to set as current
     */
    @Override
    public void setCurrentPlayer(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player is not in the game");
        }
        currentPlayer = player;
    }
    
    /**
     * Validates if a Wild Draw Four card can be played.
     * According to UNO rules, it can only be played if the player has no other cards of the same color as the top card.
     * 
     * @param player The player attempting to play the card
     * @return True if the card can be played, false otherwise
     */
    @Override
    public boolean validateWildDrawFour(Player player) {
        // Get the top card of the discard pile
        Card topCard = discardPile.getTopCard();
        String topColor = topCard.getColor();
        
        // Check if player has any cards of the same color
        for (Card card : player.getHand()) {
            if (card.getColor().equals(topColor)) {
                return false;
            }
        }
        
        return true;
    }
} 