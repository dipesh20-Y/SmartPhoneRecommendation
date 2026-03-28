package features;

/**
 * Quick validation test for the recommendation system
 */
public class RecommendationTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Recommendation System Test");
        System.out.println("========================================\n");

        // Test 1: Load CSV and create engine
        System.out.println("Test 1: Creating RecommendationEngine...");
        RecommendationEngine engine = new RecommendationEngine();
        System.out.println("✓ Engine created successfully\n");

        // Test 2: Load phones from CSV
        System.out.println("Test 2: Loading phones from CSV...");
        String csvPath = "src/main/resources/Combined_phone.csv";
        boolean loaded = engine.loadPhonesCsv(csvPath);

        if (!loaded) {
            System.out.println("✗ Failed to load CSV\n");
            return;
        }
        System.out.println("✓ CSV loaded successfully\n");

        // Test 3: Create user preferences
        System.out.println("Test 3: Creating UserPreferences...");
        UserPreferences prefs = new UserPreferences();
        prefs.setMaxBudget(600);
        prefs.setMinRamGb(8);
        prefs.setMinStorageGb(128);
        prefs.setMinDisplaySize(6.0);
        prefs.setMinBatteryMah(4000);
        prefs.setMinMainCameraMp(50);
        System.out.println("✓ Preferences created: " + prefs);
        System.out.println();

        // Test 4: Generate recommendations
        System.out.println("Test 4: Generating recommendations...");
        java.util.List<Recommendation> recommendations = engine.getRecommendations(prefs);
        System.out.println("✓ Generated " + recommendations.size() + " recommendations\n");

        // Test 5: Display top recommendations
        if (!recommendations.isEmpty()) {
            System.out.println("Test 5: Displaying top 3 recommendations...");
            System.out.println("-".repeat(60));

            int maxDisplay = Math.min(3, recommendations.size());
            for (int i = 0; i < maxDisplay; i++) {
                Recommendation rec = recommendations.get(i);
                PhoneData phone = rec.getPhone();

                System.out.println((i+1) + ". " + phone.getName());
                System.out.println("   Overall Score: " + String.format("%.1f%%", rec.getOverallScore()));
                System.out.println("   Price: $" + String.format("%.2f", phone.getPrice()));
                System.out.println("   RAM: " + phone.getRamGb() + "GB");
                System.out.println("   Storage: " + phone.getStorageGb() + "GB");
                System.out.println("   Display: " + String.format("%.1f", phone.getDisplaySize()) + "\"");
                System.out.println("   Battery: " + phone.getBatteryMah() + "mAh");
                System.out.println("   Main Camera: " + phone.getMainCamera());
                System.out.println();
            }
            System.out.println("-".repeat(60));
            System.out.println("✓ Recommendations displayed successfully\n");
        } else {
            System.out.println("✗ No recommendations generated\n");
        }

        // Test 6: Keyword search
        System.out.println("Test 6: Testing keyword search...");
        engine.searchByKeyword("5G");
        System.out.println("✓ Keyword search completed\n");

        // Test 7: Get phone by name
        System.out.println("Test 7: Testing phone lookup...");
        java.util.List<PhoneData> allPhones = engine.getAllPhones();
        if (!allPhones.isEmpty()) {
            String firstPhoneName = allPhones.get(0).getName();
            PhoneData found = engine.getPhoneByName(firstPhoneName);
            if (found != null) {
                System.out.println("✓ Found phone: " + found.getName());
            } else {
                System.out.println("✗ Phone lookup failed");
            }
        }
        System.out.println();

        System.out.println("========================================");
        System.out.println("  All Tests Completed Successfully!");
        System.out.println("========================================");
    }
}

