package Prover.Tableau;

import Prover.Formula.Hue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UpLinkTree {
    //TODO: store numbers, not colours. hues
    private Map<Pair<List<Hue>, Hue>, UpLinkTree> map;
    private Node n;
    protected UpLinkTree getUpLinks(List<Hue> zOrder, Hue successorIndex) {
        return map.get(new Pair<>(zOrder , successorIndex));
    }

    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (this == obj) {
            result = true;
        } else if (obj.getClass() != UpLinkTree.class) {
            result = false;
        } else {
            UpLinkTree c = (UpLinkTree)obj;
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

    protected Node getNode() {
        return n;
    }

    protected Map<Pair<List<Hue>, Hue>, UpLinkTree> getMap() {
        return map;
    }

    protected UpLinkTree() {
        this.map = new HashMap<>();
    }
    protected UpLinkTree(Node n) {
        this.n = n;
    }
    protected void add(List<Hue> zOrder, Hue successorIndex, Node n) {
        if (this.n == null) {
            this.map.put(new Pair<>(zOrder, successorIndex), new UpLinkTree(n));
        } else {
            throw new AssertionError();
            //TODO: change the error here
        }
    }
    protected void add(List<Hue> zOrder, Hue successorIndex, UpLinkTree tree) {
        if (this.n == null) {
            this.map.put(new Pair<>(zOrder, successorIndex), tree);
        } else {
            throw new AssertionError();
            //TODO:change the error here
        }
    }
}
