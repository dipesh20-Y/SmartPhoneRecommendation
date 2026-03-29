package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

public class LoadVocabulary {
    /*
     * This method is used to load vocabulary from file provided
     * @params filePath is location of file containing vocabulary
     * */
    public static HashSet<String> loadVocabulary(String filePath) {

        HashSet<String> vocabulary = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Concatenate all columns into a single text string
                StringBuilder allText = new StringBuilder();
                for (String col : cols) {
                    allText.append(" ").append(col);
                }

                // Convert to lowercase and remove non-alphabetic characters
                String text = allText.toString()
                        .toLowerCase()
                        .replaceAll("[^a-z ]", " ");

                // Split into words and add to vocabulary
                for (String word : text.split("\\s+")) {
                    if (word.length() > 2) {
                        vocabulary.add(word);
                    }
                }
            }

//            System.out.println("Total words loaded: " + vocabulary.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return vocabulary;
    }
}
