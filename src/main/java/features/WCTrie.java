package features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TrieDictionary implements a prefix tree used for
 * fast word lookup and auto-completion.
 *
 * Words are stored character by character.
 * This allows efficient prefix based searching.
 */
public class WCTrie {

    // Root node of the trie
    private WCTrieNode startNode;

    /**
     * Constructor initializes the root node
     */
    public WCTrie() {
        startNode = new WCTrieNode();
    }

    /**
     * Adds a word into the trie structure.
     * Each character becomes a node in the tree.
     */
    public void addWord(String term) {

        WCTrieNode pointer = startNode;

        for (int i = 0; i < term.length(); i++) {

            char letter = term.charAt(i);

            // If the path does not exist, create it
            if (!pointer.children.containsKey(letter)) {
                pointer.children.put(letter, new WCTrieNode());
            }

            // Move deeper in the tree
            pointer = pointer.children.get(letter);
        }

        // Mark that a full word ends here
        pointer.isEndOfWord = true;
    }

    /**
     * Returns all stored words that start
     * with the provided prefix.
     */
    public List<String> getWordsStartingWith(String prefix) {

        List<String> foundWords = new ArrayList<>();

        WCTrieNode pointer = startNode;

        // Traverse the trie to reach prefix end
        for (int i = 0; i < prefix.length(); i++) {

            char letter = prefix.charAt(i);

            if (!pointer.children.containsKey(letter)) {
                return foundWords; // prefix not found
            }

            pointer = pointer.children.get(letter);
        }

        // Explore remaining branches
        explore(pointer, prefix, foundWords);

        return foundWords;
    }

    /**
     * Depth-first traversal of the trie to collect words.
     * This recursively explores all children nodes.
     */
    private void explore(WCTrieNode currentNode,
                         String currentPrefix,
                         List<String> results) {

        // If this node represents a word, store it
        if (currentNode.isEndOfWord) {
            results.add(currentPrefix);
        }

        // Visit all child nodes
        for (Map.Entry<Character, WCTrieNode> branch : currentNode.children.entrySet()) {

            char nextLetter = branch.getKey();
            WCTrieNode nextNode = branch.getValue();

            explore(nextNode,
                    currentPrefix + nextLetter,
                    results);
        }
    }
}