package features;

import java.util.HashSet;

//this class represents the Trie data structure
//it supports inserting words with document ids and searching for words
public class Trie {

    //root node of the Trie, which is an empty node
    private TrieNode root;

    public Trie(){
        root = new TrieNode();
    }

    //insert a word into the Trie, associating it with a document ID (phone name)
    //if the word already exists, the document ID is simply added to its set
    public void insert(String word, String documentId){
        TrieNode current = root; //always start from root

        //for each character in the word, traverse or create the path
        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);

            //if no child exists for this character, create a new node
            if(!current.children.containsKey(c)){
                current.children.put(c, new TrieNode());
            }

            //move to the child node for this character
            current = current.children.get(c);
        }

        //mark end of word
        current.isEndOfWord= true;

        //add the document id to this word's set
        current.documentIds.add(documentId);
    }

    //searches for a word in the Trie
    //returns the set of document IDs where the word appears or and empty set if the word is not found
    public HashSet<String> search(String word){
        TrieNode current = root; //start from root

        //traverse the trie following the characters of the word
        for (int i=0; i<word.length(); i++){
            char c = word.charAt(i);

            //if the character path doesn't exist, word is not in the Trie
            if(!current.children.containsKey(c)){
                return new HashSet<>(); //return empty set
            }

            //move to the next node
            current = current.children.get(c);
        }

        //if we reached here but it's not the real end of the word
        if(!current.isEndOfWord){
            return new HashSet<>(); //return empty set
        }

        //return all documents (phone names) containing the word
        return current.documentIds;
    }
}
