package Prover;

import java.util.List;
import java.util.Set;

public class COPT {
    private Formula phi;
    private Set<Node> t;
    private Node root;
    private class Node {
        private List<Node> s;
        //note: must order colour!
        private Colour z;
    }
}
