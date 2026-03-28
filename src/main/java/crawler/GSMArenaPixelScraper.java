package crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GSMArenaPixelScraper {

    //CSV file path
    private static final String CSV_FILE = "src/main/resources/pixel_phones.csv";

    //GSMArena Google Pixel listing page
    private static final String PIXEL_LISTING_URL = "https://www.gsmarena.com/google-phones-f-107-0-r1-p1.php";

    public static void main(String[] args){

        //setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        //start Chrome browser
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<String[]> phoneData = new ArrayList<>();

        try{
            System.out.println("Opening GSMArena Pixel listing page...");
            driver.get(PIXEL_LISTING_URL);

            //wait for the phone list to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".makers ul li")));

            //get all phone links
            List<WebElement> phoneLinks = driver.findElements(By.cssSelector(".makers ul li a"));

            //store first 10 URLs
            List<String> pixelPhoneUrls = new ArrayList<>();
            for(int i =0; i< Math.min(15, phoneLinks.size()); i++){
                pixelPhoneUrls.add(phoneLinks.get(i).getAttribute("href"));
            }

            System.out.println("Found " + pixelPhoneUrls.size() + " Google Pixel phones. Starting to extract data...");

            //visit each phone page
            for(int i =0; i< pixelPhoneUrls.size(); i++){
                String phoneUrl = pixelPhoneUrls.get(i);
                try{
                    String[] data = scrapePhonePage(driver, wait, phoneUrl);
                    if(data != null){
                        phoneData.add(data);
                        System.out.println("Extracted (" + (i+1) + "/" + pixelPhoneUrls.size()+ "): " + data[0]);
                    }
                }catch (Exception e){
                    System.out.println("Failed to scrape: " + phoneUrl + " - " + e.getMessage());
                }
            }

            //save to CSV
            saveToCSV(phoneData);
            System.out.println("Mission Accomplished! Data saved to " + CSV_FILE);

        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }finally {
            driver.quit();
        }
    }

    private static String[] scrapePhonePage(WebDriver driver, WebDriverWait wait, String url){
        driver.get(url);

        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".specs-phone-name-title")));
        }catch(Exception e){
            return null;
        }

        //phone name
        String name = getText(driver, ".specs-phone-name-title");

        //Network
        String network = getSpec(driver, "Network", "Technology");

        //platform
        String os = getSpec(driver, "Platform", "OS");
        String chipset = getSpec(driver, "Platform", "Chipset");
        String cpu = getSpec(driver, "Platform", "CPU");

        //memory
        String ram = getSpec(driver, "Memory", "Internal");

        // Display
        String displayType = getSpec(driver, "Display", "Type");
        String displaySize = getSpec(driver, "Display", "Size");
        String resolution  = getSpec(driver, "Display", "Resolution");

        //Main Camera
        String mainCamera = getSpec(driver, "Main Camera", "Dual");
        if(mainCamera.equals("N/A")){
            mainCamera = getSpec(driver, "Main Camera", "Triple");
        }
        if(mainCamera.equals("N/A")){
            mainCamera = getSpec(driver, "Main Camera", "Single");
        }
        String cameraFeatures = getSpec(driver, "Main Camera", "Features");
        String cameraVideo = getSpec(driver, "Main Camera", "Video");

        //selfie Camera
        String selfieCamera = getSpec(driver, "Selfie camera", "Single");

        //sound
        String headphoneJack = getSpec(driver, "Sound", "3.5mm jack");
        String loudspeaker = getSpec(driver, "Sound", "Loudspeaker");

        //communication
        String wlan = getSpec(driver, "Comms", "WLAN");
        String bluetooth = getSpec(driver, "Comms", "Bluetooth");

        //features
        String sensors = getSpec(driver, "Features", "Sensors");

        //battery
        String battery = getSpec(driver, "Battery", "Type");
        String charging = getSpec(driver, "Battery", "Charging");

        //miscellaneous
        String Price = getSpec(driver, "Misc", "Price");

        //body
        String dimensions = getSpec(driver, "Body", "Dimensions");
        String weight =  getSpec(driver, "Body", "Weight");
        String waterResist = getSpecByDataAttr(driver, "bodyother");

        return new String[]{
                name, network, os, chipset, cpu, ram, displayType, displaySize, resolution, mainCamera, cameraFeatures, cameraVideo,
                selfieCamera, headphoneJack, loudspeaker, wlan, bluetooth, sensors,
                battery, charging, Price, dimensions, weight, waterResist, url
        };
    }


    //helper method to get specification by category and row lable
    private static String getSpec(WebDriver driver, String category, String label){
        try {
            List<WebElement> tables = driver.findElements(By.cssSelector("#specs-list table"));

            for(WebElement table: tables){
                String header = table.findElement(By.cssSelector("th")).getText().trim();
                if(header.equalsIgnoreCase(category)){
                    List<WebElement>  rows = table.findElements(By.cssSelector("tr"));
                    for(WebElement row: rows){
                        try{
                            String rowLabel = row.findElement(By.cssSelector("td.ttl")).getText().trim();
                            if(rowLabel.equalsIgnoreCase(label)){
                                return row.findElement(By.cssSelector("td.nfo")).getText().trim();
                            }
                        }catch(Exception ignored){
                            //some rows might not have the expected structure, just skip them
                        }
                    }
                }
            }
        }catch (Exception ignored){}

        return "N/A";
    }

    //helper method to get text by CSS selector
    private static String getText(WebDriver driver, String selector){
        try {
            return driver.findElement(By.cssSelector(selector)).getText().trim();
        }catch(Exception e){
            return "N/A";
        }
    }

    //helper method to get specification by data-spec attribute
    private static String getSpecByDataAttr(WebDriver driver, String dataSpec) {
        try {
            WebElement element = driver.findElement(
                    By.cssSelector("td.nfo[data-spec='" + dataSpec + "']"));
            return element.getText().trim();
        } catch (Exception e) {
            return "N/A";
        }
    }


    //save data to CSV file
    private static void saveToCSV(List<String[]> data) throws IOException{
        String[] headers = {"Name", "Network", "OS", "Chipset", "CPU",
                "RAM_Storage", "Display_Type", "Display_Size", "Resolution",
                "Main_Camera", "Camera_Features", "Camera_Video",
                "Selfie_Camera", "Headphone_Jack", "Loudspeaker",
                "WLAN", "Bluetooth", "Sensors",
                "Battery", "Charging", "Price",
                "Dimensions", "Weight", "Water_Resistance", "URL"};

        try(CSVPrinter printer = new CSVPrinter(new FileWriter(CSV_FILE), CSVFormat.DEFAULT.withHeader(headers))){
            for(String[] row: data){
                printer.printRecord((Object[]) row);
            }
        }
        System.out.println("CSV saved with " + data.size()+ " phones.");
    }

}
