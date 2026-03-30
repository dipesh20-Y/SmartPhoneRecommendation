package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * InvertedIndex constructs and manages a Trie-based inverted index for fast
 * keyword-to-document mapping in the smartphone recommendation system.
 * It processes every row of the input CSV, treating the "Name" field as the
 * unique document identifier, and indexes normalized terms from all columns
 * except the URL column. This structure supports efficient retrieval that
 * integrates directly with word completion, page ranking, and spell-checking
 * workflows.
 */
public class InvertedIndex {

    private Trie trie;

    private static final int URL_COLUMN_INDEX = 27;

    public InvertedIndex() {
        trie = new Trie();
    }

    /**
     * Builds the complete inverted index from the provided CSV dataset.
     *
     * For each phone record:
     * - The "Name" column becomes the document ID.
     * - All other columns (except URL) are tokenized.
     * - Tokens are normalized to lowercase with non-alphanumeric characters removed.
     * - Each valid term is inserted into the Trie along with its document ID.
     *
     * Null or empty file paths are rejected immediately with a clear error message.
     *
     * @param filePath absolute or relative path to the smartphone dataset CSV
     */
    public void buildIndex(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("Error: File path cannot be null or empty for building inverted index.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines(true))) {

            for (CSVRecord record : parser) {
                String documentId = record.get("Name").trim();

                if (documentId.isEmpty()) {
                    continue;
                }

                for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
                    if (columnIndex == URL_COLUMN_INDEX) {
                        continue;
                    }

                    String columnContent = record.get(columnIndex).trim();
                    String[] tokens = columnContent.split("\\s+");

                    for (String token : tokens) {
                        String cleanedTerm = token.toLowerCase().replaceAll("[^a-z0-9]", "");
                        if (!cleanedTerm.isEmpty()) {
                            trie.insert(cleanedTerm, documentId);
                        }
                    }
                }
            }

            System.out.println("Inverted index built successfully!");

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing CSV record: " + e.getMessage());
        }
    }

    /**
     * Performs a lookup for the specified term in the inverted index and
     * displays all matching phone documents.
     *
     * The search term undergoes identical normalization as during indexing
     * to guarantee consistent matching. Results are presented as a list of
     * document IDs (phone names).
     *
     * @param searchTerm the keyword or phrase to query against the index
     */
    public void search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("Error: Search term cannot be null or empty.");
            return;
        }

        String cleanedTerm = searchTerm.toLowerCase().replaceAll("[^a-z0-9]", "");

        System.out.println("\nSearching for: \"" + cleanedTerm + "\"");

        HashSet<String> matchingDocuments = trie.search(cleanedTerm);

        if (matchingDocuments.isEmpty()) {
            System.out.println("No results found for: \"" + cleanedTerm + "\"");
        } else {
            System.out.println("Found in " + matchingDocuments.size() + " phone(s):");
            for (String documentId : matchingDocuments) {
                System.out.println("- " + documentId);
            }
        }
    }
}