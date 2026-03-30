package features;

import java.util.HashSet;

/**
 * Trie implements a prefix tree (Trie) data structure optimized for the
 * smartphone recommendation system. It supports efficient word insertion
 * with associated document IDs (phone names) and fast lookup operations.
 *
 * This Trie powers the Inverted Index, enabling quick keyword-to-document
 * retrieval used in search and page ranking features.
 */
public class Trie {

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie and associates it with a document ID.
     * If the word already exists, the document ID is added to the existing set.
     *
     * @param word       the word to be inserted (should be normalized)
     * @param documentId the phone name or document identifier linked to this word
     */
    public void insert(String word, String documentId) {
        if (word == null || word.isEmpty() || documentId == null) {
            System.out.println("Error: Word and documentId cannot be null or empty during Trie insertion.");
            return;
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (!current.children.containsKey(c)) {
                current.children.put(c, new TrieNode());
            }

            current = current.children.get(c);
        }

        current.isEndOfWord = true;
        current.documentIds.add(documentId);
    }

    /**
     * Searches for a word in the Trie and returns the set of document IDs
     * (phone names) where the word appears.
     *
     * @param word the word to search for
     * @return a HashSet of document IDs containing the word, or an empty set if not found
     */
    public HashSet<String> search(String word) {
        if (word == null || word.isEmpty()) {
            System.out.println("Error: Search word cannot be null or empty.");
            return new HashSet<>();
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (!current.children.containsKey(c)) {
                return new HashSet<>();   // Word not found
            }

            current = current.children.get(c);
        }

        // Return empty set if this is not a complete word
        if (!current.isEndOfWord) {
            return new HashSet<>();
        }

        return current.documentIds;
    }
}