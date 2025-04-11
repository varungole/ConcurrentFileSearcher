package org.example;

import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to my File searcher");
        BigFileGenerator.createBigFile();
        String findWord = "imhotep";
        String fileName = EnvLoader.get("FILENAME");
        int NUM_THREADS = Integer.parseInt(EnvLoader.get("NUM_THREADS"));
        long startTime;
        long endTime;
        double elapsedMIllis;


        FileSearcher fs = new FileSearcher(findWord,fileName, NUM_THREADS);
        startTime = System.nanoTime();
        fs.startExecution();
        endTime = System.nanoTime();
        elapsedMIllis = (endTime - startTime) / 1_000_000.0;
        System.out.printf("⏱️ Concurrent search took %.2f ms%n", elapsedMIllis);


        FileSearcher fs2 = new FileSearcher(findWord,fileName, 1);
        startTime = System.nanoTime();
        fs2.startExecution();
        endTime = System.nanoTime();
        elapsedMIllis = (endTime - startTime) / 1_000_000.0;
        System.out.printf("⏱️ Single thread search took %.2f ms%n", elapsedMIllis);



    }
}