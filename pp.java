import java.util.*;

public class pp {

    static List<String> productions = new ArrayList<>();
    static Map<Character, Set<String>> FIRST = new HashMap<>();
    static Map<Character, Set<String>> FOLLOW = new HashMap<>();
    static Map<Character, Map<String, String>> TABLE = new HashMap<>();

    static int n;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of productions: ");
        n = Integer.parseInt(sc.nextLine());

        System.out.println("Enter productions (use spaces):");
        for (int i = 0; i < n; i++)
            productions.add(sc.nextLine().trim());

        // Detect non-terminals
        for (String prod : productions) {
            char nt = prod.charAt(0);
            FIRST.put(nt, new HashSet<>());
            FOLLOW.put(nt, new HashSet<>());
            TABLE.put(nt, new HashMap<>());
        }

        // FIRST
        for (char nt : FIRST.keySet())
            computeFirst(nt);

        // FOLLOW
        char start = productions.get(0).charAt(0);
        FOLLOW.get(start).add("$");

        for (char nt : FOLLOW.keySet())
            computeFollow(nt);

        // PRINT FIRST
        System.out.println("\n--- FIRST sets ---");
        for (char nt : FIRST.keySet())
            System.out.println("FIRST(" + nt + ") = " + FIRST.get(nt));

        // PRINT FOLLOW
        System.out.println("\n--- FOLLOW sets ---");
        for (char nt : FOLLOW.keySet())
            System.out.println("FOLLOW(" + nt + ") = " + FOLLOW.get(nt));

        // Build LL(1)
        buildLL1Table();

        // Input parsing
        System.out.print("\nEnter input string (tokens separated by spaces): ");
        String input = sc.nextLine().trim() + " $";

        sc.close();

        boolean ok = parseInput(input);

        if (ok)
            System.out.println("\n✔ INPUT ACCEPTED!");
        else
            System.out.println("\n❌ INPUT REJECTED!");
    }

    // ---------------------------------------------------
    static void computeFirst(char c) {

        for (String prod : productions) {

            if (prod.charAt(0) == c) {

                String rhs = prod.split("=")[1].trim();
                String[] symbols = rhs.split("\\s+");

                for (String sym : symbols) {

                    if (!isNonTerminal(sym)) { // terminal
                        FIRST.get(c).add(sym);
                        break;
                    }

                    computeFirst(sym.charAt(0)); // recursion

                    FIRST.get(c).addAll(FIRST.get(sym.charAt(0)));

                    if (!FIRST.get(sym.charAt(0)).contains("#"))
                        break;
                }
            }
        }
    }

    // ---------------------------------------------------
    static void computeFollow(char c) {

        for (String prod : productions) {

            char lhs = prod.charAt(0);
            String rhs = prod.split("=")[1].trim();
            String[] symbols = rhs.split("\\s+");

            for (int j = 0; j < symbols.length; j++) {

                if (symbols[j].equals(String.valueOf(c))) {

                    if (j + 1 < symbols.length) {

                        String next = symbols[j + 1];

                        if (!isNonTerminal(next)) {
                            FOLLOW.get(c).add(next);
                        } else {
                            for (String x : FIRST.get(next.charAt(0)))
                                if (!x.equals("#"))
                                    FOLLOW.get(c).add(x);

                            if (FIRST.get(next.charAt(0)).contains("#"))
                                FOLLOW.get(c).addAll(FOLLOW.get(lhs));
                        }

                    } else if (lhs != c) {
                        FOLLOW.get(c).addAll(FOLLOW.get(lhs));
                    }
                }
            }
        }
    }

    // ---------------------------------------------------
    // LL(1) TABLE
    // ---------------------------------------------------
    static void buildLL1Table() {

        for (String prod : productions) {

            char A = prod.charAt(0);
            String rhs = prod.split("=")[1].trim();

            Set<String> firstRHS = firstOfString(rhs);

            for (String t : firstRHS)
                if (!t.equals("#"))
                    TABLE.get(A).put(t, rhs);

            if (firstRHS.contains("#"))
                for (String b : FOLLOW.get(A))
                    TABLE.get(A).put(b, rhs);
        }
    }

    static Set<String> firstOfString(String rhs) {

        String[] symbols = rhs.split("\\s+");
        Set<String> result = new HashSet<>();

        for (String sym : symbols) {

            if (!isNonTerminal(sym)) {
                result.add(sym);
                return result;
            }

            result.addAll(FIRST.get(sym.charAt(0)));

            if (!FIRST.get(sym.charAt(0)).contains("#"))
                return result;
        }

        result.add("#");
        return result;
    }

    // ---------------------------------------------------
    // INPUT PARSER (NO STEPS PRINTED)
    // ---------------------------------------------------
    static boolean parseInput(String input) {

        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(String.valueOf(productions.get(0).charAt(0)));

        String[] tokens = input.split("\\s+");
        int idx = 0;

        while (!stack.isEmpty()) {

            String top = stack.peek();
            String cur = tokens[idx];

            // terminal match
            if (!isNonTerminal(top)) {

                if (top.equals(cur)) {
                    stack.pop();
                    idx++;
                } else {
                    return false;
                }

            } else { // NT case

                char A = top.charAt(0);

                if (!TABLE.get(A).containsKey(cur))
                    return false;

                String rhs = TABLE.get(A).get(cur);
                stack.pop();

                if (!rhs.equals("#")) {
                    String[] symbols = rhs.split("\\s+");

                    for (int i = symbols.length - 1; i >= 0; i--)
                        stack.push(symbols[i]);
                }
            }
        }

        return true;
    }

    // ---------------------------------------------------
    static boolean isNonTerminal(String s) {
        return s.length() == 1 && Character.isUpperCase(s.charAt(0));
    }
}