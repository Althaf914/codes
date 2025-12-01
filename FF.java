import java.util.*;

public class ff{

    static List<String> productions = new ArrayList<>();
    static Map<Character, Set<String>> FIRST = new HashMap<>();
    static Map<Character, Set<String>> FOLLOW = new HashMap<>();
    static int n;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of productions: ");
        n = Integer.parseInt(sc.nextLine());

        System.out.println("Enter productions (use spaces):");
        System.out.println("Example:  E = id X");
        System.out.println("          X = + id X");
        System.out.println("          X = #");

        for (int i = 0; i < n; i++) {
            productions.add(sc.nextLine().trim());
        }
        sc.close();

        // Detect all non-terminals
        for (String prod : productions) {
            char nt = prod.charAt(0);
            FIRST.put(nt, new HashSet<>());
            FOLLOW.put(nt, new HashSet<>());
        }

        // FIRST computation
        for (char nt : FIRST.keySet()) {
            computeFirst(nt);
        }

        // FOLLOW computation
        char start = productions.get(0).charAt(0);
        FOLLOW.get(start).add("$"); // add $ for start symbol

        for (char nt : FOLLOW.keySet()) {
            computeFollow(nt);
        }

        // PRINT FIRST SETS
        System.out.println("\n--- FIRST sets ---");
        for (char nt : FIRST.keySet()) {
            System.out.println("FIRST(" + nt + ") = " + FIRST.get(nt));
        }

        // PRINT FOLLOW SETS
        System.out.println("\n--- FOLLOW sets ---");
        for (char nt : FOLLOW.keySet()) {
            System.out.println("FOLLOW(" + nt + ") = " + FOLLOW.get(nt));
        }
    }

    // ---------- FIRST FUNCTION ----------
    static void computeFirst(char c) {

        for (String prod : productions) {

            if (prod.charAt(0) == c) {

                String rhs = prod.split("=")[1].trim();
                String[] symbols = rhs.split("\\s+");

                for (int j = 0; j < symbols.length; j++) {

                    String sym = symbols[j];

                    // Terminal
                    if (!isNonTerminal(sym)) {
                        FIRST.get(c).add(sym);
                        break;
                    }

                    // Non-terminal
                    computeFirst(sym.charAt(0));

                    for (String x : FIRST.get(sym.charAt(0)))
                        FIRST.get(c).add(x);

                    // If no epsilon stop
                    if (!FIRST.get(sym.charAt(0)).contains("#"))
                        break;
                }
            }
        }
    }

    // ---------- FOLLOW FUNCTION ----------
    static void computeFollow(char c) {

        for (String prod : productions) {

            char lhs = prod.charAt(0);
            String rhs = prod.split("=")[1].trim();
            String[] symbols = rhs.split("\\s+");

            for (int j = 0; j < symbols.length; j++) {

                if (symbols[j].equals(String.valueOf(c))) {

                    // If something exists after c
                    if (j + 1 < symbols.length) {

                        String next = symbols[j + 1];

                        if (!isNonTerminal(next)) {
                            FOLLOW.get(c).add(next); // terminal
                        } else {
                            for (String x : FIRST.get(next.charAt(0)))
                                if (!x.equals("#"))
                                    FOLLOW.get(c).add(x);

                            // epsilon case
                            if (FIRST.get(next.charAt(0)).contains("#"))
                                FOLLOW.get(c).addAll(FOLLOW.get(lhs));
                        }

                    } else if (lhs != c) {
                        // End of RHS → FOLLOW(lhs)
                        FOLLOW.get(c).addAll(FOLLOW.get(lhs));
                    }
                }
            }
        }
    }

    // ---------- helper ----------
    static boolean isNonTerminal(String s) {
        return s.length() == 1 && Character.isUpperCase(s.charAt(0));
    }
}