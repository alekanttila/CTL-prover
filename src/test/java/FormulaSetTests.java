import static Prover.FormulaSet.getPowerSet;
import static Prover.Hue.getMPCSets;
import static org.junit.jupiter.api.Assertions.*;

import Prover.*;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

class FormulaSetTests {

    @Test
    void powerSetTest() {
/*
        FormulaSet atoms = new FormulaSet();
        atoms.add(new Formula("f"));
        atoms.add(new Formula("g"));
        atoms.add(new Formula("h"));

        TreeSet<FormulaSet> powerSet = getPowerSet(atoms);
        FormulaSet.printSet(powerSet);
        assertEquals(8, powerSet.size());*/
    }

    /*
    @Test
    void hueTest() {
        Lexer lexer = new Lexer();
        Stack<Token> stack = lexer.scan();
        lexer.printTokenList();
        Parser parser = new Parser();
        Formula f = parser.parse(stack);
        System.out.println("/////");
        f.print();
        System.out.println("/////");
        TreeSet<FormulaSet> hues = getMPCSets(f.getClosure());
        printSet(hues);
    }
    */


}