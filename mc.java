import java.util.*;

public class mc {

    static int temp = 1;

    static void genArithmetic(String expr) {
        Stack<Character> opStack = new Stack<>();
        Stack<String> valStack = new Stack<>();   // holds operands (a, b, t1, t2)

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // If operand
            if (Character.isLetterOrDigit(c)) {
                System.out.println("LOAD " + c);
                valStack.push(String.valueOf(c));
            }
            // If operator
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                opStack.push(c);
            }
            // When encountering ')', perform operation
            else if (c == ')') {
                char op = opStack.pop();

                String right = valStack.pop();
                String left = valStack.pop();

                if (op == '+') System.out.println("ADD " + left + ", " + right);
                if (op == '-') System.out.println("SUB " + left + ", " + right);
                if (op == '*') System.out.println("MUL " + left + ", " + right);
                if (op == '/') System.out.println("DIV " + left + ", " + right);

                String t = "t" + temp++;
                System.out.println("STORE " + t);

                valStack.push(t);     // result becomes new operand
            }
        }

        // Extra operators (rare in fully parenthesized expressions)
        while (!opStack.isEmpty()) {
            char op = opStack.pop();
            String right = valStack.pop();
            String left = valStack.pop();

            if (op == '+') System.out.println("ADD " + left + ", " + right);
            if (op == '-') System.out.println("SUB " + left + ", " + right);
            if (op == '*') System.out.println("MUL " + left + ", " + right);
            if (op == '/') System.out.println("DIV " + left + ", " + right);

            String t = "t" + temp++;
            System.out.println("STORE " + t);

            valStack.push(t);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter arithmetic expression (e.g., (a+b)*c): ");
        String expr = sc.nextLine();
        sc.close();

        System.out.println("\n--- GENERATED MACHINE CODE ---");
        genArithmetic(expr);
    }
}
