package features;

import java.util.HashMap;
import java.util.Map;

/**
 * WCTrieNode represents a single character node in the WCTrie (word completion Trie).
 * Each node maintains a map of child nodes and a flag indicating whether it marks
 * the end of a complete word. This structure enables efficient prefix-based
 * autocomplete functionality in the recommendation system.
 */
public class WCTrieNode {

    /** Map containing child nodes: character → WCTrieNode */
    Map<Character, WCTrieNode> children;

    /** Flag indicating whether this node represents the end of a valid word */
    boolean isEndOfWord;

    /**
     * Constructs a new WCTrieNode with an empty children map
     * and the end-of-word flag set to false.
     */
    public WCTrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}