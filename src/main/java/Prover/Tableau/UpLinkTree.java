package Prover.Tableau;

import Prover.Formula.Hue;
import Prover.Tableau.Pair.NodeHue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: write about synchronization (lack of a need to)
public class UpLinkTree<A> {
    protected Map<Pair<A, Hue>, UpLinkTree<A>> map;
    private NodeHue n;
    protected UpLinkTree<A> getUpLinks(A a, Hue successorIndex) {
        return map.get(new Pair<>(a, successorIndex));
    }

    //TODO: override  hashcode
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (this == obj) {
            result = true;
        } else if (UpLinkTree.class.isInstance(obj)) {
            result = false;
        } else {
            UpLinkTree c = (UpLinkTree)obj;
            if (this.isNode()) {
                if (c.isNode()) {
                    //TODO: check where we use this equals method
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
        return n.node();
    }

    protected NodeHue getNodeHue() {
        return n;
    }

    protected Map<Pair<A, Hue>, UpLinkTree<A>> getMap() {
        return map;
    }

    protected UpLinkTree() {
        this.map = new HashMap<>();
    }
    protected UpLinkTree(NodeHue n) {
        this.n = n;
    }
    protected void add(A a, Hue successorIndex, NodeHue n) {
        if (this.n == null) {
            this.map.put(new Pair<>(a, successorIndex), new UpLinkTree(n));
        } else {
            throw new AssertionError();
            //TODO: change the error here
        }
    }
    protected void add(A a, Hue successorIndex, UpLinkTree tree) {
        if (this.n == null) {
            this.map.put(new Pair<>(a, successorIndex), tree);
        } else {
            throw new AssertionError();
            //TODO:change the error here
        }
    }
}
