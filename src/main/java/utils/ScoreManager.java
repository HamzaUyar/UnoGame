package main.java.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import main.java.players.Player;

/**
 * ScoreManager is responsible for tracking player scores and persisting them to a CSV file.
 * It follows the Single Responsibility Principle by focusing solely on score management.
 */
public class ScoreManager {
    private final Map<Player, Integer> scores;
    private final String csvPath;
    private static final String CSV_HEADER = "Round,Player1,Player2,Player3,Player4,Winner,CardsPlayed,RoundDuration";
    private int cardsPlayedInRound;
    private long roundStartTime;
    private String roundWinner;
    
    /**
     * Constructs a new ScoreManager with the default CSV file path.
     */
    public ScoreManager() {
        this("csv/scores.csv");
        this.cardsPlayedInRound = 0;
        this.roundStartTime = System.currentTimeMillis();
    }
    
    /**
     * Constructs a new ScoreManager with a custom CSV file path.
     * 
     * @param csvPath The path to the CSV file for score logging
     */
    public ScoreManager(String csvPath) {
        this.scores = new HashMap<>();
        this.csvPath = csvPath;
        
        initializeCSVFile();
    }
    
    /**
     * Initializes the CSV file for score logging.
     * Creates the directory and file if they don't exist.
     */
    private void initializeCSVFile() {
        // Create csv directory if it doesn't exist
        File directory = new File(Paths.get(csvPath).getParent().toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Create scores.csv file if it doesn't exist
        File file = new File(csvPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Write header
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath), StandardCharsets.UTF_8)) {
                    writer.write(CSV_HEADER);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error creating scores file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Records that a card was played in the current round.
     */
    public void recordCardPlayed() {
        cardsPlayedInRound++;
    }
    
    /**
     * Starts tracking a new round.
     */
    public void startNewRound() {
        cardsPlayedInRound = 0;
        roundStartTime = System.currentTimeMillis();
        roundWinner = "";
    }
    
    /**
     * Updates scores based on the round outcome.
     * The winner gets points equal to the sum of all opponent's card values.
     * 
     * @param winner The player who won the round
     * @param players All players in the game
     */
    public void updateScores(Player winner, List<Player> players) {
        int roundPoints = 0;
        
        // Calculate round points from remaining cards in other players' hands
        for (Player player : players) {
            if (player != winner) {
                roundPoints += player.calculateHandValue();
            }
        }
        
        // Add points to winner's score
        if (!scores.containsKey(winner)) {
            scores.put(winner, roundPoints);
        } else {
            scores.put(winner, scores.get(winner) + roundPoints);
        }
        
        // Record winner for CSV logging
        roundWinner = winner.getName();
        
        // Print updated scoreboard
        printScoreboard(players);
    }
    
    /**
     * Prints the current scoreboard to the console.
     * 
     * @param players All players in the game
     */
    private void printScoreboard(List<Player> players) {
        // Print scores
        System.out.println();
        System.out.println(ConsoleColors.YELLOW_BOLD + "===== SCOREBOARD =====" + ConsoleColors.RESET);
        
        // Sort players by score
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparing(player -> -getScore(player)))
                .collect(Collectors.toList());
        
        // Print each player's score
        for (Player player : sortedPlayers) {
            String playerName = player.getName();
            int playerScore = getScore(player);
            
            System.out.println(ConsoleColors.WHITE_BOLD + playerName + ": " + 
                               ConsoleColors.YELLOW_BOLD + playerScore + ConsoleColors.RESET);
        }
        
        System.out.println(ConsoleColors.YELLOW_BOLD + "======================" + ConsoleColors.RESET);
        System.out.println();
    }
    
    /**
     * Logs the round data to the CSV file.
     * Includes round number, player scores, winner, cards played, and round duration.
     * 
     * @param round The round number
     */
    public void logRoundToCSV(int round) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(csvPath), 
                StandardCharsets.UTF_8, 
                StandardOpenOption.APPEND)) {
            
            StringBuilder csv = new StringBuilder();
            csv.append(round).append(",");
            
            // Get player scores (assuming 4 players)
            List<Player> sortedPlayers = scores.keySet().stream()
                    .sorted(Comparator.comparing(Player::getName))
                    .collect(Collectors.toList());
            
            // Ensure we output 4 player scores (even if some are 0)
            for (int i = 0; i < 4; i++) {
                if (i < sortedPlayers.size()) {
                    csv.append(getScore(sortedPlayers.get(i)));
                } else {
                    csv.append("0");
                }
                csv.append(",");
            }
            
            // Add winner name
            csv.append(roundWinner).append(",");
            
            // Add cards played in round
            csv.append(cardsPlayedInRound).append(",");
            
            // Add round duration in seconds
            long roundDuration = (System.currentTimeMillis() - roundStartTime) / 1000;
            csv.append(roundDuration);
            
            writer.write(csv.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error logging round to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Gets the score for the specified player.
     * 
     * @param player The player whose score to get
     * @return The player's score, or 0 if the player has no score yet
     */
    public int getScore(Player player) {
        return scores.getOrDefault(player, 0);
    }
    
    /**
     * Gets all player scores.
     * 
     * @return A map of players to their scores
     */
    public Map<Player, Integer> getScores() {
        return new HashMap<>(scores);
    }
} 