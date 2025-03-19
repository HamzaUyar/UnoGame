package main.java.utils;

/**
 * Utility class for ANSI color codes to make console output more readable and colorful.
 * Colors match UNO card colors where possible.
 */
public class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Formatting
    public static final String DIVIDER = "============================================================";
    public static final String SHORT_DIVIDER = "------------------------------------";
    
    // Box drawing characters
    private static final String TOP_LEFT = "╔";
    private static final String TOP_RIGHT = "╗";
    private static final String BOTTOM_LEFT = "╚";
    private static final String BOTTOM_RIGHT = "╝";
    private static final String HORIZONTAL = "═";
    private static final String VERTICAL = "║";
    
    /**
     * Gets the appropriate color code for a card color
     * 
     * @param cardColor The UNO card color
     * @return The matching ANSI color code
     */
    public static String getColorForCard(String cardColor) {
        switch (cardColor.toLowerCase()) {
            case "red":
                return RED_BOLD_BRIGHT;
            case "blue":
                return BLUE_BOLD_BRIGHT;
            case "green":
                return GREEN_BOLD_BRIGHT;
            case "yellow":
                return YELLOW_BOLD_BRIGHT;
            default:
                return PURPLE_BOLD_BRIGHT; // Wild cards
        }
    }
    
    /**
     * Creates a nicely formatted card representation
     * 
     * @param cardString The card string (format: "Color Type")
     * @return A colorized string representation of the card
     */
    public static String formatCard(String cardString) {
        if (cardString == null || cardString.isEmpty()) {
            return "[Empty]";
        }
        
        String[] parts = cardString.split(" ", 2);
        if (parts.length < 2) {
            return "[" + cardString + "]";
        }
        
        String color = parts[0];
        String type = parts[1];
        
        String colorCode = getColorForCard(color);
        String bgColor = "";
        
        switch (color.toLowerCase()) {
            case "red":
                bgColor = RED_BACKGROUND;
                break;
            case "blue":
                bgColor = BLUE_BACKGROUND;
                break;
            case "green":
                bgColor = GREEN_BACKGROUND;
                break;
            case "yellow":
                bgColor = YELLOW_BACKGROUND;
                break;
            default:
                bgColor = BLACK_BACKGROUND;
                break;
        }
        
        return colorCode + bgColor + " " + type + " " + RESET;
    }
    
    /**
     * Formats a section header for better readability with fancy box drawing
     * 
     * @param header The header text
     * @return A formatted section header
     */
    public static String formatHeader(String header) {
        int headerLength = header.length();
        int boxWidth = headerLength + 4; // 2 spaces on each side
        
        StringBuilder result = new StringBuilder();
        result.append("\n");
        
        // Top border
        result.append(CYAN_BOLD_BRIGHT).append(TOP_LEFT);
        for (int i = 0; i < boxWidth; i++) {
            result.append(HORIZONTAL);
        }
        result.append(TOP_RIGHT).append(RESET).append("\n");
        
        // Header line
        result.append(CYAN_BOLD_BRIGHT).append(VERTICAL).append("  ");
        
        // Make UNO colorful if it's in the header
        if (header.contains("UNO")) {
            String[] parts = header.split("UNO", 2);
            result.append(parts[0])
                  .append(RED_BOLD).append("U")
                  .append(GREEN_BOLD).append("N")
                  .append(BLUE_BOLD).append("O")
                  .append(CYAN_BOLD_BRIGHT);
            
            if (parts.length > 1) {
                result.append(parts[1]);
            }
        } else {
            result.append(header);
        }
        
        result.append("  ").append(VERTICAL).append(RESET).append("\n");
        
        // Bottom border
        result.append(CYAN_BOLD_BRIGHT).append(BOTTOM_LEFT);
        for (int i = 0; i < boxWidth; i++) {
            result.append(HORIZONTAL);
        }
        result.append(BOTTOM_RIGHT).append(RESET).append("\n");
        
        return result.toString();
    }
    
    /**
     * Formats a subsection header
     *
     * @param header The subsection header text
     * @return A formatted subsection header
     */
    public static String formatSubHeader(String header) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append(CYAN_BRIGHT).append("┌");
        
        for (int i = 0; i < header.length() + 4; i++) {
            result.append("─");
        }
        
        result.append("┐").append(RESET).append("\n");
        result.append(CYAN_BRIGHT).append("│ ").append(CYAN_BOLD).append(header).append(CYAN_BRIGHT).append(" │").append(RESET).append("\n");
        result.append(CYAN_BRIGHT).append("└");
        
        for (int i = 0; i < header.length() + 4; i++) {
            result.append("─");
        }
        
        result.append("┘").append(RESET).append("\n");
        
        return result.toString();
    }
    
    /**
     * Formats text to highlight important game events
     *
     * @param text The text to highlight
     * @return Formatted text
     */
    public static String highlight(String text) {
        return YELLOW_BOLD_BRIGHT + text + RESET;
    }

    /**
     * Formats a color name with its respective ANSI color.
     * 
     * @param color The color name to format
     * @return The formatted color name
     */
    public static String formatColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return RED_BOLD_BRIGHT + color + RESET;
            case "green":
                return GREEN_BOLD_BRIGHT + color + RESET;
            case "blue":
                return BLUE_BOLD_BRIGHT + color + RESET;
            case "yellow":
                return YELLOW_BOLD_BRIGHT + color + RESET;
            default:
                return WHITE_BOLD + color + RESET;
        }
    }
    
    /**
     * Formats a player's name with decorative elements
     * 
     * @param name The player name
     * @return Formatted player name
     */
    public static String formatPlayerName(String name) {
        return WHITE_BOLD_BRIGHT + "★ " + name + " ★" + RESET;
    }
    
    /**
     * Creates a rainbow effect for text (cycles through colors)
     * 
     * @param text The text to colorize
     * @return Rainbow-colored text
     */
    public static String rainbow(String text) {
        String[] colors = {
            RED_BOLD, GREEN_BOLD, YELLOW_BOLD, BLUE_BOLD, PURPLE_BOLD, CYAN_BOLD
        };
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            result.append(colors[i % colors.length]).append(text.charAt(i));
        }
        result.append(RESET);
        
        return result.toString();
    }
} 