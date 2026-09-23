```java
import java.io.*;
import java.util.*;

public class PharmaSearchStringMatching {

    // ==================== KMP ====================

    public static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;

        while (i < pattern.length()) {
            if (Character.toLowerCase(pattern.charAt(i))
                    == Character.toLowerCase(pattern.charAt(len))) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    public static ArrayList<Integer> KMPSearch(
            String text, String pattern) {

        ArrayList<Integer> positions = new ArrayList<>();

        if (pattern == null || pattern.isEmpty())
            return positions;

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {

            if (Character.toLowerCase(text.charAt(i))
                    == Character.toLowerCase(pattern.charAt(j))) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                positions.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length()
                    && Character.toLowerCase(text.charAt(i))
                    != Character.toLowerCase(pattern.charAt(j))) {

                if (j != 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }

        return positions;
    }


    // ==================== EDIT DISTANCE ====================

    public static int editDistance(String a, String b) {

        a = a.toLowerCase();
        b = b.toLowerCase();

        int m = a.length();
        int n = b.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++)
            dp[i][0] = i;

        for (int j = 0; j <= n; j++)
            dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {

                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j],
                            Math.min(
                                    dp[i][j - 1],
                                    dp[i - 1][j - 1]
                            )
                    );
                }
            }
        }

        return dp[m][n];
    }

    public static void checkSpelling(
            String query, String[] medicines) {

        String closest = "";
        int minDistance = Integer.MAX_VALUE;

        for (String medicine : medicines) {

            int distance = editDistance(query, medicine);

            if (distance < minDistance) {
                minDistance = distance;
                closest = medicine;
            }
        }

        System.out.println("\n===== EDIT DISTANCE =====");
        System.out.println("Entered : " + query);
        System.out.println("Closest : " + closest);
        System.out.println("Distance: " + minDistance);

        if (minDistance == 0)
            System.out.println("Status  : Correct spelling");
        else if (minDistance <= 3)
            System.out.println("Status  : Possible spelling mistake");
        else
            System.out.println("Status  : No close medicine found");
    }


    // ==================== NETWORK FLOW ====================

    public static boolean bfs(
            int[][] graph, int source, int sink, int[] parent) {

        boolean[] visited = new boolean[graph.length];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {

            int u = queue.poll();

            for (int v = 0; v < graph.length; v++) {

                if (!visited[v] && graph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        return visited[sink];
    }

    public static int maxFlow(
            int[][] graph, int source, int sink) {

        int n = graph.length;
        int[][] residual = new int[n][n];

        for (int i = 0; i < n; i++)
            residual[i] = graph[i].clone();

        int[] parent = new int[n];
        int totalFlow = 0;

        while (bfs(residual, source, sink, parent)) {

            int pathFlow = Integer.MAX_VALUE;

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(
                        pathFlow,
                        residual[u][v]
                );
            }

            for (int v = sink; v != source; v = parent[v]) {

                int u = parent[v];

                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            totalFlow += pathFlow;
        }

        return totalFlow;
    }

    public static void networkFlow() {

        /*
         * 0 = Source
         * 1 = Supplier
         * 2 = Pharmacy
         * 3 = Patient
         * 4 = Sink
         */

        int[][] graph = {
                {0, 100, 0,  0,  0},
                {0,   0, 80, 20, 0},
                {0,   0,  0, 60, 0},
                {0,   0,  0,  0, 70},
                {0,   0,  0,  0,  0}
        };

        int flow = maxFlow(graph, 0, 4);

        System.out.println("\n===== NETWORK FLOW =====");
        System.out.println("Source -> Supplier -> Pharmacy -> Patient -> Sink");
        System.out.println("Maximum Medicine Flow: " + flow + " units");
        System.out.println("Algorithm: Ford-Fulkerson");
    }


    // ==================== READ CORPUS ====================

    public static String readCorpus(String filePath) {

        StringBuilder corpus = new StringBuilder();

        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader(filePath));

            String line;

            while ((line = reader.readLine()) != null) {
                corpus.append(line).append("\n");
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error reading corpus file.");
        }

        return corpus.toString();
    }


    // ==================== DISPLAY SENTENCES ====================

    public static void displaySentences(
            String corpus, String query) {

        String[] sentences =
                corpus.split("(?<=[.!?])\\s+");

        int count = 0;

        for (String sentence : sentences) {

            if (!KMPSearch(sentence, query).isEmpty()) {
                count++;
                System.out.println(count + ". " + sentence.trim());
            }
        }

        if (count == 0)
            System.out.println("No matching sentence found.");
    }


    // ==================== MAIN ====================

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String filePath = "data/pharma_corpus.txt";

        String corpus = readCorpus(filePath);

        if (corpus.isEmpty()) {
            System.out.println("Corpus file could not be loaded.");
            System.out.println("Check: data/pharma_corpus.txt");
            scanner.close();
            return;
        }

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

        while (true) {

            System.out.println("\n==============================");
            System.out.println("        PHARMA SEARCH");
            System.out.println("==============================");
            System.out.println("1. Search Medicine using KMP");
            System.out.println("2. Check Spelling using Edit Distance");
            System.out.println("3. Calculate Network Flow");
            System.out.println("4. Exit");
            System.out.println("==============================");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            // KMP
            if (choice.equals("1")) {

                System.out.print("Enter medicine or keyword: ");
                String query = scanner.nextLine();

                ArrayList<Integer> result =
                        KMPSearch(corpus, query);

                System.out.println("\n===== KMP SEARCH =====");
                System.out.println("Query: " + query);
                System.out.println("Matches Found: " + result.size());

                if (!result.isEmpty()) {
                    System.out.println("Status: MATCH FOUND");
                    System.out.println("\nMatching Sentences:");
                    displaySentences(corpus, query);
                } else {
                    System.out.println("Status: NO MATCH FOUND");
                }
            }

            // Edit Distance
            else if (choice.equals("2")) {

                System.out.print("Enter medicine name: ");
                String query = scanner.nextLine();

                checkSpelling(query, medicines);
            }

            // Network Flow
            else if (choice.equals("3")) {

                networkFlow();
            }

            // Exit
            else if (choice.equals("4")) {

                System.out.println(
                        "Thank you for using Pharma Search!"
                );
                break;
            }

            else {
                System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}
```
