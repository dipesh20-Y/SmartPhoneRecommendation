package features;


import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DataValidation {

    //regex patterns

    //price pattern -accepts format like $400, 400, $400.99
    private static final Pattern BUDGET_PATTERN =
            Pattern.compile("^\\$?\\d+(\\.\\d{1,2})?$");

    //RAM pattern -accepts format lik 8gb, 8
    private static final Pattern RAM_PATTERN =
            Pattern.compile("^\\d+(GB)?$", Pattern.CASE_INSENSITIVE);

    //Storage pattern - accepts format like 128gb, 128
    private static final Pattern STORAGE_PATTERN =
            Pattern.compile("^\\d+(GB)?$", Pattern.CASE_INSENSITIVE);

    // Display size pattern — accepts formats like: 6.3, 6.3 inches, 6
    private static final Pattern DISPLAY_SIZE_PATTERN =
            Pattern.compile("^\\d+(\\.\\d+)?(\\s*inches)?$", Pattern.CASE_INSENSITIVE);

    // Battery pattern — accepts formats like: 4000, 4000mAh, 4000 mAh
    private static final Pattern BATTERY_PATTERN =
            Pattern.compile("^\\d+(\\s*mAh)?$", Pattern.CASE_INSENSITIVE);


    //validation methods for input

    //validates budget input
    public boolean validateBudget(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Budget cannot be empty.");
            return false;
        }
        input = input.trim();
        Matcher matcher = BUDGET_PATTERN.matcher(input);
        if (matcher.matches()) {
            System.out.println("Valid budget format: " + input);
            return true;
        } else {
            System.out.println("Invalid budget format: \"" + input + "\"");
            System.out.println("   Expected formats: $400, 400, $400.99, 400.99");
            return false;
        }
    }

    //validates RAM input
    public boolean validateRAM(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("❌ RAM cannot be empty.");
            return false;
        }
        input = input.trim();
        Matcher matcher = RAM_PATTERN.matcher(input);
        if (matcher.matches()) {
            System.out.println("✅ Valid RAM format: " + input);
            return true;
        } else {
            System.out.println("❌ Invalid RAM format: \"" + input + "\"");
            System.out.println("   Expected formats: 8GB, 8, 12GB, 12");
            return false;
        }
    }

    //validates storage input
    public boolean validateStorage(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("❌ Storage cannot be empty.");
            return false;
        }
        input = input.trim();
        Matcher matcher = STORAGE_PATTERN.matcher(input);
        if (matcher.matches()) {
            System.out.println("✅ Valid storage format: " + input);
            return true;
        } else {
            System.out.println("❌ Invalid storage format: \"" + input + "\"");
            System.out.println("   Expected formats: 128GB, 128, 256GB, 256");
            return false;
        }
    }

    //validates display size input
    public boolean validateDisplaySize(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("❌ Display size cannot be empty.");
            return false;
        }
        input = input.trim();
        Matcher matcher = DISPLAY_SIZE_PATTERN.matcher(input);
        if (matcher.matches()) {
            System.out.println("✅ Valid display size format: " + input);
            return true;
        } else {
            System.out.println("❌ Invalid display size format: \"" + input + "\"");
            System.out.println("   Expected formats: 6.3, 6.3 inches, 6");
            return false;
        }
    }

    //validates battery input
    public boolean validateBattery(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("❌ Battery cannot be empty.");
            return false;
        }
        input = input.trim();
        Matcher matcher = BATTERY_PATTERN.matcher(input);
        if (matcher.matches()) {
            System.out.println("✅ Valid battery format: " + input);
            return true;
        } else {
            System.out.println("❌ Invalid battery format: \"" + input + "\"");
            System.out.println("   Expected formats: 4000, 4000mAh, 4000 mAh");
            return false;
        }
    }


    public static void main(String[] args) {
        DataValidation validator = new DataValidation();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n========================================");
        System.out.println("   Smartphone Recommendation System");
        System.out.println("        User Input Validation");
        System.out.println("========================================");

        // Validate Budget
        System.out.print("\nEnter your budget (e.g. $400, 400.99): ");
        String budget = scanner.nextLine().trim();
        validator.validateBudget(budget);

        // Validate RAM
        System.out.print("\nEnter minimum RAM (e.g. 8GB, 8): ");
        String ram = scanner.nextLine().trim();
        validator.validateRAM(ram);

        // Validate Storage
        System.out.print("\nEnter minimum storage (e.g. 128GB, 128): ");
        String storage = scanner.nextLine().trim();
        validator.validateStorage(storage);

        // Validate Display Size
        System.out.print("\nEnter minimum display size (e.g. 6.3, 6.3 inches): ");
        String displaySize = scanner.nextLine().trim();
        validator.validateDisplaySize(displaySize);

        // Validate Battery
        System.out.print("\nEnter minimum battery (e.g. 4000, 4000mAh): ");
        String battery = scanner.nextLine().trim();
        validator.validateBattery(battery);

        System.out.println("\n========================================");
        System.out.println("        Validation Complete!");
        System.out.println("========================================");

        scanner.close();
    }
}
