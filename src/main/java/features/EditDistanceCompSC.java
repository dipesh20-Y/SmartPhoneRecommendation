package features;

/**
 * EditDistanceCompSC implements the Levenshtein distance algorithm to support
 * the spell-checking feature of the smartphone recommendation system. It calculates
 * the minimum number of single-character edits (insertions, deletions, or substitutions)
 * required to transform one string into another. This distance is used to detect
 * misspelled search terms and generate accurate correction suggestions that integrate
 * with the word-completion Trie, inverted index lookup, and page-ranking pipeline.
 */
public class EditDistanceCompSC {

    /**
     * Computes the edit distance between the source and target strings using
     * a dynamic programming table. The algorithm initializes boundary conditions
     * for empty prefixes and fills the matrix by considering character matches
     * or the minimum cost among substitution, deletion, and insertion operations.
     *
     * Null inputs are explicitly rejected with a clear error message to prevent
     * downstream failures in the query processing flow.
     *
     * @param sourceString the string to be transformed (typically the user query term)
     * @param targetString the reference string (typically a dictionary or indexed term)
     * @return the minimum edit distance; returns 0 after error message if any input is null
     */
    public static int computation(String sourceString, String targetString) {
        if (sourceString == null || targetString == null) {
            System.out.println("Error: Input strings cannot be null for edit distance computation.");
            return 0;
        }

        int sourceLength = sourceString.length();
        int targetLength = targetString.length();

        int[][] dpTable = new int[sourceLength + 1][targetLength + 1];

        // Initialize first row (cost of inserting characters into empty source)
        for (int col = 0; col <= targetLength; col++) {
            dpTable[0][col] = col;
        }

        // Initialize first column (cost of deleting characters from source to empty target)
        for (int row = 0; row <= sourceLength; row++) {
            dpTable[row][0] = row;
        }

        // Fill remaining cells of the DP table
        for (int row = 1; row <= sourceLength; row++) {
            for (int col = 1; col <= targetLength; col++) {
                if (sourceString.charAt(row - 1) == targetString.charAt(col - 1)) {
                    dpTable[row][col] = dpTable[row - 1][col - 1];
                } else {
                    dpTable[row][col] = 1 + Math.min(
                            dpTable[row - 1][col - 1],   // substitution
                            Math.min(
                                    dpTable[row - 1][col],   // deletion
                                    dpTable[row][col - 1]    // insertion
                            )
                    );
                }
            }
        }


        return dpTable[sourceLength][targetLength];
    }
}