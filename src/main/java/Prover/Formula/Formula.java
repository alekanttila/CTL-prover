package Prover.Formula;

import Prover.ResultSet;

import java.util.*;

import static Prover.Formula.Formula.Arity.*;
import static Prover.Formula.Formula.Connective.*;

public class Formula implements Comparable<Formula>{

    //INNER ENUMS

    public enum Connective {
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
    public enum Arity {
        ATOMIC, BOOL, UNARY, BINARY;
    }

    //STATES

    private final Formula sf1;
    private final Formula sf2;
    private final Connective c;
    private final String id;
    //store length to avoid recursive calculations;
    // easy to keep track of when constructing
    private final int length;
    private ResultSet results;

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

    public void createResultSet() {
        this.results = new ResultSet();
        results.setFormula(this);
    }

    public void setInitialResults(String originalInput, Stack<Token> tokenStack) {
        if (this.results == null) {
            createResultSet();
        }
        this.results.setOriginalFormula(originalInput);
        this.results.setTokenStack(tokenStack);
    }

    //METHODS

    //note: this ensures sets work correctly!
    //only properly works on sugar-free formulae (think more, maybe remove this)
    //TODO: check nulls!
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (this == obj) {
            result = true;
        } else if (obj.getClass() != Formula.class) {
            result = false;
        } else {
            Formula f = (Formula)(obj);
            Connective thisConnective = this.c;
            if (c == f.c) {
                switch (this.getArity()) {
                    case BINARY:
                        //syntactic equality-orcer matters
                        result = ((this.sf1.equals(f.sf1) && this.sf2.equals(f.sf2)));
                        break;
                    case UNARY:
                        result = this.sf1.equals(f.sf1);
                        break;
                    case ATOMIC:
                        if (this.id.compareTo(f.id) == 0) {
                            result = true;
                        } else {
                            result = false;
                        }
                        break;
                    case BOOL:
                        result = (this.c == f.c);
                        break;
                    default:
                        //TODO: error
                }
            } else {
                //not required; included for clarity;
                //case when connectives are different
                result = false;
            }
        }
        return result;
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

    public Formula getSf1() {
        return sf1;
        //TODO: exception/asserror?
    }

    public Formula getSf2() {
        return sf2;
        //TODO: exception/asserror?
    }

    public Connective getC() {
        return c;
        //TODO: assertionerror??
    }

    public int getLength() {
        return length;
    }

    public String getId() {
        return id;
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

    public Map<Formula, String> getFormulaNames() {
        Map<Formula, String> result;
        if (results != null && results.getFormulaNames()!= null) {
            result = results.getFormulaNames();
        } else {
            result = new HashMap<Formula, String>();
        }
        return result;
    }

    public void addFormulaName(Formula f, String name) {
        if (results == null) {
            createResultSet();
        }
        results.addFormulaName(f, name);
    }

    public FormulaSet getClosure() {
        FormulaSet result;
        if (results != null && results.getClosure() != null) {
            result = results.getClosure();
        } else {
            result = new FormulaSet();
            result.add(this);
            //note: closure is a syntactic notion, so ~φ gets added even if φ≡~χ
            result.add(new Formula(this, NOT));

            switch (this.getArity()) {
                case BINARY:
                    result.addAll(this.sf1.getClosure());
                    result.addAll(this.sf2.getClosure());
                    break;
                case UNARY:
                    result.addAll(this.sf1.getClosure());
                    break;
                case ATOMIC:
                case BOOL:
                    break;
                default:
                    //TODO:error
            }
            if (results == null) {
                createResultSet();
            }
            results.setClosure(result);
        }
        return result;
    }

    public HueSet getAllHues() {
        HueSet result;
        if (results != null && results.getAllHues() != null) {
            result = results.getAllHues();
        } else {
            FormulaSet closure = this.getClosure();
            result = HueSet.getAllHues(closure);
            results.setAllHues(result);
        }
        return result;
    }

    public TreeSet<HueSet> getRAClasses() {
        TreeSet<HueSet> result;
        if (results != null && results.getRAClasses() != null) {
            result = results.getRAClasses();
        } else {
            HueSet allHues = this.getAllHues();
            result = allHues.getrAClasses();
        }
        return result;
    }

    public ColourSet getAllColours() {
        ColourSet result;
        if (results != null && results.getAllColours() != null) {
            result = results.getAllColours();
        } else {
            HueSet allHues = this.getAllHues();
            result = ColourSet.getAllColours(allHues);
            results.setAllColours(result);
        }
        return result;
    }

    public HueSet getFHues() {
        HueSet result;
        if (results != null && results.getFHues() != null) {
            result = results.getFHues();
        } else {
            HueSet hueSet = getAllHues();
            result = hueSet.getHuesWithF(this);
            results.setFHues(result);
        }
        return result;
    }

    public ColourSet getFColours() {
        ColourSet result;
        if (results != null && results.getFColours() != null) {
            result = results.getFColours();
        } else {
            ColourSet colourSet = getAllColours();
            result = colourSet.getColoursWithF(this);
            results.setFColours(result);
        }
        return result;
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

    public String printString() {
        String result = "";
        switch (this.getArity()) {
            case BINARY:
                result = "( " + this.sf1.printString() + " " + this.c.printString() + " ";
                result = result + this.sf2.printString() + ")";
                break;
            case UNARY:
                result = "( " + this.c.printString() + ")";
                break;
            case ATOMIC:
                result = this.id;
                break;
            case BOOL:
                result = this.c.printString();
                break;
            default:
                //TODO:error
        }
        return result;
    }

    public String sugarString() {
        Map<Formula, String> formulaNames;
        if (results != null && results.getFormulaNames() != null) {
            formulaNames = results.getFormulaNames();
        } else {
            formulaNames = new HashMap<Formula, String>();
        }
        return sugarString(formulaNames);
    }

    //TODO: note in report that sugarprint not necessarily same as original (eg theta1)
    //TODO: check subformulae for names before applying sugar?
    public String sugarString(Map<Formula, String> formulaNames) {
        String result = "";
        String name = formulaNames.get(this);
        if (name != null) {
            return name;
        }
        switch (this.sugarTest()) {
            case ATOM:
                result = result + this.id;
                break;
            case TRUE://TODO: change logic here to allow giving name to true?
                result = result + this.c.printString();
                break;
            case FALSE:
                result = result + "⊥";
                break;
            case NOT:
            case X:
            case A:
                result = result + this.c.printString();
                result = result + this.sf1.sugarString(formulaNames);
                break;
            case AND:
            case U:
                result = result + "(" + this.sf1.sugarString(formulaNames) + " " + this.c.printString() + " ";
                result = result + this.sf2.sugarString(formulaNames) + ")";
                break;
            case E:
                result = result + "E";
                result = result + this.sf1.sf1.sf1.sugarString(formulaNames);
                break;
            case F:
                result = result + "F";
                result = result + this.sf2.sugarString(formulaNames);
                break;
            case G:
                result = result + "G";
                result = result + this.sf1.sf2.sf1.sugarString(formulaNames);
                break;
            case OR:
                result = result + "(" + this.sf1.sf1.sf1.sugarString(formulaNames) + " ∨ ";
                result = result + this.sf1.sf2.sf1.sugarString(formulaNames) + ")";
                break;
            case IFTHEN:
                result = result + "(" + this.sf1.sf1.sugarString(formulaNames) + " → ";
                result = result + this.sf1.sf2.sf1.sugarString(formulaNames) + ")";
                break;
            case IFF:
                result = result + "(" + this.sf1.sf1.sf1.sugarString(formulaNames) + " ↔ ";
                result = result + this.sf2.sf1.sf1.sugarString(formulaNames) + ")";
                break;
            default:
                //TODO:error
        }
        return result;
    }
}
