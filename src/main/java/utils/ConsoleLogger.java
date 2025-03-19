package main.java.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class that redirects System.out to both the console and a log file.
 * This allows capturing all console output for debugging and record-keeping.
 */
public class ConsoleLogger {
    
    private static final String LOG_DIRECTORY = "logs";
    private static PrintStream originalOut = System.out;
    private static PrintStream fileOut = null;
    private static File logFile = null;
    
    // Fancy box-drawing characters for prettier logs
    private static final String TOP_LEFT = "╔";
    private static final String TOP_RIGHT = "╗";
    private static final String BOTTOM_LEFT = "╚";
    private static final String BOTTOM_RIGHT = "╝";
    private static final String HORIZONTAL = "═";
    private static final String VERTICAL = "║";
    
    // Regular expression to match ANSI escape codes
    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");
    
    /**
     * Initializes the console logger by redirecting System.out to both
     * the console and a timestamped log file.
     * 
     * @return The path to the created log file, or null if initialization failed
     */
    public static String initialize() {
        FileOutputStream fileOutputStream = null;
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                if (!logDir.mkdir()) {
                    System.err.println("Failed to create logs directory: " + logDir.getAbsolutePath());
                    return null;
                }
            }
            
            // Create a log file with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            logFile = new File(LOG_DIRECTORY + "/uno_game_" + timestamp + ".log");
            
            // Create file output stream
            fileOutputStream = new FileOutputStream(logFile, true);
            fileOut = new TextFormattingPrintStream(fileOutputStream);
            
            // Set System.out to our custom stream that writes to both console and file
            System.setOut(new PrintStream(new DualOutputStreamAdapter(originalOut, fileOut)));
            
            // Print a fancy start message
            printBoxedMessage("Console logging started!", ConsoleColors.CYAN_BOLD);
            System.out.println(ConsoleColors.CYAN + "Log file: " + 
                               ConsoleColors.CYAN_BOLD + logFile.getAbsolutePath() + 
                               ConsoleColors.RESET);
            
            return logFile.getAbsolutePath();
        } catch (SecurityException e) {
            System.err.println("Security error setting up logging: " + e.getMessage());
            cleanupResources(fileOutputStream);
            return null;
        } catch (FileNotFoundException e) {
            System.err.println("Error creating log file: " + e.getMessage());
            cleanupResources(fileOutputStream);
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during logger setup: " + e.getMessage());
            cleanupResources(fileOutputStream);
            return null;
        }
    }
    
    /**
     * Clean up any resources if initialization fails
     * 
     * @param fileOutputStream The file output stream to close
     */
    private static void cleanupResources(FileOutputStream fileOutputStream) {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                System.err.println("Error closing file stream: " + e.getMessage());
            }
        }
        
        if (fileOut != null) {
            fileOut.close();
            fileOut = null;
        }
        
        // Make sure System.out is restored
        if (System.out != originalOut) {
            System.setOut(originalOut);
        }
    }
    
    /**
     * Restores the original System.out and closes the log file.
     */
    public static void restore() {
        if (System.out != originalOut) {
            try {
                printBoxedMessage("Console logging stopped!", ConsoleColors.CYAN_BOLD);
                System.setOut(originalOut);
            } catch (Exception e) {
                System.err.println("Error restoring System.out: " + e.getMessage());
            } finally {
                if (fileOut != null) {
                    try {
                        fileOut.close();
                        fileOut = null;
                    } catch (Exception e) {
                        System.err.println("Error closing log file: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Gets the current log file path.
     * 
     * @return The path to the current log file, or null if no logging is active
     */
    public static String getLogFilePath() {
        return logFile != null ? logFile.getAbsolutePath() : null;
    }
    
    /**
     * Prints a message inside a decorative box.
     * 
     * @param message The message to print
     * @param color The color to use for the box
     */
    public static void printBoxedMessage(String message, String color) {
        int messageLength = message.length();
        int boxWidth = messageLength + 4; // 2 spaces on each side
        
        StringBuilder topBorder = new StringBuilder(color);
        topBorder.append(TOP_LEFT);
        for (int i = 0; i < boxWidth; i++) {
            topBorder.append(HORIZONTAL);
        }
        topBorder.append(TOP_RIGHT).append(ConsoleColors.RESET);
        
        StringBuilder messageLine = new StringBuilder(color);
        messageLine.append(VERTICAL).append("  ").append(message).append("  ").append(VERTICAL)
                  .append(ConsoleColors.RESET);
        
        StringBuilder bottomBorder = new StringBuilder(color);
        bottomBorder.append(BOTTOM_LEFT);
        for (int i = 0; i < boxWidth; i++) {
            bottomBorder.append(HORIZONTAL);
        }
        bottomBorder.append(BOTTOM_RIGHT).append(ConsoleColors.RESET);
        
        System.out.println();
        System.out.println(topBorder);
        System.out.println(messageLine);
        System.out.println(bottomBorder);
        System.out.println();
    }
    
    /**
     * Custom PrintStream that replaces ANSI color codes with readable text-based formatting
     */
    private static class TextFormattingPrintStream extends PrintStream {
        public TextFormattingPrintStream(FileOutputStream out) {
            super(out);
        }
        
        @Override
        public void print(String s) {
            if (s != null) {
                super.print(formatForLogFile(s));
            } else {
                super.print(s);
            }
        }
        
        @Override
        public void println(String s) {
            if (s != null) {
                super.println(formatForLogFile(s));
            } else {
                super.println(s);
            }
        }
        
        /**
         * Formats the string for log file output by replacing ANSI codes with
         * appropriate text formatting
         * 
         * @param input The input string possibly containing ANSI codes
         * @return The formatted string for log file
         */
        private String formatForLogFile(String input) {
            String result = input;
            
            // Replace card colors with text indicators
            result = result.replaceAll("\u001B\\[1;91m\u001B\\[41m (.*?) \u001B\\[0m", "[RED $1]");
            result = result.replaceAll("\u001B\\[1;94m\u001B\\[44m (.*?) \u001B\\[0m", "[BLUE $1]");
            result = result.replaceAll("\u001B\\[1;92m\u001B\\[42m (.*?) \u001B\\[0m", "[GREEN $1]");
            result = result.replaceAll("\u001B\\[1;93m\u001B\\[43m (.*?) \u001B\\[0m", "[YELLOW $1]");
            result = result.replaceAll("\u001B\\[1;95m\u001B\\[40m (.*?) \u001B\\[0m", "[WILD $1]");
            
            // Replace UNO with text indicator
            result = result.replaceAll("\u001B\\[1;31mU\u001B\\[1;32mN\u001B\\[1;34mO", "UNO");
            
            // Replace player name formatting
            result = result.replaceAll("\u001B\\[1;97m★ (.*?) ★\u001B\\[0m", "-> $1 <-");
            
            // Replace highlighting
            result = result.replaceAll("\u001B\\[1;93m(.*?)\u001B\\[0m", "* $1 *");
            
            // Replace headers
            result = result.replaceAll("\u001B\\[1;96m╔(═+)╗\n\u001B\\[1;96m║  (.*?)  ║\n\u001B\\[1;96m╚(═+)╝\u001B\\[0m", 
                                      "==========\n   $2   \n==========");
            
            // Replace sub headers
            result = result.replaceAll("\u001B\\[0;96m┌(─+)┐\u001B\\[0m\n\u001B\\[0;96m│ \u001B\\[1;36m(.*?)\u001B\\[0;96m │\u001B\\[0m\n\u001B\\[0;96m└(─+)┘\u001B\\[0m",
                                    "----------\n   $2   \n----------");
            
            // Strip any remaining ANSI codes
            result = ANSI_PATTERN.matcher(result).replaceAll("");
            
            return result;
        }
    }
    
    /**
     * Adapter that sends different formatted outputs to the console and log file
     */
    private static class DualOutputStreamAdapter extends java.io.OutputStream {
        private final PrintStream consoleStream;
        private final PrintStream fileStream;
        private final StringBuilder buffer = new StringBuilder();
        
        public DualOutputStreamAdapter(PrintStream console, PrintStream file) {
            this.consoleStream = console;
            this.fileStream = file;
        }
        
        @Override
        public void write(int b) throws IOException {
            char c = (char) b;
            
            // Write directly to console
            consoleStream.write(b);
            consoleStream.flush();
            
            // Buffer characters until we get a newline, then write to file
            buffer.append(c);
            if (c == '\n') {
                fileStream.print(buffer.toString());
                fileStream.flush();
                buffer.setLength(0);
            }
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // Write directly to console
            consoleStream.write(b, off, len);
            consoleStream.flush();
            
            // Add to buffer and check for newlines
            String str = new String(b, off, len);
            buffer.append(str);
            
            if (str.contains("\n")) {
                int lastNewline = buffer.lastIndexOf("\n");
                String complete = buffer.substring(0, lastNewline + 1);
                fileStream.print(complete);
                fileStream.flush();
                
                buffer.delete(0, lastNewline + 1);
            }
        }
        
        @Override
        public void flush() throws IOException {
            consoleStream.flush();
            fileStream.flush();
        }
        
        @Override
        public void close() throws IOException {
            // Flush any remaining content
            if (buffer.length() > 0) {
                fileStream.print(buffer.toString());
                buffer.setLength(0);
            }
            
            consoleStream.close();
            fileStream.close();
        }
    }
} 