package features;

import java.util.*;

public class SearchFrequency 
{
    private static TreeMap<String, Integer> search_Frequency_Map = new TreeMap<>();

    // increase count by 1
    public static void increment_Search_Frequency(String car_Name) {
        search_Frequency_Map.put(car_Name, search_Frequency_Map.getOrDefault(car_Name, 0) + 1);
    }

    // Update frequncy based on carlist from entry
    public static void update_Search_Frequency(Set<String> car_List) {
        for (String carr : car_List) {
            increment_Search_Frequency(carr);
        }
    }

    // Sort carList by search frequncy
    public static List<String> sort_CarListBy_Frequency(Set<String> car_List) {
        List<String> sorted_CarList = new ArrayList<>(search_Frequency_Map.keySet());

        // Sort the list 
        sorted_CarList.sort(Comparator.comparingInt(search_Frequency_Map::get).reversed());

        return sorted_CarList;
    }

    // Display the widely searched cars
    public static List<String> display_MostSearched_Cars(Set<String> car_List) {
        List<String> sorted_CarList = sort_CarListBy_Frequency(search_Frequency_Map.keySet());
        for (Map.Entry<String, Integer> entry : search_Frequency_Map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
     
        return sorted_CarList;
    }
}
