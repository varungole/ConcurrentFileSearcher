package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSearcher {

    String wordToFind;
    String fileName;
    private final AtomicBoolean found;
    private final int NUM_THREADS;

    public FileSearcher(String wordToFind, String fileName, int NUM_THREADS) {
        this.fileName = fileName;
        this.wordToFind = wordToFind;
        this.found =new AtomicBoolean(false);
        this.NUM_THREADS = NUM_THREADS;
    }

    private boolean loopOverLine(String[] words) {
        for(String word : words) {
            if(word.equalsIgnoreCase(wordToFind)) {
                return true;
            }
        }
        return false;
    }

    private void findWord(List<String> lines, int startLine, int endLine) {
        for(int i = startLine; i<=endLine && i<lines.size(); i++) {
            if(found.get()) break;

            String[] words = lines.get(i).split("\\s+");
            if(loopOverLine(words)) {
              if(found.compareAndSet(false, true)) {
                  System.out.println("Found the word " + wordToFind + " at line " + i+1);
              }
              break;
            }
        }
    }

    private List<String> readFileIntoList() {
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    public void createConcurrentEnvironment() {
        List<String> lines = readFileIntoList();
        int totalLines = lines.size();
        int chunkSize = (int) Math.ceil((double) totalLines/NUM_THREADS);

        CompletableFuture<?>[] futures = new CompletableFuture[NUM_THREADS];

        for(int i=0; i<NUM_THREADS; i++) {
            int startLine = i * chunkSize;
            int endLine = Math.min(startLine + chunkSize-1, totalLines-1);
            futures[i] = CompletableFuture.runAsync(() -> findWord(lines, startLine, endLine));
        }
        CompletableFuture.allOf(futures).join();

        if(!found.get()) {
            System.out.println("Word " + wordToFind + " not found");
        }
    }

    public void startExecution() {
        createConcurrentEnvironment();
    }
}
