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
            production[i] = sc.nextLine().trim().replaceAll("\\s+","");
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

        if (!Character.isUpperCase(c)) {
            addToResult(result, c);
            return;
        }

        for (int i = 0; i < n; i++) {
            if (production[i].charAt(0) == c) {

                int j = 2;

                if (production[i].charAt(j) == '#') {
                    addToResult(result, '#');
                } else {
                    while (j < production[i].length()) {
                        char next = production[i].charAt(j);
                        StringBuilder sub = new StringBuilder();

                        findFirst(sub, next);

                        for (int k = 0; k < sub.length(); k++)
                            addToResult(result, sub.charAt(k));

                        if (sub.indexOf("#") == -1)
                            break;

                        j++;
                    }
                }
            }
        }
    }

    // ---------- FOLLOW FUNCTION ----------
    static void findFollow(StringBuilder result, char c) {

        if (production[0].charAt(0) == c)
            addToResult(result, '$');   // Start symbol

        for (int i = 0; i < n; i++) {
            String prod = production[i];

            for (int j = 2; j < prod.length(); j++) {
                if (prod.charAt(j) == c) {

                    if (j + 1 < prod.length()) {

                        StringBuilder sub = new StringBuilder();
                        char next = prod.charAt(j + 1);

                        findFirst(sub, next);

                        for (int k = 0; k < sub.length(); k++)
                            if (sub.charAt(k) != '#')
                                addToResult(result, sub.charAt(k));

                        if (sub.indexOf("#") != -1)
                            findFollow(result, prod.charAt(0));

                    } else if (prod.charAt(0) != c) {
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