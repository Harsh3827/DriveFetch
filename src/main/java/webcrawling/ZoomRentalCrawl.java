package webcrawling;

import Model.CarInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZoomRentalCrawl {
    public static String car_rental_Url = "https://zoomrentals.com/";
    public static void WebCrawlZoomRentals(String startDate, String endDate, int duration, String location, String pickup, String drop) throws UnsupportedEncodingException {
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        driver.get(car_rental_Url);
        driver.manage().window().fullscreen();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Select pickup location dropdown and click it
            WebElement selectPickupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("select2-pickuplocations_form-container")));
            selectPickupBtn.click();

            // Enter pickup location
            WebElement enterPickupLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[class='select2-search__field']")));
            enterPickupLocation.sendKeys(location);

            // Choose the first option from the list of available locations
            WebElement availableLocation = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("ul.select2-results__options > li:nth-child(1)")));
            availableLocation.click();

            // Set the start date for car pickup
            WebElement startBtn = wait.until(ExpectedConditions.elementToBeClickable((By.id("txtchin"))));
            startBtn.click();
            startBtn.clear();
            startBtn.sendKeys(startDate);
            startBtn.sendKeys(Keys.RETURN);

            // Set the end date for car return
            WebElement endBtn = wait.until(ExpectedConditions.elementToBeClickable((By.id("txtchout"))));
            endBtn.click();
            endBtn.clear();
            endBtn.sendKeys(endDate);
            endBtn.sendKeys(Keys.RETURN);

            // Set pick-up time
            WebElement pickupTime = wait.until(ExpectedConditions.elementToBeClickable(By.id("pickup_time")));
            pickupTime.clear();
            pickupTime.sendKeys(pickup.replace(" ", ""));
            pickupTime.sendKeys(Keys.RETURN);

            // Set drop-off time
            WebElement dropTime = wait.until(ExpectedConditions.elementToBeClickable(By.id("dropoff_time")));
            dropTime.clear();
            dropTime.sendKeys(drop.replace(" ", ""));
            dropTime.sendKeys(Keys.RETURN);

            driver.findElements(By.cssSelector("input[type='submit']")).get(0).click();
            List<WebElement> offerCards = waitForClassElementsVisible(wait, driver);

            List<CarInfo> carRentals = new ArrayList<>();

            for (WebElement offerCard : offerCards) {
                // Check if the "SOLD OUT" input exists within the current car element
                List<WebElement> soldOutElements = offerCard.findElements(By.cssSelector("input[value='SOLD OUT']"));

                if (!soldOutElements.isEmpty()) {
                    // If the "SOLD OUT" input exists, skip this iteration
                    // System.out.println("SOLD OUT item found. Skipping...");
                    continue;
                }

                // Check if the "SOLD OUT" input exists within the current car element
                List<WebElement> inInquiryElements = offerCard.findElements(By.cssSelector("input[value='Inquiry Only']"));

                if (!inInquiryElements.isEmpty()) {
                    // If the "SOLD OUT" input exists, skip this iteration
                    // System.out.println("SOLD OUT item found. Skipping...");
                    continue;
                }

                // Extract car type
                WebElement carTypeElement = offerCard.findElement(By.className("tab-list-feature"));
                if(carTypeElement.getText().trim().isEmpty()) {
                    continue;
                }

                // Regular expression to match the middle name
                Pattern pattern = Pattern.compile("\\|\\s*([^|(]+)");
                Matcher matcher = pattern.matcher(carTypeElement.getText());
                String carType = (matcher.find()) ? matcher.group(1).trim() : "";

                // Extract car specifications (Passenger capacity, AC, Transmission type)
                WebElement carDetailsDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.col-md-4.hidden-xs.hidden-sm[align='left']")));
                WebElement secondRow = carDetailsDiv.findElements(By.cssSelector("div.row")).get(1);
                List<WebElement> carSpecs = secondRow.findElements(By.cssSelector("div.col-md-6"));

                String[] carModelExtract = carTypeElement.getText().split("\\(");
                String carModel = "";
                if(carModelExtract.length > 2) {
                    carModel = carModelExtract[2].replace(" or similar", "").replace(" or Similar", "").replace(")", "");
                }

                // Number of passengers capacity
                String noPersons = carSpecs.get(0).getText().trim();
                int passengerCapacity = Integer.parseInt(noPersons);

                // Car transmission type
                String transmission = carSpecs.get(2).getText().trim();

                // Extract total price
                String priceElement = offerCard.findElement(By.cssSelector(".col-xs-4.col-xs-6.hidden-xs div.row:nth-child(2) div.col-md-12")).getText().replace(" CAD Total", "");
                double priceNumber = Math.round((Double.parseDouble(priceElement) / duration) * 100.0) / 100.0;

                // Extract car image link
                WebElement carImageContainer = offerCard.findElement(By.cssSelector(".col-md-4.hidden-xs.hidden-sm"));
                WebElement carImageTag = carImageContainer.findElement(By.tagName("img"));
                String link = carImageTag.getAttribute("src");

                // Assuming rental company is fixed for the example
                String rentalCompany = "ZoomRental";

                // Create car rental object
                CarInfo carRental = new CarInfo(carModel, priceNumber, passengerCapacity, carType, transmission, rentalCompany,link);
                carRentals.add(carRental);

                carRental = new CarInfo("Mazda1", 70.77, 10, "Intermediate SUV", "Automatic", "Costco","www.costcotravel.com");
                carRentals.add(carRental);
            }

            // Convert list to JSON and save to file
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("ZoomRentalData.json"), carRentals);

            closeDriver(driver);

        } catch (Exception e) {
            throw new RuntimeException("Error has occurred during the web crawl", e);
        }
        finally {
        driver.quit();
    }
    }
    public static List<WebElement> waitForClassElementsVisible(WebDriverWait wait, WebDriver driver) {
        List<WebElement> elements = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("filter-cars"))
        );

        // Check if elements are found; if not, try the retry logic
        if (elements.isEmpty()) {
            // Retry logic: Check if the submit button exists and click it
            List<WebElement> submitButtons = driver.findElements(By.cssSelector("input[type='submit']"));
            if (!submitButtons.isEmpty()) {
                WebElement submitButton = submitButtons.get(0);
                submitButton.click();
            }

            // Retry waiting for the elements after clicking submit button
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.filter-cars")));
        }

        return elements;
    }

    public static void closeDriver(WebDriver driver){
        driver.quit();
    }
}
