package Prover.Formula;

import Prover.StatusMessage;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static Prover.Formula.Formula.Connective.*;
import static Prover.StatusMessage.Area.COLOURS;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.statusPrint;
import static Prover.StatusMessage.subSectionPrint;

public class ColourSet extends TreeSet<Colour> {

    private boolean rXGenerated = false;

    public static ColourSet getAllColours(HueSet hS) {
        TreeSet<HueSet> rAClasses = hS.getrAClasses();
        ColourSet result = new ColourSet();
        TreeSet<HueSet> uninstantiated = new TreeSet<HueSet>();
        Iterator<HueSet> classIterator = rAClasses.iterator();
        while (classIterator.hasNext()) {
            HueSet rAC = classIterator.next();
            subSectionPrint(COLOURS, SOME, "Getting colours for equivalence class containing " + rAC.nameString());
            uninstantiated.addAll(getClassColours(rAC));
        }
        Iterator<HueSet> i = uninstantiated.iterator();
        int counter = 0;
        while (i.hasNext()) {
            HueSet c = i.next();
            if (c.instantiables.isEmpty()) {
                result.add(new Colour(c, counter));
                counter++;
            }
        }
        return result;
    }

    public static TreeSet<HueSet> getClassColours(HueSet rAC) {
        HueSet rACCopy = new HueSet();
        rACCopy.addAll(rAC);
        TreeSet<HueSet> result = new TreeSet<HueSet>();

        Hue h = rACCopy.last();
        Iterator<Formula> fI = h.iterator();

        statusPrint(COLOURS, MAX, "Generating colours with " + h.name);

        FormulaSet hInstantiables = new FormulaSet();
        while (fI.hasNext()) {
            Formula f = fI.next();
            //do not include the formulae that h instantaties own its own
            if (f.getC()== NOT && f.getSf1().getC()== A && !h.contains(f.getSf1().getSf1().negated())) {
                hInstantiables.add(f.getSf1().getSf1().negated());
            }
        }

        //make a new potential colour consisting of only h
        HueSet justH = new HueSet();
        justH.add(h);
        justH.instantiables = hInstantiables;
        result.add(justH);

        //base case processing stops here;
        //recursive case:
        if (rACCopy.size() != 1) {

            rACCopy.remove(h);
            TreeSet<HueSet> previous = getClassColours(rACCopy);
            //previous potential colours are also potential colours at this stage
            result.addAll(previous);

            //make new potential colours using h
            Iterator<HueSet> i = previous.iterator();
            A:
            while (i.hasNext()) {
                FormulaSet newCInstantiables = new FormulaSet();
                HueSet c = i.next();
                //two loops here to avoid checking the hues in c for c-instatiables again
                Iterator<Formula> fI2 = hInstantiables.iterator();
                B:
                while (fI2.hasNext()) {
                    Formula f = fI2.next();
                    //no need to add formula to instantiables if already there
                    if (c.instantiables == null || !c.instantiables.contains(f)) {
                        Iterator<Hue> hI = c.iterator();
                        while (hI.hasNext()) {
                            Hue h2 = hI.next();
                            if (h2.contains(f)) {
                                //found a hue that instantiates an h-formula->continue with next formula
                                continue B;
                            }
                        }
                        //f.sugarPrint();
                        //add formulae that were not found in any hue to instantiables of new set
                        newCInstantiables.add(f);
                    }
                }
                if (c.instantiables != null) {
                    Iterator<Formula> fI3 = c.instantiables.iterator();
                    while (fI3.hasNext()) {
                        Formula f = fI3.next();
                        if (h.contains(f)) {
                            continue;
                        }
                        newCInstantiables.add(f);
                    }
                }
                HueSet withH = new HueSet();
                withH.addAll(c);
                withH.add(h);
                withH.instantiables = newCInstantiables;
                result.add(withH);
            }
        }
        statusPrint(COLOURS, MAX, "Number of potential colours generated: " + result.size());
        return result;
    }

    public ColourSet getColoursWithF(Formula f) {

        ColourSet result = new ColourSet();
        A:
        for (Colour c : this) {
            for (Hue h :  c) {
               if (h.contains(f)) {
                   result.add(c);
                   continue A;
               }
            }
        }
        return result;
    }

    public Colour getColour(int index) {
        return getColour("c" + index);
    }

    public void generateRX() {
        if (!rXGenerated) {
            for (Colour c : this) {
                c.generateRX(this);
            }
        }
        rXGenerated = true;
    }

    public void printRX() {
        generateRX();
        for (Colour c : this) {
            System.out.println(c.name + " rX: " + c.getSuccessors().nameString());
        }
    }

    public Colour getColour(String name) {
        Colour result = null;
        for (Colour c : this) {
            if (c.name.compareTo(name) == 0) {
                result = c;
            }
        }
        return result;
    }

    public String printString() {
        String result = "{\n";
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            result = result + c.printString(1);
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n}";
        return result;
    }

    public String sugarString() {
        String result = "{\n";
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            result = result + c.sugarString(1);
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n}";
        return result;
    }

    public void sugarPrint(Map<Formula, String> formulaNames) {
        System.out.println("{");
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            System.out.print(c.sugarString(1, formulaNames));
            if (i.hasNext()) {
                System.out.println(",");
            }
        }
        System.out.println();
        System.out.println("}");
    }

    public String sugarString(Map<Formula, String> formulaNames) {
        String result = "{\n";
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            result = result + c.sugarString(1, formulaNames);
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n}";
        return result;
    }

    public String hueString() {
        String result = "{\n";
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            result = result + "  " + c.nameString();
            if (i.hasNext()) {
                result = result + ",\n";
            }
        }
        result = result + "\n}";
        return result;
    }

    public String nameString() {
        String result = "{ ";
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            result = result + c.name;
            if (i.hasNext()) {
                result = result + ", ";
            }
        }
        result = result + " }";
        return result;
    }
}
