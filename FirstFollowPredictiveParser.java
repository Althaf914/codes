import java.util.*;

public class FirstFollowPredictiveParser {

    static String[] production = new String[20];
    static String[] firstSet = new String[20];
    static String[] followSet = new String[20];
    static int n;

    static Map<Character, Map<Character, String>> table = new LinkedHashMap<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of productions: ");
        n = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < n; i++)
            production[i] = sc.nextLine().trim().replaceAll("\\s+", "");

        // FIRST sets
        for (int i = 0; i < n; i++) {
            char nt = production[i].charAt(0);
            StringBuilder sb = new StringBuilder();
            findFirst(sb, nt);
            firstSet[i] = sb.toString();
        }

        // FOLLOW sets
        for (int i = 0; i < n; i++) {
            char nt = production[i].charAt(0);
            StringBuilder sb = new StringBuilder();
            findFollow(sb, nt);
            followSet[i] = sb.toString();
        }

        // Build parsing table
        buildParsingTable();

        // Parse input
        System.out.print("\nEnter input string: ");
        String input = sc.nextLine().trim() + "$";

        parse(input);
    }

    // FIRST ----------------------
    static void findFirst(StringBuilder result, char c) {

        if (!Character.isUpperCase(c)) {
            add(result, c);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (production[i].charAt(0) == c) {

                int j = 2;

                if (production[i].charAt(j) == '#') {
                    add(result, '#');
                } else {
                    while (j < production[i].length()) {

                        char next = production[i].charAt(j);
                        StringBuilder temp = new StringBuilder();
                        findFirst(temp, next);

                        for (int k = 0; k < temp.length(); k++)
                            add(result, temp.charAt(k));

                        if (temp.indexOf("#") == -1)
                            break;

                        j++;
                    }
                }
            }
        }
    }

    // FOLLOW ----------------------
    static void findFollow(StringBuilder result, char c) {

        if (production[0].charAt(0) == c)
            add(result, '$');

        for (int i = 0; i < n; i++) {

            String prod = production[i];

            for (int j = 2; j < prod.length(); j++) {

                if (prod.charAt(j) == c) {

                    if (j + 1 < prod.length()) {

                        StringBuilder temp = new StringBuilder();
                        char next = prod.charAt(j + 1);

                        findFirst(temp, next);

                        for (int k = 0; k < temp.length(); k++)
                            if (temp.charAt(k) != '#')
                                add(result, temp.charAt(k));

                        if (temp.indexOf("#") != -1)
                            findFollow(result, prod.charAt(0));

                    } else if (prod.charAt(0) != c) {
                        findFollow(result, prod.charAt(0));
                    }
                }
            }
        }
    }

    // BUILD TABLE -----------------
    static void buildParsingTable() {

        for (int i = 0; i < n; i++) {

            char A = production[i].charAt(0);
            table.putIfAbsent(A, new LinkedHashMap<>());

            String rhs = production[i].substring(2);

            StringBuilder first = new StringBuilder();
            findFirst(first, rhs.charAt(0));

            for (int k = 0; k < first.length(); k++) {
                char a = first.charAt(k);
                if (a != '#')
                    table.get(A).put(a, rhs);
            }

            if (first.indexOf("#") != -1) {

                StringBuilder follow = new StringBuilder();
                findFollow(follow, A);

                for (int k = 0; k < follow.length(); k++) {
                    char b = follow.charAt(k);
                    table.get(A).put(b, rhs);
                }
            }
        }
    }

    // PARSER -----------------------
    static void parse(String input) {

        Stack<Character> st = new Stack<>();
        st.push('$');
        st.push(production[0].charAt(0)); // Start symbol

        int i = 0;
        char a = input.charAt(i);

        while (!st.isEmpty()) {

            char X = st.pop();

            if (!Character.isUpperCase(X)) {

                if (X == a) {
                    if (a == '$') {
                        System.out.println("Input accepted.");
                        return;
                    }
                    i++;
                    a = input.charAt(i);
                } else {
                    System.out.println("Input rejected.");
                    return;
                }

            } else {

                if (!table.containsKey(X) || !table.get(X).containsKey(a)) {
                    System.out.println("Input rejected.");
                    return;
                }

                String rhs = table.get(X).get(a);

                if (!rhs.equals("#")) {
                    for (int k = rhs.length() - 1; k >= 0; k--)
                        st.push(rhs.charAt(k));
                }
            }
        }

        if (a == '$')
            System.out.println("Input accepted.");
        else
            System.out.println("Input rejected.");
    }

    // ADD --------------------------
    static void add(StringBuilder sb, char c) {
        if (sb.indexOf("" + c) == -1)
            sb.append(c);
    }
}
