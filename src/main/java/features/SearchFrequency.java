package features;

import java.util.*;

public class SearchFrequency {
    // TreeMap to maintain the frequency of each car name
    private static final TreeMap<String, Integer> searchFrequencyMap = new TreeMap<>();

    // Increase the frequency count for a given car name
    public static void incrementSearchFrequency(String carName) {
        carName = carName.toLowerCase();
        searchFrequencyMap.put(carName, searchFrequencyMap.getOrDefault(carName, 0) + 1);
    }


    // Update frequency based on the car list
    public static void updateSearchFrequency(List<String> carList) {
        for (String car : carList) {
            incrementSearchFrequency(car);
        }
    }

    // Get a sorted list of cars based on search frequency (descending order)
    private static List<Map.Entry<String, Integer>> getSortedCarListByFrequency() {
        List<Map.Entry<String, Integer>> sortedCarList = new ArrayList<>(searchFrequencyMap.entrySet());
        sortedCarList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));  // Sort in descending order of frequency
        return sortedCarList;
    }

    // Display the most searched cars with their frequencies and return the sorted list
    public static List<String> displayMostSearchedCars(List<String> carList) {
        // Update the frequency map with the provided car list
        updateSearchFrequency(carList);

        // Get sorted list by frequency
        List<Map.Entry<String, Integer>> sortedCarList = getSortedCarListByFrequency();

        // Display each car with its search frequency
        sortedCarList.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Return the car names sorted by frequency
        List<String> sortedCarNames = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedCarList) {
            sortedCarNames.add(entry.getKey());
        }
        return sortedCarNames;
    }
}
