package features;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

//  perform input validation for car rental information
public class DataValidation {

    private static final Pattern SINGLE_LETTER_PATTERN = Pattern.compile("^[AM]$");

    // Regular expressions for  car name, date, time, and city name
    
    static Pattern PATTERN_FOR_CAR_NAME = Pattern.compile("^[a-zA-Z0-9\\s]+$");
    private static  Pattern RANGE_PATTERN = Pattern.compile("^\\d+-\\d+$");
    static Pattern PATTERN_FOR_DATE = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$"); //date format update
    //static Pattern PATTERN_FOR_DATE = Pattern.compile("^(?:(?:0[1-9]|1\\d|2[0-8])\\s(0[1-9]|1[0-2])|(29|30)\\s(0[13-9]|1[0-2])|31\\s(0[13578]|1[02]))\\s\\d{4}$");

    static Pattern PATTERN_FOR_TIME = Pattern.compile("^(0?[1-9]|1[0-2])(:[0-5][0-9])?(\\s?[AP][M])?$");
    static Pattern PATTERN_NAME_FOR_CITY = Pattern.compile("^[a-zA-Z\\s]+$");
    static Pattern PATTERN_FOR_INTEGER = Pattern.compile("-?\\d+");

    //  to validate car name
    public static boolean validate_Car_Name(String name_Of_Car) {
        boolean check_ptrn = PATTERN_FOR_CAR_NAME.matcher(name_Of_Car).matches();
        if (!check_ptrn) {
            System.out.println("Car name os invalid! Enter proper car name.Apologies, please try again.\n");
        }
        return check_ptrn;
    }
// to validate integer for while loop cases
    public static boolean validate_Integer(int user_Response) {
        String response_String = String.valueOf(user_Response);
        boolean is_Valid = PATTERN_FOR_INTEGER.matcher(response_String).matches();
      
        if (!is_Valid) {
            System.out.println("Not a valid response. Please try again.");
        }

        return is_Valid;
    }

    // Method to validate date
    public static boolean is_Previous_Date(String input_Date) {
    	
   	 String[] rt=input_Date.toString().split("/");

   	 LocalDate date1 = LocalDate.of(Integer.parseInt(rt[2]), Integer.parseInt(rt[1]), Integer.parseInt(rt[0]));
   	
   	   Date date = new Date();
			

		    
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        String formattedDate = sdf.format(date);
	        String[] rtp=formattedDate.toString().split("/");

	        LocalDate date2 = LocalDate.of(Integer.parseInt(rtp[2]), Integer.parseInt(rtp[1]), Integer.parseInt(rtp[0]));
	    	
        // Compare the dates
        int comparison = date1.compareTo(date2);
        if(comparison<0)

            return true;
        else
        {
       	 return false;
        }
         
          
      } 
        
        
    
   
    //static Pattern PTRN_FOR_DATE = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$"); //date formate update
    
    public static boolean isValidDate(String dateStr) {
        String[] parts = dateStr.split("/");

        if (parts.length != 3) {
            return false;
        }

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) {
            return false;
        }

        if (day < 1 || day > 31) {
            return false;
        }

        if (month == 2 && day > 29) {
            return false;
        }

        if (!isLeapYear(year) && month == 2 && day == 29) {
            return false;
        }

        if ((day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) || day > 30) {
            return false;
        }

        return true;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    public static boolean validate_Date(String input_Date)
    {
    	boolean checkk_ptrn=false;
    	checkk_ptrn = PATTERN_FOR_DATE.matcher(input_Date).matches();
        
        if (!checkk_ptrn)
         {
                  System.out.println("date format is invalid. Please use the format dd/mm/yy. Kindly try again.");
              
         }
       String[] rt=input_Date.toString().split("/");
       
       if(!isValidDate(input_Date))
       {
    	   System.out.println("The date is a not valid");
           checkk_ptrn =false;
    	   
       }
    	
        if (is_Previous_Date(input_Date)) {
            System.out.println("The date is a previous date.");
            checkk_ptrn =false;
        } 
        
        System.out.println(checkk_ptrn);
             return checkk_ptrn;
        
      
    }
    
    public static boolean validate_returnDate(String pickup_Date,String return_Date)
    {
    	boolean checkk_ptrn=false;
    	 checkk_ptrn = PATTERN_FOR_DATE.matcher(return_Date).matches();
         if (!checkk_ptrn)
         {
                 System.out.println("date format is invalid. Please use the format dd/mm/yy. Kindly try again.");
                
         }
             
        if (is_Previous_Date(return_Date)) 
        {
            System.out.println("The date is a previous date.");
        }
        String[] rt=pickup_Date.toString().split("/");
        String[] rtp=return_Date.toString().split("/");
       
        
        if(rtp[1].compareTo(rt[1]) < 0)
        {
        	System.out.println("return month is previous months");
        	checkk_ptrn=false;
      	 
        	
        }
        if((rtp[1].compareTo(rt[1]) == 0) && (rtp[0].compareTo(rt[0]) < 0))
        {
        	System.out.println("return month is same but date is previous date");
        	checkk_ptrn=false;
        	 	
        }
        
       
             
       return checkk_ptrn;
    }
    

    // Method to validate time
    public static boolean validate_Time(String time) {
    	  
        boolean chk_ptrn = PATTERN_FOR_TIME.matcher(time).matches();
        if (!chk_ptrn) {
            System.out.println("Time format is invalid. Please use the format HH:MM. Kindly try again.");
        }
        return chk_ptrn;
    }

    // Method to validate city name
    // it first checks it is word or not then it checks with dictionary of words and gives similar word
    public static boolean validate_City_Name(String city_Name) {
        boolean check=false; 
       boolean str = PATTERN_NAME_FOR_CITY.matcher(city_Name).matches();
        if(str)
        {
        try 
        {
			SpellChecking.initialize_Dictionary("C:\\acc labs\\webscrapercarrental\\data\\dictionart.txt");
			 WordCompletion.initializeDictionary("C:\\acc labs\\webscrapercarrental\\data\\dictionart.txt");
			 check = SpellChecking.check_Spelling(city_Name);
			 //System.out.println(check);
		} catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(check==false)
        {
        	System.out.println("Suggestions: for u r word");
        List<String> suggestions = WordCompletion.get_Suggestions(city_Name.toLowerCase());
       

        if (!suggestions.isEmpty())
        {
            

            for (int i = 0; i < suggestions.size(); i++)
            {

                System.out.println((i + 1) + ". " + suggestions.get(i));
            }
        }
        System.out.println("please select one from above.");
        }
        return check;
        }
        System.out.println("please type word correctly with letters");
        return str;
    }

    // Method to validate user response
    public static boolean validate_User_Response(String input) {
        if (input.length() == 1 && (input.charAt(0) == 'y' || input.charAt(0) == 'n')) {
            return true;
        } else {
            System.out.print("Invalid input. Please enter 'y' or 'n'\n");
            return false;
        }
    }
  // validating given input is number or not
    public static boolean validate_Range_Input(String input) {
        boolean check = RANGE_PATTERN.matcher(input).matches();

        if (!check) {
            System.out.println("Not a valid range. Please try again.");
        }
        return check;
    }
 // validating transimmiosn types
    public static boolean validate_TTypes(String preferredTransmission)
    {
        boolean check = SINGLE_LETTER_PATTERN.matcher(preferredTransmission).matches();
        if (!check){
            System.out.println("Invalid Selected Type. Please try again.");
        }
        return check;
    }
}
