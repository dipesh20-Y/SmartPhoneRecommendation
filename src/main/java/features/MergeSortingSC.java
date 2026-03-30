package features;

import java.util.ArrayList;
import java.util.List;

/**
 * SuggestionOf_Words represents a candidate correction term along with
 * its edit distance from the user's input. Used by the spell-checking
 * feature to store and later rank possible suggestions.
 */
class SuggestionOf_Words {

    String term;
    int edit_distance;

    /**
     * Constructs a suggestion with the candidate term and its computed edit distance.
     *
     * @param term         the suggested word from vocabulary
     * @param edit_distance the Levenshtein distance from the original input
     */
    SuggestionOf_Words(String term, int edit_distance) {
        this.term = term;
        this.edit_distance = edit_distance;
    }
}

/**
 * MergeSortingSC provides a merge sort implementation to rank spelling
 * suggestions based on edit distance (lowest distance first).
 *
 * This ensures the best matching corrections are presented to the user first.
 */
public class MergeSortingSC {

    /**
     * Sorts the list of suggestions in ascending order of edit distance
     * using the merge sort algorithm. The original list is modified in-place.
     *
     * @param list the list of SuggestionOf_Words to be sorted
     */
    public static void mergeSort(List<SuggestionOf_Words> list) {
        if (list == null || list.size() < 2) {
            return;
        }

        int middle_term = list.size() / 2;

        List<SuggestionOf_Words> left_words = new ArrayList<>(list.subList(0, middle_term));
        List<SuggestionOf_Words> right_words = new ArrayList<>(list.subList(middle_term, list.size()));

        mergeSort(left_words);
        mergeSort(right_words);

        merge(list, left_words, right_words);
    }

    /**
     * Merges two sorted sublists back into the target list while maintaining
     * ascending order based on edit distance.
     *
     * @param conclusion  the target list to merge into (modified in place)
     * @param left_Words  the sorted left sublist
     * @param right_Words the sorted right sublist
     */
    private static void merge(List<SuggestionOf_Words> conclusion,
                              List<SuggestionOf_Words> left_Words,
                              List<SuggestionOf_Words> right_Words) {

        int indexA = 0;
        int indexB = 0;
        int indexC = 0;

        // Merge elements from both halves while both have remaining items
        while (indexA < left_Words.size() && indexB < right_Words.size()) {
            if (left_Words.get(indexA).edit_distance <= right_Words.get(indexB).edit_distance) {
                conclusion.set(indexC++, left_Words.get(indexA++));
            } else {
                conclusion.set(indexC++, right_Words.get(indexB++));
            }
        }

        // Append any remaining elements from the left sublist
        while (indexA < left_Words.size()) {
            conclusion.set(indexC++, left_Words.get(indexA++));
        }

        // Append any remaining elements from the right sublist
        while (indexB < right_Words.size()) {
            conclusion.set(indexC++, right_Words.get(indexB++));
        }
    }
}