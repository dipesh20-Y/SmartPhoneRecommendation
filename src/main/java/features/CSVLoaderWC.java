package features;

import features.WCTrie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * CSVLoader reads crawled CSV files and loads words into the Trie.
 * For this project, it extracts words from Brand, Model, Processor, and OS
 * columns of the combined smartphone CSV file.
 */
public class CSVLoaderWC {

    public static boolean loadCSVIntoTrie(String filePath, WCTrie trie) {
        boolean isLoaded = false;
        int wordCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read and discard header line
            String header = reader.readLine();
            if (header == null) {
                System.out.println("CSV file is empty: " + filePath);
                return false;
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip blank lines
                }

                // Split the row into columns. Our Combined_phone.csv has no quoted commas,
                // so simple split is sufficient here.
                String[] columns = line.split(",", -1);

                // Expect at least Brand, Model, and OS columns
                if (columns.length < 12) {
                    continue;
                }

                String brand = columns[0];          // Brand
                String model = columns[1];          // Model
                String processor = columns[8];      // Processor
                String os = columns[11];            // OS

                // Combine the text fields we care about
                String combined = (brand + " " + model + " " + processor + " " + os).toLowerCase();

                // Split into words on whitespace
                String[] words = combined.split("\\s+");

                for (String word : words) {
                    // Normalize
                    word = word.toLowerCase().trim();

                    // Remove numbers and special characters; keep only letters
                    word = word.replaceAll("[^a-z]", "");

                    // Insert meaningful words into Trie (length > 2)
                    if (word.length() > 2) {
                        trie.addWord(word);
                        wordCount++;
                    }
                }
            }

            isLoaded = true;
            System.out.println("Total words loaded into Trie: " + wordCount);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return isLoaded;
    }
}
