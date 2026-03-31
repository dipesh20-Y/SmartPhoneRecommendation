package features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    /**
     * Returns list of spelling suggestions sorted by edit distance.
     */
    public List<SuggestionOf_Words> getSpellingSuggestions(String word) {
        if (word == null || word.trim().isEmpty()) {
            System.out.println("Error: Search word cannot be null or empty for spell checking.");
            return new ArrayList<>();
        }

        String normalizedWord = word.toLowerCase().trim();
        List<SuggestionOf_Words> suggestions = new ArrayList<>();

        System.out.println("\n[ SPELL CHECK ] No direct matches. Checking spelling suggestions...");

        for (String vocabWord : vocabulary) {
            int distance = EditDistanceCompSC.computation(normalizedWord, vocabWord);
            if (distance <= 2) {
                suggestions.add(new SuggestionOf_Words(vocabWord, distance));
            }
        }

        if (suggestions.isEmpty()) {
            System.out.println("No spelling suggestions found for: " + normalizedWord);
            return suggestions;
        }

        MergeSortingSC.mergeSort(suggestions);
        return suggestions;
    }
}