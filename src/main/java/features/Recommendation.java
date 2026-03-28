package features;

/**
 * Recommendation represents a phone that matches user preferences.
 * It includes the phone data and various scoring metrics.
 */
public class Recommendation {

    private PhoneData phone;
    private double overallScore;
    private double budgetScore;
    private double ramScore;
    private double storageScore;
    private double cameraScore;
    private double batteryScore;
    private double displayScore;
    private double featureScore;
    private int matchedFeatures;
    private int totalRequiredFeatures;

    // Constructor
    public Recommendation(PhoneData phone) {
        this.phone = phone;
        this.overallScore = 0.0;
        this.budgetScore = 0.0;
        this.ramScore = 0.0;
        this.storageScore = 0.0;
        this.cameraScore = 0.0;
        this.batteryScore = 0.0;
        this.displayScore = 0.0;
        this.featureScore = 0.0;
        this.matchedFeatures = 0;
        this.totalRequiredFeatures = 0;
    }

    // Getters and Setters
    public PhoneData getPhone() {
        return phone;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getBudgetScore() {
        return budgetScore;
    }

    public void setBudgetScore(double budgetScore) {
        this.budgetScore = budgetScore;
    }

    public double getRamScore() {
        return ramScore;
    }

    public void setRamScore(double ramScore) {
        this.ramScore = ramScore;
    }

    public double getStorageScore() {
        return storageScore;
    }

    public void setStorageScore(double storageScore) {
        this.storageScore = storageScore;
    }

    public double getCameraScore() {
        return cameraScore;
    }

    public void setCameraScore(double cameraScore) {
        this.cameraScore = cameraScore;
    }

    public double getBatteryScore() {
        return batteryScore;
    }

    public void setBatteryScore(double batteryScore) {
        this.batteryScore = batteryScore;
    }

    public double getDisplayScore() {
        return displayScore;
    }

    public void setDisplayScore(double displayScore) {
        this.displayScore = displayScore;
    }

    public double getFeatureScore() {
        return featureScore;
    }

    public void setFeatureScore(double featureScore) {
        this.featureScore = featureScore;
    }

    public int getMatchedFeatures() {
        return matchedFeatures;
    }

    public void setMatchedFeatures(int matchedFeatures) {
        this.matchedFeatures = matchedFeatures;
    }

    public int getTotalRequiredFeatures() {
        return totalRequiredFeatures;
    }

    public void setTotalRequiredFeatures(int totalRequiredFeatures) {
        this.totalRequiredFeatures = totalRequiredFeatures;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "phone=" + phone.getName() +
                ", overallScore=" + String.format("%.2f", overallScore) +
                ", price=$" + String.format("%.2f", phone.getPrice()) +
                '}';
    }
}

