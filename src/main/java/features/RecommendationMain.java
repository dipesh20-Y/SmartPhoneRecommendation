package features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RecommendationMain serves as the primary entry point for the Smartphone
 * Recommendation System. It integrates all core features including:
 * - Multi-criteria recommendation engine with weighted scoring
 * - Trie-based word completion
 * - Inverted index with frequency counting and page ranking
 * - Spell checking using edit distance and merge sort
 * - Input validation using regex patterns
 */
public class RecommendationMain {

    private static RecommendationEngine engine;
    private static DataValidation validator;
    private static Scanner scanner;
    private static WCTrie wordCompletionTrie;
    private static HashSet<String> spellVocabulary;

    public static void main(String[] args) {
        engine = new RecommendationEngine();
        validator = new DataValidation();
        scanner = new Scanner(System.in);
        wordCompletionTrie = new WCTrie();

        String csvPath = "src/main/resources/final_combined_phones.csv";

        System.out.println("========================================");
        System.out.println("   Smartphone Recommendation System");
        System.out.println("========================================\n");

        try {
            if (!engine.loadPhonesCsv(csvPath)) {
                System.out.println("Failed to load phone data. Exiting...");
                scanner.close();
                return;
            }

            // Load data for word completion
            CSVLoaderWC.loadCSVIntoTrie(csvPath, wordCompletionTrie);

            // Load vocabulary for spell checking
            spellVocabulary = LoadVocabulary.loadVocabulary(csvPath);

            // Main menu loop
            while (true) {
                displayMainMenu();
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1":
                            getRecommendations();
                            break;
                        case "2":
                            searchPhonesByKeyword();
                            break;
                        case "3":
                            viewAllPhones();
                            break;
                        case "4":
                             comparePhones();
                             break;
                        case "5":
                            System.out.println("\nThank you for using the Smartphone Recommendation System. Goodbye!");
                            scanner.close();
                            return;
                        default:
                            System.out.println("Invalid choice. Please try 1-5.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number (1-4).");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void displayMainMenu() {
        System.out.println("Main Menu");
        System.out.println("1. Get Phone Recommendations");
        System.out.println("2. Search Phones by Keyword");
        System.out.println("3. View All Available Phones");
        System.out.println("4. Compare Phones");
        System.out.println("5. Exit");
        System.out.print("\nSelect an option (1-5): ");
    }

    /**
     * Guides the user through preference collection and generates recommendations.
     */
    private static void getRecommendations() {
        try {
            UserPreferences prefs = new UserPreferences();

            System.out.println("--- Budget ---");
            System.out.println("Price range in database: $221 - $1899");
            double maxBudget = getPositiveDoubleInput();
            prefs.setMaxBudget(maxBudget);

            System.out.println("\n--- Memory (RAM) ---");
            System.out.println("Available RAM options: 6GB, 8GB, 12GB, 16GB");
            int minRam = getValidatedRAMInput();
            prefs.setMinRamGb(minRam);

            System.out.println("\n--- Storage ---");
            System.out.println("Available storage options: 128GB, 256GB");
            int minStorage = getValidatedStorageInput();
            prefs.setMinStorageGb(minStorage);

            System.out.println("\n--- Display Size ---");
            System.out.println("Available display sizes: 154.9\", 156.4\", 157.5\", 160.0\", 169.1\", 170.2\", 172.7\", 174.1\", 203.1\"");
            double minDisplay = getValidatedDisplayInput();
            prefs.setMinDisplaySize(minDisplay);

            System.out.println("\n--- Battery ---");
            System.out.println("Available battery capacities: 3900, 4000, 4300, 4400, 4575, 4700, 4870, 5000, 5003, 5050, 5100, 5200 mAh");
            int minBattery = getValidatedBatteryInput();
            prefs.setMinBatteryMah(minBattery);

            System.out.println("\n--- Camera ---");
            System.out.println("Available camera specs: 2MP, 48MP, 50MP, 64MP, 200MP");
            int minCamera = getValidatedCameraInput();
            prefs.setMinMainCameraMp(minCamera);

            System.out.println("\n--- Optional Features ---");
            boolean needsHeadphone = getYesNoInput("Do you need a headphone jack? (y/n): ");
            prefs.setNeedsHeadphoneJack(needsHeadphone);

            boolean needsWaterResistance = getYesNoInput("Do you need water resistance? (y/n): ");
            prefs.setNeedsWaterResistance(needsWaterResistance);

            boolean needsHighRefresh = getYesNoInput("Do you prefer high refresh rate (120Hz+)? (y/n): ");
            prefs.setNeedsHighRefreshRate(needsHighRefresh);

            System.out.println("\nAnalyzing phones based on your preferences...");
            List<Recommendation> recommendations = engine.getRecommendations(prefs);

            if (recommendations.isEmpty()) {
                System.out.println("\nNo phones match your criteria.");
                System.out.println("Try adjusting your preferences or selecting from available options.");
                return;
            }
            // New Feature: Sort by Price using Heap Sort
            System.out.print("\nSort recommendations by price? (y/n): ");
            String sortChoice = scanner.nextLine().trim().toLowerCase();

            if (sortChoice.equals("y") || sortChoice.equals("yes")) {
                heapSortByPrice(recommendations);
                System.out.println("Recommendations sorted by price (lowest first) using Heap Sort.");
            }

            displayRecommendations(recommendations);

        } catch (Exception e) {
            System.out.println("Error during recommendation process: " + e.getMessage());
        }
    }

    /**
     * Displays the top recommendations with detailed information.
     */
    private static void displayRecommendations(List<Recommendation> recommendations) {
        if (recommendations.isEmpty()) {
            System.out.println("\nNo phones match your criteria.");
            System.out.println("Please try adjusting your preferences.");
            return;
        }

        System.out.println("\nTop Recommendations for You\n");

        int maxDisplay = Math.min(10, recommendations.size());
        for (int i = 0; i < maxDisplay; i++) {
            Recommendation rec = recommendations.get(i);
            PhoneData phone = rec.getPhone();

            System.out.println((i + 1) + ". " + phone.getName());
            System.out.println("   Match Score: " + String.format("%.1f%%", rec.getOverallScore()));
            System.out.println("   Price: $" + String.format("%.2f", phone.getPrice()));
            System.out.println("   RAM: " + phone.getRamGb() + "GB | Storage: " + phone.getStorageGb() + "GB");
            System.out.println("   Display: " + String.format("%.1f", phone.getDisplaySize()) + "\" | Battery: " + phone.getBatteryMah() + "mAh");
            System.out.println("   Main Camera: " + phone.getMainCamera());
            System.out.println("   Headphone Jack: " + (phone.hasHeadphoneJack() ? "Yes" : "No"));
            System.out.println("   Water Resistant: " + (phone.getWaterResistance() != null &&
                    !phone.getWaterResistance().equalsIgnoreCase("No") ? "Yes" : "No"));

            if (phone.getUrl() != null && !phone.getUrl().isEmpty()) {
                System.out.println("   More Info: " + phone.getUrl());
            }
            System.out.println();
        }

        if (recommendations.size() > maxDisplay) {
            System.out.println("... and " + (recommendations.size() - maxDisplay) + " more phones match your criteria.");
        }
    }

    /**
     * Performs keyword-based search using inverted index, frequency count,
     * and page ranking. Includes word completion and spell checking fallbacks.
     */
    private static void searchPhonesByKeyword() {
        try {
            System.out.println("\n======== Phone Keyword Search ========\n");

            System.out.print("Enter keyword to search (e.g., 'Samsung', 'pixel', 'ios'): ");
            String keyword = scanner.nextLine().trim().toLowerCase();

            if (keyword.isEmpty()) {
                System.out.println("Please enter a keyword.");
                return;
            }

            // Basic input validation
            if (!keyword.matches("^[a-z0-9\\s\\-]+$")) {
                System.out.println("Invalid search input.");
                System.out.println("\tUse only: letters, numbers, spaces, hyphens");
                return;
            }
            System.out.println("Input validation passed");

            // Pattern finding demonstration
            System.out.println("\nPATTERN FINDING: Analyzing search query...");

            Pattern brandPattern = Pattern.compile("(google|samsung|apple|oneplus|xiaomi|pixel|galaxy|iphone)", Pattern.CASE_INSENSITIVE);
            Matcher brandMatcher = brandPattern.matcher(keyword);
            if (brandMatcher.find()) {
                System.out.println("  Brand found: \"" + brandMatcher.group() + "\"");
            }

            Pattern techPattern = Pattern.compile("(camera|battery|display|chipset|processor|5g|4g|android|ios)", Pattern.CASE_INSENSITIVE);
            Matcher techMatcher = techPattern.matcher(keyword);
            if (techMatcher.find()) {
                System.out.println("  Technology found: \"" + techMatcher.group() + "\"");
            }

            Pattern specPattern = Pattern.compile("(\\d+mp|\\d+gb|\\d+mah)", Pattern.CASE_INSENSITIVE);
            Matcher specMatcher = specPattern.matcher(keyword);
            if (specMatcher.find()) {
                System.out.println("  Specification found: \"" + specMatcher.group() + "\"");
            }

            // Primary search using Inverted Index + Frequency Count + Page Ranking
            System.out.println("\nINVERTED INDEX SEARCH: Searching phones...\n");
            boolean found = engine.searchByKeyword(keyword);

            // ==================== WORD COMPLETION ====================
            // ==================== WORD COMPLETION ====================
            // Show Word Completion ONLY if no results were found
            if (!found && wordCompletionTrie != null) {
                List<String> completions = wordCompletionTrie.getWordsStartingWith(keyword);
                if (!completions.isEmpty()) {
                    System.out.println("\n[WORD COMPLETION] Suggested words:");
                    for (String c : completions) {
                        System.out.println("  - " + c);
                    }

                    System.out.print("\nType the corrected word to search with it, or type 'exit' to go back: ");
                    String corrected = scanner.nextLine().trim().toLowerCase();

                    if (!corrected.equals("exit") && !corrected.isEmpty()) {
                        engine.searchByKeyword(corrected);
                    }
                    return;
                }
            }

            // If inverted index found results, we are done
            if (found) {
                return;
            }

            // Spell Checking fallback (only if no results found)
            if (spellVocabulary == null || spellVocabulary.isEmpty()) {
                System.out.println("\nSPELL CHECK: No vocabulary loaded.");
                return;
            }

            System.out.println("\nSPELL CHECK: No direct matches. Checking spelling suggestions...");

            List<SuggestionOf_Words> suggestions = new ArrayList<>();
            for (String vocabWord : spellVocabulary) {
                int dist = EditDistanceCompSC.computation(keyword, vocabWord);
                if (dist <= 2) {
                    suggestions.add(new SuggestionOf_Words(vocabWord, dist));
                }
            }

            MergeSortingSC.mergeSort(suggestions);

            if (suggestions.isEmpty()) {
                System.out.println("No spelling suggestions found for: " + keyword);
                return;
            }

            String bestSuggestion = suggestions.get(0).term;
            System.out.println("Did you mean: " + bestSuggestion + " ?");
            System.out.print("Search with this corrected word? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("y") || answer.equals("yes")) {
                engine.searchByKeyword(bestSuggestion);
            } else {
                System.out.println("No search performed.");
            }

        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }

    /**
     * Displays all available phones sorted by name.
     */
    private static void viewAllPhones() {
        try {
            System.out.println("\nAll Available Phones\n");

            List<PhoneData> phones = engine.getAllPhones();

            if (phones == null || phones.isEmpty()) {
                System.out.println("No phones available.");
                return;
            }

            // Sort by name
            phones.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

            for (int i = 0; i < phones.size(); i++) {
                PhoneData phone = phones.get(i);
                System.out.println((i + 1) + ". " + phone.getName());
                System.out.println("   Price: $" + String.format("%.2f", phone.getPrice()));
                System.out.println("   RAM: " + phone.getRamGb() + "GB | Storage: " + phone.getStorageGb() + "GB");
                System.out.println("   Chipset: " + phone.getChipset());
                System.out.println("   More Info: " + phone.getUrl());
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Browse error: " + e.getMessage());
        }
    }

    // ==================== INPUT VALIDATION HELPERS ====================

    private static double getPositiveDoubleInput() {
        while (true) {
            System.out.print("Enter your maximum budget ($): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
                continue;
            }

            if (!validator.validateBudget(input)) {
                System.out.println("   Expected format: $500, 500, or 500.99");
                continue;
            }

            String extracted = input.replace("$", "").replace(",", "").trim();
            try {
                double value = Double.parseDouble(extracted);
                if (value > 0) return value;
                else System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Budget" + " must be a valid number.");
            }
        }
    }

    private static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    private static int getValidatedRAMInput() {
        while (true) {
            System.out.print("Select minimum RAM (choose from above): ");
            String input = scanner.nextLine().trim();
            try {
                int ram = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                if (ram == 6 || ram == 8 || ram == 12 || ram == 16) return ram;
                else System.out.println("Please select from: 6GB, 8GB, 12GB, 16GB");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid RAM option.");
            }
        }
    }

    private static int getValidatedStorageInput() {
        while (true) {
            System.out.print("Select minimum storage (choose from above): ");
            String input = scanner.nextLine().trim();
            try {
                int storage = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                if (storage == 128 || storage == 256) return storage;
                else System.out.println("Please select from: 128GB, 256GB");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid storage option.");
            }
        }
    }

    private static double getValidatedDisplayInput() {
        while (true) {
            System.out.print("Select minimum display size (choose from above): ");
            String input = scanner.nextLine().trim();
            try {
                double display = Double.parseDouble(input.replaceAll("[^0-9.]", ""));
                if (display == 154.9 || display == 156.4 || display == 157.5 || display == 160.0 ||
                        display == 169.1 || display == 170.2 || display == 172.7 || display == 174.1 || display == 203.1) {
                    return display;
                } else {
                    System.out.println("Please select from available display sizes.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid display size.");
            }
        }
    }

    private static int getValidatedBatteryInput() {
        while (true) {
            System.out.print("Select minimum battery (choose from above): ");
            String input = scanner.nextLine().trim();
            try {
                int battery = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                if (battery == 3900 || battery == 4000 || battery == 4300 || battery == 4400 ||
                        battery == 4575 || battery == 4700 || battery == 4870 || battery == 5000 ||
                        battery == 5003 || battery == 5050 || battery == 5100 || battery == 5200) {
                    return battery;
                } else {
                    System.out.println("Please select from available battery values.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid battery option.");
            }
        }
    }

    private static int getValidatedCameraInput() {
        while (true) {
            System.out.print("Select minimum camera (choose from above): ");
            String input = scanner.nextLine().trim();
            try {
                int camera = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                if (camera == 2 || camera == 48 || camera == 50 || camera == 64 || camera == 200) {
                    return camera;
                } else {
                    System.out.println("Please select from: 2MP, 48MP, 50MP, 64MP, 200MP");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid camera option.");
            }
        }
    }

    // Helper class for Merge Sort by price
    /**
     * Heap Sort implementation to sort recommendations by price (lowest to highest)
     */
    private static void heapSortByPrice(List<Recommendation> recommendations) {
        int n = recommendations.size();

        // Build heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            doHeapify(recommendations, n, i);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Swap root with last element
            Recommendation temp = recommendations.get(0);
            recommendations.set(0, recommendations.get(i));
            recommendations.set(i, temp);

            // Heapify the reduced heap
            doHeapify(recommendations, i, 0);
        }
    }

    private static void doHeapify(List<Recommendation> recommendations, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && recommendations.get(left).getPhone().getPrice() >
                recommendations.get(largest).getPhone().getPrice()) {
            largest = left;
        }

        if (right < n && recommendations.get(right).getPhone().getPrice() >
                recommendations.get(largest).getPhone().getPrice()) {
            largest = right;
        }

        if (largest != i) {
            Recommendation swap = recommendations.get(i);
            recommendations.set(i, recommendations.get(largest));
            recommendations.set(largest, swap);

            doHeapify(recommendations, n, largest);
        }
    }

    // ==================== NEW FEATURE: Compare Phones ====================

    private static void comparePhones() {
        List<PhoneData> allPhones = engine.getAllPhones();
        if (allPhones.isEmpty()) {
            System.out.println("No phones available to compare.");
            return;
        }

        System.out.println("\n=== Compare Phones ===");
        System.out.println("Enter up to 4 phone numbers to compare (separated by space):");

        // Show first 15 phones for easy selection
        for (int i = 0; i < Math.min(15, allPhones.size()); i++) {
            System.out.println((i+1) + ". " + allPhones.get(i).getName());
        }

        System.out.print("\nYour selection (e.g., 1 5 12): ");
        String input = scanner.nextLine().trim();

        String[] indices = input.split("\\s+");
        List<PhoneData> selected = new ArrayList<>();

        for (String idx : indices) {
            try {
                int index = Integer.parseInt(idx) - 1;
                if (index >= 0 && index < allPhones.size()) {
                    selected.add(allPhones.get(index));
                }
            } catch (Exception ignored) {}
        }

        if (selected.size() < 2) {
            System.out.println("Please select at least 2 phones to compare.");
            return;
        }

        if (selected.size() > 4) {
            selected = selected.subList(0, 4);
        }

        displayComparisonTable(selected);
    }

    private static void displayComparisonTable(List<PhoneData> phones) {
        System.out.println("\n" + "=".repeat(150));
        System.out.println("                                      PHONE COMPARISON");
        System.out.println("=".repeat(150));

        // Shorten phone names for better display
        String[] shortNames = new String[phones.size()];
        for (int i = 0; i < phones.size(); i++) {
            String name = phones.get(i).getName();
            shortNames[i] = name.length() > 28 ? name.substring(0, 25) + "..." : name;
        }

        // Print header row (Phone names)
        System.out.printf("%-35s", "Feature");
        for (String name : shortNames) {
            System.out.printf("%-30s", name);
        }
        System.out.println();
        System.out.println("-".repeat(150));

        // Print each feature as a row
        printRow("Price", phones, p -> "$" + String.format("%.2f", p.getPrice()));
        printRow("RAM", phones, p -> p.getRamGb() + " GB");
        printRow("Storage", phones, p -> p.getStorageGb() + " GB");
        printRow("Display Size", phones, p -> String.format("%.1f", p.getDisplaySize()) + "\"");
        printRow("Battery", phones, p -> p.getBatteryMah() + " mAh");
        printRow("Main Camera", phones, PhoneData::getMainCamera);
        printRow("OS", phones, PhoneData::getOs);
        printRow("Headphone Jack", phones, p -> p.hasHeadphoneJack() ? "Yes" : "No");
        printRow("Water Resistance", phones, p ->
                (p.getWaterResistance() != null && !p.getWaterResistance().equalsIgnoreCase("No")) ? "Yes" : "No");

        System.out.println("=".repeat(150));
    }

    private static void printRow(String feature, List<PhoneData> phones, java.util.function.Function<PhoneData, String> getter) {
        System.out.printf("%-35s", feature);
        for (PhoneData p : phones) {
            String value = getter.apply(p);
            System.out.printf("%-30s", value);
        }
        System.out.println();
    }
}