package features;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RecommendationEngine manages phone data loading, keyword search with ranking,
 * and personalized recommendations based on user preferences.
 */
public class RecommendationEngine {

    private final List<PhoneData> allPhones = new ArrayList<>();
    private final Trie invertedIndex = new Trie();

    private static final double BUDGET_WEIGHT = 0.20;
    private static final double RAM_WEIGHT = 0.15;
    private static final double STORAGE_WEIGHT = 0.10;
    private static final double CAMERA_WEIGHT = 0.20;
    private static final double BATTERY_WEIGHT = 0.15;
    private static final double DISPLAY_WEIGHT = 0.10;
    private static final double FEATURE_WEIGHT = 0.10;

    private static final Pattern CAMERA_MP_PATTERN = Pattern.compile("(\\d+)");

    public RecommendationEngine() {}

    public boolean loadPhonesCsv(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines(true))) {

            int phoneCount = 0;

            for (CSVRecord record : parser) {
                try {
                    PhoneData phone = new PhoneData();

                    phone.setName(record.get("Brand") + " " + record.get("Model"));
                    phone.setOs(record.get("OS"));
                    phone.setChipset(record.get("Processor"));
                    phone.setCpu(record.get("Processor"));
                    phone.setDisplayType(record.get("DisplaySize"));
                    phone.setDisplaySize(Double.parseDouble(record.get("DisplaySize")));
                    phone.setMainCamera(record.get("CameraMP"));
                    phone.setSelfieCamera(record.get("FrontCameraMP"));
                    phone.setHeadphoneJack(parseYesNo(record.get("HeadphoneJack")));
                    phone.setBatteryMah(Integer.parseInt(record.get("Battery")));
                    phone.setPrice(Double.parseDouble(record.get("Price")));
                    phone.setWaterResistance(record.get("WaterResistance"));
                    phone.setUrl(record.get("URL"));
                    phone.setRamStorage(record.get("RAM"));

                    try { phone.setRamGb(Integer.parseInt(record.get("RAM"))); } catch (Exception e) { phone.setRamGb(0); }
                    try { phone.setStorageGb(Integer.parseInt(record.get("Storage"))); } catch (Exception e) { phone.setStorageGb(0); }

                    phone.setSpecSummary(record.get("SpecsSummary") != null ? record.get("SpecsSummary").trim() : "");
                    phone.setDescription(record.get("Description") != null ? record.get("Description").trim() : "");

                    allPhones.add(phone);
                    indexPhoneInTrie(phone);
                    phoneCount++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            return false;
        }
    }

    private void indexPhoneInTrie(PhoneData phone) {
        indexString(phone.getName(), phone.getName());
        indexString(phone.getSpecSummary(), phone.getName());
        indexString(phone.getDescription(), phone.getName());
    }

    private void indexString(String text, String phoneName) {
        if (text == null || text.isEmpty()) return;
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-z0-9]", "");
            if (word.length() > 1) {
                invertedIndex.insert(word, phoneName);
            }
        }
    }

    private String getPhoneFullText(PhoneData phone) {
        return (phone.getDescription() + " " + phone.getSpecSummary()).toLowerCase();
    }

    public boolean searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Please enter a keyword.");
            return false;
        }

        String cleanKeyword = keyword.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (cleanKeyword.isEmpty()) {
            System.out.println("Please enter a valid keyword.");
            return false;
        }

        HashSet<String> results = invertedIndex.search(cleanKeyword);

        System.out.println("\n=== Search Results for: \"" + keyword + "\" ===");

        if (results.isEmpty()) {
            System.out.println("No phones found matching this keyword.");
            return false;
        }

        List<RankedResult> rankedResults = new ArrayList<>();

        for (String phoneName : results) {
            PhoneData phone = getPhoneByName(phoneName);
            if (phone != null) {
                String fullText = getPhoneFullText(phone);

                int frequency = FrequencyCount.countOccurrences(fullText, keyword);
                int specFrequency = FrequencyCount.countOccurrences(phone.getSpecSummary().toLowerCase(), keyword);
                int finalScore = frequency * 10 + specFrequency * 5;

                rankedResults.add(new RankedResult(phone, finalScore, frequency));
            }
        }

        rankedResults.sort((a, b) -> Integer.compare(b.finalScore, a.finalScore));

        System.out.println("Found in " + rankedResults.size() + " phone(s). Ranked by word frequency:\n");

        int count = 1;
        for (RankedResult rr : rankedResults) {
            System.out.printf("%2d. %-30s | Frequency: %d%n",
                    count++,
                    rr.phone.getName(),
                    rr.rawFrequency);

            if (rr.phone.getUrl() != null && !rr.phone.getUrl().isEmpty()) {
                System.out.println("    URL: " + rr.phone.getUrl());
            } else {
                System.out.println("    No URL available");
            }
            System.out.println();
        }

        return true;
    }

    public PhoneData getPhoneByName(String name) {
        for (PhoneData phone : allPhones) {
            if (phone.getName().equalsIgnoreCase(name)) {
                return phone;
            }
        }
        return null;
    }

    public List<PhoneData> getAllPhones() {
        return allPhones;
    }

    public List<Recommendation> getRecommendations(UserPreferences preferences) {
        try {
            if (preferences == null) return new ArrayList<>();

            List<Recommendation> recommendations = new ArrayList<>();

            for (PhoneData phone : allPhones) {
                if (!meetsMinimumRequirements(phone, preferences)) continue;

                Recommendation rec = new Recommendation(phone);

                rec.setBudgetScore(calculateBudgetScore(phone, preferences));
                rec.setRamScore(calculateRamScore(phone, preferences));
                rec.setStorageScore(calculateStorageScore(phone, preferences));
                rec.setCameraScore(calculateCameraScore(phone, preferences));
                rec.setBatteryScore(calculateBatteryScore(phone, preferences));
                rec.setDisplayScore(calculateDisplayScore(phone, preferences));
                rec.setFeatureScore(calculateFeatureScore(phone, preferences));

                double overallScore = rec.getBudgetScore() * BUDGET_WEIGHT +
                        rec.getRamScore() * RAM_WEIGHT +
                        rec.getStorageScore() * STORAGE_WEIGHT +
                        rec.getCameraScore() * CAMERA_WEIGHT +
                        rec.getBatteryScore() * BATTERY_WEIGHT +
                        rec.getDisplayScore() * DISPLAY_WEIGHT +
                        rec.getFeatureScore() * FEATURE_WEIGHT;

                rec.setOverallScore(overallScore);
                recommendations.add(rec);
            }

            if (recommendations.isEmpty()) {
                List<Recommendation> fallback = new ArrayList<>();
                for (PhoneData phone : allPhones) {
                    if (phone.getPrice() > preferences.getMaxBudget()) continue;
                    if (preferences.isNeedsHeadphoneJack() && !phone.hasHeadphoneJack()) continue;
                    if (preferences.isNeedsWaterResistance() &&
                            (phone.getWaterResistance() == null || phone.getWaterResistance().equalsIgnoreCase("No"))) continue;

                    Recommendation rec = new Recommendation(phone);
                    int matches = 0, total = 0;

                    if (preferences.getMinRamGb() > 0) { total++; if (phone.getRamGb() >= preferences.getMinRamGb()) matches++; }
                    if (preferences.getMinStorageGb() > 0) { total++; if (phone.getStorageGb() >= preferences.getMinStorageGb()) matches++; }
                    if (preferences.getMinDisplaySize() > 0) { total++; if (phone.getDisplaySize() >= preferences.getMinDisplaySize()) matches++; }
                    if (preferences.getMinBatteryMah() > 0) { total++; if (phone.getBatteryMah() >= preferences.getMinBatteryMah()) matches++; }
                    if (preferences.getMinMainCameraMp() > 0) { total++; if (parseFirstMegapixel(phone.getMainCamera()) >= preferences.getMinMainCameraMp()) matches++; }

                    double simpleScore = (total == 0) ? 50.0 : (100.0 * matches / total);
                    rec.setOverallScore(simpleScore);
                    fallback.add(rec);
                }

                fallback.sort((r1, r2) -> {
                    int cmp = Double.compare(r2.getOverallScore(), r1.getOverallScore());
                    return (cmp != 0) ? cmp : Double.compare(r1.getPhone().getPrice(), r2.getPhone().getPrice());
                });
                return fallback;
            }

            recommendations.sort((r1, r2) -> Double.compare(r2.getOverallScore(), r1.getOverallScore()));
            return recommendations;

        } catch (Exception e) {
            System.out.println("Recommendation error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean meetsMinimumRequirements(PhoneData phone, UserPreferences prefs) {
        if (phone.getRamGb() <= 0 || phone.getStorageGb() <= 0 || phone.getBatteryMah() <= 0) return false;
        if (phone.getPrice() > prefs.getMaxBudget()) return false;
        if (prefs.isNeedsHeadphoneJack() && !phone.hasHeadphoneJack()) return false;
        if (prefs.isNeedsWaterResistance() && (phone.getWaterResistance() == null || phone.getWaterResistance().equalsIgnoreCase("No"))) return false;
        return true;
    }

    private double calculateBudgetScore(PhoneData phone, UserPreferences prefs) {
        double price = phone.getPrice();
        double maxBudget = prefs.getMaxBudget();
        if (price <= maxBudget) {
            double score = 100 * (price / maxBudget);
            return Math.min(100, score);
        }
        return 0;
    }

    private double calculateRamScore(PhoneData phone, UserPreferences prefs) {
        int ram = phone.getRamGb();
        int minRam = prefs.getMinRamGb();
        if (minRam <= 0) return 50.0;
        if (ram >= minRam) return Math.min(100, 100.0 * ram / (minRam * 2));
        return Math.max(0, 50.0 * ram / minRam);
    }

    private double calculateStorageScore(PhoneData phone, UserPreferences prefs) {
        int storage = phone.getStorageGb();
        int minStorage = prefs.getMinStorageGb();
        if (minStorage <= 0) return 50.0;
        if (storage >= minStorage) return Math.min(100, 100.0 * storage / (minStorage * 2));
        return Math.max(0, 50.0 * storage / minStorage);
    }

    private double calculateCameraScore(PhoneData phone, UserPreferences prefs) {
        int mainCameraMp = parseFirstMegapixel(phone.getMainCamera());
        int minMainMp = prefs.getMinMainCameraMp();
        if (minMainMp <= 0) return 50.0;
        if (mainCameraMp >= minMainMp) return Math.min(100, 100.0 * mainCameraMp / (minMainMp * 2));
        return Math.max(0, 50.0 * mainCameraMp / minMainMp);
    }

    private double calculateBatteryScore(PhoneData phone, UserPreferences prefs) {
        int battery = phone.getBatteryMah();
        int minBattery = prefs.getMinBatteryMah();
        if (minBattery <= 0) return 50.0;
        if (battery >= minBattery) return Math.min(100, 100.0 * battery / (minBattery * 1.5));
        return Math.max(0, 50.0 * battery / minBattery);
    }

    private double calculateDisplayScore(PhoneData phone, UserPreferences prefs) {
        double displaySize = phone.getDisplaySize();
        double minDisplay = prefs.getMinDisplaySize();
        if (minDisplay <= 0) minDisplay = 5.0;
        if (displaySize < 0.1) return 0;
        if (displaySize >= minDisplay) {
            double optimal = 6.5;
            double diff = Math.abs(displaySize - optimal);
            return Math.max(0, Math.min(100, 100 - diff * 5));
        }
        return Math.max(0, 50.0 * displaySize / minDisplay);
    }

    private double calculateFeatureScore(PhoneData phone, UserPreferences prefs) {
        double score = 50;
        if (prefs.isNeedsHeadphoneJack() && phone.hasHeadphoneJack()) score += 25;
        if (prefs.isNeedsWaterResistance() && phone.getWaterResistance() != null &&
                !phone.getWaterResistance().equalsIgnoreCase("No")) score += 25;
        if (prefs.isNeedsHighRefreshRate() && phone.getDisplayType() != null &&
                (phone.getDisplayType().contains("120Hz") || phone.getDisplayType().contains("144Hz"))) score += 25;
        return Math.min(100, score);
    }

    private int parseFirstMegapixel(String cameraStr) {
        if (cameraStr == null || cameraStr.isEmpty()) return 0;
        Matcher matcher = CAMERA_MP_PATTERN.matcher(cameraStr);
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); } catch (Exception ignored) {}
        }
        return 0;
    }

    private boolean parseYesNo(String value) {
        return value != null && (value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("True") || value.equalsIgnoreCase("true"));
    }

    private static class RankedResult {
        final PhoneData phone;
        final int finalScore;
        final int rawFrequency;

        RankedResult(PhoneData phone, int finalScore, int rawFrequency) {
            this.phone = phone;
            this.finalScore = finalScore;
            this.rawFrequency = rawFrequency;
        }
    }
}