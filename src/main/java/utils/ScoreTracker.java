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
import main.java.game.GameComponentType;
import main.java.game.IGameComponent;
import main.java.game.IGameMediator;

/**
 * ScoreTracker is responsible for tracking player scores and persisting them to a CSV file.
 * It follows the Single Responsibility Principle by focusing solely on score management.
 * Implements IGameComponent interface to participate in the Mediator pattern.
 */
public class ScoreTracker implements IGameComponent {
    private final Map<Player, Integer> scores;
    private final String csvPath;
    private static final String CSV_HEADER = "Round,Player 1,Player 2,Player 3,Player 4";
    private int cardsPlayedInRound;
    private long roundStartTime;
    private String roundWinner;
    private IGameMediator mediator;
    private int currentRound = 0;
    
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
        currentRound++;
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
        
        // Log round to CSV
        logRoundToCSV();
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
     */
    private void logRoundToCSV() {
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
            // Check if file exists
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
            line.append("Round ").append(currentRound);
            
            // Add player scores
            for (int i = 1; i <= 4; i++) {
                line.append(",");
                // Find the player with this number or append 0
                boolean playerFound = false;
                for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
                    if (entry.getKey().getName().equals("Player " + i)) {
                        line.append(entry.getValue());
                        playerFound = true;
                        break;
                    }
                }
                if (!playerFound) {
                    line.append("0");
                }
            }
            
            writer.write(line.toString());
            writer.newLine();
            writer.flush(); // Ensure data is written
            
            System.out.println(ConsoleColors.GREEN + "Scores saved to " + csvPath + ConsoleColors.RESET);
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
     * Logs the final game winner to the CSV file.
     * 
     * @param winner The player who won the game
     * @param players All players in the game
     */
    public void logGameWinner(Player winner, List<Player> players) {
        BufferedWriter writer = null;
        try {
            // Open the file in append mode
            writer = Files.newBufferedWriter(Paths.get(csvPath), 
                    StandardCharsets.UTF_8, 
                    StandardOpenOption.APPEND);
            
            // Write the winner row
            StringBuilder line = new StringBuilder();
            line.append("Winner,").append(winner.getName());
            
            // Fill the remaining columns with empty values
            for (int i = 0; i < 3; i++) {
                line.append(",");
            }
            
            writer.write(line.toString());
            writer.newLine();
            writer.flush();
            
            System.out.println(ConsoleColors.GREEN_BOLD + "Game winner saved to " + csvPath + ConsoleColors.RESET);
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED_BOLD + "Error writing winner to scores file: " + e.getMessage() + ConsoleColors.RESET);
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
     * Sets the mediator for this component.
     * 
     * @param mediator The mediator to set
     */
    @Override
    public void setMediator(IGameMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Gets the type of this component.
     * 
     * @return The component type (SCORE_TRACKER)
     */
    @Override
    public GameComponentType getComponentType() {
        return GameComponentType.SCORE_TRACKER;
    }
} 