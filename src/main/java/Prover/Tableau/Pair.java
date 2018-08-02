package Prover.Tableau;

import Prover.Formula.FormulaSet;

import java.util.Objects;

//simple pair for legibility
class Pair<A, B> {
    A a;
    B b;

    public Pair() {
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (this == obj) {
            result = true;
        } else if (obj.getClass() != Pair.class) {
            result = false;
        } else {
            Pair<A, B> pair = (Pair<A, B>) (obj);
            result = this.a.equals(pair.a) && this.b.equals(pair.b);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    //TODO: check hashmap puts: must replace, not add multiple things to same; NO! put replaces; think
    //TODO: replace add/whatever for hashmaps (newS.pC) with replace; check logic
    //for code legibility
    protected static class NodeHue extends Pair<Node, FormulaSet> {
        protected NodeHue(Node n, FormulaSet h) {
            this.a = n;
            this.b = h;
        }
        protected Node node() {
            return this.a;
        }
        protected FormulaSet hue() {
            return this.b;
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = false;
            if (this == obj) {
                result = true;
            } else if (obj.getClass() != NodeHue.class) {
                result = false;
            } else {
                NodeHue nH = (NodeHue) (obj);
                result = this.a.equals(nH.a) && this.b.equals(nH.b);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
