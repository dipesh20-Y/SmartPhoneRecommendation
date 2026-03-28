package features;

import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * RecommendationMain is the interactive entry point for the smartphone
 * recommendation system. It guides users through their preferences and
 * displays personalized recommendations.
 *
 * FEATURES INTEGRATED:
 * 1. RECOMMENDATION ENGINE: Scores & ranks phones
 * 2. DATA VALIDATION: Validates user input using regex patterns
 * 3. PATTERN FINDING: Extracts numbers/keywords from user input
 * 4. INVERTED INDEX SEARCH: Searches using Trie data structure
 */
public class RecommendationMain {

    private static RecommendationEngine engine;
    private static DataValidation validator;
    private static Scanner scanner;

    // Trie for word completion (brand / model / processor / OS)
    private static WCTrie wordCompletionTrie;

    // Vocabulary and spell checker components (reused from MainSC)
    private static HashSet<String> spellVocabulary;

    public static void main(String[] args) {
        engine = new RecommendationEngine();
        validator = new DataValidation();
        scanner = new Scanner(System.in);
        wordCompletionTrie = new WCTrie();

        String csvPath = "src/main/resources/Combined_phone.csv";

        System.out.println("========================================");
        System.out.println("   Smartphone Recommendation System");
        System.out.println("========================================\n");

        System.out.println("Loading phone data from CSV...");

        try {
            if (!engine.loadPhonesCsv(csvPath)) {
                System.out.println("Failed to load phone data. Exiting...");
                scanner.close();
                return;
            }

            // Also load vocabulary for word completion from the same CSV
            CSVLoaderWC.loadCSVIntoTrie(csvPath, wordCompletionTrie);

            // Load vocabulary for spell checking using existing LoadingCSVFileSC
            spellVocabulary = LoadingCSVFileSC.loadVocabulary(csvPath);

            // Main menu loop with error handling
            try {
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
                                System.out.println("\nThank you for using the Smartphone Recommendation System. Goodbye!");
                                return;
                            default:
                                System.out.println("Invalid choice. Please try 1-4.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number (1-4).");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            } finally {
                scanner.close();
            }

        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            scanner.close();
        }
    }


    /**
     * Display main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("   Main Menu");
        System.out.println("========================================");
        System.out.println("1. Get Phone Recommendations");
        System.out.println("2. Search Phones by Keyword");
        System.out.println("3. View All Available Phones");
        System.out.println("4. Exit");
        System.out.print("\nSelect an option (1-4): ");
    }

    /**
     * Guide user through preference gathering and display recommendations
     * DATA VALIDATION & PATTERN FINDING work here automatically
     */
    private static void getRecommendations() {
        try {
            System.out.println("\n========================================");
            System.out.println("   Recommendation Wizard");
            System.out.println("   (Data Validation + Pattern Finding Active)");
            System.out.println("========================================\n");

            UserPreferences prefs = new UserPreferences();

            try {
                System.out.println("--- Budget ---");
                System.out.println("Price range in database: $221 - $1899");
                double maxBudget = getPositiveDoubleInput("Enter your maximum budget ($): ", "Budget");
                prefs.setMaxBudget(maxBudget);

                System.out.println("\n--- Memory (RAM) ---");
                System.out.println("Available RAM options: 6GB, 8GB, 12GB, 16GB");
                int minRam = getValidatedRAMInput("Select minimum RAM (choose from above): ");
                prefs.setMinRamGb(minRam);

                System.out.println("\n--- Storage ---");
                System.out.println("Available storage options: 128GB, 256GB");
                int minStorage = getValidatedStorageInput("Select minimum storage (choose from above): ");
                prefs.setMinStorageGb(minStorage);

                System.out.println("\n--- Display Size ---");
                System.out.println("Available display sizes: 154.9\", 156.4\", 157.5\", 160.0\", 169.1\", 170.2\", 172.7\", 174.1\", 203.1\"");
                double minDisplay = getValidatedDisplayInput("Select minimum display size (choose from above): ");
                prefs.setMinDisplaySize(minDisplay);

                System.out.println("\n--- Battery ---");
                System.out.println("Available battery capacities: 3900, 4000, 4300, 4400, 4575, 4700, 4870, 5000, 5003, 5050, 5100, 5200 mAh");
                int minBattery = getValidatedBatteryInput("Select minimum battery (choose from above): ");
                prefs.setMinBatteryMah(minBattery);

                System.out.println("\n--- Camera ---");
                System.out.println("Available camera specs: 2MP, 48MP, 50MP, 64MP, 200MP");
                int minCamera = getValidatedCameraInput("Select minimum camera (choose from above): ");
                prefs.setMinMainCameraMp(minCamera);

                System.out.println("\n--- Optional Features ---");
                boolean needsHeadphone = getYesNoInput("Do you need a headphone jack? (y/n): ");
                prefs.setNeedsHeadphoneJack(needsHeadphone);

                boolean needsWaterResistance = getYesNoInput("Do you need water resistance? (y/n): ");
                prefs.setNeedsWaterResistance(needsWaterResistance);

                boolean needsHighRefresh = getYesNoInput("Do you prefer high refresh rate (120Hz+)? (y/n): ");
                prefs.setNeedsHighRefreshRate(needsHighRefresh);

                System.out.println("\n\nAnalyzing phones based on your preferences...");
                List<Recommendation> recommendations = engine.getRecommendations(prefs);

                if (recommendations.isEmpty()) {
                    System.out.println("\nNo phones match your criteria.");
                    System.out.println("Try adjusting your preferences or selecting from available options.");
                    return;
                }

                displayRecommendations(recommendations);

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please try again with valid numbers.");
            } catch (Exception e) {
                System.out.println("Error during preference input: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Recommendation error: " + e.getMessage());
        }
    }



    /**
     * Display recommendations to user with URLs
     */
    private static void displayRecommendations(List<Recommendation> recommendations) {
        if (recommendations.isEmpty()) {
            System.out.println("\n❌ No phones match your criteria.");
            System.out.println("Please try adjusting your preferences.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("   Top Recommendations for You");
        System.out.println("========================================\n");

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
            System.out.println("   Water Resistant: " + (phone.getWaterResistance() != null && !phone.getWaterResistance().equalsIgnoreCase("No") ? "Yes" : "No"));

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
     * Search phones by keyword WITH PATTERN FINDING, DATA VALIDATION,
     * WORD COMPLETION and SPELL CHECKING.
     */
    private static void searchPhonesByKeyword() {
        try {
            System.out.println("\n========================================");
            System.out.println("   Phone Keyword Search");
            System.out.println("========================================\n");

            System.out.print("Enter keyword to search (e.g., 'pixel', 'ios'): ");
            String keyword = scanner.nextLine().trim().toLowerCase();

            if (keyword.isEmpty()) {
                System.out.println("Please enter a keyword.");
                return;
            }

            // ===== DATA VALIDATION: Check if input format is valid =====
            if (!keyword.matches("^[a-z0-9\\s\\-]+$")) {
                System.out.println("Invalid search input.");
                System.out.println("   Use only: letters, numbers, spaces, hyphens");
                return;
            }
            System.out.println("Input validation passed");

            // ===== PATTERN FINDING: Extract patterns from search input =====
            System.out.println("\n[PATTERN FINDING] Analyzing search query...");

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

            // ===== INVERTED INDEX SEARCH: Use RecommendationEngine to search =====
            System.out.println("\n[INVERTED INDEX SEARCH] Searching phones...\n");
            boolean found = engine.searchByKeyword(keyword);
            if (found) {
                // We have results; no need to fall back to completion/spell check
                return;
            }

            // ===== WORD COMPLETION: Suggest completions for prefixes =====
            if (wordCompletionTrie != null) {
                List<String> completions = wordCompletionTrie.getWordsStartingWith(keyword);
                if (!completions.isEmpty()) {
                    System.out.println("\n[WORD COMPLETION] Suggestions:");
                    for (String c : completions) {
                        System.out.println("  - " + c);
                    }

                    System.out.print("\nType the corrected word to search with it, or type 'exit' to go back to the main menu: ");
                    String corrected = scanner.nextLine().trim().toLowerCase();

                    if (corrected.equals("exit")) {
                        System.out.println("Returning to main menu.");
                        return;
                    }

                    if (corrected.isEmpty()) {
                        System.out.println("No word entered. Returning to main menu.");
                        return;
                    }

                    System.out.println("\n[WORD COMPLETION] Searching with: " + corrected);
                    engine.searchByKeyword(corrected);
                    return;
                }
            }

            // ===== SPELL CHECKING: Use existing edit distance + merge sort =====
            if (spellVocabulary == null || spellVocabulary.isEmpty()) {
                System.out.println("\n[ SPELL CHECK ] No vocabulary loaded for spell checking.");
                return;
            }

            System.out.println("\n[ SPELL CHECK ] No direct matches. Checking spelling suggestions...");

            List<SuggestionOf_Words> suggestions = new ArrayList<>();

            for (String vocabWord : spellVocabulary) {
                int dist = EditDistanceCompSC.compuation(keyword, vocabWord);
                // Only consider close words (distance <= 2) as in MainSC
                if (dist <= 2) {
                    suggestions.add(new SuggestionOf_Words(vocabWord, dist));
                }
            }

            // Sort suggestions using existing merge sort
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
                System.out.println("No search performed with corrected word.");
            }

        } catch (NullPointerException e) {
            System.out.println("Error: Search engine not initialized.");
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }


    /**
     * View all available phones
     */
    private static void viewAllPhones() {
        try {
            System.out.println("\n========================================");
            System.out.println("   All Available Phones");
            System.out.println("========================================\n");

            List<PhoneData> phones = engine.getAllPhones();

            if (phones == null || phones.isEmpty()) {
                System.out.println("No phones available.");
                return;
            }

            // Sort by name
            phones.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

            for (int i = 0; i < phones.size(); i++) {
                try {
                    PhoneData phone = phones.get(i);
                    System.out.println((i + 1) + ". " + phone.getName());
                    System.out.println("   Price: $" + String.format("%.2f", phone.getPrice()));
                    System.out.println("   RAM: " + phone.getRamGb() + "GB | Storage: " + phone.getStorageGb() + "GB");
                    System.out.println("   Chipset: " + phone.getChipset());
                    System.out.println();
                } catch (Exception e) {
                    System.out.println("Warning: Could not display phone at index " + i);
                    continue;
                }
            }

        } catch (Exception e) {
            System.out.println("Browse error: " + e.getMessage());
        }
    }


    // Input validation helper methods

    /**
     * Get positive double input with DATA VALIDATION & PATTERN FINDING
     * Accepts: $500, 500, 500.99, $500.99
     * Pattern Finding: Extracts number from any format
     * Data Validation: Uses regex to validate format
     */
    private static double getPositiveDoubleInput(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("❌ Input cannot be empty.");
                continue;
            }

            // ===== DATA VALIDATION: Validate format using regex =====
            if (!validator.validateBudget(input)) {
                System.out.println("   Expected format: $500, 500, or 500.99");
                continue;
            }

            // ===== PATTERN FINDING: Extract number from input =====
            // Remove $, commas, and extract the number
            String extracted = input.replace("$", "").replace(",", "").trim();

            try {
                double value = Double.parseDouble(extracted);
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("❌ Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ " + fieldName + " must be a valid number.");
            }
        }
    }

    /**
     * Get positive integer input with DATA VALIDATION & PATTERN FINDING
     * Accepts: 8GB, 8, 12GB, 12, 4000mAh, 4000, etc
     * Pattern Finding: Extracts number, ignores "GB"/"mAh" etc
     */
    private static int getPositiveIntInput(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("❌ Input cannot be empty.");
                continue;
            }

            // ===== PATTERN FINDING: Extract number from input =====
            // Remove non-numeric characters (keeps only numbers)
            String extracted = input.replaceAll("[^0-9]", "").trim();

            if (extracted.isEmpty()) {
                System.out.println("❌ Please enter a valid number.");
                continue;
            }

            try {
                int value = Integer.parseInt(extracted);
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("❌ Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ " + fieldName + " must be a valid whole number.");
            }
        }
    }

    /**
     * Get yes/no input from user
     */
    private static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }


    /**
     * Validate RAM input - accept only available options
     */
    private static int getValidatedRAMInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int ram = Integer.parseInt(input.replaceAll("[^0-9]", ""));

                // Only accept available RAM values
                if (ram == 6 || ram == 8 || ram == 12 || ram == 16) {
                    return ram;
                } else {
                    System.out.println("❌ Please select from available options: 6GB, 8GB, 12GB, 16GB");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid RAM option.");
            }
        }
    }

    /**
     * Validate Storage input - accept only available options
     */
    private static int getValidatedStorageInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int storage = Integer.parseInt(input.replaceAll("[^0-9]", ""));

                // Only accept available storage values
                if (storage == 128 || storage == 256) {
                    return storage;
                } else {
                    System.out.println("❌ Please select from available options: 128GB, 256GB");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid storage option.");
            }
        }
    }

    /**
     * Validate Display Size input - accept only available options
     */
    private static double getValidatedDisplayInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double display = Double.parseDouble(input.replaceAll("[^0-9.]", ""));

                // Only accept available display values
                if (display == 154.9 || display == 156.4 || display == 157.5 || display == 160.0 ||
                        display == 169.1 || display == 170.2 || display == 172.7 || display == 174.1 || display == 203.1) {
                    return display;
                } else {
                    System.out.println("❌ Please select from available options: 154.9\", 156.4\", 157.5\", 160.0\", 169.1\", 170.2\", 172.7\", 174.1\", 203.1\"");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid display size option.");
            }
        }
    }

    /**
     * Validate Battery input - accept only available options
     */
    private static int getValidatedBatteryInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int battery = Integer.parseInt(input.replaceAll("[^0-9]", ""));

                // Only accept available battery values
                if (battery == 3900 || battery == 4000 || battery == 4300 || battery == 4400 ||
                        battery == 4575 || battery == 4700 || battery == 4870 || battery == 5000 ||
                        battery == 5003 || battery == 5050 || battery == 5100 || battery == 5200) {
                    return battery;
                } else {
                    System.out.println("❌ Please select from available options: 3900, 4000, 4300, 4400, 4575, 4700, 4870, 5000, 5003, 5050, 5100, 5200 mAh");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid battery option.");
            }
        }
    }

    /**
     * Validate Camera input - accept only available options
     */
    private static int getValidatedCameraInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int camera = Integer.parseInt(input.replaceAll("[^0-9]", ""));

                // Only accept available camera values
                if (camera == 2 || camera == 48 || camera == 50 || camera == 64 || camera == 200) {
                    return camera;
                } else {
                    System.out.println("❌ Please select from available options: 2MP, 48MP, 50MP, 64MP, 200MP");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid camera option.");
            }
        }
    }

}
