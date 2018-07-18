package Prover;

import java.text.ParseException;
import java.util.Scanner;
import java.util.Stack;

public class TextMenu {
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Formula");
        Formula parsedF = null;
        while (parsedF == null) {
            try {
                String input = scanner.nextLine();
                Stack<Token> tokenStack;
                tokenStack = Lexer.scan(input);
                parsedF = Parser.parse(tokenStack);
            } catch (LexerException e) {
                e.printStackTrace();
                System.out.println();
                System.out.println("Try again");
            } catch (ParserException e) {
                e.printStackTrace();
                System.out.println();
                System.out.println("Try again");
            }
        }
        System.out.println("(Optional) name formula");
        String fName = scanner.nextLine();
        //regex here

    }
}
