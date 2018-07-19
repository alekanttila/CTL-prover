package Prover;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static Prover.Formula.Connective.*;

public class HueSet extends TreeSet<Hue> implements Comparable<HueSet> {

    protected FormulaSet instantiables = new FormulaSet();

    public HueSet() {
        super();
    }

    public HueSet(Formula f) {
        FormulaSet closure = f.getClosure();
        this.addAll(getAllHues(closure));
    }

    //@Override
    public int compareTo(HueSet hS) {
        int result = 0;
        Iterator<Hue> i = this.iterator();
        Iterator<Hue> i2 = hS.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            if (i2.hasNext()) {
                Hue h2 = i2.next();
                int hDiff = h.compareTo(h2);
                if (hDiff != 0) {
                    result = hDiff;
                    break;
                }
            } else {
                //if this set has the same first elements as hS but is larger:
                //TODO: why does this affect colour size???
                result = 1;
            }
        }
        return result;
    }

    public static HueSet getAllHues(FormulaSet closure) {
        TreeSet<FormulaSet> mutableHues = getAllHues(closure, closure);
        HueSet immutableHues = new HueSet();
        //name the hues in the context of this complete set of hues
        int counter = 0;
        for (FormulaSet fS : mutableHues) {
            Hue h = new Hue(fS, counter);
            immutableHues.add(h);
            counter++;
        }
        return immutableHues;
    }

    //auxiliary method: uses FormulaSets for mutability and two copies of closures for convenience
    public static TreeSet<FormulaSet> getAllHues(FormulaSet closureConst, FormulaSet closureNonConst) {
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
        TreeSet<FormulaSet> partialHues = new TreeSet<FormulaSet>();

        if (!closureCopy.isEmpty()) {
            Formula f = closureCopy.last();
            closureCopy.remove(f);
            TreeSet<FormulaSet> previousHues = getAllHues(closureConst, closureCopy);
            Iterator<FormulaSet> i = previousHues.iterator();
            //if we branched on f.c within the following while loops, we could combine
            //them into one and reduce the amount of code considerably, but we would also
            //be doing a far larger number of checks
            switch (f.c) {
                case NOT:
                    //we do not make new sets with f = NOT g,
                    //since either the previous sets already contain it,
                    //or they contain g
                    //for its negation, NOT NOT g, we do not want to add it
                    //to the sets that contain f = NOT g, but we do want to
                    //add it the sets that contain g just in case NOT NOT g
                    //is in the original getClosure
                    //note also that we are not adding NOT TRUE into any hues
                    boolean notNot = false;
                    if (closureConst.contains(new Formula(f, NOT))) {
                        notNot = true;
                    }
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        if (notNot && h.contains(f.sf1)) {
                            h.add(new Formula(f, NOT));
                        }
                        partialHues.add(h);
                    }
                    break;
                case AND:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        //do check with or+negation to potentially avoid checking two subformulae
                        if (h.contains(f.sf1.negated()) || h.contains(f.sf2.negated())) {
                            h.add(new Formula(f, NOT));
                        } else {
                            h.add(f);
                        }
                        partialHues.add(h);
                    }
                    break;
                case U:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        //code repeated here for clarity
                        if (h.contains(f.sf1)) {
                            if (h.contains(f.sf2)) {
                                //if a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                partialHues.add(h);
                            } else {
                                //if a and ~b, can have aUb and ~(aUb)
                                FormulaSet notH = new FormulaSet();
                                notH.addAll(h);
                                h.add(f);
                                notH.add(new Formula(f, NOT));
                                partialHues.add(h);
                                partialHues.add(notH);
                            }
                        } else {
                            if (h.contains(f.sf2)) {
                                //if ~a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                partialHues.add(h);
                            } else {
                                //if ~a and ~b, can have ~(aUb) but not aUb
                                h.add(new Formula(f, NOT));
                                partialHues.add(h);
                            }
                        }
                    }
                    break;
                case A:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        FormulaSet notH = new FormulaSet();
                        notH.addAll(h);
                        notH.add(new Formula(f, NOT));
                        partialHues.add(notH);
                        if (h.contains(f.sf1)) {
                           h.add(f);
                           partialHues.add(h);
                        }
                    }
                case TRUE://special case because we do not want to add bottom to a hue
                    //TODO: mention in report
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        h.add(f);
                        partialHues.add(h);
                    }
                    break;

                default:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        FormulaSet notH = new FormulaSet();
                        notH.addAll(h);
                        h.add(f);
                        notH.add(new Formula(f, NOT));
                        partialHues.add(h);
                        partialHues.add(notH);
                    }
            }
        } else {
            FormulaSet empty = new FormulaSet();
            TreeSet<FormulaSet> onlyEmpty = new TreeSet<FormulaSet>();
            onlyEmpty.add(empty);
            partialHues = onlyEmpty;
        }
        return partialHues;
    }

    public boolean[][] generateRX() {
        boolean[][] result = new boolean[this.size()][this.size()];
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            Iterator<Hue> i2 = this.iterator();
            while (i2.hasNext()) {
                Hue h2 = i2.next();
                //System.out.println("testing " + h.getIndex() + " and " + h2.getIndex());
                if (h.rX(h2)) {
                    result[h.getIndex()][h2.getIndex()] = true;
                } else {
                    result[h.getIndex()][h2.getIndex()] = false;
                }
            }
        }
        return result;
    }

    public boolean[][] generateRA() {
        boolean[][] result = new boolean[this.size()][this.size()];
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            Iterator<Hue> i2 = this.iterator();
            while (i2.hasNext()) {
                Hue h2 = i2.next();
                if (h.rA(h2)) {
                    result[h.getIndex()][h2.getIndex()] = true;
                } else {
                    result[h.getIndex()][h2.getIndex()] = false;
                }
            }
        }
        return result;
    }

    //does not use generateRA
    public TreeSet<HueSet> getRAClasses() {
        TreeSet<HueSet> result = new TreeSet<HueSet>();
        Iterator<Hue> i = this.iterator();
        HueSet foundHues = new HueSet();
        while (i.hasNext()) {
            Hue h = i.next();
            if (foundHues.contains(h)) {
                continue;
            }
            HueSet hClass = new HueSet();
            Iterator<Hue> i2 = this.iterator();
            while (i2.hasNext()) {
                Hue h2 = i2.next();
                if (foundHues.contains(h2)) {
                    continue;
                }
                if (h.rA(h2)) {
                    hClass.add(h2);
                    foundHues.add(h2);
                }
            }
            foundHues.add(h);
            result.add(hClass);
        }
        return result;
    }

    public HueSet getHueSuccessors(Hue h) {
        //check if resultset contains first
        boolean[][] rX = generateRX();
        HueSet result = new HueSet();
        int index = h.getIndex();
        for (int i = 0; i < index; i++) {
            if (rX[index][i]) {
                result.add(getHue(i));
            }
        }
        return result;
    }

    public Hue getHue(int index) {
        return getHue("h" + index);
    }

    public Hue getHue(String name) {
        Hue result = null;
        for (Hue h : this) {
            if (h.name.compareTo(name) == 0) {
                result = h;
            }
        }
        return result;
    }

    public void sugarPrint() {
        System.out.println("{");
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            System.out.print(h.name + ": ");
            h.sugarPrint();
            if (i.hasNext()) {
                System.out.println(",");
            }
        }
        System.out.println();
        System.out.println("}");
    }

    public void sugarPrint(Map<Formula, String> formulaeNames) {
        System.out.println("{");
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            System.out.print(h.name + ": ");
            h.sugarPrint(formulaeNames);
            if (i.hasNext()) {
                System.out.println(",");
            }
        }
        System.out.println();
        System.out.println("}");
    }
}
