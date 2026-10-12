import java.util.*;

public class PharmaSearchStringMatching {

    static Scanner sc = new Scanner(System.in);

    // Medicine database for this educational project
    static class Medicine {
        String name, category, uses, problems;
        String dosage, sideEffects, precautions, alternatives;
        int stock;

        Medicine(String name, String category, String uses,
                 String problems, String dosage, String sideEffects,
                 String precautions, String alternatives, int stock) {
            this.name = name;
            this.category = category;
            this.uses = uses;
            this.problems = problems;
            this.dosage = dosage;
            this.sideEffects = sideEffects;
            this.precautions = precautions;
            this.alternatives = alternatives;
            this.stock = stock;
        }
    }

    static Medicine[] medicines = {
        new Medicine("Paracetamol", "Pain reliever and fever reducer",
            "Helps relieve mild to moderate pain and reduce fever.",
            "Fever, headache and body aches.",
            "Follow the product label or professional advice.",
            "Overdose can cause serious liver damage.",
            "Avoid taking multiple products containing paracetamol.",
            "Ibuprofen may help with some types of pain or fever, "
            + "but it is not suitable for everyone. Ask a pharmacist.",
            150),

        new Medicine("Ibuprofen", "NSAID pain reliever",
            "Helps relieve pain, inflammation and fever.",
            "Some headaches, muscle pain and inflammatory conditions.",
            "Use only as directed on the label or by a professional.",
            "May cause stomach upset; serious stomach or kidney "
            + "problems can occur.",
            "May be unsuitable with stomach ulcers, kidney disease, "
            + "some heart conditions or during certain stages of pregnancy.",
            "Paracetamol may be an option for some pain or fever. "
            + "Ask a pharmacist which option is appropriate.",
            80),

        new Medicine("Amoxicillin", "Penicillin antibiotic",
            "Treats certain bacterial infections when prescribed.",
            "Some bacterial ear, throat, chest or other infections.",
            "Take only as prescribed; complete the prescribed course.",
            "Nausea, diarrhoea or allergic reactions.",
            "Tell your healthcare professional about penicillin allergy.",
            "A clinician must choose any alternative antibiotic "
            + "based on the infection and patient.",
            45),

        new Medicine("Azithromycin", "Macrolide antibiotic",
            "Treats certain bacterial infections when prescribed.",
            "Some respiratory and other bacterial infections.",
            "Take only as prescribed.",
            "Nausea, diarrhoea and possible heart rhythm problems.",
            "Check heart conditions and medicine interactions with a clinician.",
            "A healthcare professional must select an alternative "
            + "based on the infection.",
            30),

        new Medicine("Cetirizine", "Antihistamine",
            "Helps relieve allergy symptoms.",
            "Sneezing, runny nose, itching and watery eyes.",
            "Follow the label or professional advice.",
            "May cause drowsiness or dry mouth.",
            "Use caution when driving if drowsy; check suitability "
            + "for children, pregnancy or kidney disease.",
            "Loratadine is another antihistamine that may be suitable "
            + "for some people. Ask a pharmacist.",
            60),

        new Medicine("Metformin", "Antidiabetic medicine",
            "Helps control blood glucose in type 2 diabetes.",
            "Management of type 2 diabetes.",
            "Use only as prescribed.",
            "Nausea, diarrhoea and stomach discomfort.",
            "Kidney function and other medical conditions need "
            + "professional review.",
            "There is no automatic substitute. A clinician should "
            + "choose an appropriate diabetes treatment.",
            35),

        new Medicine("Omeprazole", "Proton pump inhibitor",
            "Reduces stomach acid.",
            "Acid reflux and some acid-related stomach conditions.",
            "Follow the product label or professional advice.",
            "Headache, abdominal discomfort or nausea.",
            "Seek advice for persistent symptoms or other medicines.",
            "Another acid-reducing medicine may be suitable depending "
            + "on the condition. Ask a pharmacist.",
            50),

        new Medicine("Aspirin", "Pain reliever and antiplatelet",
            "Used for certain types of pain; low-dose use may be "
            + "prescribed to prevent blood clots.",
            "Some pain conditions and specific cardiovascular uses.",
            "Use only according to the intended product and advice.",
            "Stomach irritation, bleeding or allergic reactions.",
            "Not suitable for everyone, including some people with "
            + "ulcers or bleeding risks; avoid in children unless directed.",
            "A suitable alternative depends on whether it is being "
            + "used for pain or clot prevention. Ask a clinician.",
            40),

        new Medicine("Diclofenac", "NSAID pain reliever",
            "Helps relieve pain and inflammation.",
            "Some joint, muscle and inflammatory pain.",
            "Use only as directed by a healthcare professional.",
            "Stomach upset; cardiovascular, kidney or bleeding risks.",
            "Discuss heart disease, ulcers, kidney disease and "
            + "other medicines with a professional.",
            "Paracetamol may suit some pain conditions, but the choice "
            + "depends on the person and the reason for treatment.",
            25),

        new Medicine("Atorvastatin", "Cholesterol-lowering statin",
            "Lowers LDL cholesterol and helps reduce cardiovascular risk.",
            "High cholesterol and prevention of certain cardiovascular events.",
            "Take only as prescribed.",
            "Muscle symptoms or digestive discomfort may occur.",
            "Report unexplained muscle pain and check medicine interactions.",
            "Other cholesterol-lowering treatments exist, but a clinician "
            + "must select the right treatment.",
            20),

        new Medicine("Insulin", "Blood glucose-lowering hormone",
            "Helps control blood glucose in people who need insulin.",
            "Diabetes requiring insulin treatment.",
            "Dose and timing must follow the individual prescription.",
            "Low blood sugar and injection-site reactions.",
            "Never change or stop insulin without medical guidance.",
            "Do not substitute insulin products on your own. "
            + "Contact your diabetes care team.",
            15),

        new Medicine("Antacid", "Stomach acid neutralizer",
            "Neutralizes stomach acid for short-term symptom relief.",
            "Occasional heartburn and indigestion.",
            "Follow the specific product label.",
            "Some products may cause constipation or diarrhoea.",
            "Check suitability with kidney disease and other medicines.",
            "An acid-reducing medicine may be suitable for some conditions; "
            + "ask a pharmacist.",
            55),

        new Medicine("Loratadine", "Antihistamine",
            "Helps relieve allergy symptoms.",
            "Sneezing, runny nose and itchy eyes.",
            "Follow the label or professional advice.",
            "Headache, fatigue or dry mouth may occur.",
            "Check suitability if you have liver disease or take other medicines.",
            "Cetirizine is another antihistamine, but suitability and "
            + "drowsiness differ. Ask a pharmacist.",
            40)
    };

    static Medicine findMedicine(String query) {
        for (Medicine m : medicines) {
            if (m.name.equalsIgnoreCase(query.trim()))
                return m;
        }
        return null;
    }

    // 1. KMP algorithm
    static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;

        while (i < pattern.length()) {
            if (Character.toLowerCase(pattern.charAt(i))
                    == Character.toLowerCase(pattern.charAt(len))) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    static ArrayList<Integer> KMPSearch(String text, String pattern) {
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
                if (j > 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }
        return positions;
    }

    static String medicineNamesText() {
        StringBuilder text = new StringBuilder();
        for (Medicine m : medicines)
            text.append(m.name).append("\n");
        return text.toString();
    }

    static void printMedicineDetails(Medicine m) {
        System.out.println("\n===== MEDICINE DETAILS =====");
        System.out.println("Medicine Name : " + m.name);
        System.out.println("Category      : " + m.category);
        System.out.println("Uses          : " + m.uses);
        System.out.println("Problems      : " + m.problems);
        System.out.println("Dosage        : " + m.dosage);
        System.out.println("Side Effects  : " + m.sideEffects);
        System.out.println("Precautions   : " + m.precautions);
        System.out.println("Stock         : " + m.stock + " units");

        if (m.stock > 0) {
            System.out.println("Availability  : IN STOCK");
        } else {
            System.out.println("Availability  : OUT OF STOCK");
            System.out.println("Possible alternatives to discuss:");
            System.out.println(m.alternatives);
        }

        System.out.println("\nIMPORTANT: Alternatives are informational only.");
        System.out.println("Ask a doctor or pharmacist before substituting "
                         + "any medicine.");
    }

    // 2. Edit Distance
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
                if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
            }
        }
        return dp[a.length()][b.length()];
    }

    static void checkSpelling(String query) {
        String closest = "";
        int minDistance = Integer.MAX_VALUE;

        for (Medicine m : medicines) {
            int d = editDistance(query, m.name);
            if (d < minDistance) {
                minDistance = d;
                closest = m.name;
            }
        }

        System.out.println("\n===== EDIT DISTANCE =====");
        System.out.println("Entered : " + query);
        System.out.println("Closest : " + closest.toLowerCase());
        System.out.println("Distance: " + minDistance);

        if (minDistance == 0)
            System.out.println("Status  : Correct spelling");
        else if (minDistance <= 3)
            System.out.println("Status  : Possible spelling mistake");
        else
            System.out.println("Status  : No close medicine found");
    }

    // 3. Ford-Fulkerson network flow
    static boolean bfs(int[][] graph, int source, int sink, int[] parent) {
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

    static int maxFlow(int[][] graph, int source, int sink) {
        int n = graph.length;
        int[][] residual = new int[n][n];

        for (int i = 0; i < n; i++)
            residual[i] = graph[i].clone();

        int[] parent = new int[n];
        int totalFlow = 0;

        while (bfs(residual, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            for (int v = sink; v != source; v = parent[v])
                pathFlow = Math.min(pathFlow,
                                    residual[parent[v]][v]);

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }
            totalFlow += pathFlow;
        }
        return totalFlow;
    }

    static void networkFlow() {
        int[][] graph = {
            {0, 100, 0,  0,  0},
            {0,   0, 80, 20, 0},
            {0,   0,  0, 60, 0},
            {0,   0,  0,  0, 70},
            {0,   0,  0,  0,  0}
        };

        System.out.println("\n===== NETWORK FLOW =====");
        System.out.println(
            "Source -> Supplier -> Pharmacy -> Patient -> Sink");
        System.out.println("Maximum Medicine Flow: "
                           + maxFlow(graph, 0, 4) + " units");
        System.out.println("Algorithm: Ford-Fulkerson");
    }

    // 4. Naive string matching
    static void naiveSearch(String text, String pattern) {
        int count = 0;

        if (pattern.isEmpty()) {
            System.out.println("Please enter a search term.");
            return;
        }

        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            if (text.regionMatches(true, i, pattern, 0, pattern.length()))
                count++;
        }

        System.out.println("\n===== NAIVE STRING MATCHING =====");
        System.out.println("Query: " + pattern);
        System.out.println("Matches Found: " + count);
        System.out.println(count > 0
                ? "Status: MATCH FOUND" : "Status: NO MATCH FOUND");
    }

    // 5. Longest Common Subsequence
    static int lcsLength(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (Character.toLowerCase(a.charAt(i - 1))
                        == Character.toLowerCase(b.charAt(j - 1)))
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                else
                    dp[i][j] = Math.max(dp[i - 1][j],
                                        dp[i][j - 1]);
            }
        }
        return dp[a.length()][b.length()];
    }

    static void compareMedicines() {
        System.out.print("Enter first medicine name: ");
        String a = sc.nextLine();

        System.out.print("Enter second medicine name: ");
        String b = sc.nextLine();

        System.out.println("\n===== LCS COMPARISON =====");
        System.out.println("First Medicine : " + a);
        System.out.println("Second Medicine: " + b);
        System.out.println("Common subsequence length: "
                           + lcsLength(a, b));
    }

    // 6. Display all medicines
    static void displayMedicines() {
        System.out.println("\n===== AVAILABLE MEDICINES =====");
        for (Medicine m : medicines) {
            System.out.printf("%-18s Stock: %d%n", m.name, m.stock);
        }
        System.out.println("Total Medicines: " + medicines.length);
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==============================");
            System.out.println("        PHARMA SEARCH");
            System.out.println("==============================");
            System.out.println("1. Search Medicine using KMP");
            System.out.println("2. Check Spelling using Edit Distance");
            System.out.println("3. Calculate Network Flow");
            System.out.println("4. Search using Naive String Matching");
            System.out.println("5. Compare Medicines using LCS");
            System.out.println("6. Display All Available Medicines");
            System.out.println("0. Exit");
            System.out.println("==============================");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("Enter medicine or keyword: ");
                    String query = sc.nextLine().trim();

                    ArrayList<Integer> matches =
                        KMPSearch(medicineNamesText(), query);

                    System.out.println("\n===== KMP SEARCH =====");
                    System.out.println("Query: " + query);
                    System.out.println("Matches Found: " + matches.size());

                    if (matches.isEmpty()) {
                        System.out.println("Status: NO MATCH FOUND");
                        checkSpelling(query);
                        System.out.println(
                            "Use option 2 for spelling suggestions.");
                    } else {
                        System.out.println("Status: MATCH FOUND");

                        boolean shown = false;
                        for (Medicine m : medicines) {
                            if (m.name.toLowerCase().contains(
                                    query.toLowerCase())) {
                                printMedicineDetails(m);
                                shown = true;
                            }
                        }

                        if (!shown) {
                            System.out.println(
                                "For full details, enter the complete "
                                + "medicine name.");
                        }
                    }
                    break;
                }

                case "2":
                    System.out.print("Enter medicine name: ");
                    checkSpelling(sc.nextLine().trim());
                    break;

                case "3":
                    networkFlow();
                    break;

                case "4": {
                    System.out.print("Enter medicine or keyword: ");
                    String query = sc.nextLine().trim();
                    naiveSearch(medicineNamesText(), query);
                    break;
                }

                case "5":
                    compareMedicines();
                    break;

                case "6":
                    displayMedicines();
                    System.out.print(
                        "\nEnter a medicine name to view details, "
                        + "or press Enter to return: ");
                    String name = sc.nextLine().trim();

                    if (!name.isEmpty()) {
                        Medicine m = findMedicine(name);
                        if (m != null)
                            printMedicineDetails(m);
                        else
                            System.out.println(
                                "Medicine not found in this sample database.");
                    }
                    break;

                case "0":
                    System.out.println(
                        "Thank you for using Pharma Search!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
