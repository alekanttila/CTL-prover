package Prover;

import java.util.Iterator;
import java.util.TreeSet;

import static Prover.Formula.Connective.A;
import static Prover.Formula.Connective.NOT;

public class ColourWithNull extends HueSet {

    public ColourWithNull() {
        super();
        this.instantiables = new FormulaSet();
    }

    public ColourWithNull(ColourWithNull c) {
        this.addAll(c);
        this.instantiables = c.instantiables;
    }

    public FormulaSet instantiables;


    public static TreeSet<ColourWithNull> getColours(HueSet hS) {
        TreeSet<ColourWithNull> result = new TreeSet<ColourWithNull>();
        TreeSet<ColourWithNull> uninstantiated = new TreeSet<ColourWithNull>();
        TreeSet<HueSet> rAClasses = hS.getRAClasses();
        Iterator<HueSet> classIterator = rAClasses.iterator();
        while (classIterator.hasNext()) {
            HueSet rAC = classIterator.next();
            uninstantiated.addAll(getClassColours(rAC));
        }
        Iterator<ColourWithNull> i = uninstantiated.iterator();
        while (i.hasNext()) {
            ColourWithNull c = i.next();
            if (c.instantiables.isEmpty()) {
                result.add(c);
            }
        }
        return result;
    }

    public static TreeSet<ColourWithNull> getClassColours(HueSet rAC) {
        HueSet rACCopy = new HueSet();
        rACCopy.addAll(rAC);
        TreeSet<ColourWithNull> result = new TreeSet<ColourWithNull>();

        if (!rACCopy.isEmpty()) {
            Hue h = rACCopy.last();
            rACCopy.remove(h);
            TreeSet<ColourWithNull> previous = getClassColours(rACCopy);

            Iterator<Formula> fI = h.iterator();
            FormulaSet hInstantiables = new FormulaSet();
            System.out.println("instantiables in ");
            System.out.print(h.name);
            h.sugarPrint();
            System.out.println("iiii: ");
            while (fI.hasNext()) {
                Formula f = fI.next();
                //do not include the formulae that h instantaties own its own
                if (f.c == NOT && f.sf1.c == A && !h.contains(f.sf1.sf1.negated())) {
                    hInstantiables.add(f.sf1.sf1.negated()); //TODO: write in report
                    f.sugarPrint();
                    System.out.print("---");
                    f.sf1.sf1.negated().sugarPrint();
                    System.out.println(" AND ");
                }
            }
            System.out.println();

            result.addAll(previous);

            Iterator<ColourWithNull> cI = previous.iterator();
            A:
            while (cI.hasNext()) {
                FormulaSet newCInstantiables = new FormulaSet();
                ColourWithNull c = cI.next();
                System.out.println("processing colour ");
                c.sugarPrint();
                //two loops here to avoid checking the hues in c for c-instatiables again
                Iterator<Formula> fI2 = hInstantiables.iterator();
                B:
                while (fI2.hasNext()) {
                    Formula f = fI2.next();
                    //no need to add formula to instatiables if already there
                    if (c.instantiables == null || !c.instantiables.contains(f)) {
                        Iterator<Hue> hI = c.iterator();
                        while (hI.hasNext()) {
                            Hue h2 = hI.next();
                            System.out.print("processing ");
                            System.out.println(h2.name);
                            if (h2.contains(f)) {
                                System.out.print("found: ");
                                f.sugarPrint();
                                System.out.println();
                                //found a hue that instantiates an h-formula->continue with next formula
                                continue B;
                            }
                        }
                        System.out.print("adding to instantiables: ");
                        f.sugarPrint();
                        System.out.println();
                        //add formulae that were not found in any hue to instantiables of new set
                        newCInstantiables.add(f);
                    }
                }
                if (c.instantiables != null) {
                    Iterator<Hue> hI = c.iterator();
                    Iterator<Formula> fI3 = c.instantiables.iterator();
                    while (fI3.hasNext()) {
                        Formula f = fI3.next();
                        if (h.contains(f)) {
                            continue;
                        }
                        newCInstantiables.add(f);
                    }
                }
                ColourWithNull withH = new ColourWithNull(c);
                withH.add(h);
                withH.instantiables = newCInstantiables;
                result.add(withH);
            }
        } else {
            ColourWithNull justEmpty = new ColourWithNull();
            Hue emptyHue = new Hue();
            justEmpty.add(emptyHue);
            result.add(justEmpty);
        }
        System.out.println();
        System.out.println("returning ");
        for (ColourWithNull c : result) {
            c.sugarPrint();
            System.out.println("with instantiables: ");
            c.instantiables.sugarPrint();
            System.out.println();
        }
        return result;
    }

}


