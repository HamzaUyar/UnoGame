package main.java.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.java.cards.Card;
import main.java.players.Player;
import main.java.utils.ScoreTracker;

public class GameMediator {
    private List<Player> players;
    private Player currentPlayer;
    private boolean isClockwise;
    private Deck deck;
    private DrawPile drawPile;
    private DiscardPile discardPile;
    private ScoreTracker scoreTracker;
    
    public GameMediator() {
        players = new ArrayList<>();
        isClockwise = true;
        deck = new Deck();
        drawPile = new DrawPile();
        discardPile = new DiscardPile();
        scoreTracker = new ScoreTracker();
    }
    
    public void startGame() {
        // 1. Initialize deck
        deck.initializeDeck();
        deck.shuffle();
        
        // 2. Deal cards to players (7 each)
        for (Player player : players) {
            List<Card> dealtCards = deck.dealCards(7);
            player.setHand(dealtCards);
        }
        
        // 3. Initialize draw pile with remaining cards
        drawPile.setCards(deck.getCards());
        
        // 4. Set up first card in discard pile
        Card startingCard = drawPile.drawCard();
        // If first card is a Wild Draw Four, put it back and draw another
        while (startingCard.getType().equals("Wild Draw Four")) {
            drawPile.addCard(startingCard);
            drawPile.shuffle();
            startingCard = drawPile.drawCard();
        }
        discardPile.addCard(startingCard);
        
        // 5. Apply starting card effect if it's an action card
        if (!startingCard.getType().matches("\\d+")) {
            applyCardEffect(startingCard);
        }
        
        // 6. Select random player to start
        Random random = new Random();
        currentPlayer = players.get(random.nextInt(players.size()));
        
        // 7. Print game setup
        System.out.println("Game started!");
        System.out.println("Starting player: " + currentPlayer.getName());
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand: " + player.getHand());
        }
        System.out.println("Starting card: " + startingCard);
    }
    
    public void handleTurn(Player player) {
        System.out.println("\n" + player.getName() + "'s turn");
        
        // 1. Check if player has playable cards
        Card topCard = discardPile.getTopCard();
        Card selectedCard = player.selectPlayableCard(topCard);
        
        if (selectedCard != null) {
            // Player plays a card
            System.out.println(player.getName() + " plays " + selectedCard);
            player.playCard(selectedCard);
            discardPile.addCard(selectedCard);
            applyCardEffect(selectedCard);
        } else {
            // Player must draw a card
            System.out.println(player.getName() + " has no playable cards and draws a card");
            Card drawnCard = drawPile.drawCard();
            player.addCardToHand(drawnCard);
            
            // Check if drawn card is playable
            if (isPlayable(drawnCard, topCard)) {
                System.out.println(player.getName() + " plays drawn card: " + drawnCard);
                player.playCard(drawnCard);
                discardPile.addCard(drawnCard);
                applyCardEffect(drawnCard);
            }
        }
        
        // Check if player has won
        if (player.getHand().isEmpty()) {
            System.out.println(player.getName() + " wins the round!");
            endRound(player);
            return;
        }
        
        // Move to next player
        currentPlayer = getNextPlayer();
    }
    
    public void applyCardEffect(Card card) {
        card.applyEffect(this);
    }
    
    public void replenishDrawPile() {
        if (drawPile.isEmpty()) {
            System.out.println("Draw pile is empty. Shuffling discard pile.");
            Card topCard = discardPile.removeTopCard();
            drawPile.setCards(discardPile.getCards());
            discardPile.clearCards();
            discardPile.addCard(topCard);
            drawPile.shuffle();
        }
    }
    
    public void endRound(Player winner) {
        scoreTracker.updateScores(winner, players);
        scoreTracker.logRoundToCSV(1); // Replace with actual round number
        
        // Check if winner has 500 or more points
        if (scoreTracker.getScore(winner) >= 500) {
            System.out.println(winner.getName() + " has won the game with " + scoreTracker.getScore(winner) + " points!");
        } else {
            System.out.println("Starting a new round...");
            startGame();
        }
    }
    
    public void switchDirection() {
        isClockwise = !isClockwise;
        System.out.println("Direction switched! Now playing " + (isClockwise ? "clockwise" : "counter-clockwise"));
    }
    
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
    
    public void addPlayer(Player player) {
        players.add(player);
        player.setMediator(this);
    }
    
    private boolean isPlayable(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) || 
               card.getType().equals(topCard.getType()) ||
               card.getType().equals("Wild") ||
               card.getType().equals("Wild Draw Four");
    }
    
    // Getters and setters
    public Card requestDraw() {
        replenishDrawPile();
        return drawPile.drawCard();
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    public boolean isClockwise() {
        return isClockwise;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public boolean validateWildDrawFour(Player player) {
        return player.getHand().stream()
            .noneMatch(card -> card.getColor().equals(discardPile.getTopCard().getColor()));
    }
    
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
        
        System.out.println("All hands have been shuffled and redistributed!");
        for (Player player : players) {
            System.out.println(player.getName() + "'s new hand: " + player.getHand());
        }
    }
} 