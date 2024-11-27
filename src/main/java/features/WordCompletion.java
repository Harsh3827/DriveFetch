package features;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// Helper class for Trie Node
class TrieNode {
    TrieNode[] children;  // Array to hold child nodes for ASCII characters
    boolean isWordEnd;    // Flag to indicate if this is the end of a word

    public TrieNode() {
        children = new TrieNode[128]; // Supports ASCII characters
        isWordEnd = false;
    }
}

// Main Trie class with functionality
class Trie {
    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    // Insert a word into the Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            int index = ch;
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isWordEnd = true;
    }

    // Retrieve suggestions for a given prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode node = findNode(prefix);
        List<String> suggestions = new ArrayList<>();

        if (node != null) {
            getAllWords(node, prefix, suggestions);
        }

        // Sort suggestions by Levenshtein distance
        suggestions.sort(Comparator.comparingInt(suggestion -> calculateEditDistance(prefix, suggestion)));
        return suggestions;
    }

    // Find the node corresponding to the given prefix
    private TrieNode findNode(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            int index = ch;
            if (node.children[index] == null) {
                return null;
            }
            node = node.children[index];
        }
        return node;
    }

    // Helper to collect all words from a given node
    private void getAllWords(TrieNode node, String currentPrefix, List<String> suggestions) {
        if (node.isWordEnd) {
            suggestions.add(currentPrefix);
        }
        for (int i = 0; i < node.children.length; i++) {
            if (node.children[i] != null) {
                char ch = (char) i;
                getAllWords(node.children[i], currentPrefix + ch, suggestions);
            }
        }
    }

    // Calculate Levenshtein distance between two words
    private int calculateEditDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }
}

// WordCompletion class to handle dictionary initialization and suggestions
public class WordCompletion {
    private static Trie trie;

    // Initialize dictionary from a JSON file
    public static void initialize_Dictionary_From_JsonFile(String filename) {
        trie = new Trie();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonArray = (ArrayNode) objectMapper.readTree(new File(filename));
            for (int i = 0; i < jsonArray.size(); i++) {
                trie.insert(jsonArray.get(i).get("name").asText().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize dictionary from a text file
    public static void initializeFromTextFile(String filePath) throws IOException {
        trie = new Trie();
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        while ((line = reader.readLine()) != null) {
            for (String word : line.split("\\W+")) {
                if (!word.isEmpty()) {
                    trie.insert(word.toLowerCase());
                }
            }
        }
        reader.close();
    }

    // Get suggestions for a given prefix
    public static List<String> get_Suggestions(String prefix) {
        return trie.getSuggestions(prefix);
    }
}
