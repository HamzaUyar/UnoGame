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
    
    /**
     * Initializes the console logger by redirecting System.out to both
     * the console and a timestamped log file.
     */
    public static void initialize() {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            
            // Create a log file with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File logFile = new File(LOG_DIRECTORY + "/uno_game_" + timestamp + ".log");
            
            // Create file output stream
            fileOut = new PrintStream(new FileOutputStream(logFile, true));
            
            // Set System.out to our custom stream that writes to both console and file
            System.setOut(new PrintStream(new MultipleOutputStream(originalOut, fileOut)));
            
            System.out.println("Console logging started. Log file: " + logFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            System.err.println("Error setting up console logging: " + e.getMessage());
        }
    }
    
    /**
     * Restores the original System.out and closes the log file.
     */
    public static void restore() {
        if (System.out != originalOut) {
            System.out.println("Console logging stopped.");
            System.setOut(originalOut);
            
            if (fileOut != null) {
                fileOut.close();
            }
        }
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
                out.write(b);
                out.flush();
            }
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                out.write(b);
                out.flush();
            }
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                out.write(b, off, len);
                out.flush();
            }
        }
        
        @Override
        public void flush() throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                out.flush();
            }
        }
        
        @Override
        public void close() throws IOException {
            for (java.io.OutputStream out : outputStreams) {
                out.close();
            }
        }
    }
} 