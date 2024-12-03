package features;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

// Node of the Binary Tree
// Node of the Binary Tree
class BinaryTreeNode {
    String term;
    Map<String, List<String>> documentFrequency; // Maps document name to list of links
    BinaryTreeNode left;
    BinaryTreeNode right;

    BinaryTreeNode(String term) {
        this.term = term;
        this.documentFrequency = new HashMap<>();
        this.left = null;
        this.right = null;
    }
}

// Binary Tree for Inverted Index
class BinaryTree {
    private BinaryTreeNode root;

    public BinaryTree() {
        this.root = null;
    }

    // Insert a term into the binary tree
    public void insert(String term, String document, String link) {
        root = insertRecursively(root, term, document, link);
    }

    private BinaryTreeNode insertRecursively(BinaryTreeNode node, String term, String document, String link) {
        if (node == null) {
            BinaryTreeNode newNode = new BinaryTreeNode(term);
            newNode.documentFrequency.put(document, new ArrayList<>(Collections.singletonList(link)));
            return newNode;
        }

        if (term.compareTo(node.term) < 0) {
            node.left = insertRecursively(node.left, term, document, link);
        } else if (term.compareTo(node.term) > 0) {
            node.right = insertRecursively(node.right, term, document, link);
        } else {
            // Term already exists, update document frequency
            List<String> links = node.documentFrequency.getOrDefault(document, new ArrayList<>());
            if (!links.contains(link)) {
                links.add(link);
            }
            node.documentFrequency.put(document, links);
        }

        return node;
    }

    // Search for a term and return its document frequency map
    public Map<String, List<String>> search(String term) {
        return searchRecursively(root, term);
    }

    private Map<String, List<String>> searchRecursively(BinaryTreeNode node, String term) {
        if (node == null) {
            return null;
        }

        if (term.equals(node.term)) {
            return node.documentFrequency;
        }

        if (term.compareTo(node.term) < 0) {
            return searchRecursively(node.left, term);
        } else {
            return searchRecursively(node.right, term);
        }
    }

    // Print the binary tree in-order (for debugging)
    public void printTree() {
        printTreeRecursively(root);
    }

    private void printTreeRecursively(BinaryTreeNode node) {
        if (node != null) {
            printTreeRecursively(node.left);
            System.out.println(node.term + " -> " + node.documentFrequency);
            printTreeRecursively(node.right);
        }
    }
}

public class InvertedIndexing {
    private static List<Map<String, Object>> readJSON(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static BinaryTree indexDocumentsFromJSON() {
        BinaryTree binaryTree = new BinaryTree();
        try {
            List<Map<String, Object>> jsonData = readJSON("JsonData\\All.json");

            for (Map<String, Object> entry : jsonData) {
                String documentName = entry.get("name").toString(); // Assuming "name" as the document identifier
                String documentLink = entry.get("link").toString();
                String[] tokens = documentName.split("\\s+");

                for (String token : tokens) {
                    binaryTree.insert(token.toLowerCase(), documentName, documentLink);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }

        return binaryTree;
    }

    public static void main(String[] args) {
        BinaryTree binaryTree = indexDocumentsFromJSON();

        // Search example
        String searchTerm = "kia"; // Example term to search
        Map<String, List<String>> result = binaryTree.search(searchTerm);

        if (result != null) {
            System.out.println("Search Results for '" + searchTerm + "': " + result);
        } else {
            System.out.println("Term '" + searchTerm + "' not found in the index.");
        }

        // Optional: Print entire binary tree for verification
        System.out.println("\nInverted Index:");
        binaryTree.printTree();
    }
}

