```java
import java.io.*;
import java.util.*;

public class PharmaSearchStringMatching {

    // =========================================================
    // 1. BUILD LPS ARRAY - KMP
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
    // 2. KMP STRING MATCHING
    // =========================================================
    public static ArrayList<Integer> KMPSearch(
            String text,
            String pattern) {

        ArrayList<Integer> positions =
                new ArrayList<>();

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

            if (j == pattern.length()) {

                positions.add(i - j);

                j = lps[j - 1];

            } else if (i < text.length()
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
    // 3. EDIT DISTANCE
    // =========================================================
    // Calculates the minimum number of:
    // Insertions, Deletions and Replacements
    // required to convert one word into another.
    // =========================================================
    public static int editDistance(
            String a,
            String b) {

        a = a.toLowerCase();
        b = b.toLowerCase();

        int m = a.length();
        int n = b.length();

        int[][] dp =
                new int[m + 1][n + 1];

        // Convert a string into empty string
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        // Convert empty string into b
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                if (a.charAt(i - 1)
                        == b.charAt(j - 1)) {

                    dp[i][j] =
                            dp[i - 1][j - 1];

                } else {

                    int insert =
                            dp[i][j - 1];

                    int delete =
                            dp[i - 1][j];

                    int replace =
                            dp[i - 1][j - 1];

                    dp[i][j] =
                            1 + Math.min(
                                    insert,
                                    Math.min(
                                            delete,
                                            replace
                                    )
                            );
                }
            }
        }

        return dp[m][n];
    }


    // =========================================================
    // 4. FIND CLOSEST MEDICINE
    // =========================================================
    public static void checkMedicineSpelling(
            String query,
            String[] medicines) {

        String closestMedicine = "";

        int minimumDistance =
                Integer.MAX_VALUE;

        for (String medicine : medicines) {

            int distance =
                    editDistance(
                            query,
                            medicine
                    );

            if (distance < minimumDistance) {

                minimumDistance = distance;

                closestMedicine = medicine;
            }
        }

        System.out.println();
        System.out.println(
                "========== EDIT DISTANCE RESULT =========="
        );

        System.out.println(
                "Entered Medicine : "
                        + query
        );

        System.out.println(
                "Closest Medicine : "
                        + closestMedicine
        );

        System.out.println(
                "Edit Distance    : "
                        + minimumDistance
        );

        if (minimumDistance == 0) {

            System.out.println(
                    "Status           : Exact spelling"
            );

        } else if (minimumDistance <= 3) {

            System.out.println(
                    "Status           : Possible spelling mistake"
            );

            System.out.println(
                    "Suggestion       : "
                            + closestMedicine
            );

        } else {

            System.out.println(
                    "Status           : No close medicine found"
            );
        }

        System.out.println(
                "==========================================="
        );
    }


    // =========================================================
    // 5. BFS FOR NETWORK FLOW
    // =========================================================
    public static boolean bfs(
            int[][] residualGraph,
            int source,
            int sink,
            int[] parent) {

        boolean[] visited =
                new boolean[
                        residualGraph.length
                ];

        Queue<Integer> queue =
                new LinkedList<>();

        queue.add(source);

        visited[source] = true;

        parent[source] = -1;

        while (!queue.isEmpty()) {

            int u = queue.poll();

            for (int v = 0;
                    v < residualGraph.length;
                    v++) {

                if (!visited[v]
                        && residualGraph[u][v] > 0) {

                    queue.add(v);

                    parent[v] = u;

                    visited[v] = true;
                }
            }
        }

        return visited[sink];
    }


    // =========================================================
    // 6. FORD-FULKERSON MAXIMUM FLOW
    // =========================================================
    public static int maxFlow(
            int[][] graph,
            int source,
            int sink) {

        int[][] residualGraph =
                new int[
                        graph.length
                ][
                        graph.length
                ];

        // Copy original graph
        for (int i = 0;
                i < graph.length;
                i++) {

            for (int j = 0;
                    j < graph.length;
                    j++) {

                residualGraph[i][j] =
                        graph[i][j];
            }
        }

        int[] parent =
                new int[graph.length];

        int totalFlow = 0;

        // Find augmenting paths
        while (bfs(
                residualGraph,
                source,
                sink,
                parent)) {

            int pathFlow =
                    Integer.MAX_VALUE;

            // Find minimum capacity
            // in the selected path
            for (int v = sink;
                    v != source;
                    v = parent[v]) {

                int u = parent[v];

                pathFlow =
                        Math.min(
                                pathFlow,
                                residualGraph[u][v]
                        );
            }

            // Update residual capacities
            for (int v = sink;
                    v != source;
                    v = parent[v]) {

                int u = parent[v];

                residualGraph[u][v]
                        -= pathFlow;

                residualGraph[v][u]
                        += pathFlow;
            }

            totalFlow += pathFlow;
        }

        return totalFlow;
    }


    // =========================================================
    // 7. DISPLAY NETWORK FLOW
    // =========================================================
    public static void displayNetworkFlow() {

        System.out.println();
        System.out.println(
                "========== NETWORK FLOW =========="
        );

        System.out.println();
        System.out.println(
                "Medicine Distribution Network:"
        );

        System.out.println();
        System.out.println(
                "Source -> Supplier -> Pharmacy -> Patient -> Sink"
        );

        /*
         * Nodes:
         *
         * 0 = Source
         * 1 = Supplier
         * 2 = Pharmacy
         * 3 = Patient
         * 4 = Sink
         */

        int[][] graph = {

                // S   Su  Ph  Pa  T
                { 0, 100, 0,  0,  0 },  // Source
                { 0,  0,  80, 20, 0 },  // Supplier
                { 0,  0,  0,  60, 0 },  // Pharmacy
                { 0,  0,  0,  0, 70 },  // Patient
                { 0,  0,  0,  0,  0 }   // Sink
        };

        int source = 0;
        int sink = 4;

        int maximumFlow =
                maxFlow(
                        graph,
                        source,
                        sink
                );

        System.out.println();

        System.out.println(
                "Source Capacity : 100 units"
        );

        System.out.println(
                "Supplier Capacity to Pharmacy : 80 units"
        );

        System.out.println(
                "Supplier Capacity to Patient : 20 units"
        );

        System.out.println(
                "Pharmacy Capacity to Patient : 60 units"
        );

        System.out.println();

        System.out.println(
                "Maximum Medicine Flow: "
                        + maximumFlow
                        + " units"
        );

        System.out.println(
                "Algorithm: Ford-Fulkerson"
        );

        System.out.println(
                "=================================="
        );
    }


    // =========================================================
    // 8. READ PHARMACEUTICAL CORPUS
    // =========================================================
    public static String readCorpus(
            String filePath) {

        StringBuilder corpus =
                new StringBuilder();

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    filePath
                            )
                    );

            String line;

            while ((line =
                    reader.readLine()) != null) {

                corpus.append(line);
                corpus.append("\n");
            }

            reader.close();

        } catch (FileNotFoundException e) {

            System.out.println();

            System.out.println(
                    "ERROR: Corpus file not found!"
            );

            System.out.println(
                    "Expected location:"
            );

            System.out.println(
                    filePath
            );

        } catch (IOException e) {

            System.out.println();

            System.out.println(
                    "ERROR: Could not read corpus."
            );

            System.out.println(
                    e.getMessage()
            );
        }

        return corpus.toString();
    }


    // =========================================================
    // 9. DISPLAY MATCHING SENTENCES
    // =========================================================
    public static void displayMatchingSentences(
            String corpus,
            String query) {

        String[] sentences =
                corpus.split(
                        "(?<=[.!?])\\s+"
                );

        int sentenceNumber = 0;

        System.out.println();

        System.out.println(
                "========== MATCHING SENTENCES =========="
        );

        for (String sentence : sentences) {

            ArrayList<Integer> matches =
                    KMPSearch(
                            sentence,
                            query
                    );

            if (!matches.isEmpty()) {

                sentenceNumber++;

                System.out.println();

                System.out.println(
                        sentenceNumber
                                + ". "
                                + sentence.trim()
                );
            }
        }

        if (sentenceNumber == 0) {

            System.out.println();

            System.out.println(
                    "No sentence containing the search term was found."
            );
        }

        System.out.println();

        System.out.println(
                "========================================="
        );
    }


    // =========================================================
    // 10. DISPLAY MATCH POSITIONS
    // =========================================================
    public static void displayMatchPositions(
            ArrayList<Integer> positions) {

        if (positions.isEmpty()) {
            return;
        }

        System.out.println();

        System.out.println(
                "Match positions in corpus:"
        );

        for (int i = 0;
                i < positions.size();
                i++) {

            System.out.print(
                    positions.get(i)
            );

            if (i < positions.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println();
    }


    // =========================================================
    // 11. MAIN METHOD
    // =========================================================
    public static void main(String[] args) {

        Scanner scanner =
                new Scanner(System.in);

        String filePath =
                "data/pharma_corpus.txt";


        // =====================================================
        // MEDICINE DATABASE FOR EDIT DISTANCE
        // =====================================================

        String[] medicines = {

                "paracetamol",
                "amoxicillin",
                "ibuprofen",
                "azithromycin",
                "cetirizine",
                "metformin",
                "omeprazole",
                "aspirin",
                "diclofenac",
                "atorvastatin",
                "insulin",
                "antibiotic",
                "antacid"
        };


        // =====================================================
        // LOAD CORPUS
        // =====================================================

        String corpus =
                readCorpus(filePath);

        if (corpus.isEmpty()) {

            System.out.println();

            System.out.println(
                    "Corpus could not be loaded."
            );

            System.out.println(
                    "Please check:"
            );

            System.out.println(
                    "data/pharma_corpus.txt"
            );

            scanner.close();

            return;
        }


        // =====================================================
        // MAIN MENU
        // =====================================================

        while (true) {

            System.out.println();

            System.out.println(
                    "=============================================="
            );

            System.out.println(
                    "              PHARMA SEARCH"
            );

            System.out.println(
                    "=============================================="

            );

            System.out.println(
                    "1. Search Medicine using KMP"
            );

            System.out.println(
                    "2. Check Medicine Spelling using Edit Distance"
            );

            System.out.println(
                    "3. Calculate Medicine Network Flow"
            );

            System.out.println(
                    "4. Exit"
            );

            System.out.println(
                    "=============================================="
            );

            System.out.print(
                    "Enter your choice: "
            );

            String choice =
                    scanner.nextLine();


            // =================================================
            // OPTION 1 - KMP
            // =================================================

            if (choice.equals("1")) {

                System.out.println();

                System.out.print(
                        "Enter medicine or keyword: "
                );

                String query =
                        scanner.nextLine();

                if (query.trim().isEmpty()) {

                    System.out.println(
                            "Please enter a valid keyword."
                    );

                    continue;
                }

                ArrayList<Integer> positions =
                        KMPSearch(
                                corpus,
                                query
                        );

                System.out.println();

                System.out.println(
                        "========== KMP SEARCH RESULT =========="
                );

                System.out.println(
                        "Search Query : "
                                + query
                );

                System.out.println(
                        "Matches Found: "
                                + positions.size()
                );

                if (!positions.isEmpty()) {

                    System.out.println(
                            "Status       : MATCH FOUND"
                    );

                    displayMatchPositions(
                            positions
                    );

                    displayMatchingSentences(
                            corpus,
                            query
                    );

                } else {

                    System.out.println(
                            "Status       : NO MATCH FOUND"
                    );

                    System.out.println();

                    System.out.println(
                            "Try using option 2 for spelling correction."
                    );
                }

                System.out.println(
                        "========================================"
                );
            }


            // =================================================
            // OPTION 2 - EDIT DISTANCE
            // =================================================

            else if (choice.equals("2")) {

                System.out.println();

                System.out.print(
                        "Enter medicine name: "
                );

                String query =
                        scanner.nextLine();

                if (query.trim().isEmpty()) {

                    System.out.println(
                            "Please enter a valid medicine name."
                    );

                    continue;
                }

                checkMedicineSpelling(
                        query,
                        medicines
                );
            }


            // =================================================
            // OPTION 3 - NETWORK FLOW
            // =================================================

            else if (choice.equals("3")) {

                displayNetworkFlow();
            }


            // =================================================
            // OPTION 4 - EXIT
            // =================================================

            else if (choice.equals("4")) {

                System.out.println();

                System.out.println(
                        "Thank you for using Pharma Search!"
                );

                break;
            }


            // =================================================
            // INVALID OPTION
            // =================================================

            else {

                System.out.println();

                System.out.println(
                        "Invalid choice!"
                );

                System.out.println(
                        "Please enter 1, 2, 3 or 4."
                );
            }
        }

        scanner.close();
    }
}
```
