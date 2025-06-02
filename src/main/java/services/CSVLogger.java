package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CSVLogger {
    private static final String logFile = "logs/logs.csv";
    private static CSVLogger instance;
    private static final Lock lock = new ReentrantLock();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private CSVLogger() {
        // create the log file if it doesn't exist
        File file = new File(logFile);
        if(!file.exists()) {
            try (FileWriter fw = new FileWriter(file, true); // append mode
                 BufferedWriter bw = new BufferedWriter(fw)){ // use BufferedWriter for efficiency
                bw.write("Timestamp                   User email        Action        Details\n");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
    }

    public static CSVLogger getInstance() {
        if (instance == null) {
            synchronized (CSVLogger.class) {
                if(instance == null) {
                    instance = new CSVLogger();
                }
            }
        }
        return instance;
    }

    public void log(String userId, String Action, String details) {
        lock.lock(); // ensure thread safety
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            String timestamp = dateFormat.format(new Date());

            details = escapeCSV(details);

            String logEntry = String.format("%s        %s        %s        %s\n", timestamp, userId, Action, details);

            bw.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        } finally {
            lock.unlock(); // release the lock
        }
    }

    private String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\""); // escape quotes with double quotes
            field = "\"" + field + "\""; // wrap in quotes
        }
        return field;
    }
}
