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

    // Formatting
    public static final String DIVIDER = "============================================================";
    public static final String SHORT_DIVIDER = "------------------------------------";
    
    /**
     * Gets the appropriate color code for a card color
     * 
     * @param cardColor The UNO card color
     * @return The matching ANSI color code
     */
    public static String getColorForCard(String cardColor) {
        switch (cardColor.toLowerCase()) {
            case "red":
                return RED_BOLD;
            case "blue":
                return BLUE_BOLD;
            case "green":
                return GREEN_BOLD;
            case "yellow":
                return YELLOW_BOLD;
            default:
                return PURPLE_BOLD; // Wild cards
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
        
        return getColorForCard(color) + "[" + color + " " + type + "]" + RESET;
    }
    
    /**
     * Formats a section header for better readability
     * 
     * @param header The header text
     * @return A formatted section header
     */
    public static String formatHeader(String header) {
        return CYAN_BOLD + "\n" + DIVIDER + "\n" + header + "\n" + DIVIDER + RESET + "\n";
    }
    
    /**
     * Formats a subsection header
     *
     * @param header The subsection header text
     * @return A formatted subsection header
     */
    public static String formatSubHeader(String header) {
        return CYAN + "\n" + SHORT_DIVIDER + "\n" + header + "\n" + SHORT_DIVIDER + RESET + "\n";
    }
    
    /**
     * Formats text to highlight important game events
     *
     * @param text The text to highlight
     * @return Formatted text
     */
    public static String highlight(String text) {
        return YELLOW_BOLD + text + RESET;
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
                return RED_BOLD + color + RESET;
            case "green":
                return GREEN_BOLD + color + RESET;
            case "blue":
                return BLUE_BOLD + color + RESET;
            case "yellow":
                return YELLOW_BOLD + color + RESET;
            default:
                return WHITE_BOLD + color + RESET;
        }
    }
} 