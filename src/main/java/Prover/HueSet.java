package Prover;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static Prover.Formula.Connective.*;

public class HueSet extends TreeSet<Hue> implements Comparable<HueSet> {

    //protected final Map<Hue, String> hueNames;
    protected boolean[][] rX;
    protected boolean[][] rA;
    protected TreeSet<HueSet> rAClasses;

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

    public HueSet() {
        super();
    }

    public HueSet(Formula f) {
        FormulaSet closure = f.getClosure();
        this.addAll(getHueSet(closure, closure));
    }

    public static HueSet getHueSet(FormulaSet closureConst, FormulaSet closureNonConst) {
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
        HueSet hues = new HueSet();

        if (!closureCopy.isEmpty()) {
            Formula f = closureCopy.last();
            closureCopy.remove(f);
            HueSet previousPowerSet = getHueSet(closureConst, closureCopy);
            Iterator<Hue> i = previousPowerSet.iterator();
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
                        Hue h = i.next();
                        if (notNot && h.contains(f.sf1)) {
                            h.add(new Formula(f, NOT));
                        }
                        hues.add(h);
                    }
                    break;
                case AND:
                    while (i.hasNext()) {
                        Hue h = i.next();
                        //do check with or+negation to potentially avoid checking two subformulae
                        if (h.contains(f.sf1.negated()) || h.contains(f.sf2.negated())) {
                            h.add(new Formula(f, NOT));
                        } else {
                            h.add(f);
                        }
                        hues.add(h);
                    }
                    break;
                case U:
                    while (i.hasNext()) {
                        Hue h = i.next();
                        //code repeated here for clarity
                        if (h.contains(f.sf1)) {
                            if (h.contains(f.sf2)) {
                                //if a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                hues.add(h);
                            } else {
                                //if a and ~b, can have aUb and ~(aUb)
                                Hue notH = new Hue();
                                notH.addAll(h);
                                h.add(f);
                                notH.add(new Formula(f, NOT));
                                hues.add(h);
                                hues.add(notH);
                            }
                        } else {
                            if (h.contains(f.sf2)) {
                                //if ~a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                hues.add(h);
                            } else {
                                //if ~a and ~b, can have ~(aUb) but not aUb
                                h.add(new Formula(f, NOT));
                                hues.add(h);
                            }
                        }
                    }
                    break;
                case A:
                    while (i.hasNext()) {
                        Hue h = i.next();
                        Hue notH = new Hue();
                        notH.addAll(h);
                        notH.add(new Formula(f, NOT));
                        hues.add(notH);
                        if (h.contains(f.sf1)) {
                           h.add(f);
                           hues.add(h);
                        }
                    }
                case TRUE://special case because we do not want to add bottom to a hue
                    //TODO: mention in report
                    while (i.hasNext()) {
                        Hue h = i.next();
                        h.add(f);
                        hues.add(h);
                    }
                    break;

                default:
                    while (i.hasNext()) {
                        Hue h = i.next();
                        Hue notH = new Hue();
                        notH.addAll(h);
                        h.add(f);
                        notH.add(new Formula(f, NOT));
                        hues.add(h);
                        hues.add(notH);
                    }
            }
        } else {
            Hue empty = new Hue();
            HueSet onlyEmpty = new HueSet();
            onlyEmpty.add(empty);
            hues = onlyEmpty;
        }
        return hues;
    }

    public boolean namesSet = false;

    public void generateNames() {
        Iterator<Hue> i = this.iterator();
        int counter = 0;
        while (i.hasNext()) {
            Hue h = i.next();
            h.name = "h" + counter;
            counter++;
        }
        this.namesSet = true;
    }

    public boolean[][] generateRX() {
        if (!this.namesSet) {
            this.generateNames();
        }
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
        if (!this.namesSet) {
            this.generateNames();
        }
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
