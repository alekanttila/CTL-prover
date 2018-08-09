package Prover.Formula;

import java.util.*;

public class Colour extends HueSet {

    public final String name;
    private ColourSet successors;

    public Colour(HueSet hS, int index) {
        super.addAll(hS);
        this.name = "c" + index;
    }

    public int getIndex() {
        return Integer.parseInt(this.name.substring(1));
    }

    ColourSet getSuccessors() {
        return successors;
    }

    public ColourSet getSuccessors(ColourSet cS) {
        if (this.successors == null) {
            generateRX(cS);
        }
        return this.successors;
    }

    public ColourSet generateRX(ColourSet colours) {
        successors = new ColourSet();
        for (Colour c : colours) {
            if (this.rX(c)) {
                successors.add(c);
            }
        }
        return successors;
    }

    public boolean rX(Colour c) {
        boolean result = true;
        Iterator<Hue> cI = c.iterator();
        A:
        while (cI.hasNext()) {
            Hue cHue = cI.next();
            Iterator<Hue> thisI = this.iterator();
            while (thisI.hasNext()) {
                Hue thisHue = thisI.next();
                if (thisHue.getSuccessors() != null && thisHue.getSuccessors().contains(cHue)) {
                    continue A;
                }
                if (thisHue.rX(cHue)) {
                    continue A;
                }
            }
            result = false;
            break A;
        }
        return result;
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