import java.util.*;

public class lalr {

    // ACTION table (state x terminal)
    // terminals:   i   +   *   (   )   $
    static int[][] ACTION = {
        {5, 0, 0, 4, 0, 0},
        {0, 6, 0, 0, 0, 999},
        {0, -2, 7, 0, -2, -2},
        {0, -4, -4, 0, -4, -4},
        {5, 0, 0, 4, 0, 0},
        {0, -6, -6, 0, -6, -6},
        {5, 0, 0, 4, 0, 0},
        {5, 0, 0, 4, 0, 0},
        {0, 6, 0, 0, 11, 0},
        {0, -1, 7, 0, -1, -1},
        {0, -3, -3, 0, -3, -3},
        {0, -5, -5, 0, -5, -5}
    };

    // GOTO table (state x non-terminal)
    // non-terminals: E, T, F
    static int[][] GOTO = {
        {1, 2, 3},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {8, 2, 3},
        {0, 0, 0},
        {0, 9, 3},
        {0, 0, 10},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };

    // Production lengths
    static int[] prodLen = {0, 3, 1, 3, 1, 3, 1};

    // Production LHS
    static char[] prodLHS = {' ', 'E', 'E', 'T', 'T', 'F', 'F'};

    // Terminal indexing
    static int termIndex(char c) {
        return switch(c) {
            case 'i' -> 0;
            case '+' -> 1;
            case '*' -> 2;
            case '(' -> 3;
            case ')' -> 4;
            case '$' -> 5;
            default -> -1;
        };
    }

    // Non-terminal indexing
    static int nonTermIndex(char c) {
        return switch(c) {
            case 'E' -> 0;
            case 'T' -> 1;
            case 'F' -> 2;
            default -> -1;
        };
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Grammar:");
        System.out.println("1) E -> E + T");
        System.out.println("2) E -> T");
        System.out.println("3) T -> T * F");
        System.out.println("4) T -> F");
        System.out.println("5) F -> ( E )");
        System.out.println("6) F -> i\n");

        System.out.print("Enter expression (use i for identifiers): ");
        String input = sc.nextLine();
        sc.close();

        input = input + "$";

        int[] stack = new int[100];
        int top = 0;
        stack[top] = 0;

        int ip = 0;

        while (true) {

            // SKIP SPACES
            while (ip < input.length() && input.charAt(ip) == ' ')
                ip++;

            char symbol = input.charAt(ip);
            int t = termIndex(symbol);

            if (t == -1) {
                System.out.println("Invalid symbol: " + symbol);
                return;
            }

            int state = stack[top];
            int action = ACTION[state][t];

            if (action > 0 && action != 999) {   // SHIFT
                top++;
                stack[top] = action;
                ip++;
            }
            else if (action < 0) {              // REDUCE
                int prod = -action;
                int len = prodLen[prod];
                char A = prodLHS[prod];

                top -= len;

                int gotoState = GOTO[stack[top]][nonTermIndex(A)];

                top++;
                stack[top] = gotoState;
            }
            else if (action == 999) {           // ACCEPT
                System.out.println("Accepted");
                return;
            }
            else {                              // ERROR
                System.out.println("Rejected at: " + symbol);
                return;
            }
        }
    }
}
