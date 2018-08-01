package Prover.Tableau;

import Prover.Formula.Colour;
import Prover.Formula.Hue;

import java.util.HashMap;
import java.util.Map;

class FirstHueUpLinkTree {
    //TODO: store numbers, not colours. hues
    private Map<Pair<Pair<Colour, Hue>, Hue>, FirstHueUpLinkTree> map;
    private Node n;
    protected FirstHueUpLinkTree getUpLinks(Colour c, Hue firstHue, Hue successorIndex) {
        return map.get(new Pair<>(new Pair<>(c, firstHue), successorIndex));
    }

    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (this == obj) {
            result = true;
        } else if (obj.getClass() != FirstHueUpLinkTree.class) {
            result = false;
        } else {
            FirstHueUpLinkTree c = (FirstHueUpLinkTree)obj;
            if (this.isNode()) {
                if (c.isNode()) {
                    //TODO: note somewhere we only use copies of nodes so that this works?
                    result = (this.n == c.n);
                } else {
                    result = false;
                }
            } else {
                if (c.isNode()) {
                    result = false;
                } else {
                    result = this.map.equals(c.map);
                }
            }
        }
        return result;
    }

    protected boolean isNode() {
        boolean result = false;
        if (n!=null) {
            if (map == null) {
                result = true;
            } else {
                //TODO: throw assertionerro
            }
        } else {
            if (map != null) {
                result = false;
            } else {
                //TODO: throw assertionerro
            }
        }
        return result;
    }

    protected FirstHueUpLinkTree() {
        this.map = new HashMap<>();
    }

    protected FirstHueUpLinkTree(Node n) {
        this.n = n;
    }

    protected Node getNode() {
        return n;
    }

    protected Map<Pair<Pair<Colour, Hue>, Hue>, FirstHueUpLinkTree> getMap() {
        return map;
    }

    protected void add(Colour c, Hue firstHue, Hue successorIndex, Node n) {
        if (this.n == null) {
            this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), new FirstHueUpLinkTree(n));
        } else {
            throw new AssertionError();
            //TODO: change the error here
        }
    }
    protected void add(Colour c, Hue firstHue, Hue successorIndex, FirstHueUpLinkTree tree) {
        if (this.n == null) {
            this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), tree);
        } else {
            throw new AssertionError();
            //TODO:change the error here
        }
    }
}
