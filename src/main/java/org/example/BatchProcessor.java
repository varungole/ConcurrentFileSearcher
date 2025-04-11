package org.example;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatchProcessor {

    private final int NUM_THREADS;
    private AtomicBoolean found;
    private String wordToFind;

    public BatchProcessor(int NUM_THREADS, AtomicBoolean found, String wordToFind) {
        this.NUM_THREADS = NUM_THREADS;
        this.found = found;
        this.wordToFind = wordToFind;
    }

    private void closeThreads(List<Future<?>> futures, ExecutorService executor) {
        //now here cancel other threads if found
        for(Future<?> future : futures) {
            try {
                future.get(); // blocks until this future finishes
                if (found.get()) {
                    // one thread found it, cancel the rest
                    for (Future<?> f : futures) {
                        if (!f.isDone()) {
                            f.cancel(true);
                        }
                        executor.shutdownNow();
                    }
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error in cancelling futures");
            }
        }
    }

    public boolean processBatch(List<String> batch, int lineOffset) {
        int totalLines = batch.size();
        int chunkSize = (int) Math.ceil((double) totalLines/NUM_THREADS);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<?>> futures = new CopyOnWriteArrayList<>();

        for(int i=0; i<NUM_THREADS; i++) {
            int startLine = i * chunkSize;
            int endLine = Math.min(startLine + chunkSize-1, totalLines-1);
            futures.add(executor.submit(new SearchTask(batch, startLine, endLine, lineOffset, found, wordToFind)));
        }

        closeThreads(futures, executor);
        executor.shutdown();
        return false;
    }
}
