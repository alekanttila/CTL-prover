    package Prover;

    import java.util.*;

    import static Prover.TextMenu.printRelation;

    public class Main {
    public static void main(String args[]){
        TextMenu.start();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println("START");
        Lexer lexer = new Lexer();
        Stack<Token> stack = null;
        try {
            stack = lexer.scan(input);
        } catch (LexerException e) {

        }
        lexer.printTokenList();
        Parser parser = new Parser();
        Formula f = null;
        try {
            f = parser.parse(stack);
        } catch (ParserException e) {

        }
        System.out.println();
        System.out.println("/////////////");
        f.print();
        System.out.println();
        System.out.println("///////////////");
        System.out.println("FINIIISH");
        FormulaSet closure = f.getClosure();
        HueSet hues = HueSet.getAllHues(closure);
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
        //printRelation(hues.generateRX(), "h", "rX");
        System.out.println();
        //printRelation(hues.generateRA(), "h", "rA");
        TreeSet<HueSet> rAClasses = hues.getRAClasses();
        System.out.println(rAClasses.size());
        //for (HueSet s : rAClasses) {
        //    s.sugarPrint();
        //}
        //ColourSet colours = getAllColours(hues);
        System.out.println("COLOURS");
        //for (Colour c : colours) {
        //    c.sugarPrint();
        //}
        //Hue empty = new Hue();
        //System.out.println(colours.size());
        //printRelation(colours.generateRX(),"c", "RX");
        //System.out.println(hues.first().rX(hues.first()));
    }
}
