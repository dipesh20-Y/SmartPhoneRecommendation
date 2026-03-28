package features;

import java.util.HashMap;
import java.util.HashSet;

//this class represents a single node in the Trie data structure
//each node stores its children, whether it marks the end of a word, and which phone documents contain the word.
public class TrieNode {

    //hashmap to store children nodes, where key is the character and value is next TrieNode
    HashMap<Character, TrieNode> children;

    //true if a complete word ends at this node
    boolean isEndOfWord;

    //stores the phone names (document IDs) where this word appears
    HashSet<String> documentIds;

    public TrieNode(){
        children = new HashMap<>();
        isEndOfWord = false;
        documentIds = new HashSet<>();
    }
}
