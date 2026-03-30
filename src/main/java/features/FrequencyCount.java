package features;

/**
 * FrequencyCount implements the Boyer-Moore algorithm for high-performance
 * pattern matching and term frequency analysis. This utility is a critical
 * component of the page-ranking pipeline: it counts exact occurrences of
 * search terms within smartphone document text to compute relevance scores
 * that feed the inverted index lookup and final result ordering.
 */
public class FrequencyCount {

    /**
     * Counts how many times the given pattern appears in the text.
     * This reusable method is called by the RecommendationEngine during
     * page ranking to determine term frequency for relevance scoring.
     * Null or empty inputs are rejected with a clear error message.
     * The method normalizes both inputs to lowercase before delegating
     * to the Boyer-Moore implementation.
     *
     * @param text    the document or field content to be searched
     * @param pattern the search term or keyword to locate
     * @return the total number of occurrences (0 if inputs are invalid)
     */
    public static int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null || pattern.trim().isEmpty()) {
            System.out.println("Error: Text or pattern cannot be null or empty for frequency counting.");
            return 0;
        }

        String normalizedText = text.toLowerCase();
        String normalizedPattern = pattern.toLowerCase().trim();


        return performBoyerMoore(normalizedText, normalizedPattern);
    }

    /**
     * Core Boyer-Moore pattern matching implementation.
     * Uses the bad-character heuristic to skip alignments intelligently,
     * achieving sub-linear performance on average for natural-language text.
     *
     * @param text    the normalized text to search
     * @param pattern the normalized pattern to find
     * @return the total number of exact matches found
     */
    public static int performBoyerMoore(String text, String pattern) {
        int matchCount = 0;
        int textLen = text.length();
        int patternLen = pattern.length();

        if (patternLen == 0) {
            return 0;
        }

        int[] badCharTable = createBadCharTable(pattern);
        int currentPos = 0;

        while (currentPos <= textLen - patternLen) {
            int patternIdx = patternLen - 1;

            while (patternIdx >= 0 && pattern.charAt(patternIdx) == text.charAt(currentPos + patternIdx)) {
                patternIdx--;
            }

            if (patternIdx < 0) {
                // Full match found
                matchCount++;
                if (currentPos + patternLen < textLen) {
                    currentPos += patternLen - badCharTable[text.charAt(currentPos + patternLen)];
                } else {
                    currentPos += 1;
                }
            } else {
                int shiftAmount = patternIdx - badCharTable[text.charAt(currentPos + patternIdx)];
                currentPos += Math.max(1, shiftAmount);
            }
        }

        return matchCount;
    }

    /**
     * Builds the bad-character shift table used by the Boyer-Moore algorithm.
     * For each possible character, the table stores the rightmost occurrence
     * index within the pattern (or -1 if the character does not appear).
     *
     * @param pattern the pattern for which the shift table is constructed
     * @return the 256-entry bad-character table (ASCII)
     */
    public static int[] createBadCharTable(String pattern) {
        final int ASCII_SIZE = 256;
        int[] badCharTable = new int[ASCII_SIZE];

        // Initialize all positions to -1 (character not in pattern)
        for (int i = 0; i < ASCII_SIZE; i++) {
            badCharTable[i] = -1;
        }

        // Record the rightmost occurrence of each character
        for (int i = 0; i < pattern.length(); i++) {
            badCharTable[pattern.charAt(i)] = i;
        }

        return badCharTable;
    }
}