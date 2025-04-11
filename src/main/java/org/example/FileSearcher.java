package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSearcher {

    private final String fileName;
    private final BatchProcessor batchProcessor;

    public FileSearcher(String wordToFind, String fileName, int NUM_THREADS) {
        this.fileName = fileName;
        AtomicBoolean found = new AtomicBoolean(false);
        this.batchProcessor = new BatchProcessor(NUM_THREADS, found, wordToFind);
    }


    public void createConcurrentEnvironment(int batchSize, int globalOffset) {

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            List<String> batch = new ArrayList<>(batchSize);
            String line;

            while((line = br.readLine()) != null) {
                batch.add(line);
                if(batchSize == batch.size()) {
                    if(batchProcessor.processBatch(batch, globalOffset)) return;
                    globalOffset += batchSize;
                    batch.clear();
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public void startExecution() {
        int batchSize = 100_000;
        int globalLineOffset = 0;
        createConcurrentEnvironment(batchSize, globalLineOffset);
    }
}
