package Prover;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class ColourSet extends TreeSet<Colour> {

    private boolean namesSet = false;

    public void generateNames() {
        Iterator<Colour> i = this.iterator();
        int counter = 0;
        while (i.hasNext()) {
            Colour c = i.next();
            c.name = "h" + counter;
            counter++;
        }
        this.namesSet = true;
    }

    public boolean[][] generateRX() {
        if (!this.namesSet) {
            this.generateNames();
        }
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


}
