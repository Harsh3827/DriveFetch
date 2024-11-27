package webcrawling;

import Model.CarInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CaascoTravelCrawl {
    public static String car_rental_Url = "https://www.caascotravel.com/car/";

    public static void WebCrawlCaascoTravel(String startDate, String endDate, int duration, String location) throws UnsupportedEncodingException {
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        driver.get(car_rental_Url);
        driver.manage().window().fullscreen();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Locate the web element for the "Pick Up Location" input field by its id attribute
            WebElement PickUpLocation = driver.findElement(By.id("pickuplocation"));

            // Enter the pickup location details into the input field
            PickUpLocation.sendKeys(location);

            // Pick-up date
            WebElement PickUpDate = driver.findElement(By.id("displayPickup.date"));
            PickUpDate.sendKeys(startDate);

            // Use JavaScript to directly set the value of the "Drop Off Date" field
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(String.format("document.getElementById('displayDropoff.date').value = '%s'", endDate));

            // Click the find button to search the available cars for rent and scrap multiple pages
            WebElement FindButton = driver.findElement(By.id("searchButton"));
            FindButton.click();

            // Wait condition
            WebElement ViewByVehicle = wait.until(ExpectedConditions.elementToBeClickable(By.className("list-by-vehicle-button")));
            ViewByVehicle.click();

            // Locate the car cards and extract the data
            List<WebElement> carCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.card-redesign")));

            // Store the list data
            List<CarInfo> caascoData = new ArrayList<>();

            for (WebElement carCard : carCards) {

                // Extracting details from each car card
                String carClass = carCard.findElement(By.cssSelector(".car-class")).getText();
                if (carClass.contains("Special")) continue;
                String makeModel = carCard.findElement(By.cssSelector(".make-model")).getText().replace(" Or Similar", "");
                String passengers = carCard.findElement(By.cssSelector("span[aria-label*='Passengers'] .count")).getText().trim();
                String bags = carCard.findElement(By.cssSelector("span[aria-label*='Bags'] .count")).getText().trim();
                String totalPrice = carCard.findElement(By.cssSelector(".price-amount-caa")).getText().trim().replace(" Total", ""); // Updated selector

                double price = Math.round((Double.parseDouble(totalPrice) / duration) * 100.0) / 100.0;
                String link = carCard.findElement(By.cssSelector(".car-image img")).getAttribute("src");

                // Assuming rental company is fixed for the example
                String rentalCompany = "CaascoTravel";

                // Create car rental object
                CarInfo carRental = new CarInfo(makeModel, price, Integer.parseInt(passengers), carClass, bags, rentalCompany,link);
                caascoData.add(carRental);
            }

            // Convert list to JSON and save to file
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("Web_Crawl_CaascoTravelData.json"), caascoData);

            // Close the chrome driver
//            closeDriver(driver);

        } catch (Exception e) {
            throw new RuntimeException("Error has occurred during the web crawl", e);
        }
    }

    public static void closeDriver(WebDriver driver){
        driver.quit();
    }
}
