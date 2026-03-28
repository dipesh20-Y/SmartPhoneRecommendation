package features;

import java.util.List;
import java.util.Scanner;

/**
 * Main application to demonstrate word completion using Trie.
 */
public class WordCompletion{

    public static void main(String[] args) {

        WCTrie trie = new WCTrie();

        // CSV file generated from previous crawling tasks
        String csvPath = "src/main/resources/Combined_phone.csv";

        // Load vocabulary from CSV file
        boolean isLoaded = CSVLoaderWC.loadCSVIntoTrie(csvPath, trie);
        if(!isLoaded){
            System.out.println("Error in loading csv file into trie...");
            return;
        }
        System.out.println("Vocabulary loaded into Trie successfully...");

        Scanner scanner = new Scanner(System.in);
        while(true){
            // Ask user for prefix
            System.out.print("Enter prefix for word completion: ");
            String prefix = scanner.nextLine().toLowerCase();

            // Search words
            List<String> suggestions = trie.getWordsStartingWith(prefix);

            System.out.println("\nWords starting with \"" + prefix + "\":");

            if (suggestions.isEmpty()) {
                System.out.println("No matches found.");
            } else {

                for (String word : suggestions) {
                    System.out.println(word);
                }
            }

            System.out.print("\nDo you want to search another word? (yes/no): ");
            String choice = scanner.nextLine().toLowerCase();

            if (!choice.equals("yes") && !choice.equals("y")) {
                System.out.println("Program terminated.");
                break;
            }
        }
        scanner.close();
    }
}