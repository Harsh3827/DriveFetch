package webcrawling;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// for carrentals
public class crwawl_old_carrental {

    public static String car_rental_Url = "https://www.carrentals.com/";

    static ChromeOptions chrome_Options = new ChromeOptions();

    static WebDriver driverr;
    static WebDriverWait waitt;

    public static void initDriver() {
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.OFF);
        //   chrome_Options.addArguments("--headless");
        driverr = new ChromeDriver(chrome_Options);

        waitt = new WebDriverWait(driverr, Duration.ofSeconds(30));
        driverr.get(car_rental_Url);
        driverr.navigate().refresh();

    }




    /**
     *
     *
     * @param driverr      WebDriver instance
     * @param pick_up_Date Pick-up date for car rental
     * @param return_Date  Return date for car rental
     */
    private static void navigate_To_Target_Month(WebDriver driverr, String pick_up_Date, String return_Date) {
        // Split pickup date
        String[] pickup_Date_Parts = pick_up_Date.split("/");
        String pick_Day = pickup_Date_Parts[0];
        String pick_Month = pickup_Date_Parts[1];
        String pick_Year = pickup_Date_Parts[2];
        // split the return date
        String[] return_Date_Parts = return_Date.split("/");
        String ret_Day = return_Date_Parts[0];
        String ret_Month = return_Date_Parts[1];
        String ret_Year = return_Date_Parts[2];


        WebDriverWait waitt = new WebDriverWait(driverr, Duration.ofSeconds(10));


        waitt.until(ExpectedConditions.visibilityOfElementLocated(By.id("d1-btn"))).click();
        // Wait until container present
        WebElement pagination_Container = waitt.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".uitk-date-picker-menu-pagination-container")));

        // find button for next or last month

        List<WebElement> both_Buttons = pagination_Container.findElements(By.cssSelector("button[data-stid='date-picker-paging']"));


        // getting where curser is

        WebElement current_Month_Element = driverr.findElement(By.cssSelector(".uitk-date-picker-month-name"));
        System.out.println(current_Month_Element.getText());

        boolean isPickup_Selected = false;
        boolean isReturn_Selected = false;

        // Continue navigating until the target month and year are reached
        while (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) != (Integer.parseInt(pick_Month)) || !isPickup_Selected) {


            if (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) == (Integer.parseInt(pick_Month))){
                String button_XPath = String.format("//button[@aria-label='%s %d, %d']", get_MonthName(Integer.parseInt(pick_Month)), Integer.parseInt(pick_Day), Integer.parseInt(pick_Year));
                WebElement startDate_Button = waitt.until(ExpectedConditions.elementToBeClickable(By.xpath(button_XPath)));
                startDate_Button.click();
                isPickup_Selected = true;
            }else {
                if (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) > (Integer.parseInt(pick_Month))) {
                    both_Buttons.get(0).click();
                } else {
                    both_Buttons.get(1).click();
                }
            }
            // stop thread for sometime
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException");
                e.printStackTrace();
            }

            // change the current month
            current_Month_Element = driverr.findElement(By.cssSelector(".uitk-date-picker-month-name"));
        }


        while (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) != (Integer.parseInt(ret_Month)) || !isReturn_Selected) {
            // Click the appropriate button based on whether the target month is before or after the current month
            if (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) == (Integer.parseInt(ret_Month))){
                String endDatebutton_XPath = String.format("//button[contains(@aria-label, '%s %d, %d')]", get_MonthName(Integer.parseInt(ret_Month)), Integer.parseInt(ret_Day), Integer.parseInt(ret_Year));
                WebElement endDate_Button = waitt.until(ExpectedConditions.elementToBeClickable(By.xpath(endDatebutton_XPath)));
                endDate_Button.click();
                isReturn_Selected = true;
            }else {
                if (get_MonthNumber(current_Month_Element.getText().split(" ")[0]) > (Integer.parseInt(ret_Month))) {
                    both_Buttons.get(0).click();
                } else {
                    both_Buttons.get(1).click();
                }
            }
            // stop thread for sometime
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException");
                e.printStackTrace();
            }


            // change the current month
            current_Month_Element = driverr.findElement(By.cssSelector(".uitk-date-picker-month-name"));
        }
    }

    /**
     * Resolves and selects the date in the date picker.
     *
     * @param pickUp_Date Pick-up date for car rental
     * @param return_Date Return date for car rental
     */
    public static void resolve_Date(String pickUp_Date, String return_Date) {



        // Split pickup date
        String[] parts = pickUp_Date.split("/");
        int pick_day = Integer.parseInt(parts[0]);
        int pick_month = Integer.parseInt(parts[1]);
        int pick_year = Integer.parseInt(parts[2]);


        // split return date
        String[] eparts = return_Date.split("/");
        int ret_day = Integer.parseInt(eparts[0]);
        int ret_month = Integer.parseInt(eparts[1]);
        int ret_year = Integer.parseInt(eparts[2]);

        navigate_To_Target_Month(driverr, pickUp_Date, return_Date);

        try {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            waitt.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-stid='apply-date-picker'][aria-label='Save changes and close the date picker.']")));
            // Find the element using a CSS selector
            WebElement doneButton = waitt.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-stid='apply-date-picker'][aria-label='Save changes and close the date picker.']")));
            doneButton.click();

        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println("Element not found: " );
        }
    }

    /**
     * Resolves and selects the time in the time picker.
     *
     * @param pickUp_Time Pick-up time for car rental
     * @param return_Time Return time for car rental
     */
    public static void resolve_Time(String pickUp_Time, String return_Time) {


        String pickup_Time = pickUp_Time;

        String dropOff_Time = return_Time;

        select_Time(pickup_Time, ".uitk-field-select");


        select_Time(dropOff_Time, ".uitk-field-select[aria-label='Drop-off time']");
        WebElement search_Button = driverr.findElement(By.cssSelector("button[data-testid='submit-button']"));
        search_Button.click();

        try {
            Thread.sleep(12);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        waitt.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-testid='car-offer-card']")));
        WebCrawler.createF_ile(car_rental_Url, driverr.getPageSource(), "orbitz_deals", "CarRentalFiles/");
        System.out.println("Carrental  deals extracted and add to Carrental folder.");
    }
    /**
     * Handles the pick-up location input.
     *
     * @param pickUp_Loc Pick-up location for car rental
     */

    public static void handle_PickUp_Location(String pickUp_Loc) {
        driverr.manage().window().maximize();
        waitt.until(drive -> ((JavascriptExecutor) drive).executeScript("return document.readyState").equals("complete"));

        waitt.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"wizard-car-pwa-1\"]/div[1]/div[1]/div/div/div/div/div[2]/div[1]/button"))).click();
        WebElement inputField = driverr.findElement(By.xpath("//*[@id=\"location-field-locn\"]"));
        inputField.sendKeys(pickUp_Loc);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        inputField.sendKeys(" ");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        inputField.sendKeys(Keys.BACK_SPACE);

        inputField.sendKeys(pickUp_Loc);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        inputField.sendKeys(" ");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        inputField.sendKeys(Keys.BACK_SPACE);


        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement location_Results = driverr.findElement(By.cssSelector("ul[data-stid='location-field-locn-results']"));


        List<WebElement> location_Items = location_Results.findElements(By.cssSelector("li[data-stid='location-field-locn-result-item']"));
        int aa = 1;


        for (WebElement location_Item : location_Items) {

            WebElement button_Element = location_Item.findElement(By.cssSelector("button[data-stid='location-field-locn-result-item-button']"));


            String locationSuggestion = button_Element.getAttribute("aria-label");


            aa++;
        }
        Scanner scanner = new Scanner(System.in);

        String user_Input = "1";

        List<WebElement> buttons = waitt.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("button[data-stid='location-field-locn-result-item-button']")));

        int index_To_Click = Integer.parseInt(user_Input); // Change the index based on your requirement (0-based index)


        if (index_To_Click >= 0 && index_To_Click < buttons.size()) {


            buttons.get(index_To_Click).click();

        } else {
            System.out.println("Invalid index or element not found.");
        }
    }

    /**
     * Handles the drop-off location input.
     *
     * @param drop_Off_Loc Drop-off location for car rental
     */
    public static void handle_DropOff_Location(String drop_Off_Loc) {
        driverr.manage().window().maximize();
        waitt.until(drive -> ((JavascriptExecutor) drive).executeScript("return document.readyState").equals("complete"));


        waitt.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"wizard-car-pwa-1\"]/div[1]/div[2]/div/div/div/div/div[2]/div[1]/button"))).click();
        WebElement input_Field1 = driverr.findElement(By.xpath("//*[@id=\"location-field-loc2\"]"));
        input_Field1.sendKeys(drop_Off_Loc);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement location_Results = driverr.findElement(By.cssSelector("ul[data-stid='location-field-loc2-results']"));


        List<WebElement> locationItems = location_Results.findElements(By.cssSelector("li[data-stid='location-field-loc2-result-item']"));
        int a = 1;

        for (WebElement locationItem : locationItems) {

            WebElement buttonElement = locationItem.findElement(By.cssSelector("button[data-stid='location-field-loc2-result-item-button']"));


            String locationSuggestion = buttonElement.getAttribute("aria-label");

            a++;
        }
        Scanner scanner = new Scanner(System.in);

        String userInput = "0";
        List<WebElement> buttons = driverr.findElements(By.cssSelector("ul[data-stid='location-field-loc2-results'] button[data-stid='location-field-loc2-result-item-button']"));

        int indexToClick = Integer.parseInt(userInput);

        if (indexToClick >= 0 && indexToClick < buttons.size()) {

            buttons.get(indexToClick).click();

        } else {
            System.out.println("Invalid index or element not found.");
        }
    }


    /**
     * Selects the time in the time picker.
     *
     * @param input_Time           Input time for car rental
     * @param select_Element_Selector Selector for the time selection element
     */
    public static void select_Time(String input_Time, String select_Element_Selector) {

        String[] split_Time = input_Time.split(":");
        int hourr = Integer.parseInt(split_Time[0]);
        int minutee = Integer.parseInt(split_Time[1].substring(0, 2));
        boolean isPM = split_Time[1].substring(2).trim().equalsIgnoreCase("PM");


        int time_InMinutes = hourr * 60 + minutee;
        if(isPM)
        {
            if(hourr!=12)
                time_InMinutes = hourr * 60 + minutee+720;
        }

        WebElement time_Select = driverr.findElement(By.cssSelector(select_Element_Selector));


        for (WebElement optionn : time_Select.findElements(By.tagName("option"))) {
            int optionTime = Integer.parseInt(optionn.getAttribute("data-time"));
            if (optionTime >= time_InMinutes) {
                optionn.click();
                break;
            }
        }
    }
    /**
     * Converts month number to month name.
     *
     * @param month_Number Month number
     * @return Corresponding month name
     */
    private static String get_MonthName(int month_Number) {
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months[month_Number - 1];
    }
    /**
     * Converts month name to month number.
     *
     * @param month_Name Month name
     * @return Corresponding month number
     */
    private static Integer get_MonthNumber(String month_Name) {
        HashMap<String, Integer> hash_Map = new HashMap<>();
        hash_Map.put("January",1);
        hash_Map.put("February",2);
        hash_Map.put("March",3);
        hash_Map.put("April",4);
        hash_Map.put("May",5);
        hash_Map.put("June",6);
        hash_Map.put("July",7);
        hash_Map.put("August",8);
        hash_Map.put("September",9);
        hash_Map.put("October",10);
        hash_Map.put("November",11);
        hash_Map.put("December",12);

        return hash_Map.get(month_Name);
    }

    public static void reset_Driver(){
        driverr.get(car_rental_Url);
    }

    public static void close_Driver(){
        driverr.quit();
    }
}