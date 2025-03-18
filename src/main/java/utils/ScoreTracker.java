package main.java.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import main.java.game.GameMediator;
import main.java.players.Player;

/**
 * ScoreTracker is responsible for tracking player scores and persisting them to a CSV file.
 * It follows the Single Responsibility Principle by focusing solely on score management.
 */
public class ScoreTracker {
    private final Map<Player, Integer> scores;
    private final String csvPath;
    private static final String CSV_HEADER = "Round,Player1,Player2,Player3,Player4,Winner,CardsPlayed,RoundDuration";
    private int cardsPlayedInRound;
    private long roundStartTime;
    private String roundWinner;
    private GameMediator mediator;
    
    /**
     * Constructs a new ScoreTracker with the default CSV file path.
     */
    public ScoreTracker() {
        this("csv/scores.csv");
        this.cardsPlayedInRound = 0;
        this.roundStartTime = System.currentTimeMillis();
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
     * Sets the game mediator for this score tracker.
     *
     * @param mediator The game mediator
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
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
     * @throws IllegalArgumentException if winner or players list is null
     */
    public void updateScores(Player winner, List<Player> players) {
        if (winner == null || players == null) {
            throw new IllegalArgumentException("Winner and players list cannot be null");
        }
        
        // Record round winner
        roundWinner = winner.getName();
        
        // Calculate score from opponents' cards
        int roundScore = 0;
        for (Player player : players) {
            if (player != winner) {
                roundScore += player.calculateHandValue();
            }
        }
        
        // Add score to winner
        int currentScore = scores.getOrDefault(winner, 0);
        scores.put(winner, currentScore + roundScore);
        
        // Print score update
        System.out.println(ConsoleColors.formatSubHeader("ROUND SCORE UPDATE"));
        System.out.println(ConsoleColors.GREEN_BOLD + winner.getName() + " scored " + roundScore + " points this round!" + ConsoleColors.RESET);
        
        // Print scoreboard
        System.out.println(ConsoleColors.formatSubHeader("CURRENT SCOREBOARD"));
        printScoreboard(players);
    }
    
    /**
     * Prints a formatted scoreboard showing all players' scores
     * 
     * @param players The list of players in the game
     */
    private void printScoreboard(List<Player> players) {
        // Sort players by score in descending order
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparing((Player p) -> scores.getOrDefault(p, 0)).reversed())
                .collect(Collectors.toList());
        
        // Print scores in table format
        System.out.println(ConsoleColors.CYAN + "┌─────────────┬────────┐");
        System.out.println("│ Player      │ Score  │");
        System.out.println("├─────────────┼────────┤");
        
        for (Player player : sortedPlayers) {
            int score = scores.getOrDefault(player, 0);
            String scoreColor = (player == sortedPlayers.get(0)) ? ConsoleColors.YELLOW_BOLD : ConsoleColors.WHITE;
            System.out.printf("│ %-11s │ %s%6d%s │\n", 
                    player.getName(), 
                    scoreColor,
                    score, 
                    ConsoleColors.CYAN);
        }
        
        System.out.println("└─────────────┴────────┘" + ConsoleColors.RESET);
    }
    
    /**
     * Logs the current round's scores to the CSV file.
     * 
     * @param round The round number
     */
    public void logRoundToCSV(int round) {
        // Calculate round duration in seconds
        long roundDuration = (System.currentTimeMillis() - roundStartTime) / 1000;
        
        // Make sure the CSV directory exists
        File csvDir = new File(csvPath).getParentFile();
        if (csvDir != null && !csvDir.exists()) {
            try {
                if (!csvDir.mkdirs()) {
                    System.err.println(ConsoleColors.RED_BOLD + "Failed to create directory for CSV: " + csvDir.getAbsolutePath() + ConsoleColors.RESET);
                }
            } catch (SecurityException e) {
                System.err.println(ConsoleColors.RED_BOLD + "Security error creating CSV directory: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
        
        BufferedWriter writer = null;
        try {
            // Check if file exists to write header if needed
            boolean fileExists = new File(csvPath).exists();
            
            // Create the writer with append mode
            writer = Files.newBufferedWriter(Paths.get(csvPath), 
                    StandardCharsets.UTF_8, 
                    fileExists ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            
            // Write header if this is a new file
            if (!fileExists) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }
            
            // Build the data line
            StringBuilder line = new StringBuilder();
            line.append(round);
            
            // Add player scores
            for (int i = 1; i <= 4; i++) {
                int score = 0;
                for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
                    if (entry.getKey().getName().equals("Player" + i)) {
                        score = entry.getValue();
                        break;
                    }
                }
                line.append(",").append(score);
            }
            
            // Add additional stats
            line.append(",").append(roundWinner)
                .append(",").append(cardsPlayedInRound)
                .append(",").append(roundDuration);
            
            writer.write(line.toString());
            writer.newLine();
            writer.flush(); // Ensure data is written
            
            System.out.println(ConsoleColors.GREEN + "Scores and stats saved to " + csvPath + ConsoleColors.RESET);
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error writing to scores file: " + e.getMessage() + ConsoleColors.RESET);
        } catch (Exception e) {
            System.err.println(ConsoleColors.RED_BOLD + "Unexpected error saving scores: " + e.getMessage() + ConsoleColors.RESET);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println(ConsoleColors.RED + "Error closing CSV writer: " + e.getMessage() + ConsoleColors.RESET);
                }
            }
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
     * Gets all player scores.
     * Returns an unmodifiable view of the scores map.
     * 
     * @return An unmodifiable view of the scores map
     */
    public Map<Player, Integer> getScores() {
        return Map.copyOf(scores);
    }
    
    /**
     * Checks if any player has reached the winning score (500 points).
     * 
     * @return True if a player has won, false otherwise
     */
    public boolean checkWinCondition() {
        return scores.values().stream().anyMatch(score -> score >= 500);
    }
} 