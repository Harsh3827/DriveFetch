package features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class SpellChecking {


	private static final int ALPa= 26;

	private static Trie_Node tRoot = new Trie_Node();

	
	private static class Trie_Node 
	{
		Trie_Node[] t_Children = new Trie_Node[ALPa];
		boolean word_Ending = false;
	}

    // insert method
	public static void insertingWord(String wordd) {
		Trie_Node nod = tRoot;
		for (int ii = 0; ii < wordd.length(); ii++) {
			char cc = wordd.charAt(ii);
			// condition for char weather lowercase or not
			if (cc< 'a' || cc > 'z') {
				continue;
			}
			int indexOfNode = cc - 'a';
			// Creating node if not present
			if (nod.t_Children[indexOfNode] == null) {
				nod.t_Children[indexOfNode] = new Trie_Node();
			}
			nod = nod.t_Children[indexOfNode];
		}
		nod.word_Ending = true;
	}

	
	public static boolean search(String word) {
		Trie_Node t_Node = tRoot;
		for (int i = 0; i < word.length(); i++) {
			int indexOfNode = word.charAt(i) - 'a';
			// Return false if the node for the current char doesn't exist
			if (t_Node.t_Children[indexOfNode] == null) {
				return false;
			}
			t_Node = t_Node.t_Children[indexOfNode];
		}
		// Check node presend end or not
		return t_Node != null && t_Node.word_Ending;
	}

	// create a dictionary with give filepath
	public static void initialize_Dictionary(String file_Path) throws IOException {
		File filee = new File(file_Path);
		BufferedReader readerr = new BufferedReader(new FileReader(filee));
		String linee;
		// take each line from file
		while ((linee = readerr.readLine()) != null) {
			// divide line into words
			for (String word : linee.split("\\W+")) {
				if (!word.isEmpty()) {
					//convert to lowercase and insert
					insertingWord(word.toLowerCase());
				}
			}
		}
		readerr.close();
	}

	//to perform spellchecking
	public static boolean check_Spelling(String wordd) {
		// coverting to lowercase and calling other methods
		return search(wordd.toLowerCase());
	}

	
}
