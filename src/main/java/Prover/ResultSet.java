package Prover;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

public class ResultSet {
    //TODO: write in report that this is separate to enable separate manipulation
    private String originalFormula;
    private Stack<Token> tokenStack;
    private Formula formula;
    private Map<Formula, String> formulaNames = new HashMap<Formula, String>();
    private FormulaSet closure;
    private HueSet allHues;
    private boolean[][] hueRX;
    private boolean[][] rA;
    private TreeSet<HueSet> rAClasses;
    private ColourSet allColours;
    private boolean[][] colourRX;
    private ColourSet fColours;

    public void setOriginalFormula(String originalFormula) {
        //TODO: trim etc
        this.originalFormula = originalFormula;
    }

    public String getOriginalFormula() {
        return originalFormula;
    }

    public void setTokenStack(Stack<Token> tokenStack) {
        this.tokenStack = tokenStack;
    }

    public Stack<Token> getTokenStack() {
        return tokenStack;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormulaNames(Map<Formula, String> formulaNames) {
        this.formulaNames = formulaNames;
    }

    public void addFormulaName(Formula f, String s) {
        if (this.formulaNames == null) {
            this.formulaNames = new HashMap<Formula, String>();
        }
        this.formulaNames.put(f, s);
    }

    public Map<Formula, String> getFormulaNames() {
        return formulaNames;
    }

    public void setClosure(FormulaSet closure) {
        this.closure = closure;
    }

    public FormulaSet getClosure() {
        return closure;
    }

    public void setAllHues(HueSet allHues) {
        this.allHues = allHues;
    }

    public HueSet getAllHues() {
        return allHues;
    }

    public void setHueRX(boolean[][] hueRX) {
        this.hueRX = hueRX;
    }

    public boolean[][] getHueRX() {
        return hueRX;
    }

    public void setRA(boolean[][] rA) {
        this.rA = rA;
    }

    public boolean[][] getRA() {
        return rA;
    }

    public void setRAClasses(TreeSet<HueSet> rAClasses) {
        this.rAClasses = rAClasses;
    }

    public TreeSet<HueSet> getRAClasses() {
        return rAClasses;
    }

    public void setAllColours(ColourSet allColours) {
        this.allColours = allColours;
    }

    public ColourSet getAllColours() {
        return allColours;
    }

    public void setColourRX(boolean[][] colourRX) {
        this.colourRX = colourRX;
    }

    public boolean[][] getColourRX() {
        return colourRX;
    }

    public void setFColours(ColourSet fColours) {
        this.fColours = fColours;
    }

    public ColourSet getFColours() {
        return fColours;
    }
}

