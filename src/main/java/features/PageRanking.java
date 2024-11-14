package features;

import webcrawling.AvisCanadaCrawl;
import webcrawling.BudgetCanadaCrawl;
import webcrawling.CarRentalWebCrawl;

import java.util.*;

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

    

    public static void show_Ranking(String key_word){
        B_Tree b_Tree = InvertedIndexing.index_Documents_InFolder(new String[]{"AvisFiles/","BudgetFiles/","CarRentalFiles/"});

        Map<String, Integer> document_Frequencies = b_Tree.searchh(key_word);

        if (!document_Frequencies.containsKey("avis_deals.html")){
            document_Frequencies.put("avis_deals.html",0);
        }if (!document_Frequencies.containsKey("budget_deals.html")){
            document_Frequencies.put("budget_deals.html",0);
        }if (!document_Frequencies.containsKey("orbitz_deals.html")){
            document_Frequencies.put("orbitz_deals.html",0);
        }

        // Create a PageRank object
        PageRanking page_Rank = new PageRanking();

        // Calculate PageRank based on the document frequencies
        page_Rank.calculate_Page_Rank(document_Frequencies);

        System.out.println("Ranking of website for the selected Car Model:\n");
        // Get and display ranked pages
        List<Map.Entry<String, Integer>> ranked_Pages = page_Rank.get_Ranked_Pages();
        int countt = 1;
        for (Map.Entry<String, Integer> entryy : ranked_Pages) {
           
       
            String web_site = entryy.getKey();
            if (entryy.getKey().contains("avis")){
                web_site = AvisCanadaCrawl.avis_Url;
            } else if (entryy.getKey().contains("budget")) {
                web_site = BudgetCanadaCrawl.budget_Url;
            } else if (entryy.getKey().contains("orbitz")) {
                web_site = CarRentalWebCrawl.car_rental_Url;
            }
            System.out.println(countt+". "+web_site);
            countt++;
        }
    }
    public static void main(String[] args) {
    	 B_Tree bTree = InvertedIndexing.index_Documents_InFolder(new String[]{"AvisFiles","BudgetFiles","OrbitzFiles"});

        Map<String, Integer> documentFrequencies = bTree.searchh("kia");

        // Create a PageRank object
        PageRanking pageRank = new PageRanking();

        // Calculate PageRank based on the document frequencies
        pageRank.calculate_Page_Rank(documentFrequencies);

        // Get and display ranked pages
        List<Map.Entry<String, Integer>> rankedPages = pageRank.get_Ranked_Pages();
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

}