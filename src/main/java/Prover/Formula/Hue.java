package Prover.Formula;

import java.util.*;

import static Prover.Formula.Formula.Connective.*;

public class Hue extends FormulaSet {

    //names are given in the context of some HueSet, and should
    //not be relied upon for uniquess or comparisons outside the HueSet
    //FormulaSet (and hence Hue) equality is defined in terms of member Formula equality.
    //Hues with the same members but different names are the same hue (to the equals method)
    public final String name;

    public Hue(FormulaSet fS, int index) {
        super.addAll(fS);
        this.name = "h" + index;
    }

    public int getIndex() {
        return Integer.parseInt(this.name.substring(1));
    }

    //only to be used by HueSet
    public boolean rX(Hue h) {
        boolean result = true;
        Iterator<Formula> i = this.iterator();
        A:
        while (i.hasNext()) {
            Formula f = i.next();
            switch (f.getC()) {
                case X: //R1
                    if (!h.contains(f.getSf1())) {
                        result = false;
                        break A;
                    }
                    break;
                case U: //R3
                    if (this.contains(new Formula(f.getSf2(), NOT)) && !h.contains(f)) {
                        result = false;
                        break A;
                    }
                    break;
                case NOT:
                    if (f.getSf1().getC()== X && !h.contains(new Formula(f.getSf1().getSf1(), NOT))) { //R2 //note: negated not needed here
                        result = false;
                        break A;
                    } else if (f.getSf1().getC()== U && this.contains(f.getSf1().getSf1()) && !h.contains(f)) { //R4
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
            switch(f.getC()) {
                case ATOM: //A1 forwards direction
                    if (!h.contains(f)) {
                        result = false;
                        break A;
                    }
                    break;
                case NOT:
                    if (f.getSf1().getC()== ATOM && h.contains(f.getSf1())) { //A1 backwards direction
                        result = false;
                        break A;
                    } else if (f.getSf1().getC()== A && h.contains(f.getSf1())) { //A2 backwards directoin
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

    //hues are immutable
    @Override
    public boolean add(Formula e) {
        //TODO: error!
        return false;
    }

    @Override
    public boolean addAll(Collection e) {
        //TODO: error!
        return false;
    }

    @Override
    public boolean remove(Object e) {
        //TODO: error!
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        //TODO: error!
        return false;
    }

    @Override
    public void clear() {
        //TODO: error!
    }


}
