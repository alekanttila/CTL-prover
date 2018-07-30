package Prover.Formula;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class FormulaSet extends TreeSet<Formula> implements Comparable<FormulaSet> {

    /*
    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        if (this == obj) {
            result = true;
        } else if (!FormulaSet.class.isAssignableFrom(obj.getClass())) {
            result = false;
        } else {
            FormulaSet fS = (FormulaSet) obj;
            Iterator<Formula> i = this.iterator();
            Iterator<Formula> i2 = fS.iterator();
            while (i.hasNext() && i2.hasNext()) {
                Formula f = i.next();
                Formula f2 = i2.next();
                if (!f.equals(f2)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }*/

    @Override
    public int compareTo(FormulaSet fS){
        int result = 0;
        if (this != fS) {
            Iterator<Formula> i = this.iterator();
            Iterator<Formula> i2 = fS.iterator();
            while (i.hasNext() && i2.hasNext()) {
                Formula f = i.next();
                Formula f2 = i2.next();
                if (!f.equals(f2)) {
                    result = f.compareTo(f2);
                    break;
                }
            }
            if (result == 0 && i.hasNext() && !i2.hasNext()) {
                result = 1;
            } else if (result == 0 && !i.hasNext() && i2.hasNext()) {
                result = -1;
            } else {
                //TODO: error
            }
        }
        return result;
    }

    //for minimising heap space
    public Formula getReference(Formula f1) {
        Formula result = null;
        for (Formula f2 : this) {
            if (f1.equals(f2)) {
                result = f2;
            }
        }
        if (result == null) {
            //TODO: error
        }
        return result;
    }

    public String printString() {
        String result = "{";
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            result = result + f.printString();
            if (i.hasNext()) {
                result = result + ", ";
            }
        }
        result = result + "}";
        return result;
    }

    public String sugarString() {
        String result = "{ ";
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            result = result + f.sugarString();
            if (i.hasNext()) {
                result = result + ", ";
            }
        }
        result = result + " }";
        return result;
    }

    public String sugarString(Map<Formula, String> formulaNames) {
        String result = "{ ";
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            result = result + f.sugarString(formulaNames);
            if (i.hasNext()) {
                result = result + ", ";
            }
        }
        result = result + " }";
        return result;
    }
}
