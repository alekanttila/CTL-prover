package Prover;

import java.util.Scanner;
import java.util.Stack;

public class TextMenu {
    static Scanner scanner = new Scanner(System.in);
    public static void start() {
        System.out.println("Enter Formula");
        Formula f = null;
        while (f == null) {
            try {
                String input = scanner.nextLine();
                Stack<Token> tokenStack;
                tokenStack = Lexer.scan(input);
                f = Parser.parse(tokenStack);
                f.createResultSet();
                f.results.setOriginalFormula(input);
                f.results.setTokenStack(tokenStack);
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
        //TODO: regex here for allowable names
        //TODO: name subformulae
        f.results.addFormulaName(f, fName);
        formulaMenu(f);
    }
    public static void formulaMenu(Formula f) {
        System.out.println("Enter option number:");
        System.out.println("1. Prove satisfiability");
        System.out.println("2. Display closure");
        System.out.println("3. Display hues");
        System.out.println("4. Display colours");
        System.out.println("5. Display hue successor relation (rX)");
        System.out.println("6. Display rA");
        System.out.println("7. Display color successor relation (RX)");
        System.out.println("8. Display rA-equivalence classes");
    }


}
