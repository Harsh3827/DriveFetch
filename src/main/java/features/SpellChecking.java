package features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpellChecking {

	private static final int ALPHA = 26;
	private static Trie_Node tRoot = new Trie_Node();
	private static boolean dictionaryInitialized = false;

	private static class Trie_Node {
		Trie_Node[] t_Children = new Trie_Node[ALPHA];
		boolean word_Ending = false;
	}

	public static void insertingWord(String word) {
		Trie_Node node = tRoot;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (c < 'a' || c > 'z') continue;
			int index = c - 'a';
			if (node.t_Children[index] == null) {
				node.t_Children[index] = new Trie_Node();
			}
			node = node.t_Children[index];
		}
		node.word_Ending = true;
	}

	public static boolean search(String word) {
		Trie_Node node = tRoot;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			int index = c - 'a';
			if (index < 0 || index >= ALPHA || node.t_Children[index] == null) {
				return false;
			}
			node = node.t_Children[index];
		}
		return node.word_Ending;
	}

	public static boolean isDictionaryInitialized() {
		return dictionaryInitialized;
	}

	public static void initialize_Dictionary(String filePath) throws IOException {
		if (!dictionaryInitialized) {
			File file = new File(filePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				for (String word : line.split("\\W+")) {
					if (!word.isEmpty()) {
						insertingWord(word.toLowerCase());
					}
				}
			}
			reader.close();
			dictionaryInitialized = true;
		}
	}

	public static List<String> get_Suggestions(String input, boolean isPrefix) {
		if (isPrefix) {
			// Word Completion: Find all words starting with the given prefix
			Trie_Node node = findNode(input);
			List<String> suggestions = new ArrayList<>();
			if (node != null) {
				collectWords(node, input, suggestions);
			}
			return suggestions;
		} else {
			// Spell Checking: Find closest matches based on Levenshtein distance
			List<String> allWords = new ArrayList<>();
			collectWords(tRoot, "", allWords);

			List<String> suggestions = new ArrayList<>();
			for (String candidate : allWords) {
				int distance = calculateEditDistance(input, candidate);
				if (distance <= 2) { // Allow small edit distances
					suggestions.add(candidate);
				}
			}
			suggestions.sort(Comparator.comparingInt(suggestion -> calculateEditDistance(input, suggestion)));
			return suggestions;
		}
	}

	private static Trie_Node findNode(String prefix) {
		Trie_Node node = tRoot;
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			int index = c - 'a';

			// Handle invalid characters gracefully
			if (index < 0 || index >= ALPHA) {
				return null; // Invalid character, terminate search
			}

			if (node.t_Children[index] == null) {
				return null; // Prefix not found in the trie
			}
			node = node.t_Children[index];
		}
		return node; // Return the node corresponding to the prefix
	}

	private static void collectWords(Trie_Node node, String prefix, List<String> words) {
		if (node.word_Ending) {
			words.add(prefix);
		}
		for (int i = 0; i < node.t_Children.length; i++) {
			if (node.t_Children[i] != null) {
				char c = (char) ('a' + i);
				collectWords(node.t_Children[i], prefix + c, words);
			}
		}
	}

	private static int calculateEditDistance(String word1, String word2) {
		int[][] dp = new int[word1.length() + 1][word2.length() + 1];
		for (int i = 0; i <= word1.length(); i++) {
			for (int j = 0; j <= word2.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
							dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1));
				}
			}
		}
		return dp[word1.length()][word2.length()];
	}

	public static boolean check_Spelling(String word) {
		return search(word.toLowerCase());
	}
}