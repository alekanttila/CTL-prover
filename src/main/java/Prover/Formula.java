package Prover;

import java.util.Map;
import java.util.Objects;

import static Prover.Formula.Arity.*;
import static Prover.Formula.Connective.*;
import static Prover.HueSet.getHueSet;

public class Formula implements Comparable<Formula>{

    //INNER ENUMS

    protected enum Connective {
        //note: natural order (enum compareTo order) is the
        //order in which these are declared
        //we only use compareTo with sugar-free connectives
        //(ATOMIC, TRUE, NOT, AND, X, U, A), but we establish an
        //order here between all connectives, with atom/boolean shorthand
        //connectives coming before unary coming before binary; we pretend
        //the abbreviations are basic connectives for this ordering
        ATOM, TRUE, FALSE,
        NOT, X, A, E, F, G,
        AND, U, OR, IFTHEN, IFF;

        String printString() {
            String result = null;
            switch (this) {
                case TRUE:
                    result = "⊤";
                    break;
                case FALSE:
                    result = "⊥";
                    break;
                case NOT:
                    result = "¬";
                    break;
                case AND:
                    result = "∧";
                    break;
                case OR:
                    result = "∨";
                    break;
                case IFTHEN:
                    result = "→";
                    break;
                case IFF:
                    result = "↔";
                    break;
                default:
                    result = this.toString();
            }
            return result;
        }
    }

    //this is to enable switch statements and hence improve readibility
    //of formula primary connective arity checks
    //atom and bool are separate categories for convenience
    protected enum Arity {
        ATOMIC, BOOL, UNARY, BINARY;
    }

    //STATES

    //use protected final members instead setters and getters since formulae are
    //meant to be completely inert and final
    protected final Formula sf1;
    protected final Formula sf2;
    protected final Connective c;
    protected final String id;
    //store length to avoid recursive calculations;
    // easy to keep track of when constructing
    protected final int length;

    //CONSTRUCTORS

    public Formula(Formula sf1, Formula sf2, Connective c) {
        this.sf1 = sf1;
        this.sf2 = sf2;
        this.c = c;
        this.id = null;
        this.length = sf1.length + sf2.length + 1;
        System.out.println("CONS");
        System.out.println(this.c);
    }
    public Formula(Formula sf1, Connective c) {
        this.sf1 = sf1;
        this.sf2 = null;
        this.c = c;
        this.id = null;
        this.length = sf1.length + 1;
    }
    public Formula(String id) {
        this.sf1 = null;
        this.sf2 = null;
        this.c = ATOM;
        this.id = id;
        this.length = 1;
    }
    public Formula(Connective c) {
        this.sf1 = null;
        this.sf2 = null;
        Connective c2 = null;
        if (c == TRUE || c == FALSE){
            c2 = c;
        } else {
            //error
        }
        this.c = c2;
        this.id = null;
        this.length = 1;
    }

    //METHODS

    //note: this ensures sets work correctly!
    //only properly works on sugar-free formulae
    //TODO: override hashcode
    //TODO: check nulls!
    @Override
    public boolean equals(Object o) {
        boolean e = false;
        if (this == o) {
            e = true;
        } else if (o.getClass() != Formula.class) {
            e = false;
        } else {
            Formula f = (Formula)(o);
            Connective thisConnective = this.c;
            if (c == f.c) {
                switch (this.getArity()) {
                    case BINARY:
                        //syntactic equality-orcer matters
                        e = ((this.sf1.equals(f.sf1) && this.sf2.equals(f.sf2)));
                        break;
                    case UNARY:
                        e = this.sf1.equals(f.sf1);
                        break;
                    case ATOMIC:
                        if (this.id.compareTo(f.id) == 0) {
                            e = true;
                        } else {
                            e = false;
                        }
                        break;
                    case BOOL:
                        e = (this.c == f.c);
                        break;
                    default:
                        //TODO: error
                }
            } else {
                //not required; included for clarity;
                //case when connectives are different
                e = false;
            }
        }
        return e;
    }

    @Override
    public int hashCode() {
        String hashID = "";
        if (id == null) {
            hashID = "not an atom";
        } else {
            hashID = id;
        }
        return Objects.hash(c, length, hashID);
    }

    @Override
    public int compareTo(Formula f) {
        int result = 0;
        int lengthDiff = this.length - f.length;
        if (lengthDiff != 0) {
            result = lengthDiff;
        } else {
            if (this.equals(f)) {
                result = 0;
            } else {
                int connectiveDiff = this.c.compareTo(f.c);
                if (connectiveDiff != 0) {
                    result = connectiveDiff;
                } else {
                    switch (this.getArity()) {
                        case BINARY:
                            int sf1Diff = this.sf1.compareTo(f.sf1);
                            if (sf1Diff != 0) {
                                result = sf1Diff;
                            } else {
                                result = this.sf2.compareTo(f.sf2);
                            }
                            break;
                        case UNARY:
                            result = this.sf1.compareTo(f.sf1);
                            break;
                        case ATOMIC:
                            result = this.id.compareTo(f.id);
                            break;
                        default:
                            //if arity is TRUE, we should have gotten this.equals(f) = true
                            //TODO:error
                    }
                }
            }
        }
        return result;
    }

    public Arity getArity() {
        Arity result = null;
        switch (this.c) {
            case ATOM:
                result = ATOMIC;
                break;
            case TRUE:
            case FALSE:
                result = BOOL;
                break;
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
                result = UNARY;
                break;
            case AND:
            case U:
            case OR:
            case IFTHEN:
            case IFF:
                result = BINARY;
                break;
            default:
                //TODO:error
        }
        return result;
    }

    public Formula negated() {
        Formula result = null;
        if (this.c == NOT) {
            result = this.sf1;
        } else {
            result = new Formula(this, NOT);
        }
        return result;
    }

    public FormulaSet getClosure() {
        FormulaSet returnSet = new FormulaSet();
        returnSet.add(this);
        //note: closure is a syntactic notion, so ~φ gets added even if φ≡~χ
        returnSet.add(new Formula(this, NOT));

        switch (this.getArity()) {
            case BINARY:
                returnSet.addAll(this.sf1.getClosure());
                returnSet.addAll(this.sf2.getClosure());
                break;
            case UNARY:
                returnSet.addAll(this.sf1.getClosure());
                break;
            case ATOMIC:
            case BOOL:
                break;
            default:
                //TODO:error
        }
        return returnSet;
    }

    public HueSet getHues() {
        FormulaSet closure = this.getClosure();
        return getHueSet(closure, closure);
    }

    public Connective sugarTest() {
        Connective result = null;
        switch (this.c) {
            case NOT:
                if (this.sf1.c == TRUE) {
                    result = FALSE;
                } else if (this.sf1.c == A && this.sf1.sf1.c == NOT) {
                    result = E;
                } else if (this.sf1.c == AND && this.sf1.sf1.c == NOT && this.sf1.sf2.c == NOT) {
                    result = OR;
                } else if (this.sf1.c == AND && this.sf1.sf2.c == NOT) {
                    result = IFTHEN;
                } else if (this.sf1.sugarTest() == F && this.sf1.sf2.c == NOT) {
                    result = G;
                } else {
                    result = NOT;
                }
                break;
            case AND:
                if (this.sf1.sugarTest() == IFTHEN && this.sf2.sugarTest() == IFTHEN &&
                        this.sf1.sf1.sf1.equals(this.sf2.sf1.sf2.sf1) &&
                        this.sf1.sf1.sf2.sf1.equals(this.sf2.sf1.sf1)) {
                    result = IFF;
                } else {
                    result = AND;
                }
                break;
            case U:
                if (this.sf1.c == TRUE) {
                    result = F;
                } else {
                    result = U;
                }
                break;
            default:
                result = this.c;
        }
        return result;
    }

    public void print() {
        switch (this.getArity()) {
            case BINARY:
                System.out.print("(");
                this.sf1.print();
                System.out.print(" " + this.c.printString() + " ");
                this.sf2.print();
                System.out.print(")");
                break;
            case UNARY:
                System.out.print("(");
                System.out.print(this.c.printString());
                this.sf1.print();
                System.out.print(")");
                break;
            case ATOMIC:
                System.out.print(this.id);
                break;
            case BOOL:
                System.out.print(this.c.printString());
                break;
            default:
                //TODO:error
        }
    }

    //TODO: note in report that sugarprint not necessarily same as original (eg theta1)
    public void sugarPrint() {
        switch (this.sugarTest()) {
            case ATOM:
                System.out.print(this.id);
                break;
            case TRUE:
            case FALSE:
                System.out.print(this.c.printString());
                break;
            case NOT:
            case X:
            case A:
                System.out.print(this.c.printString());
                this.sf1.sugarPrint();
                break;
            case AND:
            case U:
                System.out.print("(");
                this.sf1.sugarPrint();
                System.out.print(" " + this.c.printString() + " ");
                this.sf2.sugarPrint();
                System.out.print(")");
                break;
            case E:
                System.out.print("E");
                this.sf1.sf1.sf1.sugarPrint();
                break;
            case F:
                System.out.print("F");
                this.sf2.sugarPrint();
                break;
            case G:
                System.out.print("G");
                this.sf1.sf2.sf1.sugarPrint();
                break;
            case OR:
                System.out.print("(");
                this.sf1.sf1.sf1.sugarPrint();
                System.out.print(" ∨ ");
                this.sf1.sf2.sf1.sugarPrint();
                System.out.print(")");
                break;
            case IFTHEN:
                System.out.print("(");
                this.sf1.sf1.sugarPrint();
                System.out.print(" → ");
                this.sf1.sf2.sf1.sugarPrint();
                System.out.print(")");
                break;
            case IFF:
                System.out.print("(");
                this.sf1.sf1.sf1.sugarPrint();
                System.out.print(" ↔ ");
                this.sf2.sf1.sf1.sugarPrint();
                System.out.print(")");
                break;
            default:
                //TODO:error
        }
    }

    //TODO: check subformulae for names before applying sugar?
    public void sugarPrint(Map<Formula, String> formulaeNames) {
        String name = formulaeNames.get(this);
        if (name != null) {
            System.out.print(name);
            return;
        }
        switch (this.sugarTest()) {
            case ATOM:
                System.out.print(this.id);
                break;
            case TRUE:
            case FALSE://TODO: change logic here to allow giving name to true?
                System.out.print(this.c.printString());
                break;
            case NOT:
            case X:
            case A:
                System.out.print(this.c.printString());
                this.sf1.sugarPrint(formulaeNames);
                break;
            case AND:
            case U:
                System.out.print("(");
                this.sf1.sugarPrint(formulaeNames);
                System.out.print(" " + this.c.printString() + " ");
                this.sf2.sugarPrint(formulaeNames);
                System.out.print(")");
                break;
            case E:
                System.out.print("E");
                this.sf1.sf1.sf1.sugarPrint(formulaeNames);
                break;
            case F:
                System.out.print("F");
                this.sf2.sugarPrint(formulaeNames);
                break;
            case G:
                System.out.print("G");
                this.sf1.sf2.sf1.sugarPrint(formulaeNames);
                break;
            case OR:
                System.out.print("(");
                this.sf1.sf1.sf1.sugarPrint(formulaeNames);
                System.out.print(" ∨ ");
                this.sf1.sf2.sf1.sugarPrint(formulaeNames);
                System.out.print(")");
                break;
            case IFTHEN:
                System.out.print("(");
                this.sf1.sf1.sugarPrint(formulaeNames);
                System.out.print(" → ");
                this.sf1.sf2.sf1.sugarPrint(formulaeNames);
                System.out.print(")");
                break;
            case IFF:
                System.out.print("(");
                this.sf1.sf1.sf1.sugarPrint(formulaeNames);
                System.out.print(" ↔ ");
                this.sf2.sf1.sf1.sugarPrint(formulaeNames);
                System.out.print(")");
                break;
            default:
                //TODO:error
        }
    }
}
