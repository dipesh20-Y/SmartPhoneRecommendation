package features;

/**
 * PhoneData represents a single smartphone record with all its technical
 * specifications, pricing details, and additional metadata.
 * This class serves as the core data model used across the recommendation engine,
 * search functionality, and scoring system.
 */
public class PhoneData {

    private String name;
    private String network;
    private String os;
    private String chipset;
    private String cpu;

    private String ramStorage;
    private int ramGb;
    private int storageGb;

    private String displayType;
    private double displaySize;
    private String resolution;

    private String mainCamera;
    private String cameraFeatures;
    private String cameraVideo;
    private String selfieCamera;

    private boolean headphoneJack;
    private boolean loudspeaker;
    private String wlan;
    private String bluetooth;
    private String sensors;

    private int batteryMah;
    private String charging;

    private double price;
    private String dimensions;
    private double weight;
    private String waterResistance;
    private String url;

    // Fields used for keyword search and page ranking
    private String specSummary;
    private String description;

    public PhoneData() {}

    // ==================== GETTERS AND SETTERS ====================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = network; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getChipset() { return chipset; }
    public void setChipset(String chipset) { this.chipset = chipset; }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }

    public String getRamStorage() { return ramStorage; }
    public void setRamStorage(String ramStorage) {
        this.ramStorage = ramStorage;
        parseRamStorage();
    }

    public int getRamGb() { return ramGb; }
    public void setRamGb(int ramGb) { this.ramGb = ramGb; }

    public int getStorageGb() { return storageGb; }
    public void setStorageGb(int storageGb) { this.storageGb = storageGb; }

    public String getDisplayType() { return displayType; }
    public void setDisplayType(String displayType) { this.displayType = displayType; }

    public double getDisplaySize() { return displaySize; }
    public void setDisplaySize(double displaySize) { this.displaySize = displaySize; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getMainCamera() { return mainCamera; }
    public void setMainCamera(String mainCamera) { this.mainCamera = mainCamera; }

    public String getCameraFeatures() { return cameraFeatures; }
    public void setCameraFeatures(String cameraFeatures) { this.cameraFeatures = cameraFeatures; }

    public String getCameraVideo() { return cameraVideo; }
    public void setCameraVideo(String cameraVideo) { this.cameraVideo = cameraVideo; }

    public String getSelfieCamera() { return selfieCamera; }
    public void setSelfieCamera(String selfieCamera) { this.selfieCamera = selfieCamera; }

    public boolean hasHeadphoneJack() { return headphoneJack; }
    public void setHeadphoneJack(boolean headphoneJack) { this.headphoneJack = headphoneJack; }

    public boolean hasLoudspeaker() { return loudspeaker; }
    public void setLoudspeaker(boolean loudspeaker) { this.loudspeaker = loudspeaker; }

    public String getWlan() { return wlan; }
    public void setWlan(String wlan) { this.wlan = wlan; }

    public String getBluetooth() { return bluetooth; }
    public void setBluetooth(String bluetooth) { this.bluetooth = bluetooth; }

    public String getSensors() { return sensors; }
    public void setSensors(String sensors) { this.sensors = sensors; }

    public int getBatteryMah() { return batteryMah; }
    public void setBatteryMah(int batteryMah) { this.batteryMah = batteryMah; }

    public String getCharging() { return charging; }
    public void setCharging(String charging) { this.charging = charging; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getWaterResistance() { return waterResistance; }
    public void setWaterResistance(String waterResistance) { this.waterResistance = waterResistance; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSpecSummary() {
        return specSummary != null ? specSummary : "";
    }
    public void setSpecSummary(String specSummary) {
        this.specSummary = specSummary;
    }

    public String getDescription() {
        return description != null ? description : "";
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Parses combined RAM and Storage information from the ramStorage field.
     */
    private void parseRamStorage() {
        if (ramStorage == null || ramStorage.isEmpty()) return;

        String[] parts = ramStorage.split(",");
        if (parts.length > 0) {
            String firstPart = parts[0].trim();
            String[] tokens = firstPart.split("\\s+");

            for (String token : tokens) {
                if (token.toUpperCase().contains("GB")) {
                    try {
                        int value = Integer.parseInt(token.replaceAll("[^0-9]", ""));
                        if (storageGb == 0) storageGb = value;
                        else if (ramGb == 0) ramGb = value;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    @Override
    public String toString() {
        return "PhoneData{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", ramGb=" + ramGb +
                ", storageGb=" + storageGb +
                ", displaySize=" + displaySize +
                ", batteryMah=" + batteryMah +
                '}';
    }
}