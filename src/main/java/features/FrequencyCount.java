package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 *  This program performs the following operations:
 *  1. Reads a CSV dataset containing smartphone specifications.
 *  2. Converts the file contents into a searchable text string.
 *  3. Allows the user to search for a keyword.
 *  4. Uses the Boyer–Moore string matching algorithm to count
 *     how many times the word appears in the dataset.
 *  5. Tracks how many times a user searches for a word using
 *     a HashMap.
 */

public class FrequencyCount {

    /*
     * HashMap used to record how many times each word
     * has been searched by the user.
     *
     * Example:
     *  "camera" -> 3
     *  "samsung" -> 2
     */
    private static Map<String, Integer> searchHistory = new HashMap<>();


    public static void main(String[] args) {

        /*
         * Path to the dataset file.
         * Make sure your CSV file is located in the project root folder.
         */
        String datasetFile = "Assignment2_csvfile_filled.csv";

        /*
         * Read the dataset and convert it into a single large string.
         * This string will be used by the searching algorithm.
         */
        String datasetText = loadDataset(datasetFile);

        // If reading the file failed, stop the program
        if (datasetText == null) {
            System.out.println("Dataset could not be loaded. Program terminated.");
            return;
        }

        // Scanner object used to read input from the user
        Scanner input = new Scanner(System.in);

        System.out.println("Dataset successfully loaded.");
        System.out.println("You can now search for words inside the dataset.");
        System.out.println("Type 'exit' if you want to stop the program.");

        /*
         * The program will repeatedly ask the user for input.
         * The loop continues until the user enters "exit".
         */
        while (true) {

            System.out.print("Enter a word to search: ");
            String userWord = input.nextLine().toLowerCase();

            // If user wants to quit the program
            if (userWord.equals("exit")) {
                System.out.println("Program finished.");
                break;
            }

            /*
             * Perform Boyer–Moore search on the dataset text
             * to find the number of occurrences.
             */
            int occurrences = performBoyerMoore(datasetText.toLowerCase(), userWord);

            // Display the number of matches found
            System.out.println("Occurrences found in dataset: " + occurrences);

            /*
             * Update the search frequency map.
             * If the word already exists in the map, increment its value.
             * Otherwise insert the word with count = 1.
             */
            searchHistory.put(userWord,
                    searchHistory.getOrDefault(userWord, 0) + 1);

            // Display how many times the word has been searched
            System.out.println("Search frequency for \"" + userWord + "\": "
                    + searchHistory.get(userWord));

        }

        input.close();
    }


    /*
     * Function: loadDataset
     *
     * Reads a CSV file and converts it into a single text string.
     * Each line of the CSV file is appended into a StringBuilder.
     *
     * Why StringBuilder?
     * Because repeatedly concatenating strings using "+" is slow.
     * StringBuilder is more efficient for building large text.
     */
    private static String loadDataset(String fileName) {

        StringBuilder dataBuilder = new StringBuilder();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String currentLine;

            /*
             * Read the file line by line until we reach the end.
             */
            while ((currentLine = reader.readLine()) != null) {

                /*
                 * Each line is appended to the StringBuilder.
                 * A space is added so words do not merge together.
                 */
                dataBuilder.append(currentLine).append(" ");
            }

            reader.close();

        } catch (IOException e) {

            /*
             * If there is an error reading the file
             * (for example file not found), print the stack trace.
             */
            e.printStackTrace();
            return null;
        }

        // Convert StringBuilder to a regular String
        return dataBuilder.toString();
    }


    /*
     * Function: performBoyerMoore
     *
     * This method searches for a pattern inside a text using
     * the Boyer–Moore algorithm.
     *
     * The algorithm improves performance by skipping sections
     * of the text instead of checking every character.
     *
     * Returns:
     *  The number of times the pattern appears in the text.
     */
    private static int performBoyerMoore(String text, String pattern) {

        int totalMatches = 0;

        int textLength = text.length();
        int patternLength = pattern.length();

        /*
         * Create the Bad Character table which stores
         * the last occurrence index of each character
         * in the pattern.
         */
        int[] badCharacterTable = createBadCharTable(pattern);

        int shift = 0;

        /*
         * Continue searching while the pattern still fits
         * inside the remaining portion of the text.
         */
        while (shift <= (textLength - patternLength)) {

            int j = patternLength - 1;

            /*
             * Compare characters from right to left
             * until a mismatch occurs.
             */
            while (j >= 0 &&
                    pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            /*
             * If j < 0, it means the pattern was matched
             * successfully in the text.
             */
            if (j < 0) {

                totalMatches++;

                /*
                 * Shift pattern so that the next character
                 * in text aligns with the last occurrence
                 * of that character in the pattern.
                 */
                if (shift + patternLength < textLength) {
                    shift += patternLength -
                            badCharacterTable[text.charAt(shift + patternLength)];
                } else {
                    shift += 1;
                }

            } else {

                /*
                 * Apply the bad character rule to determine
                 * how far we should move the pattern.
                 */
                int move = j - badCharacterTable[text.charAt(shift + j)];

                shift += Math.max(1, move);
            }
        }

        return totalMatches;
    }


    /*
     * Function: createBadCharTable
     *
     * Builds the Bad Character table required for the
     * Boyer–Moore algorithm.
     *
     * The table stores the last position of every character
     * in the pattern.
     *
     * Characters not present in the pattern will have value -1.
     */
    private static int[] createBadCharTable(String pattern) {

        final int ASCII_SIZE = 256;

        int[] table = new int[ASCII_SIZE];

        // Initialize all values to -1
        for (int i = 0; i < ASCII_SIZE; i++) {
            table[i] = -1;
        }

        /*
         * Fill the table with the last occurrence
         * of each character in the pattern.
         */
        for (int i = 0; i < pattern.length(); i++) {
            table[pattern.charAt(i)] = i;
        }

        return table;
    }
}