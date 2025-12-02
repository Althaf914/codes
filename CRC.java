import java.util.*;

public class crc {

    static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    static String divide(String dividend, String divisor) {

        int pick = divisor.length();
        String temp = dividend.substring(0, pick);

        while (pick < dividend.length()) {
            if (temp.charAt(0) == '1') {
                temp = xor(divisor, temp) + dividend.charAt(pick);
            } else {
                temp = xor("0".repeat(divisor.length()), temp) + dividend.charAt(pick);
            }
            pick++;
        }

        if (temp.charAt(0) == '1')
            temp = xor(divisor, temp);
        else
            temp = xor("0".repeat(divisor.length()), temp);

        return temp;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Data Bits: ");
        String data = sc.next();

        System.out.print("Enter Key: ");
        String key = sc.next();

        String appended = data + "0".repeat(key.length() - 1);
        String remainder = divide(appended, key);

        String codeword = data + remainder;

        System.out.println("\nSender:");
        System.out.println("Remainder: " + remainder);
        System.out.println("Encoded Data: " + codeword);

        System.out.print("\nEnter Received Bits: ");
        String recv = sc.next();

        String recvRemainder = divide(recv, key);

        if (recvRemainder.contains("1"))
            System.out.println("Error Detected!");
        else
            System.out.println("No Error.");

        sc.close();
    }
}