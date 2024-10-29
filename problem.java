import java.math.BigInteger;
import java.util.*;

class Point {
    int x;
    BigInteger y;
    
    public Point(int x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}

class TestCase {
    int n;
    int k;
    Map<Integer, Map<String, String>> points;
    
    public TestCase(int n, int k) {
        this.n = n;
        this.k = k;
        this.points = new HashMap<>();
    }
    
    public void addPoint(int key, String base, String value) {
        Map<String, String> point = new HashMap<>();
        point.put("base", base);
        point.put("value", value);
        points.put(key, point);
    }
}

public class ShamirSecretSharing {
    private static BigInteger decodeValue(String base, String value) {
        try {
            switch (base) {
                case "2":
                case "3":
                case "4":
                case "8":
                case "10":
                case "16":
                    return new BigInteger(value, Integer.parseInt(base));
                case "6":
                    // Custom base 6 decoder
                    BigInteger result = BigInteger.ZERO;
                    for (char c : value.toCharArray()) {
                        result = result.multiply(BigInteger.valueOf(6))
                                     .add(BigInteger.valueOf(Character.getNumericValue(c)));
                    }
                    return result;
                case "15":
                    // Custom base 15 decoder
                    result = BigInteger.ZERO;
                    String base15Chars = "0123456789abcdef";
                    value = value.toLowerCase();
                    for (char c : value.toCharArray()) {
                        result = result.multiply(BigInteger.valueOf(15))
                                     .add(BigInteger.valueOf(base15Chars.indexOf(c)));
                    }
                    return result;
                default:
                    throw new IllegalArgumentException("Unsupported base: " + base);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error decoding value: " + value + " in base " + base, e);
        }
    }
    
    private static List<Point> processTestCase(TestCase testCase) {
        List<Point> points = new ArrayList<>();
        
        for (Map.Entry<Integer, Map<String, String>> entry : testCase.points.entrySet()) {
            int x = entry.getKey();
            Map<String, String> point = entry.getValue();
            BigInteger y = decodeValue(point.get("base"), point.get("value"));
            points.add(new Point(x, y));
        }
        
        return points;
    }
    
    private static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    // Calculate 0 - xj for numerator
                    numerator = numerator.multiply(BigInteger.valueOf(-points.get(j).x));
                    // Calculate xi - xj for denominator
                    denominator = denominator.multiply(
                        BigInteger.valueOf(points.get(i).x - points.get(j).x));
                }
            }
            
            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        
        return result;
    }
    
    private static BigInteger solvePolynomial(TestCase testCase) {
        List<Point> points = processTestCase(testCase);
        return lagrangeInterpolation(points);
    }
    
    public static void main(String[] args) {
        // First test case
        TestCase testCase1 = new TestCase(4, 3);
        testCase1.addPoint(1, "10", "4");
        testCase1.addPoint(2, "2", "111");
        testCase1.addPoint(3, "10", "12");
        testCase1.addPoint(6, "4", "213");
        
        // Second test case
        TestCase testCase2 = new TestCase(10, 7);
        testCase2.addPoint(1, "6", "134442114404553455511");
        testCase2.addPoint(2, "15", "aed7015a346d63");
        testCase2.addPoint(3, "15", "6aeeb69631c227c");
        testCase2.addPoint(4, "16", "e1b5e05623d881f");
        testCase2.addPoint(5, "8", "31603451457365262673");
        testCase2.addPoint(6, "3", "212221220112200222112020021001102022020");
        
        try {
            BigInteger secret1 = solvePolynomial(testCase1);
            BigInteger secret2 = solvePolynomial(testCase2);
            
            System.out.println("Secret for test case 1: " + secret1);
            System.out.println("Secret for test case 2: " + secret2);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}