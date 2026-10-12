import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PharmaSearch {

    // Module 1: Medicine record structure
    static class Medicine {
        String name = "";
        String category = "";
        String uses = "";
        String dosage = "";
        String sideEffects = "";
        String precautions = "";
        String content = "";

        void setField(String field, String value) {
            switch (field.toUpperCase().trim()) {
                case "MEDICINE":
                case "NAME":
                    name = value;
                    break;
                case "CATEGORY":
                    category = value;
                    break;
                case "USES":
                    uses = value;
                    break;
                case "DOSAGE":
                    dosage = value;
                    break;
                case "SIDE EFFECTS":
                case "SIDE_EFFECTS":
                    sideEffects = value;
                    break;
                case "PRECAUTIONS":
                    precautions = value;
                    break;
                default:
                    content += field + ": " + value + "\n";
            }
        }

        String searchableText() {
            return (name + " " + category + " " + uses + " "
                    + dosage + " " + sideEffects + " "
                    + precautions + " " + content).toLowerCase();
        }

        void display() {
            System.out.println("\nMedicine: " + name);
            if (!category.isEmpty())
                System.out.println("Category: " + category);
            if (!uses.isEmpty())
                System.out.println("Uses: " + uses);
            if (!dosage.isEmpty())
                System.out.println("Dosage: " + dosage);
            if (!sideEffects.isEmpty())
                System.out.println("Side Effects: " + sideEffects);
            if (!precautions.isEmpty())
                System.out.println("Precautions: " + precautions);
            if (!content.isEmpty())
                System.out.println(content);
            System.out.println("--------------------------------");
        }
    }

    // Module 2: Load all individual medicine text files
    static List<Medicine> loadCorpus() {
        List<Medicine> medicines = new ArrayList<>();
        Path folder = Paths.get("medicines");

        if (!Files.isDirectory(folder)) {
            System.out.println("ERROR: medicines folder not found.");
            System.out.println("Place it beside PharmaSearch.java.");
            return medicines;
        }

        try {
            List<Path> files = new ArrayList<>();
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(folder, "*.txt")) {
                for (Path file : stream) {
                    files.add(file);
                }
            }

            files.sort(Comparator.comparing(
                    p -> p.getFileName().toString(),
                    String.CASE_INSENSITIVE_ORDER));

            for (Path file : files) {
                Medicine medicine = new Medicine();

                try (BufferedReader reader =
                             Files.newBufferedReader(file)) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;

                        int colon = line.indexOf(':');
                        if (colon >= 0) {
                            String field =
                                    line.substring(0, colon).trim();
                            String value =
                                    line.substring(colon + 1).trim();
                            medicine.setField(field, value);
                        } else {
                            medicine.content += line + "\n";
                        }
                    }

                    if (medicine.name.isEmpty()) {
                        medicine.name = file.getFileName().toString()
                                    .replaceFirst("(?i)\\.txt$", "");
                    }

                    medicine.content =
                            file.getFileName() + "\n" + medicine.content;
                    medicines.add(medicine);

                } catch (IOException e) {
                    System.out.println("Could not read "
                            + file.getFileName() + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading medicines folder: "
                    + e.getMessage());
        }

        System.out.println("Medicine files loaded: " + medicines.size());
        return medicines;
    }

    // Module 3: Knuth-Morris-Pratt (KMP) string matching
    static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i++] = ++length;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    static boolean kmpSearch(String text, String pattern) {
        if (pattern.isEmpty()) return true;

        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == pattern.length()) return true;

            if (i < text.length()
                    && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }
        return false;
    }

    static void searchMedicine(List<Medicine> medicines, String query) {
        int count = 0;

        for (Medicine medicine : medicines) {
            if (kmpSearch(medicine.searchableText(), query)) {
                medicine.display();
                count++;
            }
        }

        if (count == 0)
            System.out.println("No matching medicines found.");
        else
            System.out.println("Total matches: " + count);
    }

    // Module 4: Edit Distance for approximate name matching
    static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            dp[i][0] = i;

        for (int j = 0; j <= b.length(); j++)
            dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1)
                        ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,
                                 dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    static void fuzzySearch(List<Medicine> medicines, String query) {
        List<Medicine> matches = new ArrayList<>();
        int bestDistance = Integer.MAX_VALUE;

        for (Medicine medicine : medicines) {
            int distance = editDistance(medicine.name, query);

            if (distance < bestDistance) {
                bestDistance = distance;
                matches.clear();
                matches.add(medicine);
            } else if (distance == bestDistance) {
                matches.add(medicine);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No medicine records available.");
            return;
        }

        System.out.println("Closest name distance: " + bestDistance);
        for (Medicine medicine : matches)
            medicine.display();
    }

    // Module 5: Edmonds-Karp maximum flow using BFS
    static int maxFlow(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = new int[n][n];

        for (int i = 0; i < n; i++)
            residual[i] = capacity[i].clone();

        int totalFlow = 0;
        int[] parent = new int[n];

        while (true) {
            Arrays.fill(parent, -1);
            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);
            parent[source] = source;

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.remove();

                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        queue.add(v);
                    }
                }
            }

            if (parent[sink] == -1) break;

            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v])
                pathFlow = Math.min(
                        pathFlow, residual[parent[v]][v]);

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            totalFlow += pathFlow;
        }

        return totalFlow;
    }

    static void demonstrateMaxFlow() {
        int[][] capacity = {
            {0, 10, 5, 0},
            {0, 0, 0, 10},
            {0, 0, 0, 10},
            {0, 0, 0, 0}
        };

        System.out.println("Maximum possible supply flow: "
                + maxFlow(capacity, 0, 3) + " units");
    }

    // Module 6: Greedy 2-approximation for Vertex Cover
    static void demonstrateVertexCover() {
        int[][] edges = {
            {0, 1},
            {1, 2},
            {2, 3},
            {3, 0},
            {1, 3}
        };

        boolean[] covered = new boolean[4];
        Set<Integer> cover = new LinkedHashSet<>();

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];

            if (!covered[u] && !covered[v]) {
                cover.add(u);
                cover.add(v);
                covered[u] = true;
                covered[v] = true;
            }
        }

        System.out.println("Drug interaction review vertices: " + cover);
        System.out.println("Review each selected vertex and its connected interactions.");
    }

    // Additional demonstration: Reservoir Sampling
    static void demonstrateSampling(List<Medicine> medicines, int k) {
        if (medicines.isEmpty() || k <= 0) {
            System.out.println("No records available for sampling.");
            return;
        }

        k = Math.min(k, medicines.size());
        List<Medicine> sample = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < medicines.size(); i++) {
            if (i < k) {
                sample.add(medicines.get(i));
            } else {
                int j = random.nextInt(i + 1);
                if (j < k)
                    sample.set(j, medicines.get(i));
            }
        }

        System.out.println("\nRandom quality-check sample:");
        for (Medicine medicine : sample)
            System.out.println("- " + medicine.name);
    }

    // Main menu
    public static void main(String[] args) {
        List<Medicine> medicines = loadCorpus();

        if (medicines.isEmpty()) {
            System.out.println("No medicine files were loaded.");
            System.out.println("Check your folder and .txt files.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== PHARMA SEARCH SYSTEM =====");
            System.out.println("1. Search medicine / keyword (KMP)");
            System.out.println("2. Find closest medicine name (Edit Distance)");
            System.out.println("3. Demonstrate supplier allocation (Max Flow)");
            System.out.println("4. Demonstrate interaction review (Vertex Cover)");
            System.out.println("5. Select random quality-check sample");
            System.out.println("6. Show number of medicine records");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter medicine name or keyword: ");
                    searchMedicine(medicines, scanner.nextLine().trim());
                    break;

                case "2":
                    System.out.print("Enter medicine name (or spelling): ");
                    fuzzySearch(medicines, scanner.nextLine().trim());
                    break;

                case "3":
                    demonstrateMaxFlow();
                    break;

                case "4":
                    demonstrateVertexCover();
                    break;

                case "5":
                    System.out.print("How many medicines to sample? ");
                    try {
                        int k = Integer.parseInt(scanner.nextLine().trim());
                        demonstrateSampling(medicines, k);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid whole number.");
                    }
                    break;

                case "6":
                    System.out.println("Total medicine records: "
                            + medicines.size());
                    break;

                case "0":
                    System.out.println("Exiting Pharma Search. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
