package features;

import java.util.*;

public class MainSC {

    public static void main(String[] args) {

        try {
            String path = "src/main/resources/Combined_phone.csv";

            HashSet<String> vocabulary =
                    LoadingCSVFileSC.loadVocabulary(path);

            if (vocabulary.isEmpty()) {
                System.out.println("❌ Error: No vocabulary loaded from CSV file.");
                return;
            }

            SpellChecker checker = new SpellChecker(vocabulary);
            Scanner sc = new Scanner(System.in);

            boolean continueChecking = true;

            while (continueChecking) {
                try {
                    System.out.print("Enter a word to check: ");
                    String input = sc.nextLine().trim().toLowerCase();

                    if (input.isEmpty()) {
                        System.out.println("Please enter a word.");
                        continue;
                    }

                    if (checker.Correct_Word(input)) {
                        System.out.println("Word is spelled correctly.");
                    } else {
                        System.out.println("Incorrect spelling.");

                        List<SuggestionOf_Words> suggestions = new ArrayList<>();
                        Iterator<String> vocabIterator = vocabulary.iterator();

                        while (vocabIterator.hasNext()) {
                            String word = vocabIterator.next();
                            int dist = EditDistanceCompSC.compuation(input, word);

                            if (dist <= 2) {
                                suggestions.add(new SuggestionOf_Words(word, dist));
                            }
                        }

                        MergeSortingSC.mergeSort(suggestions);

                        if (!suggestions.isEmpty()) {
                            String bestSuggestion = suggestions.get(0).term;
                            System.out.println("Do you mean: " + bestSuggestion + "?");
                        } else {
                            System.out.println("No suggestions found.");
                        }
                    }

                    System.out.print("\nCheck another word? (yes/no): ");
                    String response = sc.nextLine().toLowerCase();
                    continueChecking = response.equals("yes") || response.equals("y");

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please try again.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            System.out.println("\nThank you for using the spell checker!");
            sc.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
