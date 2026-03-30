package features;

import java.util.HashSet;

/**
 * SpellChecker provides fast dictionary lookup functionality to determine
 * whether a given word exists in the system's vocabulary. It supports the
 * spell-checking feature by comparing user input against terms extracted
 * from the smartphone dataset.
 */
public class SpellChecker {

    private final HashSet<String> vocabulary;

    /**
     * Constructs a SpellChecker with the provided vocabulary set.
     *
     * @param inputVocabulary the HashSet containing valid words loaded from the dataset
     */
    public SpellChecker(HashSet<String> inputVocabulary) {
        if (inputVocabulary == null) {
            System.out.println("Error: Vocabulary cannot be null for SpellChecker initialization.");
            this.vocabulary = new HashSet<>();
        } else {
            this.vocabulary = inputVocabulary;
        }
    }

    /**
     * Checks whether the given word is spelled correctly by looking it up
     * in the loaded vocabulary. The comparison is case-insensitive.
     *
     * @param value the word to be checked for spelling correctness
     * @return true if the word exists in the vocabulary, false otherwise
     */
    public boolean Correct_Word(String value) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println("Error: Input word cannot be null or empty for spell checking.");
            return false;
        }

        // Normalize to lowercase for case-insensitive matching
        String normalizedWord = value.toLowerCase().trim();

        return vocabulary.contains(normalizedWord);
    }
}