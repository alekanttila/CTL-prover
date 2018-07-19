package Prover;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ResultSet {
    private String originalFormula;
    private Stack<Token> tokenStack;
    private Formula formula;
    private Map<Formula, String> formulaNames = new HashMap<Formula, String>();
    private FormulaSet closure;
    private HueSet hueSet;
    private boolean rX;
    private boolean rA;
    private ColourSet colourSet;
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

    public ColourSet getColourSet() {
        return colourSet;
    }

    public void setHueSet(HueSet hueSet) {
        this.hueSet = hueSet;
    }

    public HueSet getHueSet() {
        return hueSet;
    }
}

