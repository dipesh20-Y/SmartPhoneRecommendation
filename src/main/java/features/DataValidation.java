package features;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataValidation handles structured user input validation for the smartphone
 * recommendation engine. It enforces strict format rules for budget, RAM,
 * storage, display size, and battery capacity using precompiled regular
 * expressions, ensuring only clean, parseable values reach the search pipeline.
 */
public class DataValidation {

    private static final Pattern BUDGET_PATTERN =
            Pattern.compile("^\\$?\\d+(\\.\\d{1,2})?$");

    private static final Pattern RAM_PATTERN =
            Pattern.compile("^\\d+(GB)?$", Pattern.CASE_INSENSITIVE);

    private static final Pattern STORAGE_PATTERN =
            Pattern.compile("^\\d+(GB)?$", Pattern.CASE_INSENSITIVE);

    private static final Pattern DISPLAY_SIZE_PATTERN =
            Pattern.compile("^\\d+(\\.\\d+)?(\\s*inches)?$", Pattern.CASE_INSENSITIVE);

    private static final Pattern BATTERY_PATTERN =
            Pattern.compile("^\\d+(\\s*mAh)?$", Pattern.CASE_INSENSITIVE);

    /**
     * Validates a user-supplied budget value.
     * Accepts formats such as 400, $400, 400.99, or $400.99.
     *
     * @param userInput the raw budget string entered by the user
     * @return true if the input matches the expected numeric format, false otherwise
     */
    public boolean validateBudget(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("Budget cannot be empty.");
            return false;
        }

        String cleanedInput = userInput.trim();
        Matcher matcher = BUDGET_PATTERN.matcher(cleanedInput);

        if (matcher.matches()) {
//            System.out.println("Valid budget format: " + cleanedInput);
            return true;
        } else {
            System.out.println("Invalid budget format: \"" + cleanedInput + "\"");
            System.out.println("   Expected formats: $400, 400, $400.99, 400.99");
            return false;
        }
    }

    /**
     * Validates a user-supplied RAM value.
     * Accepts formats such as 8, 8GB, 12, or 12GB.
     *
     * @param userInput the raw RAM string entered by the user
     * @return true if the input matches the expected format, false otherwise
     */
    public boolean validateRAM(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("RAM cannot be empty.");
            return false;
        }

        String cleanedInput = userInput.trim();
        Matcher matcher = RAM_PATTERN.matcher(cleanedInput);

        if (matcher.matches()) {
            System.out.println("Valid RAM format: " + cleanedInput);
            return true;
        } else {
            System.out.println("Invalid RAM format: \"" + cleanedInput + "\"");
            System.out.println("   Expected formats: 8GB, 8, 12GB, 12");
            return false;
        }
    }

    /**
     * Validates a user-supplied storage value.
     * Accepts formats such as 128, 128GB, 256, or 256GB.
     *
     * @param userInput the raw storage string entered by the user
     */
    public void validateStorage(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("Storage cannot be empty.");
            return;
        }

        String cleanedInput = userInput.trim();
        Matcher matcher = STORAGE_PATTERN.matcher(cleanedInput);

        if (matcher.matches()) {
            System.out.println("Valid storage format: " + cleanedInput);
        } else {
            System.out.println("Invalid storage format: \"" + cleanedInput + "\"");
            System.out.println("   Expected formats: 128GB, 128, 256GB, 256");
        }
    }

    /**
     * Validates a user-supplied display size value.
     * Accepts formats such as 6.3, 6, or 6.3 inches.
     *
     * @param userInput the raw display size string entered by the user
     */
    public void validateDisplaySize(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("Display size cannot be empty.");
            return;
        }

        String cleanedInput = userInput.trim();
        Matcher matcher = DISPLAY_SIZE_PATTERN.matcher(cleanedInput);

        if (matcher.matches()) {
            System.out.println("Valid display size format: " + cleanedInput);
        } else {
            System.out.println("Invalid display size format: \"" + cleanedInput + "\"");
            System.out.println("   Expected formats: 6.3, 6.3 inches, 6");
        }
    }

    /**
     * Validates a user-supplied battery capacity value.
     * Accepts formats such as 4000, 4000mAh, or 4000 mAh.
     *
     * @param userInput the raw battery string entered by the user
     */
    public void validateBattery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("Battery cannot be empty.");
            return;
        }

        String cleanedInput = userInput.trim();
        Matcher matcher = BATTERY_PATTERN.matcher(cleanedInput);

        if (matcher.matches()) {
            System.out.println("Valid battery format: " + cleanedInput);
        } else {
            System.out.println("Invalid battery format: \"" + cleanedInput + "\"");
            System.out.println("   Expected formats: 4000, 4000mAh, 4000 mAh");
        }
    }

    public static void main(String[] args) {
        DataValidation validator = new DataValidation();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Smartphone Recommendation System");
        System.out.println("User Input Validation");

        // Validate Budget
        System.out.print("\nEnter your budget (e.g. $400, 400.99): ");
        String budgetInput = scanner.nextLine();
        validator.validateBudget(budgetInput);

        // Validate RAM
        System.out.print("\nEnter minimum RAM (e.g. 8GB, 8): ");
        String ramInput = scanner.nextLine();
        validator.validateRAM(ramInput);

        // Validate Storage
        System.out.print("\nEnter minimum storage (e.g. 128GB, 128): ");
        String storageInput = scanner.nextLine();
        validator.validateStorage(storageInput);

        // Validate Display Size
        System.out.print("\nEnter minimum display size (e.g. 6.3, 6.3 inches): ");
        String displayInput = scanner.nextLine();
        validator.validateDisplaySize(displayInput);

        // Validate Battery
        System.out.print("\nEnter minimum battery (e.g. 4000, 4000mAh): ");
        String batteryInput = scanner.nextLine();
        validator.validateBattery(batteryInput);

        System.out.println("\nValidation Completed!");

        scanner.close();
    }
}