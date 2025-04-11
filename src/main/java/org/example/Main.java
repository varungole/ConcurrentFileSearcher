package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to my File searcher");
        String findWord = "time";
        String fileName = EnvLoader.get("FILENAME");
        int NUM_THREADS = Integer.parseInt(EnvLoader.get("NUM_THREADS"));
        FileSearcher fs = new FileSearcher(findWord,fileName, NUM_THREADS);
        fs.startExecution();
    }
}