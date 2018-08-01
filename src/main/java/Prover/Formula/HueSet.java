package Prover.Formula;


import Prover.Mode;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static Prover.Formula.Formula.Connective.*;

public class HueSet extends TreeSet<Hue> implements Comparable<HueSet> {

    TreeSet<HueSet> rAClasses;

    protected FormulaSet instantiables = new FormulaSet();

    public HueSet() {
        super();
    }

    //@Override
    public int compareTo(HueSet hS) {
        int result = 0;
        if (this != hS) {
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
        immutableHues.generateRX();
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
            //if we branched on f.getC()within the following while loops, we could combine
            //them into one and reduce the amount of code considerably, but we would also
            //be doing a far larger number of checks
            switch (f.getC()) {
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
                        if (notNot && h.contains(f.getSf1())) {
                            h.add(closureConst.getReference(new Formula(f, NOT)));
                        }
                        partialHues.add(h);
                    }
                    break;
                case AND:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        //do check with or+negation to potentially avoid checking two subformulae
                        if (h.contains(f.getSf1().negated()) || h.contains(f.getSf2().negated())) {
                            h.add(closureConst.getReference(new Formula(f, NOT)));
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
                        if (h.contains(f.getSf1())) {
                            if (h.contains(f.getSf2())) {
                                //if a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                partialHues.add(h);
                            } else {
                                //if a and ~b, can have aUb and ~(aUb)
                                FormulaSet notH = new FormulaSet();
                                notH.addAll(h);
                                h.add(f);
                                notH.add(closureConst.getReference(new Formula(f, NOT)));
                                partialHues.add(h);
                                partialHues.add(notH);
                            }
                        } else {
                            if (h.contains(f.getSf2())) {
                                //if ~a and b, can have aUb, but not ~(aUb)
                                h.add(f);
                                partialHues.add(h);
                            } else {
                                //if ~a and ~b, can have ~(aUb) but not aUb
                                h.add(closureConst.getReference(new Formula(f, NOT)));
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
                        notH.add(closureConst.getReference(new Formula(f, NOT)));
                        partialHues.add(notH);
                        if (h.contains(f.getSf1())) {
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
                case X:
                    if (Mode.xHues) {
                        while (i.hasNext()) {
                            FormulaSet h = i.next();
                            //f = Xa; if ~X~a is not in h, we can add ~Xa
                            if (!h.contains(new Formula(new Formula(f.getSf1().negated(), X), NOT))) {
                                FormulaSet notH = new FormulaSet();
                                notH.addAll(h);
                                notH.add(closureConst.getReference(new Formula(f, NOT)));
                                partialHues.add(notH);
                            }
                            //f = Xa; if X~a is not in h, we can add Xa
                            if (!h.contains(new Formula(f.getSf1().negated(), X))) {
                                h.add(f);
                                partialHues.add(h);
                            }
                        }
                        break;
                    }
                default:
                    while (i.hasNext()) {
                        FormulaSet h = i.next();
                        FormulaSet notH = new FormulaSet();
                        notH.addAll(h);
                        h.add(f);
                        notH.add(closureConst.getReference(new Formula(f, NOT)));
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

    public void generateRX() {
        for (Hue h : this) {
            h.getSuccessors(this);
        }
    }

    public TreeSet<HueSet> getrAClasses() {
        if (this.rAClasses == null) {
            generateRAClasses();
        }
        return this.rAClasses;
    }

    public TreeSet<HueSet> generateRAClasses() {
        TreeSet<HueSet> result = new TreeSet<HueSet>();
        HueSet foundHues = new HueSet();
        for (Hue h : this) {
            if (foundHues.contains(h)) {
                continue;
            }
            HueSet hClass = new HueSet();
            for (Hue h2 : this) {
                if (h.rA(h2)) {
                    //could check foundhues here as well, but the answer is in the matrix anyway
                    hClass.add(h2);
                    foundHues.add(h2);
                }
            }
            result.add(hClass);
        }
        this.rAClasses = result;
        return result;
    }

    public Hue getHue(int index) {
        return getHue("h" + index);
    }

    //should only be used when when iterating through all hues (for complexity reasons)
    public Hue getHue(String name) {
        Hue result = null;
        for (Hue h : this) {
            if (h.name.compareTo(name) == 0) {
                result = h;
            }
        }
        return result;
    }

    public boolean containsF(Formula f) {
        boolean result = false;
        for (Hue h : this) {
            if (h.contains(f)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void printRX(Map<Formula, String> formulanames) {
        for (Hue h : this) {
            System.out.println(h.getSuccessors().sugarString(0, formulanames));
        }
    }

    public String printString(int indentLevel) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        String result = indent + "{\n";
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            result = result + indent + "  " + h.printString();
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n" + indent + "}";
        return result;
    }

    public String nameString() {
        String result = "{ ";
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            result = result + h.name;
            if (i.hasNext()) {
                result = result + ", ";
            }
        }
        result = result + " }";
        return result;
    }

    public String sugarString(int indentLevel) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        String result = indent + "{\n";
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            result = result + indent + "  " + h.sugarString();
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n" + indent + "}";
        return result;
    }

    public String sugarString(int indentLevel, Map<Formula, String> formulaNames) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        String result = indent + "{\n";
        Iterator<Hue> i = this.iterator();
        while (i.hasNext()) {
            Hue h = i.next();
            result = result + indent + "  " + h.sugarString(formulaNames);
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n" + indent + "}";
        return result;
    }


}
