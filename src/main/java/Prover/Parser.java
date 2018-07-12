package Prover;

import java.util.Stack;

import static Prover.Formula.Connective.*;

public class Parser {

    static Token currentToken;
    static Stack<Token> tokenStack;

    public static Formula parse(Stack<Token> input) {
        tokenStack = input;
        currentToken = input.pop();
        Formula f = alpha();
        return removeSugar(f);
    }

    private static void eat(Token.Type t) {
        if (t == currentToken.getType()) {
            currentToken = tokenStack.pop();
        }
    }

    private static Formula removeSugar(Formula f) {
        Formula returnFormula = null;
            switch (f.c) {
                case AND:
                case U:
                    returnFormula = new Formula(removeSugar(f.sf1), removeSugar(f.sf2), f.c);
                    break;
                case OR:
                    Formula notSf1 = new Formula(removeSugar(f.sf1), NOT);
                    Formula notSf2 = new Formula(removeSugar(f.sf2), NOT);
                    Formula neither = new Formula(notSf1, notSf2, AND);
                    returnFormula = new Formula(neither, NOT);
                    break;
                case IFTHEN:
                    Formula notConsequent = new Formula(removeSugar(f.sf2), NOT);
                    Formula notIfThen = new Formula(removeSugar(f.sf1), notConsequent, AND);
                    returnFormula = new Formula(notIfThen, NOT);
                    break;
                case IFF:
                    Formula ifThen = new Formula(f.sf1, f.sf2, IFTHEN);
                    Formula onlyIf = new Formula(f.sf2, f.sf1, IFTHEN);
                    returnFormula = removeSugar(new Formula(ifThen, onlyIf, AND));
                    break;
                case NOT:
                case X:
                case A:
                    returnFormula = new Formula(removeSugar(f.sf1), f.c);
                    break;
                case E:
                    Formula not = new Formula(removeSugar(f.sf1), NOT);
                    Formula aNot = new Formula(not, A);
                    returnFormula = new Formula(aNot, NOT);
                    break;
                case F:
                    returnFormula = new Formula(new Formula(TRUE), removeSugar(f.sf1), U);
                    break;
                case G:
                    Formula not2 = new Formula(f.sf1, NOT);
                    Formula fNot = new Formula(not2, F);
                    returnFormula = removeSugar(new Formula(fNot, NOT));
                    break;
                case ATOM:
                    returnFormula = f;
                    break;
                case TRUE:
                    returnFormula = f;
                    break;
                case FALSE:
                    returnFormula = new Formula(new Formula(TRUE), NOT);
                    break;
                default:
                    //TODO: error
            }
        return returnFormula;
    }




    private static Formula alpha() {
        System.out.println("ALPHA");
        Formula betaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
            case LB:
                betaF = beta();
                f = alpha2(betaF);
                break;
            default:
                //TODO: error
        }
        System.out.println("ALPHA FORMULA");
        f.print();
        return f;
    }

    private static Formula alpha2(Formula betaF) {
        System.out.println("ALPHA2");
        Formula betaF2 = null;
        Formula alpha2F = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case IFF:
                eat(Token.Type.IFF);
                betaF2 = beta();
                f = new Formula(betaF, betaF2, IFF);
                alpha2F = alpha2(f);
                if (alpha2F != null) {
                    f = alpha2F;
                }
                break;
            case RB:
            case END:
                f = betaF;
                break;
            default:
                //TODO: error
        }
        System.out.println("ALPHA2 FORMULA");
        if (f != null) {
            f.print();
        }
        f.print();
        return f;
    }

    private static Formula beta() {
        System.out.println("BETA");
        Formula gammaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
            case LB:
                gammaF = gamma();
                f = beta2(gammaF);
                break;
            default:
                //TODO: error
        }
        System.out.println("BETA FORMULA");
        f.print();
        return f;
    }

    private static Formula beta2(Formula gammaF) {
        System.out.println("BETA2");
        Formula gammaF2 = null;
        Formula beta2F = null;
        Formula f = null;
        System.out.println("TOKEN");
        System.out.println(currentToken.getType());
        System.out.println("TOKEN");
        switch (currentToken.getType()) {
            case IFTHEN:
                eat(Token.Type.IFTHEN);
                gammaF2 = gamma();
                f = new Formula(gammaF, gammaF2, IFTHEN);
                beta2F = beta2(f);
                if (beta2F != null) {
                    f = beta2F;
                }
                break;
            case RB:
            case END:
            case IFF:
                f = gammaF;
                break;
            default:
                //TODO: error
        }
        System.out.println("BETA2 FORMULA");
        if (f != null) {
            f.print();
        }
        f.print();
        return f;
    }

    private static Formula gamma() {
        System.out.println("GAMMA");
        Formula deltaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
            case LB:
                deltaF = delta();
                f = gamma2(deltaF);
                break;
            default:
                //TODO: error
        }
        System.out.println("GAMMA FORMULA");
        f.print();
        return f;
    }

    private static Formula gamma2(Formula deltaF) {
        System.out.println("GAMMA2");
        Formula deltaF2 = null;
        Formula gamma2F = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case OR:
                eat(Token.Type.OR);
                deltaF2 = delta();
                f = new Formula(deltaF, deltaF2, OR);
                gamma2F = gamma2(f);
                if (gamma2F != null) {
                    f = gamma2F;
                }
                break;
            case RB:
            case END:
            case IFF:
            case IFTHEN:
                f = deltaF;
                break;
            default:
                //TODO: error
        }
        System.out.println("GAMMA2 FORMULA");
        if (f != null) {
            f.print();
        }
        return f;
    }

    private static Formula delta() {
        System.out.println("DELTA");
        Formula zetaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
            case LB:
                zetaF = zeta();
                f = delta2(zetaF);
                break;
            default:
                //TODO: error
        }
        System.out.println("DELTA FORMULA");
        f.print();
        return f;
    }

    private static Formula delta2(Formula zetaF) {
        System.out.println("DELTA2");
        Formula zetaF2 = null;
        Formula delta2F = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case AND:
                eat(Token.Type.AND);
                zetaF2 = zeta();
                f = new Formula(zetaF, zetaF2, AND);
                delta2F = delta2(f);
                if (delta2F != null) {
                    f = delta2F;
                }
                break;
            case RB:
            case END:
            case IFF:
            case IFTHEN:
            case OR:
                f = zetaF;
                break;
            default:
                //TODO: error
        }
        System.out.println("DELTA2 FORMULA");
        if (f != null) {
            f.print();
        }
        return f;
    }

    private static Formula zeta() {
        System.out.println("ZETA");
        Formula etaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case NOT:
            case X:
            case A:
            case E:
            case F:
            case G:
            case LB:
                etaF = eta();
                f = zeta2(etaF);
                break;
            default:
                //TODO: error
        }
        System.out.println("ZETA FORMULA");
        f.print();
        return f;
    }

    private static Formula zeta2(Formula etaF) {
        System.out.println("ZETA2");
        Formula etaF2 = null;
        Formula zeta2F = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case U:
                eat(Token.Type.U);
                etaF2 = eta();
                f = new Formula(etaF, etaF2, U);
                zeta2F = zeta2(f);
                if (zeta2F != null) {
                    f = zeta2F;
                }
                break;
            case RB:
            case END:
            case IFF:
            case IFTHEN:
            case OR:
            case AND:
                f = etaF;
                break;
            default:
                System.out.println("ERROR");
                //TODO: error
        }
        System.out.println("ZETA2 FORMULA");
        if (f != null) {
            f.print();
        }
        return f;
    }

    private static Formula eta() {
        System.out.println("ETA");
        Formula etaF = null;
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
            case TRUE:
            case FALSE:
            case LB:
                f = theta();
                break;
            case NOT:
                eat(Token.Type.NOT);
                etaF = eta();
                f = new Formula(etaF, NOT);
                break;
            case X:
                eat(Token.Type.X);
                etaF = eta();
                f = new Formula(etaF, X);
                break;
            case A:
                eat(Token.Type.A);
                etaF = eta();
                f = new Formula(etaF, A);
                break;
            case E:
                eat(Token.Type.E);
                etaF = eta();
                f = new Formula(etaF, E);
                break;
            case F:
                eat(Token.Type.F);
                etaF = eta();
                f = new Formula(etaF, F);
                break;
            case G:
                eat(Token.Type.G);
                etaF = eta();
                f = new Formula(etaF, G);
                break;
            default:
                //TODO: error
        }
        System.out.println("ETA FORMULA");
        f.print();
        return f;
    }

    private static Formula theta() {
        System.out.println("THETA");
        Formula f = null;
        switch (currentToken.getType()) {
            case ATOM:
                f = new Formula(currentToken.getId());
                eat(Token.Type.ATOM);
                break;
            case TRUE:
                f = new Formula(TRUE);
                eat(Token.Type.TRUE);
                break;
            case FALSE:
                f = new Formula(FALSE);
                eat(Token.Type.FALSE);
                break;
            case LB:
                eat(Token.Type.LB);
                f = alpha();
                eat(Token.Type.RB);
                break;
            default:
                //TODO: error
        }
        System.out.println("THETA FORMULA");
        f.print();
        return f;
    }
}


