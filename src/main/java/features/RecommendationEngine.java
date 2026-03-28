package features;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RecommendationEngine is the core system for recommending smartphones
 * based on user preferences. It loads phone data from CSV, applies scoring
 * algorithms, and returns ranked recommendations.
 */
public class RecommendationEngine {

    private List<PhoneData> allPhones;
    private Trie invertedIndex;

    // Scoring weights (can be adjusted)
    private static final double BUDGET_WEIGHT = 0.20;
    private static final double RAM_WEIGHT = 0.15;
    private static final double STORAGE_WEIGHT = 0.10;
    private static final double CAMERA_WEIGHT = 0.20;
    private static final double BATTERY_WEIGHT = 0.15;
    private static final double DISPLAY_WEIGHT = 0.10;
    private static final double FEATURE_WEIGHT = 0.10;

    // Regex patterns for parsing specifications
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$(\\d+(?:\\.\\d{2})?)");
    private static final Pattern CAMERA_MP_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern BATTERY_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern DISPLAY_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    private static final Pattern RAM_PATTERN = Pattern.compile("(\\d+)GB\\s*RAM");
    private static final Pattern STORAGE_PATTERN = Pattern.compile("(\\d+)GB");

    public RecommendationEngine() {
        this.allPhones = new ArrayList<>();
        this.invertedIndex = new Trie();
    }

    /**
     * Load phones from CSV file and populate the data structures
     */
    public boolean loadPhonesCsv(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines(true))) {

            int phoneCount = 0;
            for (CSVRecord record : parser) {
                try {
                    PhoneData phone = new PhoneData();

                    // Parse basic info
                    phone.setName(record.get("Brand") + " " + record.get("Model"));
                    phone.setOs(record.get("OS"));
                    phone.setChipset(record.get("Processor"));
                    phone.setCpu(record.get("Processor"));
                    phone.setDisplayType(record.get("DisplaySize"));
                    phone.setDisplaySize(Double.parseDouble(record.get("DisplaySize")));
                    phone.setResolution("");
                    phone.setMainCamera(record.get("CameraMP"));
                    phone.setCameraFeatures("");
                    phone.setCameraVideo("");
                    phone.setSelfieCamera(record.get("FrontCameraMP"));
                    phone.setHeadphoneJack(parseYesNo(record.get("HeadphoneJack")));
                    phone.setLoudspeaker(true);
                    phone.setWlan("");
                    phone.setBluetooth("");
                    phone.setSensors("");
                    phone.setBatteryMah(Integer.parseInt(record.get("Battery")));
                    phone.setCharging("");
                    phone.setPrice(Double.parseDouble(record.get("Price")));
                    phone.setDimensions("");
                    phone.setWeight(0);
                    phone.setWaterResistance(record.get("WaterResistance"));
                    phone.setUrl(record.get("URL"));
                    phone.setRamStorage(record.get("RAM"));

                    // Parse RAM and Storage directly from CSV columns
                    try {
                        int ram = Integer.parseInt(record.get("RAM"));
                        phone.setRamGb(ram);
                    } catch (Exception e) {
                        phone.setRamGb(0);
                    }

                    try {
                        int storage = Integer.parseInt(record.get("Storage"));
                        phone.setStorageGb(storage);
                    } catch (Exception e) {
                        phone.setStorageGb(0);
                    }

                    phone.setNetwork("");

                    allPhones.add(phone);

                    // Add to inverted index for keyword searching
                    indexPhoneInTrie(phone);
                    phoneCount++;

                } catch (Exception e) {
                    System.out.println("Warning: Skipping phone record - " + e.getMessage());
                    continue;
                }
            }

            System.out.println("Successfully loaded " + phoneCount + " phones from CSV.");
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + csvFilePath);
            return false;
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error loading CSV: " + e.getMessage());
            return false;
        }
    }


    /**
     * Index all phone specifications in the Trie for keyword search
     */
    private void indexPhoneInTrie(PhoneData phone) {
        indexString(phone.getName(), phone.getName());
        indexString(phone.getChipset(), phone.getName());
        indexString(phone.getDisplayType(), phone.getName());
        indexString(phone.getWlan(), phone.getName());
        indexString(phone.getBluetooth(), phone.getName());
        indexString(phone.getOs(), phone.getName());
    }

    /**
     * Helper to index strings into Trie
     */
    private void indexString(String text, String phoneName) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-z0-9]", "");
            if (word.length() > 0) {
                invertedIndex.insert(word, phoneName);
            }
        }
    }

    /**
     * Get recommendations based on user preferences
     */
    public List<Recommendation> getRecommendations(UserPreferences preferences) {
        try {
            if (preferences == null) {
                System.out.println("Error: Invalid preferences.");
                return new ArrayList<>();
            }

            List<Recommendation> recommendations = new ArrayList<>();

            // First pass: normal scoring based on preferences
            for (PhoneData phone : allPhones) {
                try {
                    if (!meetsMinimumRequirements(phone, preferences)) {
                        continue;
                    }

                    Recommendation rec = new Recommendation(phone);

                    rec.setBudgetScore(calculateBudgetScore(phone, preferences));
                    rec.setRamScore(calculateRamScore(phone, preferences));
                    rec.setStorageScore(calculateStorageScore(phone, preferences));
                    rec.setCameraScore(calculateCameraScore(phone, preferences));
                    rec.setBatteryScore(calculateBatteryScore(phone, preferences));
                    rec.setDisplayScore(calculateDisplayScore(phone, preferences));
                    rec.setFeatureScore(calculateFeatureScore(phone, preferences));

                    double overallScore =
                            rec.getBudgetScore() * BUDGET_WEIGHT +
                                    rec.getRamScore() * RAM_WEIGHT +
                                    rec.getStorageScore() * STORAGE_WEIGHT +
                                    rec.getCameraScore() * CAMERA_WEIGHT +
                                    rec.getBatteryScore() * BATTERY_WEIGHT +
                                    rec.getDisplayScore() * DISPLAY_WEIGHT +
                                    rec.getFeatureScore() * FEATURE_WEIGHT;

                    rec.setOverallScore(overallScore);
                    recommendations.add(rec);

                } catch (Exception e) {
                    System.out.println("Warning: Could not score " + phone.getName() + " - " + e.getMessage());
                    continue;
                }
            }

            // If no phones matched all soft criteria, fall back to simpler logic
            if (recommendations.isEmpty()) {
                List<Recommendation> fallback = new ArrayList<>();

                for (PhoneData phone : allPhones) {
                    // Only enforce very basic constraints in fallback
                    if (phone.getPrice() > preferences.getMaxBudget()) {
                        continue;
                    }
                    if (preferences.isNeedsHeadphoneJack() && !phone.hasHeadphoneJack()) {
                        continue;
                    }
                    if (preferences.isNeedsWaterResistance() &&
                            (phone.getWaterResistance() == null ||
                                    phone.getWaterResistance().equalsIgnoreCase("No"))) {
                        continue;
                    }

                    Recommendation rec = new Recommendation(phone);

                    // Simple score: count how many of the preferred specs this phone meets
                    int matches = 0;
                    int total = 0;

                    if (preferences.getMinRamGb() > 0) {
                        total++;
                        if (phone.getRamGb() >= preferences.getMinRamGb()) {
                            matches++;
                        }
                    }
                    if (preferences.getMinStorageGb() > 0) {
                        total++;
                        if (phone.getStorageGb() >= preferences.getMinStorageGb()) {
                            matches++;
                        }
                    }
                    if (preferences.getMinDisplaySize() > 0) {
                        total++;
                        if (phone.getDisplaySize() >= preferences.getMinDisplaySize()) {
                            matches++;
                        }
                    }
                    if (preferences.getMinBatteryMah() > 0) {
                        total++;
                        if (phone.getBatteryMah() >= preferences.getMinBatteryMah()) {
                            matches++;
                        }
                    }
                    if (preferences.getMinMainCameraMp() > 0) {
                        total++;
                        if (parseFirstMegapixel(phone.getMainCamera()) >= preferences.getMinMainCameraMp()) {
                            matches++;
                        }
                    }

                    double simpleScore = (total == 0) ? 50.0 : (100.0 * matches / total);
                    rec.setOverallScore(simpleScore);
                    fallback.add(rec);
                }

                // Sort fallback by simple score (and then by price ascending as tie-breaker)
                fallback.sort((r1, r2) -> {
                    int cmp = Double.compare(r2.getOverallScore(), r1.getOverallScore());
                    if (cmp != 0) return cmp;
                    return Double.compare(r1.getPhone().getPrice(), r2.getPhone().getPrice());
                });

                return fallback;
            }

            // Sort by overall score descending
            recommendations.sort((r1, r2) -> Double.compare(r2.getOverallScore(), r1.getOverallScore()));

            return recommendations;

        } catch (NullPointerException e) {
            System.out.println("Error: Invalid data. Please try again.");
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Recommendation error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Check if phone meets minimum requirements.
     *
     * IMPORTANT: Only budget and must-have boolean features are treated as
     * hard filters. Numeric "minimum" values are used mainly for scoring.
     */
    private boolean meetsMinimumRequirements(PhoneData phone, UserPreferences prefs) {
        // Basic sanity checks on loaded data
        if (phone.getRamGb() <= 0 || phone.getStorageGb() <= 0 || phone.getBatteryMah() <= 0) {
            return false;
        }

        // Budget check - user sets maximum price they can pay
        if (phone.getPrice() > prefs.getMaxBudget()) {
            return false;
        }

        // Must-have feature: headphone jack
        if (prefs.isNeedsHeadphoneJack() && !phone.hasHeadphoneJack()) {
            return false;
        }

        // Must-have feature: water resistance
        if (prefs.isNeedsWaterResistance() &&
                (phone.getWaterResistance() == null ||
                        phone.getWaterResistance().equalsIgnoreCase("No"))) {
            return false;
        }

        // High refresh rate is treated as a soft preference in scoring only,
        // so we do NOT filter phones here based on it.

        return true;
    }

    /**
     * Calculate budget score (0-100)
     * Phones within budget range get high scores, penalty for far exceed
     */
    private double calculateBudgetScore(PhoneData phone, UserPreferences prefs) {
        double price = phone.getPrice();
        double maxBudget = prefs.getMaxBudget();

        if (price <= maxBudget) {
            // Closer to max budget = better value
            double score = 100 * (price / maxBudget);
            return Math.min(100, score);
        }

        return 0; // Outside budget range
    }

    /**
     * Calculate RAM score (0-100).
     * Below the user's desired RAM still gets a low score instead of 0,
     * so those phones can appear but will be ranked lower.
     */
    private double calculateRamScore(PhoneData phone, UserPreferences prefs) {
        int ram = phone.getRamGb();
        int minRam = prefs.getMinRamGb();

        if (minRam <= 0) {
            return 50.0; // neutral score if user did not specify
        }

        if (ram >= minRam) {
            double score = 100 * (ram / (double) (minRam * 2));
            return Math.min(100, score);
        } else {
            // Below preference: give up to 50 points based on how close it is
            double ratio = ram / (double) minRam; // 0.0–1.0
            return Math.max(0, 50 * ratio);
        }
    }

    /**
     * Calculate storage score (0-100) with soft behavior below preference.
     */
    private double calculateStorageScore(PhoneData phone, UserPreferences prefs) {
        int storage = phone.getStorageGb();
        int minStorage = prefs.getMinStorageGb();

        if (minStorage <= 0) {
            return 50.0;
        }

        if (storage >= minStorage) {
            double score = 100 * (storage / (double) (minStorage * 2));
            return Math.min(100, score);
        } else {
            double ratio = storage / (double) minStorage;
            return Math.max(0, 50 * ratio);
        }
    }

    /**
     * Calculate camera score (0-100) with soft behavior below preference.
     */
    private double calculateCameraScore(PhoneData phone, UserPreferences prefs) {
        int mainCameraMp = parseFirstMegapixel(phone.getMainCamera());
        int minMainMp = prefs.getMinMainCameraMp();

        if (minMainMp <= 0) {
            return 50.0;
        }

        if (mainCameraMp >= minMainMp) {
            double score = 100 * (mainCameraMp / (double) (minMainMp * 2));
            return Math.min(100, score);
        } else {
            double ratio = mainCameraMp / (double) minMainMp;
            return Math.max(0, 50 * ratio);
        }
    }

    /**
     * Calculate battery score (0-100) with soft behavior below preference.
     */
    private double calculateBatteryScore(PhoneData phone, UserPreferences prefs) {
        int battery = phone.getBatteryMah();
        int minBattery = prefs.getMinBatteryMah();

        if (minBattery <= 0) {
            return 50.0;
        }

        if (battery >= minBattery) {
            double score = 100 * (battery / (double) (minBattery * 1.5));
            return Math.min(100, score);
        } else {
            double ratio = battery / (double) minBattery;
            return Math.max(0, 50 * ratio);
        }
    }

    /**
     * Calculate display score (0-100) with soft behavior below preference.
     */
    private double calculateDisplayScore(PhoneData phone, UserPreferences prefs) {
        double displaySize = phone.getDisplaySize();
        double minDisplay = prefs.getMinDisplaySize();

        if (minDisplay <= 0) {
            minDisplay = 5.0; // default reasonable minimum
        }

        if (displaySize < 0.1) {
            return 0; // invalid data
        }

        if (displaySize >= minDisplay) {
            double optimalSize = 6.5;
            double difference = Math.abs(displaySize - optimalSize);
            double score = 100 - (difference * 5);
            return Math.max(0, Math.min(100, score));
        } else {
            // Below preference: scale up to 50 based on closeness to minDisplay
            double ratio = displaySize / minDisplay;
            return Math.max(0, 50 * ratio);
        }
    }

    /**
     * Calculate feature score (0-100)
     * Based on matching optional features (headphone jack, water resistance, etc.)
     */
    private double calculateFeatureScore(PhoneData phone, UserPreferences prefs) {
        double baseScore = 50; // Base score if no specific features required
        double bonus = 0;

        if (prefs.isNeedsHeadphoneJack() && phone.hasHeadphoneJack()) {
            bonus += 25;
        }

        if (prefs.isNeedsWaterResistance() && phone.getWaterResistance() != null &&
            !phone.getWaterResistance().equalsIgnoreCase("No")) {
            bonus += 25;
        }

        if (prefs.isNeedsHighRefreshRate() && phone.getDisplayType() != null &&
            (phone.getDisplayType().contains("120Hz") || phone.getDisplayType().contains("144Hz"))) {
            bonus += 25;
        }

        return Math.min(100, baseScore + bonus);
    }

    /**
     * Search phones by keyword using the inverted index.
     * Returns true if at least one phone is found, otherwise false.
     */
    public boolean searchByKeyword(String keyword) {
        try {
            // Validate input
            if (keyword == null || keyword.isEmpty()) {
                System.out.println("Please enter a keyword.");
                return false;
            }

            // Clean the keyword
            String cleanKeyword = keyword.toLowerCase().replaceAll("[^a-z0-9]", "");

            if (cleanKeyword.isEmpty()) {
                System.out.println("Please enter a valid keyword.");
                return false;
            }

            // Search using inverted index
            HashSet<String> results = invertedIndex.search(cleanKeyword);

            System.out.println("\n=== Search Results for: \"" + keyword + "\" ===");

            if (results == null || results.isEmpty()) {
                System.out.println("No phones found matching this keyword.");
                return false;
            } else {
                System.out.println("Found in " + results.size() + " phone(s):");
                for (String phoneName : results) {
                    System.out.println("  - " + phoneName);
                }
                return true;
            }

        } catch (NullPointerException e) {
            System.out.println("Error: Invalid search operation. Please try again.");
            return false;
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get phone by name
     */
    public PhoneData getPhoneByName(String name) {
        for (PhoneData phone : allPhones) {
            if (phone.getName().equalsIgnoreCase(name)) {
                return phone;
            }
        }
        return null;
    }

    /**
     * Get all phones
     */
    public List<PhoneData> getAllPhones() {
        return allPhones;
    }

    // Parsing helper methods

    private double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return 0;

        Matcher matcher = PRICE_PATTERN.matcher(priceStr);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private double parseDisplaySize(String displayStr) {
        if (displayStr == null || displayStr.isEmpty()) return 0;

        Matcher matcher = DISPLAY_PATTERN.matcher(displayStr);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private int parseBattery(String batteryStr) {
        if (batteryStr == null || batteryStr.isEmpty()) return 0;

        Matcher matcher = BATTERY_PATTERN.matcher(batteryStr);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private double parseWeight(String weightStr) {
        if (weightStr == null || weightStr.isEmpty()) return 0;

        try {
            String[] parts = weightStr.split("\\s+");
            return Double.parseDouble(parts[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseFirstMegapixel(String cameraStr) {
        if (cameraStr == null || cameraStr.isEmpty()) return 0;

        Matcher matcher = CAMERA_MP_PATTERN.matcher(cameraStr);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private boolean parseYesNo(String value) {
        return value != null && (value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("true"));
    }
}

