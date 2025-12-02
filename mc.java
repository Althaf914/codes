import java.util.*;

public class mc {

    static int temp = 1;

    static void genArithmetic(String expr) {
        Stack<Character> opStack = new Stack<>();
        Stack<String> valStack = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                System.out.println("LOAD " + c);
                valStack.push(String.valueOf(c));
            }

            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                opStack.push(c);
            }

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

                valStack.push(t);
            }
        }

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
