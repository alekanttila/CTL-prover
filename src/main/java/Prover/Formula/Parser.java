package Prover.Formula;

import java.util.Stack;

import static Prover.Formula.Formula.Connective.*;
import static Prover.Formula.Token.Type.RB;

public class Parser {

    static Token currentToken;
    static Stack<Token> tokenStack;
    static int currentPosition;

    public static Formula parse(Stack<Token> input) throws ParserException {
        tokenStack = input;
        currentToken = input.pop();
        currentPosition = 0;
        Formula f = alpha();
        return removeSugar(f);
    }

    private static void eat(Token.Type t) {
        if (t == currentToken.getType()) {
            currentToken = tokenStack.pop();
            currentPosition++;
        } else throw new AssertionError("Parse error: eat: unexpected Token type");
    }

    private static Formula removeSugar(Formula f) {
        Formula returnFormula = null;
            switch (f.getC()) {
                case AND:
                case U:
                    returnFormula = new Formula(removeSugar(f.getSf1()), removeSugar(f.getSf2()), f.getC());
                    break;
                case OR:
                    Formula notSf1 = new Formula(removeSugar(f.getSf1()), NOT);
                    Formula notSf2 = new Formula(removeSugar(f.getSf2()), NOT);
                    Formula neither = new Formula(notSf1, notSf2, AND);
                    returnFormula = new Formula(neither, NOT);
                    break;
                case IFTHEN:
                    Formula notConsequent = new Formula(removeSugar(f.getSf2()), NOT);
                    Formula notIfThen = new Formula(removeSugar(f.getSf1()), notConsequent, AND);
                    returnFormula = new Formula(notIfThen, NOT);
                    break;
                case IFF:
                    Formula ifThen = new Formula(f.getSf1(), f.getSf2(), IFTHEN);
                    Formula onlyIf = new Formula(f.getSf2(), f.getSf1(), IFTHEN);
                    returnFormula = removeSugar(new Formula(ifThen, onlyIf, AND));
                    break;
                case NOT:
                case X:
                case A:
                    returnFormula = new Formula(removeSugar(f.getSf1()), f.getC());
                    break;
                case E:
                    Formula not = new Formula(removeSugar(f.getSf1()), NOT);
                    Formula aNot = new Formula(not, A);
                    returnFormula = new Formula(aNot, NOT);
                    break;
                case F:
                    returnFormula = new Formula(new Formula(TRUE), removeSugar(f.getSf1()), U);
                    break;
                case G:
                    Formula not2 = new Formula(f.getSf1(), NOT);
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
                    throw new AssertionError("Connective " + f.getC()+ " not accounted for");
            }
        return returnFormula;
    }

    private static Formula alpha() throws ParserException {
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
                throw new ParserException("Syntax error: expected alpha production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only alpha-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula alpha2(Formula betaF) throws ParserException {
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
                throw new ParserException("Syntax error: expected alpha2 production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only alpha2-produce with one of: '=', ')', or the end of input.");
        }
        return f;
    }

    private static Formula beta() throws ParserException {
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
                throw new ParserException("Syntax error: expected beta production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only beta-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula beta2(Formula gammaF) throws ParserException {
        Formula gammaF2 = null;
        Formula beta2F = null;
        Formula f = null;
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
                throw new ParserException("Syntax error: expected beta2 production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only beta2-produce with one of: '>', '=', ')', or the end of input.");
        }
        return f;
    }

    private static Formula gamma() throws ParserException {
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
                throw new ParserException("Syntax error: expected gamma production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only gamma-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula gamma2(Formula deltaF) throws ParserException {
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
                throw new ParserException("Syntax error: expected gamma2 production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only gamma2-produce with one of: '|', '>', '=', ')', or the end of input.");
        }
        return f;
    }

    private static Formula delta() throws ParserException {
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
                throw new ParserException("Syntax error: expected delta production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only delta-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula delta2(Formula zetaF) throws ParserException {
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
                throw new ParserException("Syntax error: expected delta2 production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only delta2-produce with one of: '&', '>', '=', ')', or the end of input.");
        }
        return f;
    }

    private static Formula zeta() throws ParserException {
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
                throw new ParserException("Syntax error: expected zeta production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only zeta-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula zeta2(Formula etaF) throws ParserException {
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
                throw new ParserException("Syntax error: expected zeta2 production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only zeta2-produce with one of: 'U', '>', '=', ')', or the end of input.");
        }
        return f;
    }

    private static Formula eta() throws ParserException {
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
                throw new ParserException("Syntax error: expected eta production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only eta-produce with one of: atom, '1', '0', '~', 'X', 'A', 'E', 'F', 'G', or '('.");
        }
        return f;
    }

    private static Formula theta() throws ParserException {
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
                if (currentToken.getType() == Token.Type.RB) {
                    eat(RB);
                } else {
                    throw new ParserException("Syntax error: attempted theta production, but, but after processing "
                            + "'(' and alpha-producing, received a token of type "
                            + currentToken.getType() + " at input position " + currentPosition +
                            ". Expected ')'.");
                }
                break;
            default:
                throw new ParserException("Syntax error: expected theta production, but received a token of type "
                        + currentToken.getType() + " at input position " + currentPosition +
                        ". Can only theta-produce with one of: atom, '1', '0', or '('.");
        }
        return f;
    }
}


