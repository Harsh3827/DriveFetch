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
import java.util.concurrent.TimeUnit;

public class CostcoTravelCrawl {
    public static String car_rental_Url = "https://www.costcotravel.ca/Rental-Cars/";

    public static void WebCrawlCaascoTravel(String startDate, String endDate, int duration, String location) throws UnsupportedEncodingException {
        ChromeOptions chrome_Options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chrome_Options);
        driver.get(car_rental_Url);
        driver.manage().window().fullscreen();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Store the list data
            List<CarInfo> costcoData = new ArrayList<>();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler"))).click();

            System.out.println("--------->section 1");
            // Locate the radio button using its name or value attribute
            WebElement radioButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='ON']")));
            System.out.println("--------->section 2");

            // Click on the radio button to select it
            radioButton.click();
            //Click the continue button
            driver.findElement(By.cssSelector("button[data-id='confirm_button'][data-confirm='true']")).click();

            // Wait condition
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); // Implicit time
            driver.findElement(By.id("dropOfDifferentLocation")).click();

            //Task 1 -Enter pick-up location(text box)
            WebElement pickupLocation1 = driver.findElement(By.id("pickupLocationTextWidget"));
            pickupLocation1.sendKeys(location);

            // Click the dropdown option
            pickupLocation1.sendKeys(Keys.SPACE); // Trigger dropdown
            Thread.sleep(2000); // Add a slight delay to ensure dropdown loads

            pickupLocation1.sendKeys(Keys.ARROW_DOWN);  // Navigate to the first option
            pickupLocation1.sendKeys(Keys.ENTER);

            // Create a WebDriverWait instance
            WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for the date input field to be present in the DOM
            WebElement dateInputField = wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("pickUpDateWidget")));

            // Make the input field editable (if it's read-only)
            dateInputField.getAttribute("readonly"); // This may not work in all cases, depending on the framework

            // Clear any existing text in the input field (optional)
            dateInputField.clear();

            //Task 1- Enter the date in the format "mm/dd/yyyy"(text box)
            String dateToEnter = startDate; // Replace with your desired date
            dateInputField.sendKeys(dateToEnter);
            // #################################################

            // ################## Time Selection ################
            driver.findElement(By.cssSelector("option[value='09:30 AM']")).click();
            // ##################################################

            // ################# Drop-off Location ###################
            // driver.findElement(By.id("dropoffLocationTextWidget")).sendKeys("(YYZ)"); // Toronto Pearson International Airport
            //
            driver.findElement(By.cssSelector("#dropoffTimeWidget option[value='08:30 PM']")).click();
            // #####################################################

            // ################ second Try Drop-off location ################
            // Task 1- Enter pick-up location
            WebElement dropofflocation = driver.findElement(By.id("dropoffLocationTextWidget"));
            dropofflocation.sendKeys(location);

            // Task 1 Click the dropdown option
            dropofflocation.sendKeys(Keys.SPACE); // Trigger dropdown
            Thread.sleep(2000); // Add a slight delay to ensure dropdown loads

            dropofflocation.sendKeys(Keys.ARROW_DOWN);  // Navigate to the first option
            dropofflocation.sendKeys(Keys.ENTER);

            // ###############################################################

            //
            driver.findElement(By.id("findMyCarButton")).click();

            String pickupLocation = "(YQG) Windsor Airport";
            String dropoffLocation = "(YYZ) Toronto Pearson International Airport";
            String pickupDate = "10-10-2024";
            String pickupTime = "Noon";
            String dropoffTime = "Noon";

//            // Data to be written in the Page-2 file
//            List<String[]> dataLines = Arrays.asList(
//                    new String[]{"Pickup Location", "Dropoff Location", "Pickup Date", "Pickup Time", "Dropoff Time"},
//                    new String[]{pickupLocation, dropoffLocation, pickupDate, pickupTime, dropoffTime}
//            );

            // Assuming rental company is fixed for the example
            String rentalCompany = "CostcoTravel";
//
//            // Create car rental object
//            CarInfo carRental = new CarInfo("Test", 11.12, 10, "Mazda", "5", rentalCompany,"www.costcotravel.com");
//            costcoData.add(carRental);

            // Convert list to JSON and save to file
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("CostcoTravelsData.json"), costcoData);

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
