package features;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import webcrawling.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

class JSONReader {
    public static List<Map<String, Object>> readJSON(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {});
    }
}

public class PageRanking {
    private Map<String, Integer> page_Scores;
    private PriorityQueue<Map.Entry<String, Integer>> priority_Queue;

    public PageRanking() {
        page_Scores = new HashMap<>();
        // Initialize priority queue with a comparator for sorting
        priority_Queue = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
    }

    public void calculate_Page_Rank(Map<String, Integer> document_Frequencies) {

        if (document_Frequencies != null) {
            // Simple ranking based on frequency (replace with more advanced ranking)
            page_Scores.putAll(document_Frequencies);

            // Populate the priority queue for ranking
            priority_Queue.addAll(page_Scores.entrySet());
        } else {
            System.out.println("Error: Document frequencies are null");
        }

    }
    // get ranked pages
    public List<Map.Entry<String, Integer>> get_Ranked_Pages() {
        // Get and return the ranked pages
        List<Map.Entry<String, Integer>> ranked_Pages = new ArrayList<>();

        while (!priority_Queue.isEmpty()) {
            ranked_Pages.add(priority_Queue.poll());
        }

        return ranked_Pages;
    }

    public void calculate_Page_Rank_FromJSON(List<Map<String, Object>> jsonData, String keyword) {
        for (Map<String, Object> car : jsonData) {
            String name = car.get("name").toString();
            String link = car.get("link").toString();

            // If the car name contains the keyword, add to scores
            if (name.toLowerCase().contains(keyword.toLowerCase())) {
                page_Scores.put(link, page_Scores.getOrDefault(link, 0) + 1);
            }
        }

        // Populate the priority queue
        priority_Queue.addAll(page_Scores.entrySet());
    }

    public static void show_Ranking(String keyword) {
        try {
            // Read JSON data from the file
            String filePath = "JsonData\\All.json"; // Path to the JSON file
            List<Map<String, Object>> jsonData = JSONReader.readJSON(filePath);

            // Create a PageRanking object
            PageRanking pageRanking = new PageRanking();

            // Calculate PageRank based on the JSON data and the provided keyword
            pageRanking.calculate_Page_Rank_FromJSON(jsonData, keyword);

            // Display the ranked pages
            System.out.println("Ranking of websites for the selected car model:\n");
            List<Map.Entry<String, Integer>> rankedPages = pageRanking.get_Ranked_Pages();
            int count = 1;
            for (Map.Entry<String, Integer> entry : rankedPages) {
                System.out.println(count + ". " + entry.getKey() + " (Score: " + entry.getValue() + ")");
                count++;
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Read JSON data
            String filePath = "JsonData\\All.json"; // Path to the JSON file
            List<Map<String, Object>> jsonData = JSONReader.readJSON(filePath);

            // Keyword for ranking
            String keyword = "Kia"; // Example keyword

            // Create PageRanking object
            PageRanking pageRanking = new PageRanking();

            // Rank pages based on the keyword
            pageRanking.calculate_Page_Rank_FromJSON(jsonData, keyword);

            // Get and display ranked pages
            List<Map.Entry<String, Integer>> rankedPages = pageRanking.get_Ranked_Pages();
            for (Map.Entry<String, Integer> entry : rankedPages) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
    }
}