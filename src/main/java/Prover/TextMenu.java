package Prover;

import Prover.Formula.*;
import Prover.Prover.Tableau;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;

import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.NONE;
import static Prover.StatusMessage.Level.SOME;

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
        nameMenu(f);

    }

    public static void nameMenu(Formula f) {
        System.out.println("Parsed formula is " + f.sugarString());
        System.out.println("(Optional) Name formula");
        System.out.println("1. α");
        System.out.println("2. β");
        System.out.println("3. γ");
        System.out.println("4. φ");
        System.out.println("5. χ");
        System.out.println("6. ψ");
        System.out.println("7. Name subformulae");
        System.out.println("8. Skip");

        //TODO: regex here for allowable names
        //TODO: name subformulae

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    f.addFormulaName(f, "α");
                    formulaMenu(f);
                    break;
                case 2:
                    f.addFormulaName(f, "β");
                    formulaMenu(f);
                    break;
                case 3:
                    f.addFormulaName(f, "γ");
                    formulaMenu(f);
                    break;
                case 4:
                    f.addFormulaName(f, "φ");
                    formulaMenu(f);
                    break;
                case 5:
                    f.addFormulaName(f, "χ");
                    formulaMenu(f);
                    break;
                case 6:
                    f.addFormulaName(f, "ψ");
                    formulaMenu(f);
                    break;
                case 7:
                    //TODO
                case 8:
                    formulaMenu(f);
                    break;
                default:
                    System.out.println("No such option. Try again.");
            }

        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("Input incorrect. Try again.");
        }
    }

    public static void formulaMenu(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. Check satisfiability");
            System.out.println("2. Get formula information");
            System.out.println("3. Settings");
            System.out.println("4. New formula");
            System.out.println("5. Quit");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 0:
                        Tableau t = new Tableau(f);
                        System.out.println(t.solveBreadthFirst2());
                        System.out.println(t.infoString());
                        break;
                    case 1:
                        Tableau t2 = new Tableau(f);
                        System.out.println(t2.solveBreadthFirst());
                        System.out.println(t2.infoString());
                        break;
                    case 2:
                        infoMenu(f);
                    case 3:
                        settings(f);
                    case 4:
                        scanner.nextLine();
                        start();
                    case 5:
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
            System.out.println("6. Display color successor relation (RX)");
            System.out.println("7. Display rA-equivalence classes (+size)");
            System.out.println("8. Return");
            System.out.println("9. Quit");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        FormulaSet closure = f.getClosure();
                        System.out.println(closure.sugarString(f.getFormulaNames()));
                        System.out.print("Size: ");
                        System.out.println(closure.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 2:
                        HueSet hueSet = f.getAllHues();
                        System.out.println(hueSet.sugarString(0, f.getFormulaNames()));
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
                        System.out.println(colourSet2.hueString());
                        System.out.print("Size: ");
                        System.out.println(colourSet2.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 5:
                        f.getAllHues().printRX(f.getFormulaNames());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 6:
                        f.getAllColours().printRX(f.getFormulaNames());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 7:
                        TreeSet<HueSet> rAClasses = f.getRAClasses();
                        System.out.println("{");
                        for (HueSet c : rAClasses) {
                            System.out.println(c.sugarString(1, f.getFormulaNames()));
                        }
                        System.out.println("}");
                        System.out.print("Size: ");
                        System.out.println(rAClasses.size());
                        System.out.println("Enter any key to return to menu");
                        scanner.nextLine();
                        break;
                    case 8:
                        formulaMenu(f);
                    case 9:
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

    public static void settings(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. Progress message settings");
            System.out.println("2. Character settings");
            if (!Mode.xHues) {
                System.out.println("3. Toggle hue type used (current: regular (slower))");
            } else {
                System.out.println("3. Toggle hue type used (current: more restrictive hues (faster))");
            }
            System.out.println("4. Return");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reportSettings(f);
                    case 2: //TODO
                    case 3:
                        if (Mode.xHues) {
                            Mode.xHues = false;
                        } else {
                            Mode.xHues = true;
                        }
                        settings(f);
                    case 4:
                        formulaMenu(f);
                    default:
                        System.out.println("No such option. Try again.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }
        }
    }

    public static void reportSettings(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. Set progress reporting level");
            System.out.println("2. Set areas to report on");
            System.out.println("3. Return");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reportLevelSettings(f);
                    case 2:
                        reportAreaSettings(f);
                    case 3:
                        settings(f);
                    default:
                        System.out.println("No such option. Try again.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }
        }
    }

    public static void reportAreaSettings(Formula f) {
        while (true) {
            if (StatusMessage.setAreas.isEmpty()) {
                System.out.println("Currently no areas set");
            } else {
                System.out.print("Current areas are");
                for (StatusMessage.Area a : StatusMessage.setAreas) {
                    System.out.print(" " + a);
                }
                System.out.println();
            }
            System.out.println("Enter option number:");
            for (int i = 0; i < StatusMessage.Area.values().length + 1; i++) {
                if (i < StatusMessage.Area.values().length) {
                    System.out.println((i + 1) + ". Toggle " + StatusMessage.Area.values()[i]);
                } else {
                    System.out.println((i + 1) + ". Return");
                }
            }
            try {
                int choice = scanner.nextInt();
                if ((choice - 1) < StatusMessage.Area.values().length) {
                    StatusMessage.Area a = StatusMessage.Area.values()[choice - 1];
                    if (StatusMessage.setAreas.contains(a)) {
                        StatusMessage.setAreas.remove(a);
                    } else {
                        StatusMessage.setAreas.add(a);
                    }
                    reportAreaSettings(f);
                } else if (choice == StatusMessage.Area.values().length + 1) {
                    reportSettings(f);
                } else {
                    System.out.println("No such option. Try again.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }
        }
    }

    public static void reportLevelSettings(Formula f) {
        while (true) {
            System.out.println("Enter option number:");
            System.out.println("1. NONE");
            System.out.println("2. SOME");
            System.out.println("3. MAX");
            System.out.println("(Current level is " + StatusMessage.setLevel + ")");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        StatusMessage.setLevel = NONE;
                        System.out.println("Level set to NONE");
                        reportSettings(f);
                    case 2:
                        StatusMessage.setLevel = SOME;
                        System.out.println("Level set to SOME");
                        reportSettings(f);
                    case 3:
                        StatusMessage.setLevel = MAX;
                        System.out.println("Level set to MAX");
                        reportSettings(f);
                    default:
                        System.out.println("No such option. Try again.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Input incorrect. Try again.");
            }
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