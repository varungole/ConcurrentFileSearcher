package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
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

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<?>> futures = new CopyOnWriteArrayList<>();

        for(int i=0; i<NUM_THREADS; i++) {
            int startLine = i * chunkSize;
            int endLine = Math.min(startLine + chunkSize-1, totalLines-1);

            Future<?> future = executor.submit(() -> {
               try {
                   for(int j=startLine; j<=endLine && j<lines.size(); j++) {
                       if(Thread.currentThread().isInterrupted() || found.get()) {
                           System.out.println("🚫 Thread " + Thread.currentThread().getName() + " was cancelled.");
                           break;
                       }
                       String[] words = lines.get(j).split("\\s+");
                       if(loopOverLine(words)) {
                           if(found.compareAndSet(false, true)) {
                               System.out.println("Found the word " + wordToFind + " at line " + j+1);
                           }
                           break;
                       }
                   }
               } catch (Exception e) {
                   System.out.println("Error finding the word");
               }
            });

            futures.add(future);
        }

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
                    }
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error in cancelling futures");
            }
        }

        executor.shutdown();

        if(!found.get()) {
            System.out.println("Did not find the word " + wordToFind);
        }
    }

    public void startExecution() {
        createConcurrentEnvironment();
    }
}
