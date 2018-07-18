package Prover;

import java.util.*;

import static Prover.Formula.Connective.*;

public class Colour extends HueSet {

    protected String name;

    public Colour() {
        super();
    }

    public Colour(Colour c) {
        super.addAll(c);
    }

    public Colour(HueSet hS) {
        super.addAll(hS);
    }

    public int getIndex() {
        return Integer.parseInt(this.name.substring(1));
    }

    public static ColourSet getColours(HueSet hS) {
        ColourSet result = new ColourSet();
        TreeSet<HueSet> uninstantiated = new TreeSet<HueSet>();
        TreeSet<HueSet> rAClasses = hS.getRAClasses();
        Iterator<HueSet> classIterator = rAClasses.iterator();
        while (classIterator.hasNext()) {
            HueSet rAC = classIterator.next();
            uninstantiated.addAll(getClassColours(rAC));
        }
        Iterator<HueSet> i = uninstantiated.iterator();
        while (i.hasNext()) {
            HueSet c = i.next();
            if (c.instantiables.isEmpty()) {
                result.add(new Colour(c));
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

    public boolean rX(Colour c, boolean[][] hueRX) {
        boolean result = true;
        Iterator<Hue> i = c.iterator();
        if (hueRX == null) {
            A:
            while (i.hasNext()) {
                Hue h = i.next();
                Iterator<Hue> i2 = c.iterator();
                while (i2.hasNext()) {
                    Hue h2 = i2.next();
                    if (h2.rX(h)) {
                        continue A;
                    }
                }
                result = false;
                break A;
            }
        } else {
            A:
            while (i.hasNext()) {
                Hue h = i.next();
                for (boolean[] rXRow : hueRX) {
                    if (rXRow[h.getIndex()]) {
                        continue A;
                    }
                }
                result = false;
                break A;
            }
        }
        return result;
    }

    //optional args support:
    public boolean rX(Colour c) {
       return rX(c, null);
    }

    //colours are immutable
    @Override
    public boolean add(Hue e) {
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


