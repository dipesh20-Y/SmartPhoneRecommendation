package features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WCTrie implements a prefix tree (Trie) specifically designed for
 * word completion and autocomplete functionality in the smartphone
 * recommendation system. It efficiently stores vocabulary terms and
 * supports fast prefix-based searches for real-time suggestions.
 */
public class WCTrie {

    private final WCTrieNode root;

    /**
     * Constructs a new WCTrie with an empty root node.
     */
    public WCTrie() {
        root = new WCTrieNode();
    }

    /**
     * Adds a word to the Trie for word completion support.
     * Each character of the word becomes a node in the tree.
     *
     * @param term the word to be inserted into the Trie
     */
    public void addWord(String term) {
        if (term == null || term.trim().isEmpty()) {
            System.out.println("Error: Term cannot be null or empty for word completion insertion.");
            return;
        }

        String normalizedTerm = term.toLowerCase().trim();
        WCTrieNode current = root;

        for (int i = 0; i < normalizedTerm.length(); i++) {
            char ch = normalizedTerm.charAt(i);

            if (!current.children.containsKey(ch)) {
                current.children.put(ch, new WCTrieNode());
            }

            current = current.children.get(ch);
        }

        current.isEndOfWord = true;
    }

    /**
     * Returns a list of all words in the Trie that start with the given prefix.
     * Used for real-time autocomplete suggestions.
     *
     * @param prefix the prefix string to search for
     * @return list of matching words; empty list if no matches found
     */
    public List<String> getWordsStartingWith(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            System.out.println("Error: Prefix cannot be null or empty for word completion search.");
            return new ArrayList<>();
        }

        List<String> suggestions = new ArrayList<>();
        String normalizedPrefix = prefix.toLowerCase().trim();

        WCTrieNode current = root;

        // Traverse to the end of the prefix
        for (int i = 0; i < normalizedPrefix.length(); i++) {
            char ch = normalizedPrefix.charAt(i);

            if (!current.children.containsKey(ch)) {
                return suggestions; // No words with this prefix
            }

            current = current.children.get(ch);
        }

        // Collect all words starting from this prefix
        explore(current, normalizedPrefix, suggestions);

        return suggestions;
    }

    /**
     * Recursively explores the Trie from the current node to collect
     * all complete words that start with the given prefix.
     *
     * @param node          current Trie node
     * @param currentPrefix the prefix built so far
     * @param results       list to store matching words
     */
    private void explore(WCTrieNode node, String currentPrefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentPrefix);
        }

        for (Map.Entry<Character, WCTrieNode> entry : node.children.entrySet()) {
            char nextChar = entry.getKey();
            WCTrieNode nextNode = entry.getValue();

            explore(nextNode, currentPrefix + nextChar, results);
        }
    }
}