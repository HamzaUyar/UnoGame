package main.java.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.players.Player;

/**
 * ScoreTracker is responsible for tracking player scores and persisting them to a CSV file.
 * It follows the Single Responsibility Principle by focusing solely on score management.
 */
public class ScoreTracker {
    private final Map<Player, Integer> scores;
    private final String csvPath;
    private static final String CSV_HEADER = "Round,Player1,Player2,Player3,Player4";
    
    /**
     * Constructs a new ScoreTracker with the default CSV file path.
     */
    public ScoreTracker() {
        this("resources/scores.csv");
    }
    
    /**
     * Constructs a new ScoreTracker with a custom CSV file path.
     * 
     * @param csvPath The path to the CSV file for score logging
     */
    public ScoreTracker(String csvPath) {
        this.scores = new HashMap<>();
        this.csvPath = csvPath;
        
        initializeCSVFile();
    }
    
    /**
     * Initializes the CSV file for score logging.
     * Creates the directory and file if they don't exist.
     */
    private void initializeCSVFile() {
        // Create resources directory if it doesn't exist
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
     * Updates scores based on the round outcome.
     * The winner gets points equal to the sum of all opponent's card values.
     * 
     * @param winner The player who won the round
     * @param players All players in the game
     * @throws IllegalArgumentException if winner or players list is null
     */
    public void updateScores(Player winner, List<Player> players) {
        if (winner == null || players == null) {
            throw new IllegalArgumentException("Winner and players list cannot be null");
        }
        
        // Calculate score from opponents' cards
        int roundScore = 0;
        for (Player player : players) {
            if (player != winner) {
                roundScore += player.getHand().stream()
                        .mapToInt(card -> card.getValue())
                        .sum();
            }
        }
        
        // Add score to winner
        int currentScore = scores.getOrDefault(winner, 0);
        scores.put(winner, currentScore + roundScore);
        
        System.out.println(winner.getName() + " scored " + roundScore + " points this round.");
        System.out.println("Current scores:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + scores.getOrDefault(player, 0));
        }
    }
    
    /**
     * Logs the current round's scores to the CSV file.
     * 
     * @param round The round number
     */
    public void logRoundToCSV(int round) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath), 
                StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            StringBuilder line = new StringBuilder();
            line.append(round);
            
            for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
                line.append(",").append(entry.getValue());
            }
            
            writer.write(line.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to scores file: " + e.getMessage());
        }
    }
    
    /**
     * Gets the score for a specific player.
     * 
     * @param player The player to get the score for
     * @return The player's score, or 0 if the player has no score
     */
    public int getScore(Player player) {
        return scores.getOrDefault(player, 0);
    }
    
    /**
     * Gets a map of all player scores.
     * Returns an unmodifiable view to maintain encapsulation.
     * 
     * @return An unmodifiable view of the scores map
     */
    public Map<Player, Integer> getScores() {
        return Collections.unmodifiableMap(scores);
    }
    
    /**
     * Checks if any player has reached the win condition score threshold.
     * 
     * @return True if any player has 500 or more points, false otherwise
     */
    public boolean checkWinCondition() {
        return scores.values().stream().anyMatch(score -> score >= 500);
    }
} 