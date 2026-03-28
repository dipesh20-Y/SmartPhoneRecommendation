package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

//this class builds and manages the inverted index using a trie
//it reads phone data from the CSV file, indexes all words from each phone's specs, and allows searching for words across all phone documents
public class InvertedIndex {

    //the trie that stores all indexed words
    private Trie trie;

    //columns to skip during indexing
    //we skip url column(indexed 27) since url are not useful for searching
    private static final int URL_COLUMN_INDEX = 27;

    public InvertedIndex(){
        trie = new Trie();
    }

    //builds the Inverted index from given CSV file
    //reads each row, uses the phone name as document ID, and indexes all words from all relevant columns
    public void buildIndex(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath));
             CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines(true))) {

            for (CSVRecord record : parser) {
                // Use phone Name as document ID
                String documentId = record.get("Name").trim();

                // Go through every column value in this record
                for (int i = 0; i < record.size(); i++) {

                    // Skip URL column
                    if (i == URL_COLUMN_INDEX) {
                        continue;
                    }

                    // Split column value into words
                    String[] words = record.get(i).trim().split("\\s+");

                    for (String word : words) {
                        String cleanWord = word.toLowerCase()
                                .replaceAll("[^a-z0-9]", "");
                        if (!cleanWord.isEmpty()) {
                            trie.insert(cleanWord, documentId);
                        }
                    }
                }
            }
            System.out.println("Inverted index built successfully!");

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    //searches for a word in the inverted index
    public void search(String word){

        //clean the search word the same way we cleaned during insert
        String cleanWord = word.toLowerCase().replaceAll("[^a-z0-9]", "");
        System.out.println("\nSearching for: \"" + cleanWord + "\"");

        HashSet<String> results = trie.search(cleanWord);

        if(results.isEmpty()){
            System.out.println("No results found for: \"" + cleanWord + "\"");
        }else{
            System.out.println("Found in " + results.size() + " phone(s):");
            for(String doc: results){
                System.out.println("- " + doc);
            }
        }
    }
}
