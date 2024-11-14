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

// TrieNode class data structure
// helper class for trie
// it contains an array of siz 128

class TrieNodehelper 
{
    TrieNodehelper[] child;
    boolean wordcompleted;

    public TrieNodehelper() {
        // Initialize an array for using ASCII characters
    	// and make boolean as false
        this.child = new TrieNodehelper[128];
        this.wordcompleted = false;
    }
}

// Trie class to perform trie methods
// main class or entry point of this file
class Triemain 
{
    public TrieNodehelper root;

    // Constructor  calling
    // intializing helper class
    public Triemain() {
        this.root = new TrieNodehelper();
    }

    // insert a word into the Triemaing
    public void insertNode(String wordgiven) {
        TrieNodehelper nodd = root;
        for (char chh : wordgiven.toCharArray()) {
            int indexx = chh;
            if (nodd.child[indexx] == null) {
                nodd.child[indexx] = new TrieNodehelper();
            }
            nodd = nodd.child[indexx];
        }
        nodd.wordcompleted = true;
    }

    //  word suggestions for a given prefix 
    public List<String> getSuggestions(String prefixx) {
        TrieNodehelper nodd = findNode(prefixx);
        System.out.println(nodd);
        List<String> suggestionss = new ArrayList<>();
        if (nodd != null) {
            getAllWords(nodd, prefixx, suggestionss);
        }

        // Sort based on (edit distance)
        suggestionss.sort(Comparator.comparingInt(suggest -> calculateEditDistance(prefixx, suggest)));


        return suggestionss;
    }

    // help mtd  to find the node related  to a given prefix
    private TrieNodehelper findNode(String prefixx) {
        TrieNodehelper nodx = root;
        for (char ch : prefixx.toCharArray()) {
            int indexx = ch;
            if (nodx.child[indexx] == null) {
                return null;
            }
            nodx = nodx.child[indexx];
        }
        return nodx;
    }

    // Helper mtd to retrieve all words
    private void getAllWords(TrieNodehelper nodee, String currentPrefixx, List<String> suggestionss) {
        if (nodee.wordcompleted) {
            String suggestionWithoutOrSimilar = removeOrSimilar(currentPrefixx);
            suggestionss.add(suggestionWithoutOrSimilar);
        }

        for (int ik = 0; ik < nodee.child.length; ik++) {
            if (nodee.child[ik] != null) {
                char ch = (char) ik;
                getAllWords(nodee.child[ik], currentPrefixx + ch, suggestionss);
            }
        }
    }

    // method 
    private String removeOrSimilar(String suggestionn) {
        // Assuming "or similar" end of the suggestion
        return suggestionn.replace(" or similar", "");
    }

    // mtd to find edit distance between two words
    private int calculateEditDistance(String wordfirst, String wordsecond) {
        int[][] dp = new int[wordfirst.length() + 1][wordsecond.length() + 1];

        for (int i = 0; i <= wordfirst.length(); i++) {
            for (int j = 0; j <= wordsecond.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(wordfirst.charAt(i - 1), wordsecond.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[wordfirst.length()][wordsecond.length()];
    }

    // mtd cost of substituting one character with another
    private int costOfSubstitution(char aa, char bd) {
        return aa == bd ? 0 : 1;
    }

    // find the minimum of a set of numbers
    private int min(int... num) {
        return Arrays.stream(num).min().orElse(Integer.MAX_VALUE);
    }
    

    public void helper() {
        suggesthelper(root, "");
    }
  
   static  ArrayList<String> aa=new ArrayList<String>();
    private void suggesthelper(TrieNodehelper nodee, String preefix)
    {
        if (nodee.wordcompleted) {
          
            aa.add(preefix);
        }
        for (char chh = 0; chh < 128; chh++) {
            if (nodee.child[chh] != null) {
                suggesthelper(nodee.child[chh], preefix + chh);
            }
        }
    }
    public   List<String> suggest(String inputstring)
    {
    	
        helper();
       
        TrieNodehelper nodee = root;
        List<String> suggestionss = new ArrayList<>();
        for(int ii=0;ii<aa.size();ii++)
        {
           int n= calculateEditDistance(inputstring,aa.get(ii));
          
            if(n==2|| n==1)
            	  suggestionss.add(aa.get(ii));
        // Collect suggestions using Levenshtein distance
       
        }
        return suggestionss;
    }

   
    
}

// WordCompletion class to provide word completion
public class WordCompletion 
{
    
    public static Triemain trie;

    

    //  initialize the Trie with  a JSON file
    public static void initialize_Dictionary_From_JsonFile(String filename)
    {
        trie = new Triemain();

        try {
            // Read the JSON file
            ObjectMapper object_Mapper = new ObjectMapper();
            ArrayNode jsonArray = (ArrayNode) object_Mapper.readTree(new File(filename));
            for (int ii = 0; ii < jsonArray.size(); ii++) {
                trie.insertNode(jsonArray.get(ii).get("name").asText().toLowerCase());
            }
        } catch (IOException e) {
           
            e.printStackTrace();
        }
    }
    // intial from filepath
    
    public static void initializeDictionary(String filePathstring) throws IOException 
    {
    	trie = new Triemain();
		File file = new File(filePathstring);
		BufferedReader read = new BufferedReader(new FileReader(file));
		String linee;
		
		while ((linee = read.readLine()) != null) {
			// Split the line into words using non-word characters as delimiters
			for (String wordd : linee.split("\\W+")) {
				if (!wordd.isEmpty()) {
					// Insert the lowercase version of the word into the trie
					trie.insertNode(wordd.toLowerCase());
				}
			}
		}
		read.close();
	}

    // Method to get suggestions
    public static List<String> get_Suggestions(String prefixx) 
    
    {
        return trie.suggest(prefixx);
    }

}
