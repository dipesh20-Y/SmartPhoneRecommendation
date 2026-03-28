package features;
import java.util.HashMap;
import java.util.Map;

/**
 * TrieNode represents each character node in the Trie.
 * Each node contains:
 * - children nodes
 * - a flag to indicate end of a word
 */
public class WCTrieNode {

    // Map to store child characters
    Map<Character, WCTrieNode> children;

    // Indicates whether this node completes a word
    boolean isEndOfWord;

    // Constructor
    public WCTrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
}
