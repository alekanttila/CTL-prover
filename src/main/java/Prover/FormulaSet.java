package Prover;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static Prover.Formula.Connective.*;

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

    public static TreeSet<FormulaSet> getMPCSets(FormulaSet closureConst, FormulaSet closureNonConst) {
        //we keep passing the original getClosure to perform containment
        //checks, and use a copy that we manipulate to build the hue
        //closureCopy copied here so we can just pass getClosure for convenience
        //this is all ugly because java does not have proper inner methods,
        //and you can't define recursive lambdas within  methods
        FormulaSet closureCopy = new FormulaSet();
        closureCopy.addAll(closureNonConst);

        //we don't want to manipulate the original set;
        //copy for proper call by value
        //FormulaSet set = new FormulaSet();
        //set.addAll(getClosure);
        TreeSet<FormulaSet> mpcSet = new TreeSet<FormulaSet>();

        if (!closureCopy.isEmpty()) {
            Formula f = closureCopy.last();
            closureCopy.remove(f);
            TreeSet<FormulaSet> previousPowerSet = getMPCSets(closureConst, closureCopy);
            Iterator<FormulaSet> i = previousPowerSet.iterator();
            //test here to reduce overall number of checks;
            //some code repeated as a result
            switch (f.c) {
                case NOT:
                    //we do not make new sets with f = NOT g,
                    //since either the previous sets already contain it,
                    //or they contain g
                    //for its negation, NOT NOT g, we do not want to add it
                    //to the sets that contain f = NOT g, but we do want to
                    //add it the sets that contain g just in case NOT NOT g
                    //is in the original getClosure
                    //note also that we are not adding NOT TRUE into any MPC sets
                    boolean notNot = false;
                    if (closureConst.contains(new Formula(f, NOT))) {
                        notNot = true;
                    }
                    while (i.hasNext()) {
                        FormulaSet s = i.next();
                        if (notNot && s.contains(f.sf1)) {
                            s.add(new Formula(f, NOT));
                        }
                        mpcSet.add(s);
                    }
                    break;
                case AND:
                    while (i.hasNext()) {
                        FormulaSet s = i.next();
                        if (s.contains(f.sf1.negated()) || s.contains(f.sf2.negated())) {
                            s.add(new Formula(f, NOT));
                        } else {
                            s.add(f);
                        }
                        mpcSet.add(s);
                    }
                    break;
                case TRUE:
                    while (i.hasNext()) {
                        FormulaSet s = i.next();
                        s.add(f);
                        mpcSet.add(s);
                    }
                    break;
                default:
                    while (i.hasNext()) {
                        FormulaSet s = i.next();
                        FormulaSet notS = new FormulaSet();
                        notS.addAll(s);
                        s.add(f);
                        notS.add(new Formula(f, NOT));
                        mpcSet.add(s);
                        mpcSet.add(notS);
                    }
            }
        } else {
            FormulaSet emptySet = new FormulaSet();
            TreeSet<FormulaSet> pEmptySet = new TreeSet<FormulaSet>();
            pEmptySet.add(emptySet);
            mpcSet = pEmptySet;
        }
        return mpcSet;
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

    public void sugarPrint(Map<Formula, String> formulaeNames) {
        System.out.print("{ ");
        Iterator<Formula> i = this.iterator();
        while (i.hasNext()) {
            Formula f = i.next();
            f.sugarPrint(formulaeNames);
            if (i.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print(" }");
    }

    /*
    public static void printSet(TreeSet<FormulaSet> sFS) {
        System.out.println("{");
        Iterator<FormulaSet> i = sFS.iterator();
        int counter = 0;
        while (i.hasNext()) {
            FormulaSet fS = i.next();
            System.out.print(counter + ": ");
            fS.print();
            if (counter != fS.size()) {
                System.out.println(",");
                counter++;
            }
        }
        System.out.println("}");
    }

    public static void sugarPrintSet(TreeSet<FormulaSet> sFS) {
        System.out.println("{");
        Iterator<FormulaSet> i = sFS.iterator();
        int counter = 0;
        while (i.hasNext()) {
            FormulaSet fS = i.next();
            System.out.print(counter + ": ");
            fS.sugarPrint();
            if (counter != fS.size()) {
                System.out.println(",");
                counter++;
            }
        }
        System.out.println("}");
    }

    public static void sugarPrintSet(TreeSet<FormulaSet> sFS, Map<Formula, String> formulaeNames) {
        System.out.println("{");
        Iterator<FormulaSet> i = sFS.iterator();
        int counter = 0;
        while (i.hasNext()) {
            FormulaSet fS = i.next();
            System.out.print(counter + ": ");
            fS.sugarPrint(formulaeNames);
            if (counter != fS.size()) {
                System.out.println(",");
                counter++;
            }
        }
        System.out.println("}");
    }*/

    public static TreeSet<FormulaSet> getPowerSet(FormulaSet input) {
        //we don't want to manipulate the original set;
        //copy for proper call by value
        FormulaSet set = new FormulaSet();
        set.addAll(input);
        TreeSet<FormulaSet> powerSet = new TreeSet<FormulaSet>();
        if (!set.isEmpty()) {
            Formula f = set.first();
            set.remove(f);
            TreeSet<FormulaSet> previousPowerSet = getPowerSet(set);
            Iterator<FormulaSet> i = previousPowerSet.iterator();
            while (i.hasNext()) {
                FormulaSet s = i.next();
                FormulaSet notS = new FormulaSet();
                notS.addAll(s);
                s.add(f);
                notS.add(new Formula(f, NOT));
                powerSet.add(s);
                powerSet.add(notS);
            }
        } else {
            FormulaSet emptySet = new FormulaSet();
            TreeSet<FormulaSet> pEmptySet = new TreeSet<FormulaSet>();
            pEmptySet.add(emptySet);
            return pEmptySet;
        }
        return powerSet;
    }
}
