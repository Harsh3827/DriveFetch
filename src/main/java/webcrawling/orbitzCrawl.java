package webcrawling;

import com.fasterxml.jackson.databind.ObjectMapper;
import Model.CarInfo;  // Import the CarInfo model
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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class orbitzCrawl {

    public static String avis_Url = "https://www.orbitz.com/";
    public static void WebCrawlOrbitz(String startDate, String endDate, int duration, String location, String time1, String time2) throws UnsupportedEncodingException {
        // Set up Chrome options and driver
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);

        // Convert dates to required format
        String convertedFromDate = convertDateFormat(startDate, "M/d/yyyy");
        String convertedToDate = convertDateFormat(endDate, "M/d/yyyy");

        // Encode location and dates
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.name());
        String encodedFromDate = URLEncoder.encode(convertedFromDate, StandardCharsets.UTF_8.name());
        String encodedToDate = URLEncoder.encode(convertedToDate, StandardCharsets.UTF_8.name());

        // Convert time formats to match the 12-hour time format (e.g., "10:00 AM" -> "1000AM")
        String encodedTime1 = URLEncoder.encode(convertTimeTo12HourFormat(time1), StandardCharsets.UTF_8.name());
        String encodedTime2 = URLEncoder.encode(convertTimeTo12HourFormat(time2), StandardCharsets.UTF_8.name());

        // Construct URL with time parameters
        String url = String.format("https://www.orbitz.com/carsearch?date1=%s&date2=%s&locn=%s&time1=%s&time2=%s",
                encodedFromDate, encodedToDate, encodedLocation, encodedTime1, encodedTime2);

        driver.get(url);
        driver.manage().window().fullscreen();
       // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
       // WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("submit-btn")));
      //  submitButton.click(); // Perform the click action

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            List<WebElement> offerCards = waitForClassElementsVisible(wait, driver, "offer-card-desktop");


            // Create list to hold CarInfo objects
            List<CarInfo> carInfoList = new ArrayList<>();

            for (WebElement offerCard : offerCards) {
                WebElement carNameElement = offerCard.findElement(By.className("uitk-text"));
                String carName = carNameElement.getText().replace(" or similar", "").replace(" or larger - Vehicle determined upon pick-up", "");
                if (carName.contains("Managers Special")) continue;

                WebElement carTypeElement = offerCard.findElement(By.tagName("h3"));
                String carType = carTypeElement.getText();

                WebElement noPersonsElement = offerCard.findElement(By.cssSelector("div.uitk-text span"));
                String noPersons = noPersonsElement.getText();

                WebElement transmissionElement = offerCard.findElement(By.cssSelector("div.uitk-text span:nth-child(5)"));
                String transmission = transmissionElement.getText();

                WebElement priceElement = offerCard.findElement(By.cssSelector(".total-price"));
                String price = priceElement.getText().replaceAll("\\$", "");
                int priceNumber = Integer.parseInt(price) / duration;

                WebElement linkElement = offerCard.findElement(By.cssSelector("a[data-stid='default-link']"));
                String link = linkElement.getAttribute("href");

                // Create CarInfo object
                CarInfo carInfo = new CarInfo(carName, priceNumber, Integer.parseInt(noPersons), carType, transmission, "Orbitz", link);
                carInfoList.add(carInfo);
            }

            // Initialize Jackson ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Serialize the list of CarInfo objects to JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("Web_Crawl_Orbitz.json"), carInfoList);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred during the web crawl", e);
        }
    }

    public static String convertTimeTo12HourFormat(String time) {
        // Time format handling
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hhmma");

        LocalTime parsedTime = LocalTime.parse(time, inputFormatter);
        return parsedTime.format(outputFormatter).toUpperCase();  // Convert to 12-hour format with AM/PM
    }

    public static List<WebElement> waitForClassElementsVisible(WebDriverWait wait, WebDriver driver, String className) {
        // Try to wait for elements to become visible
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(className)));
        return elements;
    }

    public static String convertDateFormat(String inputDate, String outputFormat) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            return outputDateFormat.format(inputDateFormat.parse(inputDate));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convertToISOFormat(String date, String currentFormat) {
        // Define the input and output formats
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(currentFormat);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the input date and format to the desired output
        LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
        return parsedDate.format(outputFormatter);
    }

    public static int calculateDurationISO(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate); // Assumes ISO format like "2024-11-22"
        LocalDate end = LocalDate.parse(endDate);
        return (int) Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }
    public static void closeDriver(){
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        driver.quit();
    }
}
