package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * LoadVocabulary reads a CSV dataset and constructs a clean HashSet of
 * unique terms to serve as the master vocabulary for spell-checking and
 * word-completion features. Only alphabetic words longer than two characters
 * are retained after normalization. This vocabulary powers edit-distance
 * corrections and Trie-based autocomplete across the recommendation engine.
 */
public class LoadVocabulary {

    /**
     * Loads and normalizes all valid terms from the given CSV file into a
     * HashSet for fast lookup. The method skips the header row, concatenates
     * every column of each data row, removes non-alphabetic characters, and
     * retains only words longer than two characters.
     * The complex regex splitter correctly handles commas inside quoted fields.
     *
     * @param filePath absolute or relative path to the vocabulary CSV file
     * @return a HashSet containing the cleaned vocabulary; returns an empty set
     *         if the file cannot be read or the path is invalid
     */
    public static HashSet<String> loadVocabulary(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("Error: File path cannot be null or empty for loading vocabulary.");
            return new HashSet<>();
        }

        HashSet<String> vocabularySet = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip the header line
            String currentLine = reader.readLine();

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().isEmpty()) {
                    continue;
                }

                // Split line handling quoted fields that may contain commas
                String[] columns = currentLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Combine every column into a single string for tokenization
                StringBuilder combinedText = new StringBuilder();
                for (String column : columns) {
                    combinedText.append(" ").append(column);
                }

                // Normalize: lowercase and keep only letters and spaces
                String normalizedText = combinedText.toString()
                        .toLowerCase()
                        .replaceAll("[^a-z ]", " ");

                // Extract and store meaningful words
                for (String token : normalizedText.split("\\s+")) {
                    if (token.length() > 2) {
                        vocabularySet.add(token);
                    }
                }
            }

//            System.out.println("Total words loaded into vocabulary: " + vocabularySet.size());

        } catch (IOException e) {
            System.out.println("Error reading vocabulary file: " + e.getMessage());
        }

        return vocabularySet;
    }
}