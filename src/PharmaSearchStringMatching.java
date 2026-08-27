import java.io.*;
import java.util.*;

public class PharmaSearchStringMatching {

    // =========================================================
    // BUILD LPS ARRAY
    // =========================================================
    public static int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            char current =
                    Character.toLowerCase(pattern.charAt(i));

            char previous =
                    Character.toLowerCase(pattern.charAt(length));

            if (current == previous) {

                length++;
                lps[i] = length;
                i++;

            } else {

                if (length != 0) {

                    length = lps[length - 1];

                } else {

                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }


    // =========================================================
    // KMP STRING MATCHING ALGORITHM
    // =========================================================
    public static ArrayList<Integer> KMPSearch(
            String text,
            String pattern) {

        ArrayList<Integer> positions = new ArrayList<>();

        if (pattern == null || pattern.length() == 0) {
            return positions;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            char textChar =
                    Character.toLowerCase(text.charAt(i));

            char patternChar =
                    Character.toLowerCase(pattern.charAt(j));

            if (textChar == patternChar) {

                i++;
                j++;
            }

            // Complete pattern found
            if (j == pattern.length()) {

                positions.add(i - j);

                j = lps[j - 1];

            }

            // Mismatch
            else if (i < text.length()
                    && Character.toLowerCase(text.charAt(i))
                    != Character.toLowerCase(pattern.charAt(j))) {

                if (j != 0) {

                    j = lps[j - 1];

                } else {

                    i++;
                }
            }
        }

        return positions;
    }


    // =========================================================
    // READ PHARMACEUTICAL CORPUS FILE
    // =========================================================
    public static String readCorpus(String filePath) {

        StringBuilder corpus = new StringBuilder();

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(filePath));

            String line;

            while ((line = reader.readLine()) != null) {

                corpus.append(line);
                corpus.append("\n");
            }

            reader.close();

        } catch (FileNotFoundException e) {

            System.out.println();
            System.out.println("ERROR: Corpus file not found!");
            System.out.println("Expected file location:");
            System.out.println(filePath);
            System.out.println();

        } catch (IOException e) {

            System.out.println();
            System.out.println("ERROR: Could not read corpus file.");
            System.out.println(e.getMessage());
            System.out.println();
        }

        return corpus.toString();
    }


    // =========================================================
    // DISPLAY MATCHING SENTENCES
    // =========================================================
    public static void displayMatchingSentences(
            String corpus,
            String query) {

        // Split corpus into sentences
        String[] sentences =
                corpus.split("(?<=[.!?])\\s+");

        int sentenceNumber = 0;

        System.out.println();
        System.out.println("========== MATCHING SENTENCES ==========");

        for (String sentence : sentences) {

            ArrayList<Integer> matches =
                    KMPSearch(sentence, query);

            if (!matches.isEmpty()) {

                sentenceNumber++;

                System.out.println();
                System.out.println(
                        sentenceNumber + ". "
                                + sentence.trim());
            }
        }

        if (sentenceNumber == 0) {

            System.out.println();
            System.out.println(
                    "No sentence containing the search term was found.");
        }

        System.out.println();
        System.out.println(
                "========================================");
    }


    // =========================================================
    // DISPLAY MATCH POSITIONS
    // =========================================================
    public static void displayMatchPositions(
            ArrayList<Integer> positions) {

        if (positions.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("Match positions in corpus:");

        for (int i = 0; i < positions.size(); i++) {

            System.out.print(positions.get(i));

            if (i < positions.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println();
    }


    // =========================================================
    // MAIN METHOD
    // =========================================================
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Corpus file location
        String filePath = "data/pharma_corpus.txt";


        // =====================================================
        // PROJECT TITLE
        // =====================================================

        System.out.println();
        System.out.println(
                "==============================================");

        System.out.println(
                "              PHARMA SEARCH");

        System.out.println(
                "        STRING MATCHING MODULE");

        System.out.println(
                "       KMP ALGORITHM IMPLEMENTATION");

        System.out.println(
                "==============================================");


        // =====================================================
        // LOAD CORPUS
        // =====================================================

        String corpus = readCorpus(filePath);


        if (corpus.isEmpty()) {

            System.out.println(
                    "Corpus could not be loaded.");

            System.out.println(
                    "Please check that this file exists:");

            System.out.println(
                    "data/pharma_corpus.txt");

            scanner.close();

            return;
        }


        // =====================================================
        // CORPUS INFORMATION
        // =====================================================

        System.out.println();

        System.out.println(
                "Pharmaceutical corpus loaded successfully!");

        System.out.println(
                "Corpus size: "
                        + corpus.length()
                        + " characters");


        // =====================================================
        // USER SEARCH LOOP
        // =====================================================

        while (true) {

            System.out.println();
            System.out.println(
                    "----------------------------------------------");

            System.out.print(
                    "Enter a medicine or keyword to search");

            System.out.print(
                    " (type 'exit' to stop): ");

            String query = scanner.nextLine();


            // =================================================
            // EXIT
            // =================================================

            if (query.equalsIgnoreCase("exit")) {

                System.out.println();
System.out.println(
                        "Thank you for using Pharma Search!");

                break;
            }


            // =================================================
            // EMPTY INPUT
            // =================================================

            if (query.trim().isEmpty()) {

                System.out.println();

                System.out.println(
                        "Please enter a valid medicine or keyword.");

                continue;
            }


            // =================================================
            // RUN KMP
            // =================================================

            ArrayList<Integer> positions =
                    KMPSearch(corpus, query);


            // =================================================
            // DISPLAY SEARCH RESULT
            // =================================================

            System.out.println();

            System.out.println(
                    "============== SEARCH RESULT ==============");

            System.out.println(
                    "Search Query : " + query);

            System.out.println(
                    "Matches Found: "
                            + positions.size());


            if (!positions.isEmpty()) {

                System.out.println(
                        "Status       : MATCH FOUND");

                // Display positions
                displayMatchPositions(positions);

                // Display matching sentences
                displayMatchingSentences(
                        corpus,
                        query);

            } else {

                System.out.println(
                        "Status       : NO MATCH FOUND");

                System.out.println();

                System.out.println(
                        "Try another pharmaceutical term such as:");

                System.out.println(
                        "paracetamol");

                System.out.println(
                        "amoxicillin");

                System.out.println(
                        "ibuprofen");

                System.out.println(
                        "antibiotic");

                System.out.println(
                        "fever");

                System.out.println(
                        "pain");

                System.out.println(
                        "diabetes");

                System.out.println(
                        "blood pressure");
            }


            System.out.println(
                    "============================================");
        }


        // =====================================================
        // CLOSE SCANNER
        // =====================================================

        scanner.close();
    }
}
