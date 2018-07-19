package Prover;

import java.util.Iterator;
import java.util.TreeSet;

import static Prover.Formula.Connective.*;

public class ColourSet extends TreeSet<Colour> {

    private boolean namesSet = false;

    public static ColourSet getAllColours(HueSet hS) {
        ColourSet result = new ColourSet();
        TreeSet<HueSet> uninstantiated = new TreeSet<HueSet>();
        TreeSet<HueSet> rAClasses = hS.getRAClasses();
        Iterator<HueSet> classIterator = rAClasses.iterator();
        while (classIterator.hasNext()) {
            HueSet rAC = classIterator.next();
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

        FormulaSet hInstantiables = new FormulaSet();
        //System.out.println("instantiables in ");
        //System.out.print(h.name);
        //h.sugarPrint();
        //System.out.println("iiii: ");
        while (fI.hasNext()) {
            Formula f = fI.next();
            //do not include the formulae that h instantaties own its own
            if (f.c == NOT && f.sf1.c == A && !h.contains(f.sf1.sf1.negated())) {
                hInstantiables.add(f.sf1.sf1.negated()); //TODO: write in report
                //f.sugarPrint();
                //System.out.print("---");
                //f.sf1.sf1.negated().sugarPrint();
                //System.out.println(" AND ");
            }
        }
        //System.out.println();

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
                //System.out.println("processing colour ");
                //c.sugarPrint();
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
                            //System.out.print("processing ");
                            //System.out.println(h2.name);
                            if (h2.contains(f)) {
                                //System.out.print("found: ");
                                //f.sugarPrint();
                                //System.out.println();
                                //found a hue that instantiates an h-formula->continue with next formula
                                continue B;
                            }
                        }
                        //System.out.print("adding to instantiables: ");
                        //f.sugarPrint();
                        //System.out.println();
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
        //System.out.println();
        //System.out.println("returning ");
        /*for (Colour c : result) {
            c.sugarPrint();
            System.out.println("with instantiables: ");
            c.instantiables.sugarPrint();
            System.out.println();
        }*/
        return result;
    }

    public boolean[][] generateRX() {
        boolean[][] result = new boolean[this.size()][this.size()];
        Iterator<Colour> i = this.iterator();
        while (i.hasNext()) {
            Colour c = i.next();
            Iterator<Colour> i2 = this.iterator();
            while (i2.hasNext()) {
                Colour c2 = i2.next();
                if (c.rX(c2)) {
                    result[c.getIndex()][c2.getIndex()] = true;
                } else {
                    result[c.getIndex()][c2.getIndex()] = false;
                }
            }
        }
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

    public ColourSet getColourSuccessors(Colour c) {
        //check if resultset contains first
        boolean[][] rX = generateRX();
        ColourSet result = new ColourSet();
        int index = c.getIndex();
        for (int i = 0; i < index; i++) {
            if (rX[index][i]) {
                result.add(getColour(i));
            }
        }
        return result;
    }

    public Colour getColour(int index) {
        return getColour("h" + index);
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

}
