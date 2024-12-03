package org.harshpatel.drivefetch;

import Model.CarInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import features.*;
import org.openqa.selenium.chrome.ChromeDriver;
import webcrawling.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static webcrawling.orbitzCrawl.convertToISOFormat;

public class Entry {

    // Initialize global scanner for user input
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws UnsupportedEncodingException {
        while (true) {
            // Clear screen (optional, depends on the terminal being used)
            System.out.println("\n\n");

            // Welcome Header
            System.out.println("-------------------------------------------------");
            System.out.println("    Welcome to the Drive Fetch Application       ");
            System.out.println("-------------------------------------------------");

            // Menu Options with improved spacing and formatting
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Perform Crawling");
            System.out.println("2. Perform Filtering (Parsing)");
            System.out.println("3. Exit");
            System.out.println("-------------------------------------------------");

            // Read user input
            int choice = scanner.nextInt();

            // Handle different menu options
            switch (choice) {
                case 1:
                    // Perform Web Crawling
                    System.out.println("\nStarting the crawling process...");
                    performCrawling();  // Start web crawling for car rental sites
                    break;
                case 2:
                    // Perform Filtering (Parsing)
                    System.out.println("\nFetching car details and filtering deals...");
                    List<CarInfo> carInfoList = getAllCarDetails(); // Get car details
                    filter_Car_Deals(carInfoList);  // Filter car deals based on user input
                    break;
                case 3:
                    // Exit the program
                    System.out.println("\nExiting program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    // Handle invalid choices
                    System.out.println("\nInvalid choice. Please select again.");
                    break;
            }
        }
    }




    public static List<CarInfo> getAllCarDetails() {
        List<CarInfo> listOfCars = new ArrayList<>();

        // Read data from individual JSON files and add to the list
        readCrawledFile(listOfCars, "Web_Crawl_CaascoTravelData.json");
        readCrawledFile(listOfCars, "Web_Crawl_Orbitz.json");
        readCrawledFile(listOfCars, "Web_Crawl_CarrentalData.json");
        readCrawledFile(listOfCars, "ZoomRentalData.json");
        readCrawledFile(listOfCars, "CostcoTravelsData.json");

       // readCrawledFile(listOfCars, "JsonData/CarRentalsIsData.json");

        // Save the combined data to All.json
        save_CarInfo_To_Json(listOfCars, "All");

        return listOfCars;
    }


    private static void readCrawledFile(List<CarInfo> listOfCars, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        try {
            // Read car info array from the JSON file and add to the list
            CarInfo[] cars = objectMapper.readValue(file, CarInfo[].class);
            listOfCars.addAll(Arrays.asList(cars));
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void fetch_Car_Analysis(List<CarInfo> CarInfo_List) {
        // Get the frequency count map from the FrequencyCount class
        Map<String, Integer> frequencyMap = FrequencyCount.get_Frequency_Count("D:\\Project\\DriveFetch\\JsonData\\All.json");

        // Check if the frequency map is empty
        if (frequencyMap.isEmpty()) {
            System.out.println("No car data found.");
            return;
        }

        // Sort the map by value (frequency) in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Print the sorted car analysis
        System.out.println("\n-------------------- Car Analysis --------------------");
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            System.out.println(String.format("Car: %-20s | Total Available: %-5d", entry.getKey(), entry.getValue()));
        }
        System.out.println("-------------------------------------------------------");
    }


    private static void filter_Car_Deals(List<CarInfo> CarInfo_List) {
        System.out.println("");
        System.out.println("\n*     CAR DEALS FILTER MENU      *");
        System.out.println("-----------------------------------------");

        String refine_Selection;
        do {
            System.out.println("Do you want to filter the car deals? (y/n): ");
            refine_Selection = scanner.next().toLowerCase();
        }
        while (!inputValidator.isValidYesNoResponse(refine_Selection));

        List<CarInfo> process_Filter = new ArrayList<>();
        while (refine_Selection.equals("y")) {
            process_Filter = CarInfo_List;
            process_Filter.sort(Comparator.comparingDouble(CarInfo::getPrice));
            String option = "1";

            boolean validInput = false;

            while (!validInput) {
                try {
                    do {
                        System.out.println("\nSelect an appropriate option to filter the deals:\n1. Display deals by price (LOW - HIGH)\n2. Filter by Car Name\n3. Filter by Car Price\n4. Filter by Transmission Type\n5. Sort by Passenger Capacity \n6. Show Car Count Analysis\n7. Exit");
                        option = scanner.next();
                    } while (!inputValidator.isValidInteger(Integer.parseInt(option)));
                    validInput = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Please enter a valid response.");
                }
            }
            switch (Integer.parseInt(option)) {
                case 1:
                    display_Car_List(process_Filter);
                    break;
                case 2:
                    System.out.println("The available Car Companies:");
                    List<String> car_List = CarInfo_List.stream()
                            .map(ele -> ele.getName().split(" ")[0])
                            .filter(name -> name != null && !name.isEmpty())
                            .collect(Collectors.toList());

                    System.out.println(new HashSet<>(car_List));

                    try {
                        SpellChecking.initialize_Dictionary("JsonData/All.json");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    boolean check;
                    String preferred_Car_Name;
                    do {
                        System.out.print("\nEnter your preferred Car Company from above list: ");
                        preferred_Car_Name = scanner.next().toLowerCase();

                        check = SpellChecking.check_Spelling(preferred_Car_Name);
                        if (!check) {
                            System.out.println("No such Car exists. Please try again any other from the given list...");
                        }
                    } while (!check);

                    SearchFrequency.incrementSearchFrequency(preferred_Car_Name);

                    List<String> most_Searched_Cars = SearchFrequency.displayMostSearchedCars(car_List);
                    System.out.println();
                    if (!most_Searched_Cars.isEmpty()) {
                        System.out.println("\n--- Most Searched Cars ---");
                        for (String car : most_Searched_Cars) {
                            System.out.println(car);
                        }
                    }
                    process_Filter = filterBy_CarName(CarInfo_List, preferred_Car_Name);
                    display_Car_List(process_Filter);

                    String s;
                    do {
                        System.out.print("\nDo you want to see Page Rank of websites for the given Car Model (y/n): ");
                        s = scanner.next();
                    } while (!inputValidator.isValidYesNoResponse(s));

                    if (s.equalsIgnoreCase("y")) {
                        try {
                            PageRanking.show_Ranking(preferred_Car_Name);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case 3:
                    String preferred_Price_Range;

                    do {
                        System.out.print("\nEnter preferred price range (e.g., 50-200): ");
                        preferred_Price_Range = scanner.next().toLowerCase();
                    } while (!inputValidator.isValidRangeInput(preferred_Price_Range));

                    process_Filter = filterByPriceRange(CarInfo_List, preferred_Price_Range);
                    display_Car_List(process_Filter);
                    break;
                case 4:

                    String preferredTransmission;
                    do {
                        System.out.print("\nEnter preferred transmission type (A or M): ");
                        preferredTransmission = scanner.next().toUpperCase();
                    } while (!inputValidator.isValidTransmissionType(preferredTransmission));

                    if (preferredTransmission.equalsIgnoreCase("A")) {
                        process_Filter = filterBy_Transmission(CarInfo_List, "Automatic");
                    } else if (preferredTransmission.equalsIgnoreCase("M")) {
                        process_Filter = filterBy_Transmission(CarInfo_List, "Manual");
                    } else {
                        process_Filter = CarInfo_List;  // Retain the original list if no match
                    }

                    display_Car_List(process_Filter);

                    break;
                case 5:
                    int preferredPassengerCapacity;
                    do {
                        System.out.println("Enter preferred passenger capacity: ");
                        preferredPassengerCapacity = scanner.nextInt();

                    } while (!inputValidator.isValidInteger(preferredPassengerCapacity));
                    process_Filter = filterBy_Passenger_Capacity(CarInfo_List, preferredPassengerCapacity);
                    display_Car_List(process_Filter);

                    break;
               /* case 6:
                    int preferredLuggageCapacity;
                    do {
                      //  System.out.println("Enter preferred luggage capacity: ");
                        preferredLuggageCapacity = scanner.nextInt();
                    }
                    while (!inputValidator.isValidInteger(preferredLuggageCapacity));
                  //  process_Filter = filterBy_Luggage_Capacity(CarInfo_List, preferredLuggageCapacity);
                    display_Car_List(process_Filter);
                    break;*/
                case 6:
                    fetch_Car_Analysis(CarInfo_List);

                    break;
                case 7:
                    refine_Selection = "no";
                    break;
                default:
                    System.out.println("Invalid option. Please enter a valid option.");
            }
        }
    }

    private static List<CarInfo> filterByPriceRange(List<CarInfo> CarInfo_List, String preferred_Price_Range) {
        String[] priceRange = preferred_Price_Range.split("-");

        if (priceRange.length != 2) {
            throw new IllegalArgumentException("Invalid price range format");
        }

        int minPrice = Math.min(Integer.parseInt(priceRange[0].trim()), Integer.parseInt(priceRange[1].trim()));
        int maxPrice = Math.max(Integer.parseInt(priceRange[0].trim()), Integer.parseInt(priceRange[1].trim()));

        return CarInfo_List.stream()
                .filter(car -> {
                    try {
                        double carPrice = car.getPrice();
                        return carPrice >= minPrice && carPrice <= maxPrice;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .collect(Collectors.toList());
    }

    private static int get_User_Selection(int maxOption) {
        System.out.print("Select an option (or Enter 0 to select all): ");
        while (!scanner.hasNextInt()) {
            System.out.println("\nInvalid input. Please enter a valid option.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static List<CarInfo> filterBy_CarName(List<CarInfo> CarInfo_List, String preferred_CarName) {
        try {
            SpellChecking.initialize_Dictionary("JsonData/All.json");
            WordCompletion.initialize_Dictionary_From_JsonFile("JsonData/All.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> suggestions = WordCompletion.get_Suggestions(preferred_CarName.toLowerCase());

        if (!suggestions.isEmpty()) {
            System.out.println("Suggestions:");

            for (int i = 0; i < suggestions.size(); i++) {
                System.out.println((i + 1) + ". " + suggestions.get(i));
            }

            int selectedOption = get_User_Selection(suggestions.size());

            if (selectedOption >= 1 && selectedOption <= suggestions.size()) {
                preferred_CarName = suggestions.get(selectedOption - 1);
            } else if (selectedOption == 0) {

            } else {
                System.out.println("Invalid selection. Using original input.");
            }
        }

        String finalPreferredCarName = preferred_CarName;
        return CarInfo_List.stream()
                .filter(car -> car.getName().equalsIgnoreCase(finalPreferredCarName) || car.getName().toLowerCase().contains(finalPreferredCarName))
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .collect(Collectors.toList());
    }

    private static List<CarInfo> filterBy_Transmission(List<CarInfo> CarInfo_List, String preferred_Transmission) {
        return CarInfo_List.stream()
                .filter(car -> car.getTransmissionType().equalsIgnoreCase(preferred_Transmission))
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .collect(Collectors.toList());
    }

    private static List<CarInfo> filterBy_Passenger_Capacity(List<CarInfo> CarInfo_List, int preferred_Passenger_Capacity) {

        Optional<CarInfo> maxPassengerCapacity = CarInfo_List.stream()
                .max(Comparator.comparingInt(CarInfo::getPassengerCapacity));

        if (preferred_Passenger_Capacity > maxPassengerCapacity.get().getPassengerCapacity()) {
            preferred_Passenger_Capacity = maxPassengerCapacity.get().getPassengerCapacity();
        }
        int finalPreferredPassengerCapacity = preferred_Passenger_Capacity;

        return CarInfo_List.stream()
                .filter(car -> car.getPassengerCapacity() >= finalPreferredPassengerCapacity)
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .collect(Collectors.toList());
    }



    private static void display_Car_List(List<CarInfo> CarInfo_List) {
        System.out.println("+-------------------------+----------------------------------------+-------------------+------------------------+------------------------+--------------------------+");
        System.out.println("|      Car Group          |          Car Model                     |    Rent Price     |   Passenger Capacity   |    Transmission Type    |     Rental Company      |");
        System.out.println("+-------------------------+----------------------------------------+-------------------+------------------------+------------------------+--------------------------+");

        for (CarInfo CarInfo : CarInfo_List) {
            System.out.printf("| %-23s | %-38s | $%-16.2f | %-22d | %-24s | %-22s |\n",
                    CarInfo.getCarGroup(),
                    CarInfo.getName(),
                    CarInfo.getPrice(),
                    CarInfo.getPassengerCapacity(),
                    CarInfo.getTransmissionType(),
                    CarInfo.getCarCompany());
        }

        System.out.println("+-------------------------+----------------------------------------+-------------------+------------------------+------------------------+--------------------------+");
    }


    private static void save_CarInfo_To_Json(List<CarInfo> CarInfoo_Llist, String file_name) {
        ObjectMapper obj_Mapper = new ObjectMapper();
        String dir_Path = "JsonData/";

        try {
            File diry = new File(dir_Path);

            if (!diry.exists()) {
                diry.mkdirs();
            }

            File filee = new File(diry, file_name + ".json");

            try {
                obj_Mapper.writeValue(filee, CarInfoo_Llist);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("deprecation")
    private static void performCrawling() throws UnsupportedEncodingException {
        Scanner scanner = new Scanner(System.in);

        String response;
        do {
            String same_Location_Response;
  /*          do {
                System.out.print("Are pickup and drop-off locations the same? (y/n): ");
                same_Location_Response = scanner.nextLine().toLowerCase();
            } while (!inputValidator.isValidYesNoResponse(same_Location_Response));
*/
            String pickup_Location;
            do {
                System.out.print("Enter pickup location: ");
                pickup_Location = scanner.nextLine();
            } while (!inputValidator.isValidCityName(pickup_Location));

            String pickup_Date;
            do {
                System.out.print("Enter pickup date (DD/MM/YYYY): ");
                pickup_Date = scanner.nextLine();
            } while (!inputValidator.validateDate(pickup_Date));

            String return_Date;
            do {
                System.out.print("Enter return date (DD/MM/YYYY): ");
                return_Date = scanner.nextLine();
            } while (!inputValidator.validateReturnDate(pickup_Date, return_Date));

            //CarRentalWebCrawl.resolve_Date(pickup_Date, return_Date);

            pickup_Date = convert_Date_Format(pickup_Date);
            return_Date = convert_Date_Format(return_Date);

            String pickup_Date_orbit = convertToISOFormat(pickup_Date,"MM/dd/yyyy");
            String return_Date_orbit = convertToISOFormat(return_Date,"MM/dd/yyyy");


            int duration = orbitzCrawl.calculateDurationISO(pickup_Date_orbit,return_Date_orbit);


            //orbitzCrawl.WebCrawlOrbitz(pickup_Date_orbit,return_Date_orbit,duration,pickup_Location);

           // AvisCanadaCrawl.resolve_Date(pickup_Date, return_Date);
         //   BudgetCanadaCrawl.resolve_Date(pickup_Date, return_Date);

            String pickup_Time;
            do {
                System.out.print("Enter pickup time (HH:MM AM/PM): ");
                pickup_Time = scanner.nextLine();

                if(!inputValidator.isValidTime((pickup_Time))) {
                    System.out.println("Invalid time format. Please use HH:MM format. Kindly try again.");
                    continue;
                }

                String[] split_Time = pickup_Time.split(":");
                int hourr = Integer.parseInt(split_Time[0]);
                int minutee = Integer.parseInt(split_Time[1].substring(0, 2));
                boolean isPM = split_Time[1].substring(2).trim().equalsIgnoreCase("PM");

                if (isPM) {
                    if (hourr != 12)
                        hourr += 12;
                }
                LocalTime Time = LocalTime.of(hourr, minutee, 0);

                LocalTime currentTime = LocalTime.now();

                Date date1 = new Date(pickup_Date);
                Date date2 = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                String datep = sdf.format(date1);
                String formattedDate = sdf.format(date2);

                String[] rt = datep.split("/");
                String[] rtp = formattedDate.split("/");

                if (rtp[0].compareTo(rt[0]) == 0) {
                    if (rtp[1].compareTo(rt[1]) == 0) {
                        if (Time.compareTo(currentTime) < 0) {
                            System.out.println("Enter TIME GREATER THAN CURRENT TIME as you gave present date ");
                            System.out.print("Enter pickup time (HH:MM AM/PM): ");
                            pickup_Time = scanner.nextLine();
                        }
                    }
                }

            }
            while (!inputValidator.isValidTime(pickup_Time));

            String return_Time;
            do {
                System.out.print("Enter return time (HH:MM AM/PM): ");
                return_Time = scanner.nextLine();

                if(!inputValidator.isValidTime((return_Time))) {
                    System.out.println("Invalid time format. Please use HH:MM format. Kindly try again.");
                    continue;
                }

                String[] split_Time = return_Time.split(":");
                int hourr = Integer.parseInt(split_Time[0]);
                int minutee = Integer.parseInt(split_Time[1].substring(0, 2));
                boolean isPM = split_Time[1].substring(2).trim().equalsIgnoreCase("PM");

                int time_InMinutes = hourr * 60 + minutee;
                if (isPM) {
                    if (hourr != 12)
                        time_InMinutes += 720;
                }
                String[] split_Time1 = pickup_Time.split(":");
                int hourr1 = Integer.parseInt(split_Time1[0]);
                int minutee1 = Integer.parseInt(split_Time1[1].substring(0, 2));
                boolean isPM1 = split_Time1[1].substring(2).trim().equalsIgnoreCase("PM");

                int time_InMinutes1 = hourr1 * 60 + minutee1;
                if (isPM1) {
                    if (hourr1 != 12)
                        time_InMinutes1 += 720;
                }

                String[] rt = pickup_Date.split("/");
                String[] rtp = return_Date.split("/");

                if (rtp[0].compareTo(rt[0]) == 0) {
                    if (rtp[1].compareTo(rt[1]) == 0) {
                        if (time_InMinutes < time_InMinutes1) {
                            System.out.println("Provide a time at least 3 hours after the pickup time, and it should be later than the pickup time.");
                            System.out.print("Enter return time (HH:MM AM/PM): ");
                            return_Time = scanner.nextLine();
                        }
                    }
                }

            } while (!inputValidator.isValidTime(return_Time));
            CarRentalWebCrawl.WebCrawlCarRentals(pickup_Date_orbit,return_Date_orbit,duration,pickup_Location);
            CaascoTravelCrawl.WebCrawlCaascoTravel(pickup_Date,return_Date,duration,pickup_Location);
            orbitzCrawl.WebCrawlOrbitz(pickup_Date_orbit,return_Date_orbit,duration,pickup_Location,pickup_Time,return_Time);
            ZoomRentalCrawl.WebCrawlZoomRentals(pickup_Date,return_Date,duration,pickup_Location,pickup_Time,return_Time);
           //  CostcoTravelCrawl.WebCrawlCaascoTravel(pickup_Date,return_Date,duration,pickup_Location);
            try {
              //  AvisCanadaCrawl.resolve_Time(pickup_Time, return_Time);
               // BudgetCanadaCrawl.resolve_Time(pickup_Time, return_Time);
                //CarRentalWebCrawl.resolve_Time(pickup_Time, return_Time);
              //  AvisCanadaCrawl.fetch_Car_Deals();
              //  BudgetCanadaCrawl.fetch_Car_Deals();

            } catch (Exception ex) {
                System.out.println("Exception caught: No cars available at that location. Please try again!");
            }

            System.out.print("Do you want to continue? (yes/no): ");
            response = scanner.nextLine();
            if (response.equalsIgnoreCase("yes")) {
               //AvisCanadaCrawl.reset_Driver();
               // BudgetCanadaCrawl.reset_Driver();
                //CarRentalWebCrawl.reset_Driver(driver);
            }
        } while (response.equalsIgnoreCase("yes"));

      //  AvisCanadaCrawl.close_Driver();
       // BudgetCanadaCrawl.close_Driver();
        // CarRentalWebCrawl.closeDriver();
    }

    public static String convert_Date_Format(String inputtDdate) {
        SimpleDateFormat original_Format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat target_Format = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date datee = original_Format.parse(inputtDdate);
            return target_Format.format(datee);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}