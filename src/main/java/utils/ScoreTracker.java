package main.java.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.players.Player;

public class ScoreTracker {
    private Map<Player, Integer> scores;
    private String csvPath = "resources/scores.csv";
    
    public ScoreTracker() {
        scores = new HashMap<>();
        
        // Create resources directory if it doesn't exist
        File directory = new File("resources");
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
                    writer.write("Round,Player1,Player2,Player3,Player4");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error creating scores file: " + e.getMessage());
            }
        }
    }
    
    public void updateScores(Player winner, List<Player> players) {
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
    
    public int getScore(Player player) {
        return scores.getOrDefault(player, 0);
    }
    
    public Map<Player, Integer> getScores() {
        return scores;
    }
    
    public boolean checkWinCondition() {
        return scores.values().stream().anyMatch(score -> score >= 500);
    }
} 