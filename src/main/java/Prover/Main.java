    package Prover;

    import java.util.*;

    import static Prover.Colour.getColours;
    import static Prover.HueSet.getHueSet;
    import static Prover.HueSet.printRelation;

    public class Main {
    public static void main(String args[]){
        System.out.println("START");
        Lexer lexer = new Lexer();
        Stack<Token> stack = lexer.scan();
        lexer.printTokenList();
        Parser parser = new Parser();
        Formula f = parser.parse(stack);
        System.out.println();
        System.out.println("/////////////");
        f.print();
        System.out.println();
        System.out.println("///////////////");
        System.out.println("FINIIISH");
        FormulaSet closure = f.getClosure();
        HueSet hues = getHueSet(closure, closure);
        Map<Formula, String> names = new HashMap<Formula, String>();
        names.put(f, "φ");
        System.out.println("HUE");
        hues.sugarPrint(names);
        System.out.println(hues.size());
        System.out.println("getClosure:");
        int x = 0;
        for (FormulaSet s : hues) {
            if (s.contains(new Formula(new Formula(Formula.Connective.TRUE), Formula.Connective.NOT))) {
                x++;
            }
        }
        System.out.println(x);
        closure.sugarPrint(names);
        System.out.println("getClosure done");
        f.sugarPrint(names);
        System.out.println();
        hues.generateNames();
        printRelation(hues.generateRX());
        System.out.println();
        printRelation(hues.generateRA());
        TreeSet<HueSet> rAClasses = hues.getRAClasses();
        System.out.println(rAClasses.size());
        for (HueSet s : rAClasses) {
            s.sugarPrint();
        }
        TreeSet<Colour> colours = getColours(hues);
        System.out.println("COLOURS");
        for (Colour c : colours) {
            c.sugarPrint();
        }
        Hue empty = new Hue();
        System.out.println(colours.size());

    }
}
