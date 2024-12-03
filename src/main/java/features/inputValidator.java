package features;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class inputValidator {

    private static final Pattern TRANSMISSION_PATTERN = Pattern.compile("^[AM]$");

    // Regular expressions for vehicle name, date, time, and city name
    static Pattern VEHICLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static Pattern RANGE_PATTERN = Pattern.compile("^\\d+-\\d+$");
    static Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    static Pattern TIME_PATTERN = Pattern.compile("^(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)$");
    static Pattern CITY_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    static Pattern INTEGER_PATTERN = Pattern.compile("-?\\d+");

    // Method to validate vehicle name
    public static boolean isValidVehicleName(String vehicleName) {
        boolean isValid = VEHICLE_NAME_PATTERN.matcher(vehicleName).matches();
        if (!isValid) {
            System.out.println("The vehicle name is invalid! Please enter a proper name. Apologies, try again.\n");
        }
        return isValid;
    }

    // Method to validate integer responses for loop cases
    public static boolean isValidInteger(int response) {
        String responseStr = String.valueOf(response);
        boolean isValid = INTEGER_PATTERN.matcher(responseStr).matches();

        if (!isValid) {
            System.out.println("Invalid response. Please try again.");
        }

        return isValid;
    }

    // Method to check if the given date is in the past
    static LocalDate currentDate;
    static LocalDate inputDate;

    public static boolean isPastDate(String dateStr) {
        try {
            String[] dateParts = dateStr.split("/");
            inputDate = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]));

            Date currentDateObj = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(currentDateObj);
            String[] currentDateParts = formattedDate.split("/");

            currentDate = LocalDate.of(Integer.parseInt(currentDateParts[2]), Integer.parseInt(currentDateParts[1]), Integer.parseInt(currentDateParts[0]));
        } catch (RuntimeException e) {
            System.out.println("RuntimeException occurred");
        }

        // Compare the dates
        int comparison = inputDate.compareTo(currentDate);
        return comparison < 0;
    }

    // Method to check if the given date is valid
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

        if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
            return false;
        }

        return true;
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // Method to validate date format and check if it's a valid future date
    public static boolean validateDate(String inputDate) {
        boolean isValid = DATE_PATTERN.matcher(inputDate).matches();

        if (!isValid) {
            System.out.println("Invalid date format. Please use dd/mm/yyyy format. Kindly try again.");
            return false;
        }

        if (!isValidDate(inputDate)) {
            System.out.println("The date provided is not valid. Please enter valid date.");
            return false;
        }

        if (isPastDate(inputDate)) {
            System.out.println("The date is from the past. Please enter valid date.");
            return false;
        }

        return true;
    }

    // Method to validate return date
    public static boolean validateReturnDate(String pickupDate, String returnDate) {
        boolean isValid = DATE_PATTERN.matcher(returnDate).matches();

        if (!isValid) {
            System.out.println("Invalid date format. Please use dd/mm/yyyy format. Kindly try again.");
            return false;
        }

        if (isPastDate(returnDate)) {
            System.out.println("The return date is from the past.");
            return false;
        }

        // Define the formatter for DD/MM/YYYY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse the dates
        LocalDate pickupDateCompare = LocalDate.parse(pickupDate, formatter);
        LocalDate returnDateCompare = LocalDate.parse(returnDate, formatter);

        // Compare dates
        if (returnDateCompare.isBefore(pickupDateCompare) || returnDateCompare.equals(pickupDateCompare)) {
            System.out.println("Return date should be after pickup date.");
            return false;
        }

        return true;
    }

    // Method to validate time
    public static boolean isValidTime(String time) {
        boolean isValid = TIME_PATTERN.matcher(time).matches();
//        if (!isValid) {
//            System.out.println("Invalid time format. Please use HH:MM format. Kindly try again.");
//        }
        return isValid;
    }

    // Method to validate city name
// Method to validate city name
    public static boolean isValidCityName(String cityName) {
        boolean isValid = CITY_NAME_PATTERN.matcher(cityName).matches();
        if (isValid) {
            try {
                // Initialize both SpellChecking and WordCompletion dictionaries
                SpellChecking.initialize_Dictionary("D:\\Project\\DriveFetch\\data\\dictionaryCheck.txt");
                WordCompletion.initializeFromTextFile("D:\\Project\\DriveFetch\\data\\dictionaryCheck.txt");

                // Check if the city name is valid via SpellChecking
                isValid = SpellChecking.check_Spelling(cityName);

                if (!isValid) {
                    // Attempt WordCompletion suggestions first
                    System.out.print("Did you mean ");
                    List<String> completionSuggestions = WordCompletion.get_Suggestions(cityName.toLowerCase());
                    Set<String> uniqueSuggestions = new HashSet<>(completionSuggestions);

                    if (!uniqueSuggestions.isEmpty()) {
                        // Build the WordCompletion result string
                        String completionResult = String.join(" / ", uniqueSuggestions);

                        // Print in bold
                        System.out.print("\u001B[1m" + completionResult + "\u001B[0m");
                        System.out.println("?\n");
                    } else {
                        // If no suggestions are found via WordCompletion, attempt SpellChecking suggestions
                        List<String> spellSuggestions = SpellChecking.get_Suggestions(cityName.toLowerCase(), false); // Updated
                        if (!spellSuggestions.isEmpty()) {
                            String bestSuggestion = spellSuggestions.get(0); // Get the best suggestion
                            System.out.println("" + bestSuggestion + "?");
                        } else {
                            // If no suggestions are found at all
                            System.out.println("No suggestions found, please type a new word.");
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println("Unable to initialize dictionary data.");
            }
        } else {
            System.out.println("Please enter a valid city name using letters only.");
        }
        return isValid;
    }

    // Method to validate user yes/no response
    public static boolean isValidYesNoResponse(String input) {
        if (input.length() == 1 && (input.charAt(0) == 'y' || input.charAt(0) == 'n')) {
            return true;
        } else {
            System.out.print("Invalid input. Please enter 'y' or 'n'.\n");
            return false;
        }
    }

    // Method to validate range input
    public static boolean isValidRangeInput(String input) {
        boolean isValid = RANGE_PATTERN.matcher(input).matches();
        if (!isValid) {
            System.out.println("Invalid range. Please try again.");
        }
        return isValid;
    }

    // Method to validate transmission type
    public static boolean isValidTransmissionType(String preferredTransmission) {
        boolean isValid = TRANSMISSION_PATTERN.matcher(preferredTransmission).matches();
        if (!isValid) {
            System.out.println("Invalid transmission type selected. Please try again.");
        }
        return isValid;
    }
}
