package Prover.Tableau;

import Prover.Formula.Hue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//TODO: write about synchronization (lack of a need to)
public class ConcurrentUpLinkTree<A> extends UpLinkTree<A> {
    protected ConcurrentMap<Pair<A, Hue>, ConcurrentUpLinkTree<A>> map;
    private Node n;
    protected ConcurrentUpLinkTree<A> getUpLinks(A a, Hue successorIndex) {
        return map.get(new Pair<>(a, successorIndex));
    }

    //TODO: override  hashcode
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (this == obj) {
            result = true;
        } else if (ConcurrentUpLinkTree.class.isInstance(obj)) {
            result = false;
        } else {
            ConcurrentUpLinkTree c = (ConcurrentUpLinkTree)obj;
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

    protected ConcurrentMap<Pair<A, Hue>, UpLinkTree<A>> getMap() {
        return (ConcurrentMap)map;
    }

    protected ConcurrentUpLinkTree() {
        this.map = new ConcurrentHashMap<>();
    }
    protected ConcurrentUpLinkTree(Node n) {
        this.n = n;
    }
    protected void add(A a, Hue successorIndex, Node n) {
        if (this.n == null) {
            this.map.put(new Pair<>(a, successorIndex), new ConcurrentUpLinkTree(n));
        } else {
            throw new AssertionError();
            //TODO: change the error here
        }
    }
    protected void add(A a, Hue successorIndex, ConcurrentUpLinkTree tree) {
        if (this.n == null) {
            this.map.put(new Pair<>(a, successorIndex), tree);
        } else {
            throw new AssertionError();
            //TODO:change the error here
        }
    }
}
