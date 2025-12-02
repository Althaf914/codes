import java.util.*;

public class la{
    
    static final Set<String> keywords= new HashSet<>(Arrays.asList(
        "int", "float", "if", "else", "else if", "return", "while", "for", "do"
    ));


    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        StringBuilder code= new StringBuilder();

        System.out.println("Enter the code (type END to stop)");

        while(true){
            String line= sc.nextLine();

            if(line.equals("END")) break;
            code.append(line).append("\n");
        }

        analyze(code.toString());
    }

    public static void analyze(String code){
        int i=0, n= code.length();

        while(i<n){
            char ch= code.charAt(i);

            if(Character.isWhitespace(ch)){
                i++;
                continue;
            }

            if(ch=='/' && i+1<n && code.charAt(i+1)=='/'){
                i+=2;

                while(i<n && code.charAt(i) != '\n') i++;
                continue;
            }

            if(Character.isLetter(ch) || ch=='_'){
                StringBuilder word= new StringBuilder();
                while(i<n && (Character.isLetterOrDigit(code.charAt(i))) || code.charAt(i)=='_'){
                    word.append(code.charAt(i));
                    i++;
                }

                String token= word.toString();

                if(keywords.contains(token)){
                    System.out.println(token+" : Keyword");
                }else{
                    System.out.println(token+" : Identifier");
                }

                continue;
            }

            if(Character.isDigit(ch)){
                StringBuilder num= new StringBuilder();
                while(i<n && Character.isDigit(code.charAt(i))){
                    num.append(code.charAt(i));
                    i++;
                }

                System.out.println(num+ " : Number");
                continue;
            }

            if("+-*/%=<>(){};,".indexOf(ch)!=-1){
                System.out.println(ch + " : Operator/Punctuation");
                i++;
                continue;
            }

            System.out.println(ch+" : Unknown");
            i++;
        }

        System.out.println("\nEnd of Tokens");
    }
}