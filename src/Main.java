import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Operation {
    SUM {
        int action(int a, int b) { return a + b; }
    },
    SUBTRACT {
        int action(int a, int b) { return a - b; }
    },
    MULTIPLY {
        int action(int a, int b) { return a * b; }
    },
    DIVIDE {
        int action(int a, int b) { return a / b; }
    };

    abstract int action(int a, int b);
}

enum Roman {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    final int value;

    Roman(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    static int toInt(String romanNumber) {
        int result = 0;
        romanNumber = romanNumber.toUpperCase();
        String[] romanNumberArr = romanNumber.split("");
        for (int i = 0; i < romanNumberArr.length - 1; i++) {
            if (Roman.getIndex(romanNumberArr[i]) < Roman.getIndex(romanNumberArr[i + 1])) {
                result -= Roman.valueOf(romanNumberArr[i]).value;
            } else {
                result += Roman.valueOf(romanNumberArr[i]).value;
            }
        }
        return result + Roman.valueOf(romanNumberArr[romanNumberArr.length - 1]).value;
    }

    static String toRoman(int number) {
        Roman[] romanValues = Roman.values();
        StringBuilder result = new StringBuilder();
        int dev;
        byte i = 12;
        while (number > 0) {
            dev = number / romanValues[i].getValue();
            number %= romanValues[i].getValue();
            while (dev != 0) {
                result.append(romanValues[i]);
                dev--;
            }
            i--;
        }
        return result.toString();
    }

    static boolean isValid(String romanNumber) {
        romanNumber = romanNumber.toUpperCase();
        //^(M{0,3})(D?C{0,3}|C[DM])(L?X{0,3}|X[LC])(V?I{0,3}|I[VX])$
        Pattern romanPattern = Pattern.compile("^(M*)(D?C*|C[DM])(L?X*|X[LC])(V?I*|I[VX])$");
        Matcher romanMatcher = romanPattern.matcher(romanNumber);
        return romanMatcher.matches();
    }

    static int getIndex(String r) {
        return Roman.valueOf(r).ordinal();
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String expression;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            expression = scanner.nextLine();
            System.out.println(calc(expression));
        }
    }

    public static String calc(String input) throws Exception {
        String[] expression = input.split(" ");

        if (expression.length != 3) {
            throw new Exception("Ошибка: формат записи, требуется два операнда и один оператор");
        }

        if (Roman.isValid(expression[0]) && !Roman.isValid(expression[2]) ||
                !Roman.isValid(expression[0]) && Roman.isValid(expression[2])) {
            throw new Exception("Ошибка: используйте только одну систему счисления");
        }

        int number1, number2;
        boolean isRoman = Roman.isValid(expression[0]) && Roman.isValid(expression[2]);
        try {
            number1 = isRoman ? Roman.toInt(expression[0]) : Integer.parseInt(expression[0]);
            number2 = isRoman ? Roman.toInt(expression[2]) : Integer.parseInt(expression[2]);
        } catch (NumberFormatException e) {
            throw new Exception("Ошибка: некорректное римское число");
        }

        if (number1 < 1 || number1 > 10 || number2 < 1 || number2 > 10) {
            throw new Exception("Ошибка: калькулятор поддерживает числа от 1(I) до 10(X) включительно");
        }

        Operation op;
        switch (expression[1]) {
            case "+" -> op = Operation.SUM;
            case "-" -> op = Operation.SUBTRACT;
            case "*" -> op = Operation.MULTIPLY;
            case "/" -> op = Operation.DIVIDE;
            default -> throw new Exception("Ошибка: некорректный оператор, получено " + expression[1]);
        }

        int result;
        try {
            result = op.action(number1, number2);
        } catch (ArithmeticException e) {
            throw new Exception("Ошибка: делить на ноль нельзя");
        }

        if (result < 1 && isRoman) {
            throw new Exception("Ошибка: в римской системе счисления нет 0 и отрицательных чисел");
        }

        return isRoman ? Roman.toRoman(result) : Integer.toString(result);
    }
}
