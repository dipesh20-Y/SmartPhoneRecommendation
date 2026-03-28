package features;

public class EditDistanceCompSC {

    // This method calculates the edit distance between two strings
    // using a dynamic programming approach
    public static int compuation(String s1, String s2) {

        // Create a 2D table where each cell represents the minimum
        // number of operations required to convert a prefix of s1
        // into a prefix of s2
        int[][] dynamic_programming =
                new int[s1.length() + 1][s2.length() + 1];

        // Initialize the row index for iterating through the table
        int t = 0;

        // Loop through all characters of the first string (including empty prefix)
        while (t <= s1.length()) {

            // Initialize the column index for each new row
            int z = 0;

            // Loop through all characters of the second string (including empty prefix)
            while (z <= s2.length()) {

                // If the first string is empty, the cost is equal to
                // the number of insertions needed to build the second string
                if (t == 0) {
                    dynamic_programming[t][z] = z;
                }

                // If the second string is empty, the cost is equal to
                // the number of deletions needed to reduce the first string
                else if (z == 0) {
                    dynamic_programming[t][z] = t;
                }

                // If the current characters are the same,
                // no additional operation is required
                else if (s1.charAt(t - 1) == s2.charAt(z - 1)) {
                    dynamic_programming[t][z] =
                            dynamic_programming[t - 1][z - 1];
                }

                // If the characters are different, consider the minimum
                // cost among insertion, deletion, and substitution
                else {
                    dynamic_programming[t][z] =
                            1 + Math.min(
                                    dynamic_programming[t - 1][z - 1], // substitution
                                    Math.min(
                                            dynamic_programming[t - 1][z],  // deletion
                                            dynamic_programming[t][z - 1]   // insertion
                                    )
                            );
                }

                // Move to the next column in the table
                z++;
            }

            // Move to the next row in the table
            t++;
        }

        // The final cell contains the edit distance between the two full strings
        return dynamic_programming[s1.length()][s2.length()];
    }
}