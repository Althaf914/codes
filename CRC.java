import java.util.*;

public class CRC {

    // XOR operation for CRC division
    static String xorBits(String divisorPart, String currentBits) {
        int len = currentBits.length();
        StringBuilder result = new StringBuilder();

        for (int i = 1; i < len; i++) {
            if (divisorPart.charAt(i) == currentBits.charAt(i))
                result.append('0');
            else
                result.append('1');
        }

        return result.toString();
    }

    // Perform Modulo-2 division
    static String mod2Divide(String dividend, String divisor) {

        int divisorLen = divisor.length();
        String currentSegment = dividend.substring(0, divisorLen);
        int dividendLen = dividend.length();

        int index = divisorLen;

        while (index < dividendLen) {

            // If MSB is 1 → XOR with divisor
            if (currentSegment.charAt(0) == '1')
                currentSegment = xorBits(divisor, currentSegment) + dividend.charAt(index);
            else  // If MSB is 0 → XOR with all zeros
                currentSegment = xorBits("0".repeat(divisorLen), currentSegment) + dividend.charAt(index);

            index++;
        }

        // Last XOR after final step
        if (currentSegment.charAt(0) == '1')
            currentSegment = xorBits(divisor, currentSegment);
        else
            currentSegment = xorBits("0".repeat(divisorLen), currentSegment);

        return currentSegment;
    }

    // Encode data (sender side)
    static String encodeData(String dataBits, String generatorKey) {

        int keyLength = generatorKey.length();

        String dataWithZeros = dataBits + "0".repeat(keyLength - 1);
        String remainder = mod2Divide(dataWithZeros, generatorKey);

        System.out.println("\nData with zeroes appended: " + dataWithZeros);
        System.out.println("Sender side remainder: " + remainder);

        String encodedFrame = dataBits + remainder;
        System.out.println("Encoded Data (Data + Remainder): " + encodedFrame);

        return encodedFrame;
    }

    // Receiver side checking
    static boolean receiverCheck(String receivedFrame, String generatorKey) {

        String remainderAtReceiver = mod2Divide(receivedFrame, generatorKey);

        System.out.println("\nReceived Encoded Data: " + receivedFrame);
        System.out.println("Receiver side remainder: " + remainderAtReceiver);

        // No error if remainder is all zeros
        return !remainderAtReceiver.contains("1");
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the length of data: ");
        int dataLength = sc.nextInt();

        System.out.print("Enter the data bits: ");
        String dataBits = sc.next();

        System.out.print("Enter the length of key: ");
        int keyLength = sc.nextInt();

        System.out.print("Enter the generator key (polynomial bits): ");
        String generatorKey = sc.next();

        System.out.print("Inject error? (1 = Yes, 0 = No): ");
        int injectError = sc.nextInt();

        System.out.println("\n==== Sender Side ====");
        String encodedData = encodeData(dataBits, generatorKey);

        if (injectError == 1) {
            System.out.print("\nEnter bit position to flip (1 to " + encodedData.length() + "): ");
            int bitPosition = sc.nextInt() - 1;

            char[] frameArray = encodedData.toCharArray();
            frameArray[bitPosition] = (frameArray[bitPosition] == '0') ? '1' : '0';
            encodedData = new String(frameArray);
        }

        System.out.println("\n==== Receiver Side ====");
        if (receiverCheck(encodedData, generatorKey))
            System.out.println("\nNo Error Detected.");
        else
            System.out.println("\nError Detected in the Data.");

        sc.close();
    }
}