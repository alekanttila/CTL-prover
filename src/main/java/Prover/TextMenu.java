package Prover;

import Prover.Formula.*;
import Prover.Prover.Tableau;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;

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
                f.setInitialResults(input, tokenStack);
            } catch (LexerException e) {
                e.printStackTrace();
                System.out.println();
                System.out.println("Try again.");
            } catch (ParserException e) {
                e.printStackTrace();
                System.out.println();
                System.out.println("Try again.");
            }
        }
        System.out.println("(Optional) name formula");
        String fName = scanner.nextLine();
        //TODO: regex here for allowable names
        //TODO: name subformulae
        f.addFormulaName(f, fName);
        formulaMenu(f);
    }

    public static void formulaMenu(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. Check satisfiability");
            System.out.println("2. Get information");
            System.out.println("3. Quit");


            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        Tableau t = new Tableau(f);
                        System.out.println(t.solveBreadthFirst());
                        t.printInfo();
                        break;
                    case 2:
                        infoMenu(f);
                    case 3:
                        System.exit(0);
                    default:
                        System.out.println("No such option. Try again.");
                }

            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }

        }

    }

    public static void infoMenu(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. Display closure (+size)");
            System.out.println("2. Display hues (+size)");
            System.out.println("3. Display full colours (+size)");
            System.out.println("4. Display colours in terms of hues (+size)");
            System.out.println("5. Display hue successor relation (rX)");
            System.out.println("6. Display rA");
            System.out.println("7. Display color successor relation (RX)");
            System.out.println("8. Display rA-equivalence classes (+size)");
            System.out.println("9. Return");
            System.out.println("10. Quit");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        FormulaSet closure = f.getClosure();
                        closure.sugarPrint(f.getFormulaNames());
                        System.out.println();
                        System.out.print("Size: ");
                        System.out.println(closure.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 2:
                        HueSet hueSet = f.getAllHues();
                        hueSet.sugarPrint(f.getFormulaNames());
                        System.out.println();
                        System.out.print("Size: ");
                        System.out.println(hueSet.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 3:
                        ColourSet colourSet = f.getAllColours();
                        colourSet.sugarPrint(f.getFormulaNames());
                        System.out.print("Size: ");
                        System.out.println(colourSet.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 4:
                        ColourSet colourSet2 = f.getAllColours();
                        colourSet2.huePrint();
                        System.out.println();
                        System.out.print("Size: ");
                        System.out.println(colourSet2.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 5:
                        printRelation(f.getHueRX(), "rX");
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 6:
                        printRelation(f.getRA(), "rA");
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 7:
                        printRelation(f.getColourRX(), "RX");
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 8:
                        TreeSet<HueSet> rAClasses = f.getRAClasses();
                        System.out.println("{");
                        for (HueSet c : rAClasses) {
                            c.sugarPrint(f.getFormulaNames());
                            System.out.println();
                        }
                        System.out.println("}");
                        System.out.print("Size: ");
                        System.out.println(rAClasses.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 9:
                        formulaMenu(f);
                    case 10:
                        System.exit(0);
                    default:
                        System.out.println("No such option. Try again.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }
        }
    }

    static public void prompt(String s) {
        try {
            int count = System.in.available();
            while (count > 0) {
                System.in.skip(count);
                count = System.in.available();
            }
            if (s != null) {
                System.out.print(s);
            }
            System.in.read();
        } catch (IOException e) {
        }
    }


    public static void printRelation(boolean[][] r, String rName) {
        String memberInitial = null;
        switch (rName.charAt(0)) {
            case 'r':
                memberInitial = "h";
                break;
            case 'R':
                memberInitial = "c";
                break;
            default:
                throw new AssertionError("Relation name incorrectly specified");
        }
        boolean printed = false;
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < r[i].length; j++) {
                if (r[i][j]) {
                    System.out.print(memberInitial + i + rName + memberInitial + j + " ");
                    printed = true;
                }
            }
            if (printed) {
                System.out.println();
                printed = false;
            }
        }
    }
}