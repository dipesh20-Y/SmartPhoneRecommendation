package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is used to count the word with pattern, and it also gives the implementation of Boyer Moore algorithm
 */
public class FrequencyCount {

    /**
     * REUSABLE METHOD - Used by RecommendationEngine for Frequency Count & Page Ranking
     */
    public static int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null || pattern.trim().isEmpty()) return 0;
        return performBoyerMoore(text.toLowerCase(), pattern.toLowerCase().trim());
    }

    public static int performBoyerMoore(String text, String pattern) {
        int totalMatches = 0;
        int textLength = text.length();
        int patternLength = pattern.length();

        if (patternLength == 0) return 0;

        int[] badCharacterTable = createBadCharTable(pattern);
        int shift = 0;

        while (shift <= textLength - patternLength) {
            int j = patternLength - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }
            if (j < 0) {
                totalMatches++;
                if (shift + patternLength < textLength) {
                    shift += patternLength - badCharacterTable[text.charAt(shift + patternLength)];
                } else {
                    shift += 1;
                }
            } else {
                int move = j - badCharacterTable[text.charAt(shift + j)];
                shift += Math.max(1, move);
            }
        }
        return totalMatches;
    }

    public static int[] createBadCharTable(String pattern) {
        final int ASCII_SIZE = 256;
        int[] table = new int[ASCII_SIZE];
        for (int i = 0; i < ASCII_SIZE; i++) table[i] = -1;
        for (int i = 0; i < pattern.length(); i++) {
            table[pattern.charAt(i)] = i;
        }
        return table;
    }
}