package org.example;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

class SearchTask implements Callable<Optional<Integer>> {
    private final List<String> lines;
    private final int startLine;
    private final int endLine;
    private final int offSet;
    private final AtomicBoolean found;
    private final String wordToFind;

    public SearchTask(List<String> lines, int startLine, int endLine, int offSet, AtomicBoolean found, String wordToFind) {
        this.lines = lines;
        this.startLine = startLine;
        this.endLine = endLine;
        this.offSet = offSet;
        this.found = found;
        this.wordToFind = wordToFind;
    }

    private void checkWord(String word, String wordToFind, int offSet, int j) {
        if (word.equalsIgnoreCase(wordToFind)) {
            if (found.compareAndSet(false, true)) {
                System.out.println("✅ Found \"" + wordToFind + "\" at line " + (offSet + j + 1));
            }
        }
    }

    public boolean wordAlreadyFound() {
        return Thread.currentThread().isInterrupted() || found.get();
    }

    @Override
    public Optional<Integer> call() {
        for (int j = startLine; j <= endLine && j < lines.size(); j++) {
            if (wordAlreadyFound()) break;

            String[] words = lines.get(j).split("\\s+");
            for (String word : words) {
                checkWord(word, wordToFind, offSet, j);
            }
        }
        return Optional.empty();
    }
}
