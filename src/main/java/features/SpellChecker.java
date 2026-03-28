package features;

import java.util.HashSet;   // HashSet is used to store vocabulary words for fast lookup

// This class is responsible for checking whether a given word
// exists in the vocabulary loaded from the CSV file
public class SpellChecker {

    // This HashSet stores all valid words extracted from the dataset
    // Using a set ensures that lookups are efficient and duplicates are avoided
    private HashSet<String> storingWords;

    // Constructor initializes the spell checker with the vocabulary data
    // passed from the main program
    public SpellChecker(HashSet<String> inputData) {
        this.storingWords = inputData;
    }

    // This method checks if a word is spelled correctly
    // by verifying its presence in the stored vocabulary
    public boolean Correct_Word(String value) {

        // Handle edge cases where the input is null or empty
        // Such inputs cannot be valid dictionary words
        if (value == null || value.length() == 0) {
            return false;
        }

        // Convert the input word to lowercase so that
        // the comparison is case-insensitive
        String normalization_words = value.toLowerCase();

        // Check if the normalized word exists in the HashSet
        // and return the result of that lookup
        return storingWords.contains(normalization_words);
    }
}