import java.util.*;

public class FirstFollow {

    static String[] production = new String[20];
    static String[] firstSet = new String[20];
    static String[] followSet = new String[20];
    static int n;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of productions: ");
        n = Integer.parseInt(sc.nextLine());

        System.out.println("Enter productions (like E=TX or X=+TX or X=#):");
        for (int i = 0; i < n; i++) {
            production[i] = sc.nextLine().trim().replaceAll("\\s+", "");
        }
        sc.close();

        System.out.println("\n--- FIRST sets ---");
        for (int i = 0; i < n; i++) {
            char nonTerm = production[i].charAt(0);
            StringBuilder result = new StringBuilder();
            findFirst(result, nonTerm);
            firstSet[i] = result.toString();
            System.out.println("FIRST(" + nonTerm + ") = { " + result + " }");
        }

        System.out.println("\n--- FOLLOW sets ---");
        for (int i = 0; i < n; i++) {
            char nonTerm = production[i].charAt(0);
            StringBuilder result = new StringBuilder();
            findFollow(result, nonTerm);
            followSet[i] = result.toString();
            System.out.println("FOLLOW(" + nonTerm + ") = { " + result + " }");
        }
    }

    // ---------- FIRST FUNCTION ----------
    static void findFirst(StringBuilder result, char c) {

        // Terminal → FIRST is the terminal itself
        if (!Character.isUpperCase(c)) {
            addToResult(result, c);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (production[i].charAt(0) == c) {

                // Loop through RHS: for consistency with FOLLOW
                for (int j = 2; j < production[i].length(); j++) {

                    char next = production[i].charAt(j);

                    // If epsilon
                    if (next == '#') {
                        addToResult(result, '#');
                        break;
                    }

                    // FIRST(next)
                    StringBuilder sub = new StringBuilder();
                    findFirst(sub, next);

                    // Add FIRST(next) except duplicates
                    for (int k = 0; k < sub.length(); k++)
                        addToResult(result, sub.charAt(k));

                    // If no epsilon → stop
                    if (sub.indexOf("#") == -1)
                        break;

                    // Otherwise continue to next symbol
                }
            }
        }
    }

    // ---------- FOLLOW FUNCTION ----------
    static void findFollow(StringBuilder result, char c) {

        // Add $ for start symbol
        if (production[0].charAt(0) == c)
            addToResult(result, '$');

        for (int i = 0; i < n; i++) {
            String prod = production[i];

            for (int j = 2; j < prod.length(); j++) {

                if (prod.charAt(j) == c) {

                    // If there is a symbol after c
                    if (j + 1 < prod.length()) {

                        char next = prod.charAt(j + 1);
                        StringBuilder sub = new StringBuilder();

                        findFirst(sub, next);

                        // Add FIRST(next) except epsilon
                        for (int k = 0; k < sub.length(); k++)
                            if (sub.charAt(k) != '#')
                                addToResult(result, sub.charAt(k));

                        // If epsilon → FOLLOW(LHS)
                        if (sub.indexOf("#") != -1)
                            findFollow(result, prod.charAt(0));

                    } else if (prod.charAt(0) != c) {
                        // If at end → FOLLOW(LHS)
                        findFollow(result, prod.charAt(0));
                    }
                }
            }
        }
    }

    // ---------- ADD TO RESULT (Avoid Duplicates) ----------
    static void addToResult(StringBuilder result, char c) {
        if (result.indexOf(String.valueOf(c)) == -1)
            result.append(c);
    }
}