package htmlparser;


import Model.CarInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AvisParser {
	

    
//peforming parse
    public static List<CarInfo> parse_Files() {
    	//  list
        List<CarInfo> combined_CarInfoo_Llist = new ArrayList<>();
 // folders
        String[] folderr_Ppaths = {"AvisFiles/"};

        for (String folder_Path : folderr_Ppaths) {
       
            File fr = new File(folder_Path);

       
            if (fr.isDirectory()) {
             
                File[] fss = fr.listFiles();

                if (fss != null) {
                    for (File fie : fss) {
//                 
                   
                        combined_CarInfoo_Llist.addAll(parse_CarRental_Website(fie.getAbsolutePath()));
//                      
                    }
                } else {
                    System.out.println("The folder is empty.");
                }
            } else {
                System.out.println("The specified path is not a directory.");
            }
        }

        return combined_CarInfoo_Llist;
    }
   // parsing car renatl website
    /*
     * input : taking file as input and forming parsing and add data to json file
     */
    private static List<CarInfo> parse_CarRental_Website(String filee_Ppath) {
    	
        List<CarInfo> CarInfoo_Llist = new ArrayList<>();

        String rental_Company = "";
      
        if (filee_Ppath.toLowerCase().contains("avis")) {
            rental_Company = "avis";
        } 

        try {
            // create a file
            File inpt = new File(filee_Ppath);
         
            // parsing the website
            Document doct = Jsoup.parse(inpt, "UTF-8");

          
            Elements car_Eles = doct.select(".step2dtl-avilablecar-section");
            

            for (Element car_Element : car_Eles) {
         

                String car_Name = car_Element.select("p.featurecartxt.similar-car").text().split(" or")[0];
                if (car_Name.contains("Mystery Car")) {
                    continue;
                }
            

                double car_Price = Double.parseDouble(car_Element.select("p.payamntp").text().replaceAll("[^0-9.]", ""));
              
                int passenger_Capacity = Integer.parseInt(fetch_Int(car_Element.select("span.four-seats-feat").first().text()));
               // System.out.println(passenger_Capacity);
                String car_Group = car_Element.select("h3[ng-bind='car.carGroup']").text();
              // System.out.println(car_Group);
                String transmission_Type = car_Element.select("span.four-automatic-feat").text();
                //System.out.println(transmission_Type);
                int large_Bag = Integer.parseInt(fetch_Int(car_Element.select("span.four-bags-feat").first().text()));
                //System.out.println(large_Bag);
                int small_Bag = Integer.parseInt(fetch_Int(car_Element.select("span.four-bags-feat-small").first().text()));
                //System.out.println(small_Bag);
                //System.out.println("------------");
                // create car info 
                CarInfo CarInfo = new CarInfo(car_Name, car_Price, passenger_Capacity, car_Group, transmission_Type, large_Bag, small_Bag, rental_Company);
                // add data
               // CarInfo.toString();
                CarInfoo_Llist.add(CarInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return CarInfoo_Llist;
    }

    public static String fetch_Int(String str) {
        Pattern pat = Pattern.compile("\\d+");
        Matcher ma = pat.matcher(str);
        if (ma.find()) {
            return ma.group();
        }
        return str;
    }


}