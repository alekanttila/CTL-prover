package Prover.Formula;

import java.util.*;

public class Colour extends HueSet {

    public final String name;

    public Colour(HueSet hS, int index) {
        super.addAll(hS);
        this.name = "c" + index;
    }

    public int getIndex() {
        return Integer.parseInt(this.name.substring(1));
    }

    public boolean rX(Colour c, boolean[][] hueRX) {
        boolean result = true;
        Iterator<Hue> cI = c.iterator();
        A:
        while (cI.hasNext()) {
            Hue cHue = cI.next();
            Iterator<Hue>  thisI = this.iterator();
            while (thisI.hasNext()) {
                Hue thisHue = thisI.next();
                if (hueRX[thisHue.getIndex()][cHue.getIndex()]) {
                    continue A;
                }
            }
            result = false;
            break A;
        }
        return result;



                /*
        boolean result = true;
        Iterator<Hue> i = c.iterator();
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
        return result;
        */
    }

    @Override
    public String printString(int indentLevel) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        return indent + this.name + ":\n" + super.printString(indentLevel);
    }

    @Override
    public String nameString() {
        return this.name + ": " + super.nameString();
    }

    @Override
    public String sugarString(int indentLevel) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        return indent + this.name + ":\n" + super.sugarString(indentLevel);
    }

    @Override
    public String sugarString(int indentLevel, Map<Formula, String> formulaNames) {
        String indent = "";
        for (int i = 0; i < indentLevel; i++) {
            indent = indent + "  ";
        }
        return indent + this.name + ":\n" + super.sugarString(indentLevel, formulaNames);
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