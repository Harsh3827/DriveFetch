package webcrawling;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class d {
    public static void main(String[] args) {
        // Set up ChromeOptions and WebDriver
        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriver driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Open the target URL
            driver.get("https://www.momondo.ca/car-rental");
            String url = "https://www.momondo.ca/car-rental";

            // Locate the pickup location input field and enter data
            WebElement pickupLocationField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[aria-label='Pick-up location']")));
            pickupLocationField.clear();
            String inputText = "";
            if (inputText == "Windsor") {
                pickupLocationField.sendKeys("Windsor");
            }
            pickupLocationField.sendKeys("Windsor");

            List<WebElement> suggestions = driver.findElements(By.xpath("//ul[@aria-label='Pick-up location suggestions']/li"));

            if (suggestions.isEmpty()) {
                // Select first suggestion if no match is found
                WebElement firstSuggestion = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//ul[@aria-label='Pick-up location suggestions']/li[1]")));
                firstSuggestion.click();
            }

            // Select Start Date
            WebElement startDateField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div[aria-label='Start date']")));
            startDateField.click();
            selectDate(driver, "21/11/2024"); // Replace with your desired start date
            WebElement startTime = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[aria-label='Start time input']")));
            startTime.click();
            selectTime(driver,"12:00 AM");

            // Select End Date
            WebElement endDateField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div[aria-label='End date']")));
            endDateField.click();
            selectDate(driver, "22/11/2024"); // Replace with your desired end date

            WebElement endTime = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[aria-label='End time input']")));
            endTime.click();
            selectTime(driver,"10:30 AM");

            // Submit the search form
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")));
            searchButton.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class='zZcm-cards']")));
            WebCrawler.createF_ile(url, driver.getPageSource(), "Mondo", "Mondo/");
            System.out.println("Mondo  deals extracted and add to Mondo folder.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Close the WebDriver
        }
    }

    private static void selectTime(WebDriver driver, String time) throws Exception {
        // Normalize time input
        if (time.equalsIgnoreCase("12:00 PM")) {
            time = "Noon";
        } else if (time.equalsIgnoreCase("12:00 AM")) {
            time = "Midnight";
        }

        // Locate the list of times for Start Time
        List<WebElement> timeOptions = driver.findElements(By.xpath("//ul[@aria-label='Start time input']/li[@aria-label]"));

        // If no options for Start Time, try End Time
        if (timeOptions.isEmpty()) {
            timeOptions = driver.findElements(By.xpath("//ul[@aria-label='End time input']/li[@aria-label]"));
        }

        // Loop through the options and select the matching time
        for (WebElement option : timeOptions) {
            String optionLabel = option.getAttribute("aria-label");
            if (optionLabel != null && optionLabel.equalsIgnoreCase(time)) {
                option.click(); // Click the matching time
                return;
            }
        }

        // If no match is found, fall back to default as Noon
        for (WebElement option : timeOptions) {
            if (option.getAttribute("aria-label").equalsIgnoreCase("Noon")) {
                option.click(); // Click Noon as default
                return;
            }
        }

        throw new Exception("Time not found and default Noon not available."); // Throw an exception if no option matches
    }



    /**
     * Selects the given date from the date picker.
     *
     * @param driver WebDriver instance
     * @param date   Date to be selected in dd/MM/yyyy format
     */
    public static void selectDate(WebDriver driver, String date) throws Exception {
        String[] dateParts = date.split("/");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        String monthYearToMatch = getMonthName(Integer.parseInt(month)) + " " + year;

        while (true) {
            // Get the current displayed month and year
            String currentMonthYear = driver.findElement(By.xpath(
                            "//div[@class='Gagx-content']//div[2]//table[1]/caption[@class='w0lb w0lb-month-name w0lb-mod-align-center']"))
                    .getText();

            if (currentMonthYear.equalsIgnoreCase(monthYearToMatch)) {
                break; // Exit loop when the desired month and year are found
            }

            // Click the "Next" button to navigate to the next month
            driver.findElement(By.cssSelector("div[aria-label='Next month']")).click();
            Thread.sleep(500);
        }

        // Find and click on the specific day
        List<WebElement> allDates = driver.findElements(By.xpath(
                "//div[@class='Gagx-content']//div[2]//table[1]//tr//td//div[@aria-label]"));
        for (WebElement element : allDates) {
            String matchDate = element.getAttribute("aria-label");
            if (matchDate != null && matchDate.contains(day)) {
                element.click();
                return; // Exit after selecting the date
            }
        }
        throw new Exception("Date not found: " + day);
    }

    /**
     * Converts the numeric month to its name (e.g., "01" -> "January").
     *
     * @param month The month in numeric format (01-12)
     * @return The month name in English
     */
    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }
}
