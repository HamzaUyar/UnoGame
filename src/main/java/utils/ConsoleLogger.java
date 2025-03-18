package main.java.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that redirects System.out to both the console and a log file.
 * This allows capturing all console output for debugging and record-keeping.
 */
public class ConsoleLogger {
    
    private static final String LOG_DIRECTORY = "logs";
    private static PrintStream originalOut = System.out;
    private static PrintStream fileOut = null;
    private static File logFile = null;
    
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
            fileOut = new PrintStream(fileOutputStream);
            
            // Set System.out to our custom stream that writes to both console and file
            System.setOut(new PrintStream(new MultipleOutputStream(originalOut, fileOut)));
            
            System.out.println(ConsoleColors.CYAN + "Console logging started. Log file: " + 
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
                System.out.println(ConsoleColors.CYAN + "Console logging stopped." + ConsoleColors.RESET);
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
     * Helper class that multiplexes output to multiple output streams.
     */
    private static class MultipleOutputStream extends java.io.OutputStream {
        private final java.io.OutputStream[] outputStreams;
        
        public MultipleOutputStream(java.io.OutputStream... outputStreams) {
            this.outputStreams = outputStreams;
        }
        
        @Override
        public void write(int b) throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                try {
                    out.write(b);
                    out.flush();
                } catch (IOException e) {
                    handleStreamError(e);
                }
            }
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                try {
                    out.write(b);
                    out.flush();
                } catch (IOException e) {
                    handleStreamError(e);
                }
            }
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                try {
                    out.write(b, off, len);
                    out.flush();
                } catch (IOException e) {
                    handleStreamError(e);
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                try {
                    out.flush();
                } catch (IOException e) {
                    handleStreamError(e);
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                try {
                    out.close();
                } catch (IOException e) {
                    handleStreamError(e);
                }
            }
        }
        
        /**
         * Handles I/O errors during stream operations without throwing exceptions
         * to prevent breaking the entire output system.
         * 
         * @param e The IOException that occurred
         */
        private void handleStreamError(IOException e) {
            // Print to original System.err to avoid recursion
            originalOut.println("Error writing to output stream: " + e.getMessage());
        }
    }
} 