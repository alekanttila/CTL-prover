package Prover;

import Prover.Formula.ColourSet;
import Prover.Formula.Formula;
import Prover.Formula.FormulaSet;
import Prover.Formula.HueSet;
import Prover.Formula.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

public class ResultSet {
    //TODO: write in report that this is separate to enable separate manipulation
    private String originalFormula;
    private Stack<Token> tokenStack;
    private Formula formula;
    //TODO: add formulanames as member in all these;; change methods
    private Map<Formula, String> formulaNames = new HashMap<Formula, String>();
    private FormulaSet closure;
    private HueSet allHues;
    private ColourSet allColours;
    private ColourSet fColours;//remove

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

    public TreeSet<HueSet> getRAClasses() {
        TreeSet<HueSet> result;
        if (getAllHues() != null) {
            result = getAllHues().getrAClasses();
        } else {
            result = null;
        }
        return result;
    }

    public void setAllColours(ColourSet allColours) {
        this.allColours = allColours;
    }

    public ColourSet getAllColours() {
        return allColours;
    }

    public void setFColours(ColourSet fColours) {
        this.fColours = fColours;
    }

    public ColourSet getFColours() {
        return fColours;
    }
}

