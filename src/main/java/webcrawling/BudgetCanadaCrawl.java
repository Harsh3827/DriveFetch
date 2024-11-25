  package webcrawling;

  import org.openqa.selenium.*;
  import org.openqa.selenium.chrome.ChromeDriver;
  import org.openqa.selenium.chrome.ChromeOptions;
  import org.openqa.selenium.interactions.Actions;
  import org.openqa.selenium.support.ui.ExpectedConditions;
  import org.openqa.selenium.support.ui.Select;
  import org.openqa.selenium.support.ui.WebDriverWait;

  import java.io.File;
  import java.time.Duration;
  import java.util.*;
  import java.util.logging.Level;
  import java.util.logging.Logger;
  import java.util.stream.Collectors;


public class BudgetCanadaCrawl {

    public static String budget_Url = "https://www.budget.ca/en/reservation#/time-and-place";

    private static Hashtable<String, String> urlMap = new Hashtable<String, String>();

    public static void main(String[] args) {

    }

    public static void check_For_PopUp() {
        By popupButtonLocator = By.cssSelector(".bx-row.bx-row-submit button[data-click='close']");
        try {
            WebElement popupButton = waitt.until(ExpectedConditions.presenceOfElementLocated(popupButtonLocator));
            popupButton.click();
        } catch (Exception e) {
        }
    }

    static ChromeOptions chrome_Options = new ChromeOptions();
    static WebDriver driverr;
    static WebDriverWait waitt;

    public static void init_Driver() {
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.OFF);

        // Initialize WebDriver and Chrome Options
        driverr = new ChromeDriver(chrome_Options);
        Actions actions = new Actions(driverr);
        waitt = new WebDriverWait(driverr, Duration.ofSeconds(30));

        // Navigate to the URL
        driverr.get(budget_Url);

        try {
            // Wait for the alert box to be visible
            WebElement alertBox = waitt.until(ExpectedConditions.visibilityOfElementLocated(By.id("bx-form-2259663-step-1")));

            // Perform an action if the alert box is found
            actions.moveToElement(alertBox).click().perform();

            // Handle decline button within the alert box
            WebElement declineOffer = waitt.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='reset']")));
            declineOffer.click();

            System.out.println("Pop-up handled successfully.");
        } catch (TimeoutException e) {
            System.out.println("No pop-up found, continuing...");
            // If no pop-up is found within the timeout, refresh the page
            driverr.navigate().refresh();
        }

        // Refresh the page after handling the pop-up
        driverr.navigate().refresh();
    }


    public static String user_Pickup_Loc = "";

    public static String resolve_Location(String pickup_Location, String picLoc_dropdown, String suggestionBox) {
        check_For_PopUp();
        user_Pickup_Loc = pickup_Location;
        WebElement input_PickUp_Field = driverr.findElement(By.id(picLoc_dropdown));
        input_PickUp_Field.clear();
        input_PickUp_Field.sendKeys(pickup_Location);
        return fetch_Pickup_Locations(driverr, suggestionBox);
    }

    static boolean find_Suggestion = false;

    public static String fetch_Pickup_Locations(WebDriver driverr, String suggestion_Id) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, List<String>> suggestion_Map = new HashMap<>();
        WebDriverWait waitt = new WebDriverWait(driverr, Duration.ofSeconds(2));
        WebElement suggestion_Div = waitt.until(ExpectedConditions.presenceOfElementLocated(By.id(suggestion_Id)));
        WebElement angucomplete_Results = suggestion_Div.findElement(By.className("angucomplete-results"));
        List<WebElement> child_Elements = angucomplete_Results.findElements(By.xpath("./div"));
        HashMap<String, WebElement> locationElement_Map = new HashMap<>();

        for (WebElement child_Element : child_Elements) {
            WebElement div_Web_element = child_Element.findElement(By.xpath(".//div[contains(@class, 'angucomplete-selection-category')]"));
            String ppString = div_Web_element.findElement(By.tagName("p")).getText();
            if (ppString.contains("Airport Rental Locations") || ppString.contains("Neighborhood Rental Locations")) {
                String span_String = div_Web_element.findElement(By.tagName("span")).getText();
                String category_Name = ppString.replace(span_String, "").trim();
                List<String> div_List = new ArrayList<>();
                List<WebElement> divAll_LocIn_Category = child_Element.findElements(By.cssSelector("div.angucomplete-description"));
                for (WebElement divv : divAll_LocIn_Category) {
                    String locName = divv.findElement(By.tagName("span")).getText();
                    if (find_Suggestion) {
                        locName = divv.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
                    }
                    div_List.add(locName);
                    locationElement_Map.put(locName, divv);
                }
                if (child_Elements.get(child_Elements.size()-1) == child_Element) {
                    find_Suggestion = false;
                }
                suggestion_Map.put(category_Name, div_List);
            } else if (find_Suggestion) {
                String span_String = div_Web_element.findElement(By.tagName("span")).getText();
                String category_Name = ppString.replace(span_String, "").trim();
                List<String> div_List = new ArrayList<>();
                List<WebElement> divAll_LocIn_Category = child_Element.findElements(By.cssSelector("div.angucomplete-description"));
                for (WebElement divv : divAll_LocIn_Category) {
                    String loc_Name = divv.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
                    div_List.add(loc_Name);
                    locationElement_Map.put(loc_Name, divv);
                }
                suggestion_Map.put(category_Name, div_List);
            }
        }

        if (suggestion_Map.isEmpty()) {
            find_Suggestion = true;
            fetch_Pickup_Locations(driverr, suggestion_Id);
            return "";
        }

        int indexx = 1;
        List<String> combined_List = new ArrayList<>();
        for (Map.Entry<String, List<String>> entryy : suggestion_Map.entrySet()) {
            if (!entryy.getValue().isEmpty()) {
                combined_List.addAll(entryy.getValue());
                for (String element : entryy.getValue()) {
                    indexx++;
                }
            }
        }

        int selected_Index;
        do {
            Scanner location_Selection = new Scanner(System.in);
            selected_Index = 1;
        } while (selected_Index > combined_List.size());

        String selected_Loc = combined_List.get(selected_Index - 1);
        locationElement_Map.get(selected_Loc).click();
        if (find_Suggestion) {
            fetch_Pickup_Locations(driverr, suggestion_Id);
        } else {
        }
        return selected_Loc;
    }

    public static void resolve_Date(String pickup_Date, String return_Date) {
        WebElement input_PickUpDate_Field = driverr.findElement(By.id("from"));
        WebElement input_ReturnDate_Field = driverr.findElement(By.id("to"));
        input_PickUpDate_Field.clear();
        input_ReturnDate_Field.clear();
        input_PickUpDate_Field.sendKeys(pickup_Date);
        input_ReturnDate_Field.sendKeys(return_Date);
    }

    private static void scroll_DownTo_LoadAll_Elements(WebDriver driverr) {
        JavascriptExecutor js_Executor = (JavascriptExecutor) driverr;
        int current_Height, new_Height = 0;

        do {
            current_Height = new_Height;
            js_Executor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new_Height = Integer.parseInt(js_Executor.executeScript("return document.body.scrollHeight").toString());
        } while (new_Height > current_Height);
    }
    static WebDriverWait waiting;
    public static Hashtable<String, String> fetch_Car_Deals() throws InterruptedException {

        Thread.sleep(Duration.ofSeconds(30).toMillis());
        driverr.findElement(By.id("res-home-select-car")).click();

        if (check_For_Warning()) {
            return null;
        }

        check_For_PopUp();

        scroll_DownTo_LoadAll_Elements(driverr);

        String contentt = driverr.getPageSource();

        String folder_List[] = {"AvisHtml", "BudgetHtml"};
        File webPage_Folder = new File("AvisHtml");
        int file_Counter = 1;
        if (webPage_Folder.exists()){
            file_Counter = webPage_Folder.listFiles().length;
        }

        urlMap.putAll(WebCrawler.createF_ile(budget_Url, contentt, "budget_deals", "BudgetFiles/"));

        System.out.println("Budget Car deals extracted and add to budget files folder");
        return urlMap;
    }

    private static boolean check_For_Warning() {
        try {
            WebElement elementt = waitt.until(ExpectedConditions.visibilityOfElementLocated(By.id("warning-msg-err")));
            if (elementt.isDisplayed()) {
                System.out.println(elementt.getText());
                driverr.get(budget_Url);


                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static void resolve_Time(String pickup_Time, String return_Time) {
        WebElement select_PickUp_TimeElement = driverr.findElement(By.xpath("//select[@ng-model='vm.reservationModel.pickUpTime']"));
        Select select_Pickup_Time = new Select(select_PickUp_TimeElement);
        String formatted_Pickup_Time = String.format("string:%s", pickup_Time);
        select_Pickup_Time.selectByValue(formatted_Pickup_Time);

        WebElement select_DropOff_TimeElement = driverr.findElement(By.xpath("//select[@ng-model='vm.reservationModel.dropTime']"));
        Select selectDropOffTime = new Select(select_DropOff_TimeElement);
        String formattedDropOffTime = String.format("string:%s", return_Time);
        selectDropOffTime.selectByValue(formattedDropOffTime);
    }

    public static void reset_Driver(){
        driverr.get(budget_Url);
    }

    public static void close_Driver() {
        driverr.quit();
    }
}
