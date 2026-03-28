package features;

/**
 * UserPreferences captures the user's requirements for phone recommendations.
 * This includes budget constraints, desired specifications, and feature preferences.
 */
public class UserPreferences {

    // Budget constraints
    private double minBudget;
    private double maxBudget;

    // Preferred specifications
    private int minRamGb;
    private int minStorageGb;
    private double minDisplaySize;
    private int minBatteryMah;
    private int minMainCameraMp;
    private int minSelfiesCameraMp;

    // Feature preferences (must-have features)
    private boolean needsHeadphoneJack;
    private boolean needsWaterResistance;
    private boolean needsHighRefreshRate;

    // Priority weights (1-5 scale)
    private int budgetPriority = 3;      // Default priority
    private int ramPriority = 3;
    private int storagePriority = 3;
    private int cameraPriority = 3;
    private int batteryPriority = 3;
    private int displayPriority = 3;

    // Constructor with defaults
    public UserPreferences() {
        this.minBudget = 0;
        this.maxBudget = 10000;
        this.minRamGb = 4;
        this.minStorageGb = 64;
        this.minDisplaySize = 5.0;
        this.minBatteryMah = 3000;
        this.minMainCameraMp = 12;
        this.minSelfiesCameraMp = 8;
        this.needsHeadphoneJack = false;
        this.needsWaterResistance = false;
        this.needsHighRefreshRate = false;
    }

    // Getters and Setters
    public double getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(double minBudget) {
        this.minBudget = minBudget;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public int getMinRamGb() {
        return minRamGb;
    }

    public void setMinRamGb(int minRamGb) {
        this.minRamGb = minRamGb;
    }

    public int getMinStorageGb() {
        return minStorageGb;
    }

    public void setMinStorageGb(int minStorageGb) {
        this.minStorageGb = minStorageGb;
    }

    public double getMinDisplaySize() {
        return minDisplaySize;
    }

    public void setMinDisplaySize(double minDisplaySize) {
        this.minDisplaySize = minDisplaySize;
    }

    public int getMinBatteryMah() {
        return minBatteryMah;
    }

    public void setMinBatteryMah(int minBatteryMah) {
        this.minBatteryMah = minBatteryMah;
    }

    public int getMinMainCameraMp() {
        return minMainCameraMp;
    }

    public void setMinMainCameraMp(int minMainCameraMp) {
        this.minMainCameraMp = minMainCameraMp;
    }

    public int getMinSelfiesCameraMp() {
        return minSelfiesCameraMp;
    }

    public void setMinSelfiesCameraMp(int minSelfiesCameraMp) {
        this.minSelfiesCameraMp = minSelfiesCameraMp;
    }

    public boolean isNeedsHeadphoneJack() {
        return needsHeadphoneJack;
    }

    public void setNeedsHeadphoneJack(boolean needsHeadphoneJack) {
        this.needsHeadphoneJack = needsHeadphoneJack;
    }

    public boolean isNeedsWaterResistance() {
        return needsWaterResistance;
    }

    public void setNeedsWaterResistance(boolean needsWaterResistance) {
        this.needsWaterResistance = needsWaterResistance;
    }

    public boolean isNeedsHighRefreshRate() {
        return needsHighRefreshRate;
    }

    public void setNeedsHighRefreshRate(boolean needsHighRefreshRate) {
        this.needsHighRefreshRate = needsHighRefreshRate;
    }

    public int getBudgetPriority() {
        return budgetPriority;
    }

    public void setBudgetPriority(int budgetPriority) {
        this.budgetPriority = Math.max(1, Math.min(5, budgetPriority));
    }

    public int getRamPriority() {
        return ramPriority;
    }

    public void setRamPriority(int ramPriority) {
        this.ramPriority = Math.max(1, Math.min(5, ramPriority));
    }

    public int getStoragePriority() {
        return storagePriority;
    }

    public void setStoragePriority(int storagePriority) {
        this.storagePriority = Math.max(1, Math.min(5, storagePriority));
    }

    public int getCameraPriority() {
        return cameraPriority;
    }

    public void setCameraPriority(int cameraPriority) {
        this.cameraPriority = Math.max(1, Math.min(5, cameraPriority));
    }

    public int getBatteryPriority() {
        return batteryPriority;
    }

    public void setBatteryPriority(int batteryPriority) {
        this.batteryPriority = Math.max(1, Math.min(5, batteryPriority));
    }

    public int getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(int displayPriority) {
        this.displayPriority = Math.max(1, Math.min(5, displayPriority));
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "budget=$" + minBudget + "-$" + maxBudget +
                ", minRam=" + minRamGb + "GB" +
                ", minStorage=" + minStorageGb + "GB" +
                ", minDisplay=" + minDisplaySize + "\"" +
                ", minBattery=" + minBatteryMah + "mAh" +
                ", headphoneJack=" + needsHeadphoneJack +
                ", waterResistance=" + needsWaterResistance +
                '}';
    }
}

