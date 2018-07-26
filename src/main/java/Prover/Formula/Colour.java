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


