package Prover.Formula;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class FormulaSet extends TreeSet<Formula> implements Comparable<FormulaSet> {

    @Override
    public int compareTo(FormulaSet fS){
        int result = 0;
        if (this != fS) {
            Iterator<Formula> i = this.iterator();
            Iterator<Formula> i2 = fS.iterator();
            while (i.hasNext() && i2.hasNext()) {
                Formula f = i.next();
                Formula f2 = i2.next();
                int fDiff = f.compareTo(f2);
                if (fDiff != 0) {
                    result = fDiff;
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

    public void print() {
        System.out.print("{ ");
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            f.print();
            if (i.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print(" }");
    }

    public void sugarPrint() {
        System.out.print("{ ");
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            f.sugarPrint();
            if (i.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print(" }");
    }

    public void sugarPrint(Map<Formula, String> formulaNames) {
        System.out.print("{ ");
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            f.sugarPrint(formulaNames);
            if (i.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print(" }");
    }
}
