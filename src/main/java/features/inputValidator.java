package features;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class inputValidator {

    private static final Pattern TRANSMISSION_PATTERN = Pattern.compile("^[AM]$");

    // Regular expressions for vehicle name, date, time, and city name
    static Pattern VEHICLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static Pattern RANGE_PATTERN = Pattern.compile("^\\d+-\\d+$");
    static Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    static Pattern TIME_PATTERN = Pattern.compile("^(0?[1-9]|1[0-2])(:[0-5][0-9])?(\\s?[AP][M])?$");
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

        if ((day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) || day > 30) {
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
            System.out.println("The date provided is not valid.");
            return false;
        }

        if (isPastDate(inputDate)) {
            System.out.println("The date is from the past.");
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

        String[] pickupDateParts = pickupDate.split("/");
        String[] returnDateParts = returnDate.split("/");

        if (returnDateParts[1].compareTo(pickupDateParts[1]) < 0) {
            System.out.println("Return month cannot be before pickup month.");
            return false;
        }

        if (returnDateParts[1].equals(pickupDateParts[1]) && returnDateParts[0].compareTo(pickupDateParts[0]) < 0) {
            System.out.println("Return date cannot be before pickup date in the same month.");
            return false;
        }

        return true;
    }

    // Method to validate time
    public static boolean isValidTime(String time) {
        boolean isValid = TIME_PATTERN.matcher(time).matches();
        if (!isValid) {
            System.out.println("Invalid time format. Please use HH:MM format. Kindly try again.");
        }
        return isValid;
    }

    // Method to validate city name
    public static boolean isValidCityName(String cityName) {
        boolean isValid = CITY_NAME_PATTERN.matcher(cityName).matches();
        if (isValid) {
            try {
                SpellChecking.initialize_Dictionary("D:\\ACC\\DriveFetch\\data\\dictionaryCheck.txt");
                WordCompletion.initializeDictionary("D:\\ACC\\DriveFetch\\data\\dictionaryCheck.txt");
                isValid = SpellChecking.check_Spelling(cityName);

                if (!isValid) {
                    System.out.println("Suggestions: ");
                    List<String> suggestions = WordCompletion.get_Suggestions(cityName.toLowerCase());
                    Set<String> uniqueSuggestions = new HashSet<>(suggestions);

                    if (!uniqueSuggestions.isEmpty()) {
                        Iterator<String> iterator = uniqueSuggestions.iterator();
                        while (iterator.hasNext()) {
                            System.out.println(iterator.next());
                        }
                        System.out.println("Please select one from the suggestions.");
                    } else {
                        System.out.println("No suggestions found, please type a new word.");
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
