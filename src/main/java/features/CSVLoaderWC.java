package features;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVLoaderWC is responsible for parsing a smartphone dataset CSV file and populating
 * a word-completion Trie (WCTrie) with normalized terms. It selectively extracts
 * and processes content from the Brand, Model, Processor, and OS columns to build
 * an efficient autocomplete dictionary for search queries.
 */
public class CSVLoaderWC {
    private static final List<PhoneData> phoneDatabase = new ArrayList<>();

    /**
     * Loads and normalizes relevant terms from the CSV dataset into the provided WCTrie.
     * <p>
     * This method performs the following steps:
     * 1. Validates input parameters.
     * 2. Skips the CSV header and processes each data row.
     * 3. Extracts Brand, Model, Processor, and OS fields.
     * 4. Combines the fields, converts to lowercase, removes non-alphabetic characters,
     * and inserts words longer than two characters into the Trie.
     * 5. Reports loading statistics and confirms activation of all system features.
     * Error handling ensures graceful failure with clear console messages for file I/O issues
     * or invalid input.
     *
     * @param filePath the absolute or relative path to the CSV file
     * @param trie     the WCTrie instance that will store the extracted vocabulary
     */
    public static void loadCSVIntoTrie(String filePath, WCTrie trie) {
        // Early validation for required parameters
        if (filePath == null || filePath.trim().isEmpty() || trie == null) {
            System.out.println("Invalid input: filePath cannot be null or empty and trie cannot be null.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;

            // Discard header row (first line of the CSV)
            String headerRow = reader.readLine();
            if (headerRow == null) {
                System.out.println("CSV file is empty: " + filePath);
                return;
            }

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().isEmpty()) {
                    continue; // ignore blank lines
                }

                // Split row by commas (simple split is safe for this dataset)
                String[] columns = currentLine.split(",", -1);

                // Ensure we have at least the expected number of columns
                if (columns.length < 12) {
                    continue;
                }

                // Extract the four fields used for word completion vocabulary
                String brandField = columns[0].trim();
                String modelField = columns[1].trim();
                String processorField = columns[8].trim();
                String osField = columns[11].trim();

                // Merge fields and prepare for tokenization
                String mergedContent = (brandField + " " + modelField + " " + processorField + " " + osField)
                        .toLowerCase().trim();

                String[] tokens = mergedContent.split("\\s+");

                for (String token : tokens) {
                    // Remove any remaining non-letter characters
                    String cleanedToken = token.replaceAll("[^a-z]", "");

                    // Only meaningful terms (length > 2) are added to the Trie
                    if (cleanedToken.length() > 2) {
                        trie.addWord(cleanedToken);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }

    }
}