package htmlparser;


import Model.CarInfo;
import Model.CarInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// html parser for budget
public class BudgetParser {
	// main method using for only testing i removed it increase code performance and readability

   
// calling parsefiles
    public static List<CarInfo> parse_Files() {
    	// creating a list
        List<CarInfo> combined_CarInfo_List = new ArrayList<>();
 // creating folders
        String[] folder_Paths = { "BudgetFiles/"};

        for (String folder_Path : folder_Paths) {
       
            File folderr = new File(folder_Path);

       
            if (folderr.isDirectory()) {
             
                File[] filess = folderr.listFiles();

                if (filess != null) {
                    for (File filee : filess) {
//                 
                   
                        combined_CarInfo_List.addAll(parse_CarRental_Website(filee.getAbsolutePath()));

                    }
                } else {
                    System.out.println("The folder is empty.");
                }
            } else {
                System.out.println("The specified path is not a directory.");
            }
        }

        return combined_CarInfo_List;
    }
   // parsing car renatl website
    private static List<CarInfo> parse_CarRental_Website(String filee_Ppath) {
    	
        List<CarInfo> CCarInfo_Llist = new ArrayList<>();

        String rental_Company = "";
        // Parse local HTML file

       if (filee_Ppath.toLowerCase().contains("budget")) {
            rental_Company = "budget";
        }
        try {
        
            File inpt = new File(filee_Ppath);
        
        
            Document documentt = Jsoup.parse(inpt, "UTF-8");

           
            Elements car_Elements = documentt.select(".step2dtl-avilablecar-section");
            
            
            

            for (Element car_Element : car_Elements) {
         

                String car_Name = car_Element.select("p.featurecartxt.similar-car").text().split(" or")[0];
                if (car_Name.contains("Mystery Car")) {
                    continue;
                }
               // System.out.println(car_Element);
                //System.out.println(car_Name);

                double car_Price = Double.parseDouble(car_Element.select("p.payamntp").text().replaceAll("[^0-9.]", ""));
               //System.out.println(car_Price);
               String transmission_Type = car_Element.select("p[ng-if=car.automaticCaption]").first().text().split(" ")[0];
               //System.out.println(transmission_Type);
               String car_Group = car_Element.select("h3[ng-bind='car.carGroup']").text();
               //System.out.println(car_Group);
             
               
               //<span class="c-icon seat-icon"></span>  
               //<span class="c-icon bag-large"></span> 
                //<span class="c-icon small-large"></span> 
               String c=car_Element.select("div.tableDiv.vehicle-features").text();
               //System.out.println(c);
               String div[]=c.split(" ");
               
               
                 
                 
                int passenger_Capacity = Integer.parseInt(div[0]);
                		//fetch_Int(car_Element.select("span.c-icon seat-icon").first().text()));
                //System.out.println(passenger_Capacity);
//                String car_Group = car_Element.select("h3[ng-bind='car.carGroup']").text();
//               System.out.println(car_Group);
//                String transmission_Type = car_Element.select("p").text();
//                System.out.println(transmission_Type);
                int large_Bag =Integer.parseInt( div[1]);
                		//Integer.parseInt(fetch_Int(car_Element.select("span.four-bags-feat").first().text()));
                //System.out.println(large_Bag);
                int small_Bag = Integer.parseInt(div[2]);
                		//(car_Element.select("span.four-bags-feat-small").first().text()));
                //System.out.println(small_Bag);
                //System.out.println("------------");
                // create car info 
                CarInfo CarInfo = new CarInfo(car_Name, car_Price, passenger_Capacity, car_Group, transmission_Type, rental_Company,"None");
                // add data
                CarInfo.toString();
                CCarInfo_Llist.add(CarInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return CCarInfo_Llist;
    }

   


    

}
