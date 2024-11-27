package webcrawling;

import Model.CarInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static webcrawling.orbitzCrawl.convertDateFormat;

public class  CarRentalWebCrawl {
    public static String car_rental_Url = "https://www.carrentals.com/";
    public static void WebCrawlCarRentals(String startDate, String endDate, int duration, String location) throws UnsupportedEncodingException {
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        String convertedFromDate = convertDateFormat(startDate, "M/d/yyyy");
        String convertedToDate = convertDateFormat(endDate, "M/d/yyyy");
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.name());
        String encodedFromDate = URLEncoder.encode(convertedFromDate, StandardCharsets.UTF_8.name());
        String encodedToDate = URLEncoder.encode(convertedToDate, StandardCharsets.UTF_8.name());
        String url = String.format("https://www.carrentals.com/carsearch?locn=%s&date1=%s&date2=%s",
                encodedLocation, encodedFromDate, encodedToDate);
        driver.get(url);
        driver.manage().window().fullscreen();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            List<WebElement> offerCards = waitForClassElementsVisible(wait, driver, "offer-card-desktop");

            List<CarInfo> carRentals = new ArrayList<>();

            for (WebElement offerCard : offerCards) {
                WebElement carTypeElement = offerCard.findElement(By.cssSelector("h3.uitk-heading"));
                String carType = carTypeElement.getText();
                if (carType.contains("Special")) continue;

                WebElement carModelElement = offerCard.findElement(By.cssSelector("div.uitk-text"));
                String carModel = carModelElement.getText().replace(" or similar", "");

                WebElement noPersonsElement = offerCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute"));
                String noPersons = noPersonsElement.getText();
                int passengerCapacity = Integer.parseInt(noPersons);

                WebElement transmissionElement = offerCard.findElement(By.xpath("//span[contains(text(), 'Automatic') or contains(text(), 'Manual')]"));
                String transmission = transmissionElement.getText();

                WebElement priceElement = offerCard.findElement(By.cssSelector(".total-price"));
                String price = priceElement.getText().replaceAll("\\$", "");
                double priceNumber = Double.parseDouble(price) / duration;

                WebElement linkElement = offerCard.findElement(By.cssSelector("a[data-stid='default-link']"));
                String link = linkElement.getAttribute("href");

                // Assuming rental company is fixed for the example
                String rentalCompany = "CarRental";

                // Create car rental object
                CarInfo carRental = new CarInfo(carModel, priceNumber, passengerCapacity, carType, transmission, rentalCompany,link);
                carRentals.add(carRental);
            }

            // Convert list to JSON and save to file
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("Web_Crawl_CarrentalData.json"), carRentals);

        } catch (Exception e) {
            throw new RuntimeException("Error has occurred during the web crawl", e);
        }


    }
    public static List<WebElement> waitForClassElementsVisible(WebDriverWait wait, WebDriver driver, String className) {
        // Try to wait for elements to become visible
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(className)));

        // Check if elements are found; if not, try the retry logic
        if (elements.isEmpty()) {
            // Retry logic: Check if the submit button exists and click it
            List<WebElement> submitButtons = driver.findElements(By.name("submit-btn"));
            if (!submitButtons.isEmpty()) {
                WebElement submitButton = submitButtons.get(0);
                submitButton.click();
            }

            // Retry waiting for the elements after clicking submit button
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(className)));
        }

        return elements;
    }

    public static void closeDriver(){
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        driver.quit();
    }

}

