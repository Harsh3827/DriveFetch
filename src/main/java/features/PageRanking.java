package features;

import java.util.*;

public class PageRanking {
    private Map<String, Integer> pageScores; // Maps page name to its score
    private PriorityQueue<Map.Entry<String, Integer>> priorityQueue; // For ranking pages
    private BinaryTree invertedIndex; // The inverted index

    public PageRanking(BinaryTree invertedIndex) {
        pageScores = new HashMap<>();
        // Initialize priority queue with a comparator for sorting in descending order
        priorityQueue = new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue()));
        this.invertedIndex = invertedIndex;
    }

    // Get ranked pages
    public List<Map.Entry<String, Integer>> getRankedPages() {
        List<Map.Entry<String, Integer>> rankedPages = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            rankedPages.add(priorityQueue.poll());
        }
        return rankedPages;
    }

    // Calculate PageRank from the JSON data for the given keyword
    public void calculatePageRankFromJSON(String keyword) {
        // Retrieve documents (pages) from the inverted index
        Map<String, List<String>> termFrequencies = invertedIndex.search(keyword);

        if (termFrequencies != null) {
            for (Map.Entry<String, List<String>> entry : termFrequencies.entrySet()) {
                String document = entry.getKey(); // Document name
                int frequency = entry.getValue().size(); // Number of links for this document

                // Simple scoring: frequency + existing score
                int newScore = pageScores.getOrDefault(document, 0) + frequency;
                pageScores.put(document, newScore);
            }

            // Populate the priority queue
            priorityQueue.addAll(pageScores.entrySet());
        } else {
            System.out.println("No pages found for keyword: " + keyword);
        }
    }

    // Display the ranking of pages for a given keyword
    public static void show_Ranking(String keyword) {
        // Create and populate the inverted index
        BinaryTree invertedIndex = InvertedIndexing.indexDocumentsFromJSON();

        // Create a PageRanking object
        PageRanking pageRanking = new PageRanking(invertedIndex);

        // Calculate PageRank based on the JSON data and the provided keyword
        pageRanking.calculatePageRankFromJSON(keyword);

        // Display the ranked pages
        System.out.println("Ranking of websites for the selected keyword:\n");
        List<Map.Entry<String, Integer>> rankedPages = pageRanking.getRankedPages();
        int count = 1;
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(count + ". " + entry.getKey() + " (Score: " + entry.getValue() + ")");
            count++;
        }

        // Optionally, display associated links from the inverted index
        System.out.println("\nLinks for each ranked page:");
        Map<String, List<String>> termFrequencies = invertedIndex.search(keyword);
        if (termFrequencies != null) {
            for (Map.Entry<String, List<String>> entry : termFrequencies.entrySet()) {
                String document = entry.getKey();
                List<String> links = entry.getValue();
                System.out.println("Document: " + document + " -> Links: " + links);
            }
        }
    }
}
