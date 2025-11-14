import java.util.*;

public class TAC_Simple {
    
    static int t= 1;

    static String newTemp(){
        return "t"+ (t++);
    }

    public static String generate(String expr, List<String> tac){
        int n= expr.length();
        int bal= 0;

        for(int i=n-1; i>=0; i--){
            char c= expr.charAt(i);

            if(c == ')') bal++;
            if(c == '(') bal--;

            if(bal==0 && (c=='+' || c=='-')){
                String left= generate(expr.substring(0,i), tac);
                String right= generate(expr.substring(i+1), tac);
                String temp= newTemp();
                tac.add(temp+" = " + left + " " + c + " " + right);
                return temp;
            }
        }

        for(int i=n-1; i>=0; i--){
            char c= expr.charAt(i);

            if(c == ')') bal++;
            if(c == '(') bal--;

            if(bal==0 && (c=='*' || c=='/')){
                String left= generate(expr.substring(0,i), tac);
                String right= generate(expr.substring(i+1), tac);
                String temp= newTemp();
                tac.add(temp+" = " + left + " " + c + " " + right);
                return temp;
            }
        }

        if(expr.charAt(0)=='(' && expr.charAt(n-1)==')'){
            return generate(expr.substring(1,n-1), tac);
        }

        return expr;
    }

    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        String input= sc.nextLine().trim().replaceAll("\\s+", "");

        String parts[]= input.split("=");   

        String left= parts[0];
        String right= parts[1];

        List<String> tac= new ArrayList<>();

        String finalTemp= generate(right, tac);

        tac.add(left + " = " + finalTemp);

        System.out.println("\nThree Address Code:");
        for (String s : tac)
            System.out.println(s);
    }
}
