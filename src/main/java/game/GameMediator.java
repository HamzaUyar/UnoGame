package main.java.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import main.java.cards.Card;
import main.java.cards.actioncards.WildCard;
import main.java.players.Player;
import main.java.ui.GameUI;
import main.java.utils.ConsoleColors;
import main.java.utils.ScoreTracker;

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
    private ScoreTracker scoreTracker;
    private GameState gameState;
    private int roundNumber = 1;
    private int dealerIndex;
    private GameUI ui;
    private Map<GameComponentType, List<IGameComponent>> componentRegistry;
    
    /**
     * Creates a new GameMediator instance with initialized components.
     * Initializes and registers all game components.
     */
    public GameMediator() {
        this.players = new ArrayList<>();
        this.isClockwise = true;
        this.deck = new Deck();
        this.drawPile = new DrawPile();
        this.discardPile = new DiscardPile();
        this.scoreTracker = new ScoreTracker();
        this.gameState = GameState.INITIALIZED;
        this.dealerIndex = 0;
        this.ui = new GameUI();
        this.componentRegistry = new HashMap<>();
        
        // Initialize component lists
        for (GameComponentType type : GameComponentType.values()) {
            componentRegistry.put(type, new ArrayList<>());
        }
        
        // Register all components with the mediator
        registerComponent(this.deck);
        registerComponent(this.drawPile);
        registerComponent(this.discardPile);
        registerComponent(this.scoreTracker);
    }
    
    /**
     * Registers a game component with the mediator.
     * 
     * @param component The component to register
     */
    @Override
    public void registerComponent(IGameComponent component) {
        GameComponentType type = component.getComponentType();
        componentRegistry.get(type).add(component);
        component.setMediator(this);
    }
    
    /**
     * Starts a new game.
     * Initializes the deck, deals cards to players, and sets up the discard pile.
     */
    @Override
    public void startGame() {
        this.gameState = GameState.IN_PROGRESS;
        
        ui.displayRoundHeader(roundNumber);
        displayPlayerInfo();
        initializeGameComponents();
        determineStartingPlayer();
        setupGameBoard();
    }

    /**
     * Displays information about the number of players in the game.
     */
    private void displayPlayerInfo() {
        System.out.println(ConsoleColors.WHITE_BRIGHT + "Game starting with " + players.size() + " players." + ConsoleColors.RESET);
        
        // Start tracking the new round
        scoreTracker.startNewRound();
    }

    /**
     * Initializes all game components for a new game.
     */
    private void initializeGameComponents() {
        // Initialize deck
        deck.initializeDeck();
    }

    /**
     * Sets up the game board by shuffling the deck, dealing cards, and setting up piles.
     */
    private void setupGameBoard() {
        // Shuffle the deck
        shuffleDeck();
        
        // Deal cards to players (7 each)
        dealCards();
        
        // Set up the draw and discard piles
        Card startingCard = setupPiles();
        
        // Apply starting card effect if it's an action card
        if (!startingCard.getType().matches("\\d+")) {
            applyCardEffect(startingCard);
        }
        
        // Print game setup
        ui.displayGameSetupComplete(currentPlayer.getName());
    }
    
    /**
     * Determines the starting player by having each player draw a card.
     * The player with the highest value card becomes the dealer/starting player.
     * This implementation ensures proper randomization.
     */
    private void determineStartingPlayer() {
        // First make sure we have a deck ready and properly shuffled
        deck.initializeDeck(); // This now includes shuffling
        
        ui.displayDeterminingDealerHeader();
        
        // Create a map to store player -> card drawn
        Map<Player, Card> drawnCards = new HashMap<>();
        
        // Each player draws a card
        drawCardsForDealerSelection(drawnCards);
        
        // Find the player with the highest card value
        selectStartingPlayer(drawnCards);
    }

    /**
     * Has each player draw a card for dealer selection.
     * 
     * @param drawnCards Map to store the cards drawn by each player
     */
    private void drawCardsForDealerSelection(Map<Player, Card> drawnCards) {
        for (Player player : players) {
            Card drawnCard = deck.dealCards(1).get(0);
            drawnCards.put(player, drawnCard);
            ui.displayPlayerDrawingCard(player.getName(), drawnCard.toString());
        }
    }

    /**
     * Selects the starting player based on the highest card drawn.
     * 
     * @param drawnCards Map of players to cards drawn
     */
    private void selectStartingPlayer(Map<Player, Card> drawnCards) {
        Player startingPlayer = drawnCards.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getValue()))
                .map(Map.Entry::getKey)
                .orElse(players.get(0)); // Fallback to first player if there's a problem
        
        // Set the starting player and dealer
        currentPlayer = startingPlayer;
        dealerIndex = players.indexOf(startingPlayer);
        
        // Set dealer status for the starting player
        for (Player player : players) {
            player.setAsDealer(player == startingPlayer);
        }
        
        ui.displayDealerSelectedMessage(startingPlayer.getName());
    }
    
    /**
     * Shuffles the deck.
     */
    private void shuffleDeck() {
        deck.shuffle();
        ui.displayDeckShuffled();
    }
    
    /**
     * Deals 7 cards to each player in a left direction.
     * The dealer draws one card from the deck, gives it to the next player,
     * and does this until each player (including the dealer) has 7 cards.
     */
    private void dealCards() {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Cannot deal cards to empty player list");
        }
        
        ui.displayDealingCardsHeader(currentPlayer.getName());
        
        // Clear any existing cards from players' hands
        for (Player player : players) {
            player.clearHand();
        }
        
        // Start dealing to the player after the dealer
        int startingPlayerIndex = (dealerIndex + 1) % players.size();
        
        // Deal 7 cards to each player, one at a time in a left direction
        for (int cardNum = 0; cardNum < 7; cardNum++) {
            ui.displayDealRoundHeader(cardNum + 1);
            
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
                    ui.displayPlayerHand(player.getName(), player.getHand());
                }
            }
        }
        
        ui.displayAllPlayersDealt();
    }
    
    /**
     * Sets up the draw and discard piles after dealing.
     * Places the remaining deck as the Draw Pile and starts the Discard Pile.
     * If the top card drawn is an Action Card, its effect is followed immediately.
     * 
     * @return The starting card placed on the discard pile
     */
    private Card setupPiles() {
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
        
        ui.displayStartingCard(startingCard.toString());
        
        return startingCard;
    }
    
    /**
     * Prints a player's hand in a nicely formatted way
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
        if (this.gameState != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Cannot handle turn when game is not in progress");
        }
        
        ui.displayPlayerTurnHeader(player.getName());
        
        // 1. Check if player has playable cards
        Card topCard = discardPile.getTopCard();
        ui.displayTopCard(topCard.toString());
        
        // Print current player's hand
        printPlayerHand(player);
        
        Card selectedCard = player.selectPlayableCard(topCard);
        
        if (selectedCard != null) {
            // Player plays a card
            ui.displayPlayerPlayingCard(player.getName(), selectedCard.toString());
            player.playCard(selectedCard);
            discardPile.addCard(selectedCard);
            
            // Track that a card was played
            scoreTracker.recordCardPlayed();
            
            applyCardEffect(selectedCard);
        } else {
            // Player must draw a card
            ui.displayPlayerDrawingCardOnTurn(player.getName());
            Card drawnCard = drawPile.drawCard();
            player.addCardToHand(drawnCard);
            
            // Check if drawn card is playable
            if (isPlayable(drawnCard, topCard)) {
                ui.displayPlayerPlayingDrawnCard(player.getName(), drawnCard.toString());
                player.playCard(drawnCard);
                discardPile.addCard(drawnCard);
                
                // Track that a card was played
                scoreTracker.recordCardPlayed();
                
                applyCardEffect(drawnCard);
            } else {
                ui.displayDrawnCardCannotBePlayed();
            }
        }
        
        // Check if player has won
        if (player.getHand().isEmpty()) {
            ui.displayPlayerWinsRound(player.getName());
            endRound(player);
            return;
        } else {
            ui.displayPlayerCardCount(player.getName(), player.getHand().size());
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
        
        // Make sure the card has a mediator reference
        card.setMediator(this);
        
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
        scoreTracker.updateScores(winner, players);
        
        // Log the round to CSV
        scoreTracker.logRoundToCSV(roundNumber);
        
        // Check if game over (someone reached 500 points)
        if (isGameWon()) {
            this.gameState = GameState.GAME_OVER;
            
            // Find the winner
            Player gameWinner = players.stream()
                    .max(Comparator.comparing(player -> scoreTracker.getScore(player)))
                    .orElse(winner);
            
            ui.displayPlayerWinsGame(gameWinner.getName(), scoreTracker.getScore(gameWinner));
        } else {
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
    }
    
    /**
     * Checks if any player has won the game by reaching the winning score.
     * 
     * @return True if a player has reached the winning score, false otherwise
     */
    private boolean isGameWon() {
        return players.stream()
                .anyMatch(player -> scoreTracker.getScore(player) >= 500);
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
     * Validates whether a Wild Draw Four play is legal according to official UNO rules.
     * Legal if the player has no cards matching the color on top (Wild Draw Four is a last resort).
     * 
     * @param player The player who played the Wild Draw Four
     * @return True if the play is valid, false otherwise
     */
    @Override
    public boolean validateWildDrawFour(Player player) {
        Card topCard = discardPile.getTopCard();
        String topColor = topCard.getColor();
        
        // Cannot validate if the top card is wild (no color)
        if (topColor.isEmpty()) {
            return true;
        }
        
        // Check if player has any cards matching the top color
        for (Card card : player.getHand()) {
            if (card.getColor().equals(topColor)) {
                return false; // Found a matching color, Wild Draw Four not allowed
            }
        }
        
        return true; // No matching colors, Wild Draw Four is allowed
    }
    
    /**
     * Adds a player to the game.
     * 
     * @param player The player to add
     */
    @Override
    public void addPlayer(Player player) {
        if (this.gameState != GameState.INITIALIZED) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        players.add(player);
        player.setMediator(this);
    }
    
    /**
     * Gets the next player in turn order.
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
    }
    
    /**
     * Allows a player to draw a card.
     * 
     * @return The drawn card
     */
    @Override
    public Card requestDraw() {
        Card drawnCard = drawPile.drawCard();
        
        // If draw pile is empty, replenish it from the discard pile
        if (drawnCard == null) {
            replenishDrawPile();
            drawnCard = drawPile.drawCard();
            
            // If still null after replenishing, create a new Wild card as fallback
            if (drawnCard == null) {
                ui.displayNoCardsLeftWarning();
                drawnCard = new WildCard();
                drawnCard.setMediator(this);
            }
        }
        
        return drawnCard;
    }
    
    /**
     * Gets all players in the game.
     * 
     * @return A copy of the list of players
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
     * @param player The new current player
     */
    @Override
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    /**
     * Checks if the game is over.
     * 
     * @return True if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver() {
        return this.gameState == GameState.GAME_OVER;
    }
    
    /**
     * Creates and adds a specified number of players to the game.
     * 
     * @param numPlayers The number of players to create and add
     */
    @Override
    public void createPlayers(int numPlayers) {
        if (this.gameState != GameState.INITIALIZED) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        
        for (int i = 1; i <= numPlayers; i++) {
            Player player = new Player("Player" + i);
            addPlayer(player);
        }
    }
} 