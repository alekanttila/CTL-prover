package Prover;

import java.util.*;

import static Prover.Formula.Connective.*;

public class Hue extends FormulaSet {

    //names are given in the context of some HueSet, and should
    //not be relied upon for uniquess or comparisons outside the HueSet
    //FormulaSet (and hence Hue) equality is defined in terms of member Formula equality.
    //Hues with the same members but different names are the same hue (to the equals method)
    protected String name;

    public int getIndex() {
        return Integer.parseInt(this.name.substring(1));
    }

    public boolean rX(Hue h) {
        boolean result = true;
        Iterator<Formula> i = this.iterator();
        A:
        while (i.hasNext()) {
            Formula f = i.next();
            switch (f.c) {
                case X: //R1
                    if (!h.contains(f.sf1)) {
                        result = false;
                        break A;
                    }
                    break;
                case U: //R3
                    if (this.contains(new Formula(f.sf2, NOT)) && (h.contains(f))) {
                        result = false;
                        break A;
                    }
                    break;
                case NOT:
                    if (f.sf1.c == X && !h.contains(new Formula(f.sf1.sf1, NOT))) { //R2
                        result = false;
                        break A;
                    } else if (f.sf1.c == U && this.contains(f.sf1.sf1) && !h.contains(f)) { //R4
                        result = false;
                        break A;
                    }
                    break;
                default:
                    //do nothing
            }
        }
        return result;
    }

    public boolean rA(Hue h) {
        boolean result = true;
        Iterator<Formula> i = this.iterator();
        A:
        while (i.hasNext()) {
            Formula f = i.next();
            switch(f.c) {
                case ATOM: //A1 forwards direction
                    if (!h.contains(f)) {
                        result = false;
                        break A;
                    }
                    break;
                case NOT:
                    if (f.sf1.c == ATOM && h.contains(f.sf1)) { //A1 backwards direction
                        result = false;
                        break A;
                    } else if (f.sf1.c == A && h.contains(f.sf1)) { //A2 backwards directoin
                        result = false;
                        break A;
                    }
                    break;
                case A: //A2 forwards direction
                    if (!h.contains(f)) {
                        result = false;
                        break A;
                    }
                    break;
                default:
                    //do nothing
            }
        }
        return result;
    }


}