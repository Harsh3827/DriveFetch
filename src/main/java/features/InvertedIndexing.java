package features;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.io.IOException;
import java.util.*;

class B_Tree_Node {
    List<String> node_key;
    List<Map<String, Integer>> node_values;
    List<B_Tree_Node> node_children;

    B_Tree_Node() {
        node_key = new ArrayList<>();
        node_values = new ArrayList<>();
        node_children = new ArrayList<>();
    }
}

class B_Tree {
    private int tt;
    private B_Tree_Node roott;

    B_Tree(int t) {
        this.tt = t;
        this.roott = new B_Tree_Node();
    }

    public void ins_ert(String keyy, String documentt, int ffrequency) {
        B_Tree_Node root = this.roott;
        if (root.node_key.size() == (2 * tt) - 1) {
            B_Tree_Node new_Root = new B_Tree_Node();
            this.roott = new_Root;
            new_Root.node_children.add(root);
            split_Child(new_Root, 0);
            insert_Non_Full(new_Root, keyy, documentt, ffrequency);
        } else {
            insert_Non_Full(root, keyy, documentt, ffrequency);
        }
    }

    private void insert_Non_Full(B_Tree_Node npr, String kkey, String ddocument, int frequencyy) {
        int ip = npr.node_key.size() - 1;

        if (npr.node_children.isEmpty()) {
            while (ip >= 0 && kkey.compareTo(npr.node_key.get(ip)) < 0) {
                ip--;
            }
            ip++;
            npr.node_key.add(ip, kkey);
            npr.node_values.add(ip, new HashMap<>(Collections.singletonMap(ddocument, frequencyy)));
        } else {
            while (ip >= 0 && kkey.compareTo(npr.node_key.get(ip)) < 0) {
                ip--;
            }
            ip++;

            if (npr.node_children.get(ip).node_key.size() == (2 * tt) - 1) {
                split_Child(npr, ip);
                if (kkey.compareTo(npr.node_key.get(ip)) > 0) {
                    ip++;
                }
            }

            insert_Non_Full(npr.node_children.get(ip), kkey, ddocument, frequencyy);
        }
    }

    private void split_Child(B_Tree_Node npr, int ip) {
        B_Tree_Node yp = npr.node_children.get(ip);
        B_Tree_Node zp = new B_Tree_Node();
        npr.node_children.add(ip + 1, zp);
        npr.node_key.add(ip, yp.node_key.get(tt - 1));
        npr.node_values.add(ip, yp.node_values.get(tt - 1));

        zp.node_key.addAll(yp.node_key.subList(tt, yp.node_key.size()));
        zp.node_values.addAll(yp.node_values.subList(tt, yp.node_values.size()));
        yp.node_key.subList(tt - 1, yp.node_key.size()).clear();
        yp.node_values.subList(tt - 1, yp.node_values.size()).clear();

        if (!yp.node_children.isEmpty()) {
            zp.node_children.addAll(yp.node_children.subList(tt, yp.node_children.size()));
            yp.node_children.subList(tt, yp.node_children.size()).clear();
        }
    }

    public Map<String, Integer> searchh(String keyy) {
        return searchh(roott, keyy);
    }

    private Map<String, Integer> searchh(B_Tree_Node np, String keyy) {
        int ipr = 0;
        while (ipr < np.node_key.size() && keyy.compareTo(np.node_key.get(ipr)) > 0) {
            ipr++;
        }

        if (ipr < np.node_key.size() && keyy.equals(np.node_key.get(ipr))) {
            return np.node_values.get(ipr);
        } else if (np.node_children.isEmpty()) {
            return null;
        } else {
            return searchh(np.node_children.get(ipr), keyy);
        }
    }
}

public class InvertedIndexing {
//    public static void invertedIndexing(String key) {
//        // Create a B-tree with a node size of 2
//        B_Tree bTree = new B_Tree(2);
//
//        
//        // Search for a keyword
//        String keyword = "example";
//        Map<String, Integer> result = bTree.searchh(keyword);
//
//        // Display search result
//        if (result != null) {
//            System.out.println("Occurrences of '" + keyword + "': " + result);
//        } else {
//            System.out.println("No occurrences of '" + keyword + "'.");
//        }
//    }

    private static String read_Html_File(File filee) {

        StringBuilder conttent = new StringBuilder();

        try {
            Document docc = Jsoup.parse(filee, "UTF-8");

            // Traverse through all text nodes in the document
            docc.traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    // Append a space before the text content of each element (except for the first one)
                    if (node instanceof TextNode && conttent.length() > 0) {
                        conttent.append(" ");
                    }
                }

                @Override
                public void tail(Node node, int depth) {
                    // No action needed for tail
                }
            });

            // Append the whole text content to the StringBuilder
            conttent.append(docc.text());
        } catch (IOException e) {
            System.out.println("file not found");
        }

        return conttent.toString();
    }

    public static B_Tree index_Documents_InFolder(String[] folder_Paths) {
        B_Tree b_Tree = new B_Tree(2);
        for (String folder_Path : folder_Paths) {
            File folderr = new File(folder_Path);
            if (folderr.exists() && folderr.isDirectory()) {
                File[] filess = folderr.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));

                if (filess != null) {
                    for (File filee : filess) {
                        if (filee.isFile()) {
                            String documentName = filee.getName();
                            String content = read_Html_File(filee);
//                            if (filee.getName().toLowerCase().contains("orbitz")){
//                                System.out.println(content);
//                            }
                            indexDocument(b_Tree, documentName, content);
                        }
                    }
                }
                else
                {
                	System.out.println("files is empty");
                }
            } else {
            	System.out.println("file not exist ");
            }
        }
        return b_Tree;
    }

    private static void indexDocument(B_Tree bTree, String documentName, String content) {
        // Tokenize the content (replace with your own tokenization logic)
        String[] tokens = content.split("\\s+");

        for (String token : tokens) {
//            System.out.println(token);
//            if (documentName.toLowerCase().contains("orbitz")){
//                System.out.println(token);
//            }
//            System.out.println(token);
            // Update the B-tree
            Map<String, Integer> frequencies = bTree.searchh(token.toLowerCase());
            if (frequencies == null) {
                frequencies = new HashMap<>();
            }
//            System.out.println(frequencies);

            int currentFrequency = frequencies.getOrDefault(documentName, 0);
            frequencies.put(documentName, currentFrequency + 1);
            bTree.ins_ert(token.toLowerCase(), documentName, currentFrequency + 1);
        }
    }
}