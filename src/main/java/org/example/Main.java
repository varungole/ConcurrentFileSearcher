package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    private static void test(String nThreaded, String findWord, String fileName, int NUM_THREADS) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        FileSearcher fs = new FileSearcher(findWord,fileName, NUM_THREADS, executor);
        long startTime = System.nanoTime();
        fs.startExecution();
        long endTime = System.nanoTime();
        double elapsedMIllis = (endTime - startTime) / 1_000_000.0;
        System.out.printf(nThreaded + " search took %.2f ms%n", elapsedMIllis);
        executor.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to my File searcher");
        BigFileGenerator.createBigFile();
        String findWord = "imhotep";
        String fileName = EnvLoader.get("FILENAME");
        String message = "Threaded took";

        test(5+message,findWord, fileName, 5);
        test(1+message ,findWord, fileName, 1);
        test(10+message,findWord, fileName, 10);
        test(100+message ,findWord, fileName, 100);


    }
}