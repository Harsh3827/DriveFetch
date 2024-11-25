package webcrawling;


import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;


public class Enterprice {

    //	Declaring WebDriver and Waiting
    static WebDriver driver;
    static WebDriverWait waiting;
    public static StringBuilder loclist = new StringBuilder();

    //	Object creations
    static pickingDate DP = new pickingDate();
    static TimePicker TP =new TimePicker();
    //static CarData CD = new CarData();
    //static utility utility = new utility();


    public static String[] demo= {"2024","11","18"};
    public static String[] demo1= {"2024","11","19"};

    public static void main(String[] args) throws Exception {
//			String[] pntdemo=
        websitepages("Windsor - Windsor Walker Road HLE",demo,"16:00",demo1,"16:00");
//		for(String df:pntdemo) {
//			System.out.println(df);
//		}
    }

    public void closebrowser() throws IOException {
        driver.close();
        driver.quit();

    }

    //	Method to Enter user details in the Web Form
    public static boolean websitepages(String pickuploc, String[] pickupdate, String pickuptime,
                                       String[] dropdate, String droptime ) throws Exception{
        boolean flag=true;
        boolean nextpage=true;

//		Calling the URL in browser
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--incognito");
//		opt.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        driver=new ChromeDriver(opt);
//		driver.manage().deleteAllCookies();
        driver.manage().window().maximize();

//		Launching the reservation page
        driver.get("https://www.hertz.com/rentacar/reservation");
        waiting = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(Duration.ofSeconds(20).toMillis());
//		Dealing with Cookies pop-up
        waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@class='cc-btn cc-allow cc-btn-format']")));
        driver.findElement(By.xpath("//*[@class='cc-btn cc-allow cc-btn-format']")).click();

        //Waiting till the next page is loaded
        waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//input[@id=\"pickup-location\"]")));

//		Entering pickup location
        WebElement Loc = driver.findElement(By.xpath("//input[@id=\"pickup-location\"]"));
        Loc.sendKeys(pickuploc);

        try {
            waiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='wL']")));
            WebElement dropdown = driver.findElement(By.xpath("//div[@class='ww-item'][1]"));
            if (dropdown.isDisplayed()) {
                dropdown.click();
                System.out.println("Dropdown found and clicked.");
            } else {
                flag = false; // Dropdown not found, set flag to false
                System.out.println("Dropdown not visible.");
            }
        } catch (Exception e) {
            flag = false; // Exception during dropdown check
            System.out.println("Dropdown not available for pickup location.");
        }


//		Setting pickup date and time
        WebElement PickupDate = driver.findElement(By.xpath("//*[@id='pickup-date-box']"));
        DP.PickDate(Integer.parseInt(pickupdate[0]),Integer.parseInt(pickupdate[1]),Integer.parseInt(pickupdate[2]),driver, PickupDate);
        WebElement PickupTime = driver.findElement(By.xpath("//*[@id='pickup-time']"));
        TP.PickTime(pickuptime,PickupTime);

        Thread.sleep(Duration.ofSeconds(2).toMillis());

//		Setting Drop date and  time
        WebElement DropDate = driver.findElement(By.xpath("//*[@id='dropoff-date-box']"));
        DP.PickDate(Integer.parseInt(dropdate[0]),Integer.parseInt(dropdate[1]),Integer.parseInt(dropdate[2]), driver, DropDate);
        WebElement DropTime = driver.findElement(By.xpath("//*[@id='dropoff-time']"));
        TP.PickTime(droptime,DropTime);
//		System.out.println("Drop Off time and date selected");

        Thread.sleep(Duration.ofSeconds(2).toMillis());

//		Selecting age if its visible
        if (flag) {
            try {
                WebElement ageOption = driver.findElement(By.xpath("(//*[@id='ageSelector']//option[@value='25'])[2]"));
                if (ageOption.isDisplayed()) {
                    ageOption.click();
                    System.out.println("Driver age selected.");
                }
            } catch (Exception e) {
                System.out.println("Driver age selection not available.");
            }
        }

//		Clicking on the Search Button
        driver.findElement(By.xpath("//*[@id='new-resSubmit']")).click();

        Thread.sleep(Duration.ofSeconds(3).toMillis());


//		Code for additional popup
        try {
            driver.findElement(By.xpath("//*[@id='ok-btn']")).click();
        } catch(Exception e) {}
        Thread.sleep(Duration.ofSeconds(3).toMillis());

        //If navigated to Location Tab
        if(!flag) {
            System.out.println("Inside");

            Thread.sleep(Duration.ofSeconds(5).toMillis());
//		//Clicking on Select reservation
            driver.findElement(By.xpath("//button[text()='Select for Reservation']")).click();
            Thread.sleep(Duration.ofSeconds(2).toMillis());

//		selecting age
            waiting.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='ageSelector']/option[@value='25']")));
            driver.findElement(By.xpath("//*[@id='ageSelector']/option[@value='25']")).click();
            Thread.sleep(Duration.ofSeconds(2).toMillis());
            driver.findElement(By.xpath("//button[@id='age-overlay-submit']")).click();
        }
        Thread.sleep(Duration.ofSeconds(10).toMillis());

        //Check if next page is displayed
        try {
            WebElement np= driver.findElement(By.xpath("//div[text()='Showing All Vehicle Results']"));
            nextpage=np.isDisplayed();
//		if(np.isDisplayed()) {
//			nextpage = false;
//		}
        }catch (Exception e) {}

        System.out.println(nextpage);
        return nextpage;
    }
}


//--------------------------------------------------------------------------------------------------------------------------
//Method to Enter user details in the Web Form
//public static void websitepages(String pickuploc, String[] pickupdate, String pickuptime,
//		String[] dropdate, String droptime ) throws Exception{
//
////	Calling the URL in browser
//	ChromeOptions opt = new ChromeOptions();
////	opt.addArguments("--incognito");
////	opt.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
//	driver=new ChromeDriver(opt);
////	driver.manage().deleteAllCookies();
//	driver.manage().window().maximize();
//	driver.get("https://www.hertz.com/rentacar/reservation");
//	waiting = new WebDriverWait(driver, Duration.ofSeconds(30));
//
////	Dealing with Cookies pop-up
//	waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath( "//*[@class=\"cc-btn cc-allow cc-btn-format\"]")));
//	driver.findElement(By.xpath("//*[@class=\"cc-btn cc-allow cc-btn-format\"]")).click();
//
////	Entering pickup location
//	WebElement Loc = driver.findElement(By.xpath("//input[@id=\"pickup-location\"]"));
//	Loc.sendKeys(pickuploc);
//	//New code
//	Loc.sendKeys(Keys.TAB);
//	Thread.sleep(Duration.ofSeconds(3).toMillis());
//
////	waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class=\"wL\"]")));
////	driver.findElement(By.xpath("//div[@class=\"ww-item\"][1]")).click();
////	pickuploc=Loc.getAttribute("value");
//
////	Setting pickup date and time
//	WebElement PickupDate = driver.findElement(By.xpath("//*[@id='pickup-date-box']"));
//	DP.PickDate(Integer.parseInt(pickupdate[0]),Integer.parseInt(pickupdate[1]),Integer.parseInt(pickupdate[2]),driver, PickupDate);
//	WebElement PickupTime = driver.findElement(By.xpath("//*[@id='pickup-time']"));
//	TP.PickTime(pickuptime,PickupTime);
////	System.out.println("Pickup time and date selected");
//
//	Thread.sleep(Duration.ofSeconds(2).toMillis());
//
////	Setting Drop date and  time
//	WebElement DropDate = driver.findElement(By.xpath("//*[@id='dropoff-date-box']"));
//	DP.PickDate(Integer.parseInt(dropdate[0]),Integer.parseInt(dropdate[1]),Integer.parseInt(dropdate[2]), driver, DropDate);
//	WebElement DropTime = driver.findElement(By.xpath("//*[@id='dropoff-time']"));
//	TP.PickTime(droptime,DropTime);
////	System.out.println("Drop Off time and date selected");
//
//	Thread.sleep(Duration.ofSeconds(2).toMillis());
//
////	Selecting Age of Driver
////	try {
////	driver.findElement(By.xpath("(//*[@id='ageSelector']//option[@value='25'])[2]")).click();
////	} catch (Exception e) {}
//
////	Calling method to capture screenshot of the web page
////	utility.captureScreenshot("Homepage.png",driver);
//
////	Clicking on the Search Button
//	driver.findElement(By.xpath("//*[@id='new-resSubmit']")).click();
//
//	Thread.sleep(Duration.ofSeconds(3).toMillis());
//
////	Code for additional popup
//	try {
//		driver.findElement(By.xpath("//*[@id='ok-btn']")).click();
//	} catch(Exception e) {
////		System.out.println("Clicked on pop-up");
//	}
//
////	click on Select Reservation button
//	try {
////		driver.findElement(By.xpath("//*[@class='loc-select-btn-second loc-link primary']")).click();
//		driver.findElement(By.xpath("//button[text()='Select for Reservation']")).click();
//	} catch (Exception e) {}
//
//	Thread.sleep(Duration.ofSeconds(3).toMillis());
//
////	Code for additional popup
//	try {
//		driver.findElement(By.xpath("//*[@id='ok-btn']")).click();
//	} catch(Exception e) {}
//
//	Thread.sleep(Duration.ofSeconds(3).toMillis());
//
//	//Selecting the Age in the Pop-up
//	waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id='ageSelector']/option[@value='25']")));
//	try {
//		driver.findElement(By.xpath("//*[@id='ageSelector']/option[@value='25']")).click();
//		Thread.sleep(Duration.ofSeconds(2).toMillis());
//		driver.findElement(By.xpath("//button[@id='age-overlay-submit']")).click();
//		System.out.println("Here");
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//
//	Thread.sleep(Duration.ofSeconds(10).toMillis());
//	waiting.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.xpath("//*[@class='gtm-vehicle']"))));
//
////	Calling method to capture screenshot of the web page
////	utility.captureScreenshot("CarList.png",driver);
//
//	return loclist.toString().split(",");
//}
