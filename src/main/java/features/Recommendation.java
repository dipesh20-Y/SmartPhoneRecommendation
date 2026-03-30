package features;

/**
 * Recommendation represents a scored smartphone suggestion returned by the
 * recommendation engine. It wraps a PhoneData object and stores both the
 * overall weighted score and individual component scores for transparency
 * in the decision-making process.
 */
public class Recommendation {

    private final PhoneData phone;
    private double overallScore;

    private double budgetScore;
    private double ramScore;
    private double storageScore;
    private double cameraScore;
    private double batteryScore;
    private double displayScore;
    private double featureScore;

    /**
     * Constructs a new Recommendation object for the given phone.
     * All individual scores are initialized to 0.0.
     *
     * @param phone the PhoneData object to be wrapped in this recommendation
     */
    public Recommendation(PhoneData phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Error: PhoneData cannot be null in Recommendation.");
        }

        this.phone = phone;
        this.overallScore = 0.0;
        this.budgetScore = 0.0;
        this.ramScore = 0.0;
        this.storageScore = 0.0;
        this.cameraScore = 0.0;
        this.batteryScore = 0.0;
        this.displayScore = 0.0;
        this.featureScore = 0.0;
    }

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

    @Override
    public String toString() {
        return String.format("Recommendation[%s | Score: %.1f%%]",
                phone.getName(), overallScore);
    }
}