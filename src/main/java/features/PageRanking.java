package features;

import java.util.*;

public class PageRanking {
    private Map<String, Integer> pageScores;
    private PriorityQueue<Map.Entry<String, Integer>> priorityQueue;
    private BinaryTree invertedIndex; // The inverted index

    public PageRanking(BinaryTree invertedIndex) {
        pageScores = new HashMap<>();
        // Initialize priority queue with a comparator for sorting
        priorityQueue = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        this.invertedIndex = invertedIndex; // Pass the inverted index here
    }

    // get ranked pages
    public List<Map.Entry<String, Integer>> get_Ranked_Pages() {
        // Get and return the ranked pages
        List<Map.Entry<String, Integer>> ranked_Pages = new ArrayList<>();

        while (!priorityQueue.isEmpty()) {
            ranked_Pages.add(priorityQueue.poll());
        }

        return ranked_Pages;
    }

    public void calculate_Page_Rank_FromJSON(String keyword) {
//        // Populate the priority queue
//        priorityQueue.addAll(pageScores.entrySet());

        // Retrieve documents (pages) from the inverted index
        Map<String, Integer> termFrequencies = invertedIndex.search(keyword);

        if (termFrequencies != null) {
            for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
                String page = entry.getKey();
                int frequency = entry.getValue();

                // Exceptional Handling
                // Simple scoring: frequency + existing score
                int newScore = pageScores.getOrDefault(page, 0) + frequency;
                pageScores.put(page, newScore);
            }

            // Populate the priority queue
            priorityQueue.addAll(pageScores.entrySet());
        } else {
            System.out.println("No pages found for keyword: " + keyword);
        }
    }

    public static void show_Ranking(String keyword) {
        // Create and populate the inverted index
        BinaryTree invertedIndex = InvertedIndexing.indexDocumentsFromJSON();
        // Create a PageRanking object
        PageRanking pageRanking = new PageRanking(invertedIndex);

        // Calculate PageRank based on the JSON data and the provided keyword
        pageRanking.calculate_Page_Rank_FromJSON(keyword);

        // Display the ranked pages
        System.out.println("Ranking of websites for the selected car model:\n");
        List<Map.Entry<String, Integer>> rankedPages = pageRanking.get_Ranked_Pages();
        int count = 1;
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(count + ". " + entry.getKey() + " (Score: " + entry.getValue() + ")");
            count++;
        }
    }
}