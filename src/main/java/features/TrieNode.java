package features;

import java.util.HashMap;
import java.util.HashSet;

/**
 * TrieNode represents a single node in the Trie (prefix tree) data structure.
 * Each node maintains references to its child nodes, marks whether it represents
 * the end of a valid word, and stores the set of document IDs (phone names)
 * where the word appears.
 *
 * This class is a core building block of the InvertedIndex used for fast
 * keyword-based search and page ranking.
 */
public class TrieNode {

    /** Map of child nodes: character → TrieNode */
    HashMap<Character, TrieNode> children;

    /** Flag indicating whether this node marks the end of a complete word */
    boolean isEndOfWord;

    /** Set of document IDs (phone names) containing the word that ends at this node */
    HashSet<String> documentIds;

    /**
     * Constructs a new TrieNode with initialized empty children map,
     * default end-of-word flag as false, and an empty document ID set.
     */
    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        documentIds = new HashSet<>();
    }
}