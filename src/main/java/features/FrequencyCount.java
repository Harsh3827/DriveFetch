package features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//frequncy count class
public class FrequencyCount {
 
	// main mtd
    public static void main(String[] args) {
    	// path name
        String path_Of_File = "JsonData/All.json";

        try {
        	// objectmapper
            ObjectMapper object_Mapper = new ObjectMapper();
            // creating a new file
            File filee = new File(path_Of_File);

            // Read the JSON file into a JsonNode
            JsonNode json_Node = object_Mapper.readTree(filee);

            // Process the JsonNode to get the frequency count
            Map<String, Integer> map_For_Frequency = get_Frequency_Count(path_Of_File);

            // Print the frequency count
            for (Map.Entry<String, Integer> entry_Of_Car : map_For_Frequency.entrySet()) {
                System.out.println("Name: " + entry_Of_Car.getKey() + ", Frequency: " + entry_Of_Car.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }
  // get frequncy count mtd
    public static Map<String, Integer> get_Frequency_Count(String file_Path) {

    	// calling object mapper
        ObjectMapper object_Mapper = new ObjectMapper();
        // creating aa new filr
        File filee = new File(file_Path);

        // Read the JSON file into a JsonNode
        JsonNode json_Node = null;
        // try and catch block
        try {
            json_Node = object_Mapper.readTree(filee);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // creating a hashmap
        Map<String, Integer> frequency_Map = new HashMap<>();

        // Iterate through the array in the JSON file
        Iterator<JsonNode> elementss = json_Node.elements();
        while (elementss.hasNext()) {
            JsonNode elementt = elementss.next();
            String name_Of_Car = elementt.get("name").asText();
            //updating the according to occurrence of car appearance on site
            frequency_Map.put(name_Of_Car, frequency_Map.getOrDefault(name_Of_Car, 0) + 1);
        }

        return frequency_Map;
    }
}