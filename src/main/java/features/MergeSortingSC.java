package features;

import java.util.ArrayList;   // ArrayList is used to store temporary sublists during merge sort
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;        // List interface allows flexible handling of collections

// This class represents a possible correction word
// along with its edit distance from the input word
class SuggestionOf_Words {

    // Stores the candidate word from the vocabulary
    String term;

    // Stores the edit distance between the input word and this term
    int edit_distance;

    // Constructor initializes the word and its corresponding edit distance
    SuggestionOf_Words(String term, int edit_distance) {
        this.term = term;
        this.edit_distance = edit_distance;
    }
}

// This class contains the merge sort implementation
// used to rank suggestion words based on edit distance
public class MergeSortingSC {

    // Public method that applies merge sort on a list of suggestion words
    public static void mergeSort(List<SuggestionOf_Words> list) {

        // If the list has fewer than two elements, it is already sorted
        if (list.size() < 2) return;

        // Determine the midpoint of the list to divide it into two halves
        int middle_term = list.size() / 2;

        // Create the left sublist containing the first half of elements
        List<SuggestionOf_Words> left_words =
                new ArrayList<>(list.subList(0, middle_term));

        // Create the right sublist containing the remaining elements
        List<SuggestionOf_Words> right_words =
                new ArrayList<>(list.subList(middle_term, list.size()));

        // Recursively sort the left half
        mergeSort(left_words);

        // Recursively sort the right half
        mergeSort(right_words);

        // Merge the two sorted halves back into the original list
        merge(list, left_words, right_words);
    }




    // This helper method combines two sorted sublists into one sorted list
    private static void merge(List<SuggestionOf_Words> conclusion,
                              List<SuggestionOf_Words> left_Words,
                              List<SuggestionOf_Words> right_Words) {

        // Index for traversing the left sublist
        int indexA = 0;

        // Index for traversing the right sublist
        int indexB = 0;

        // Index for placing elements into the final merged list
        int indexC = 0;

        // Continue merging as long as both sublists still have elements
        for (; indexA < left_Words.size() && indexB < right_Words.size(); ) {

            // Compare edit distances to decide which word should come first
            if (left_Words.get(indexA).edit_distance
                    <= right_Words.get(indexB).edit_distance) {

                // Place the left word into the result list and move forward
                conclusion.set(indexC++, left_Words.get(indexA++));
            }
            else {
                // Place the right word into the result list and move forward
                conclusion.set(indexC++, right_Words.get(indexB++));
            }
        }

        // Add any remaining words from the left sublist
        // once the right sublist has been fully processed
        do {
            conclusion.set(indexC++, left_Words.get(indexA++));
        } while (indexA < left_Words.size());

        // Add any remaining words from the right sublist
        // once the left sublist has been fully processed
        do {
            conclusion.set(indexC++, right_Words.get(indexB++));
        } while (indexB < right_Words.size());
    }
}