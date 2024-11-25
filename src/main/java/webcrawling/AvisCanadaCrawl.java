
package webcrawling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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


public class AvisCanadaCrawl {
	
	static {
        // Suppress Selenium CDP version mismatch warnings
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    }


    public static void init_Driver() {
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.OFF);
        chrome_Options.addArguments("--headless");
        driverr = new ChromeDriver(chrome_Options);
        Actions actions = new Actions(driverr);
        wwait = new WebDriverWait(driverr, Duration.ofSeconds(30));
        driverr.get(avis_Url);

        // Check if the alert box appears, and close it if found
        try {
            WebElement alertBox = wwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bx-creative-2281357")));
            actions.moveToElement(alertBox).click().perform();

            // Decline the offer if the button is present
            WebElement declineOffer = wwait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='reset']")));
            declineOffer.click();
        } catch (Exception e) {
            System.out.println("No pop-up found, continuing...");
        }

        // Refresh the page after handling the pop-up
        driverr.navigate().refresh();
    }

    public static String avis_Url = "https://www.avis.ca/en/home";

    
    public static void checkFor_PopUp() {
        By popupButtonLocator = By.cssSelector(".bx-row.bx-row-submit button[data-click='close']");
        try {
            WebElement popupButton = wwait.until(ExpectedConditions.presenceOfElementLocated(popupButtonLocator));
            popupButton.click();
        } catch (Exception e) {
        	 System.out.println("popup not showing");
        }
    }

    public static ChromeOptions chrome_Options = new ChromeOptions();
    static WebDriver driverr;
    static WebDriverWait wwait;



    public static String user_Pickup_Loc = "";

    public static String resolve_Location(String pickup_Location, String picLoc_drop_down, String suggestion_Box) {
        checkFor_PopUp();
        user_Pickup_Loc = pickup_Location;
        WebElement input_PickUp_Field = driverr.findElement(By.id(picLoc_drop_down));
        input_PickUp_Field.clear();
        input_PickUp_Field.sendKeys(pickup_Location);
        return fetch_Pickup_Locations(driverr, suggestion_Box);
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
        HashMap<String, WebElement> location_Element_Map = new HashMap<>();

        for (WebElement child_Element : child_Elements) {
            WebElement div_Web_element = child_Element.findElement(By.xpath(".//div[contains(@class, 'angucomplete-selection-category')]"));
            String ppString = div_Web_element.findElement(By.tagName("p")).getText();
            if (ppString.contains("Airport Rental Locations") || ppString.contains("Neighborhood Rental Locations")) {
                String span_String = div_Web_element.findElement(By.tagName("span")).getText();
                String category_Name = ppString.replace(span_String, "").trim();
                List<String> div_List = new ArrayList<>();
                List<WebElement> divAll_LocIn_Category = child_Element.findElements(By.cssSelector("div.angucomplete-description"));
                for (WebElement divv : divAll_LocIn_Category) {
                    String loc_Name = divv.findElement(By.tagName("span")).getText();
                    if (find_Suggestion) {
                        loc_Name = divv.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
                    }
                    div_List.add(loc_Name);
                    location_Element_Map.put(loc_Name, divv);
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
                    location_Element_Map.put(loc_Name, divv);
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
                System.out.println("\n" + entryy.getKey());
                combined_List.addAll(entryy.getValue());
                for (String elementt : entryy.getValue()) {
                    System.out.println(indexx + ". " + elementt);
                    indexx++;
                }
            }
        }

        int selected_Index;
        do {
            System.out.println("\nPlease select any one locations from the suggested locations above: ");
            Scanner location_Selection = new Scanner(System.in);
            selected_Index = location_Selection.nextInt();
        } while (selected_Index > combined_List.size());

        String selected_Loc = combined_List.get(selected_Index - 1);
        location_Element_Map.get(selected_Loc).click();
        if (find_Suggestion) {
            fetch_Pickup_Locations(driverr, suggestion_Id);
        } else {
        }
        return selected_Loc;
    }

    public static void resolve_Date(String pickup_Date, String return_Date) {
        WebElement input_PickUp_Date_Field = driverr.findElement(By.id("from"));
        WebElement input_Return_Date_Field = driverr.findElement(By.id("to"));
        input_PickUp_Date_Field.clear();
        input_Return_Date_Field.clear();
        input_PickUp_Date_Field.sendKeys(pickup_Date);
        input_Return_Date_Field.sendKeys(return_Date);
    }

    private static void scrollDownToLoadAll_Elements(WebDriver driverr) {
        JavascriptExecutor js_Executor = (JavascriptExecutor) driverr;
        int current_Height,

                new_Height = 0;

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

    public static void fetch_Car_Deals() {
        WebDriverWait wait = new WebDriverWait(driverr, Duration.ofSeconds(10)); // wait up to 10 seconds
        WebElement carSelectButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("res-home-select-car")));
        carSelectButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (check_For_Warning()) {
            return;
        }

        checkFor_PopUp();

        scrollDownToLoadAll_Elements(driverr);

        String contentt = driverr.getPageSource();

        String folderListt[] = {"AvisHtml", "BudgetHtml"};
        File webPageFolderr = new File("AvisHtml");
        int file_Counter = 1;
        if (webPageFolderr.exists()){
            file_Counter = webPageFolderr.listFiles().length;
        }

        WebCrawler.createF_ile(avis_Url, contentt, "avis_deals", "AvisFiles/");

        System.out.println("Avis deals extracted and add to avis folder");
    }

    private static boolean check_For_Warning() {
        try {
            WebElement elementt = wwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("warning-msg-err")));
            if (elementt.isDisplayed()) {
                System.out.println(elementt.getText());
                driverr.get(avis_Url);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static void resolve_Time(String pickup_Time, String return_Time) {
        WebElement select_PickUp_Time_Element = driverr.findElement(By.xpath("//select[@ng-model='vm.reservationModel.pickUpTime']"));
        Select select_Pickup_Time = new Select(select_PickUp_Time_Element);
        String formatted_Pickup_Time = String.format("string:%s", pickup_Time);
        select_Pickup_Time.selectByValue(formatted_Pickup_Time);

        WebElement select_DropOffTime_Element = driverr.findElement(By.xpath("//select[@ng-model='vm.reservationModel.dropTime']"));
        Select select_DropOff_Time = new Select(select_DropOffTime_Element);
        String formatted_DropOff_Time = String.format("string:%s", return_Time);
        select_DropOff_Time.selectByValue(formatted_DropOff_Time);
    }

    public static void reset_Driver(){
        driverr.get(avis_Url);
    }

    public static void close_Driver() {
        driverr.quit();
    }
}
