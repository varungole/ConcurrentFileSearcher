package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSearcher {

    private final String fileName;
    private final BatchProcessor batchProcessor;
    ExecutorService executorService;
    public FileSearcher(String wordToFind, String fileName, int NUM_THREADS, ExecutorService executorService) {
        this.fileName = fileName;
        AtomicBoolean found = new AtomicBoolean(false);
        this.batchProcessor = new BatchProcessor(NUM_THREADS, found, wordToFind);
        this.executorService = executorService;
    }

    private void sendBatchForProcessing(List<String> batch, int globalOffset, int batchSize) {
        if(batchProcessor.processBatch(batch, globalOffset, executorService)) return;
        batch.clear();
    }

    public void createConcurrentEnvironment(int batchSize, int globalOffset) {

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            List<String> batch = new ArrayList<>(batchSize);
            String line;

            while((line = br.readLine()) != null) {
                batch.add(line);
                if(batchSize == batch.size()) {
                    sendBatchForProcessing(batch, globalOffset, batchSize);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public void startExecution() {
        int batchSize = 500_000;
        int globalLineOffset = 0;
        createConcurrentEnvironment(batchSize, globalLineOffset);
    }
}
