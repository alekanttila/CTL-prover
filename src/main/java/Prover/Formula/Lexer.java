package Prover.Formula;

import java.util.Stack;
import java.util.regex.Pattern;

public class Lexer {

    private static Stack<Token> tokenStack = new Stack<Token>();

    public static Stack<Token> scan(String input) throws LexerException {
        tokenStack.clear();
        tokenStack.push(new Token(Token.Type.END));

        for (int i = input.length() - 1; i >= 0; i--) {
            switch (input.charAt(i)) {
                case '~':
                case '¬':
                case '-':
                    tokenStack.push(new Token(Token.Type.NOT));
                    break;
                case '&':
                case '∧':
                case '*':
                    tokenStack.push(new Token(Token.Type.AND));
                    break;
                case '|':
                case '∨':
                case '+':
                    tokenStack.push(new Token(Token.Type.OR));
                    break;
                case '>':
                case '→':
                    tokenStack.push(new Token(Token.Type.IFTHEN));
                    break;
                case '=':
                case '↔':
                    tokenStack.push(new Token(Token.Type.IFF));
                    break;
                case '1':
                case '⊤':
                    tokenStack.push(new Token(Token.Type.TRUE));
                    break;
                case '0':
                case '⊥':
                    tokenStack.push(new Token(Token.Type.FALSE));
                    break;
                case 'X':
                    tokenStack.push(new Token(Token.Type.X));
                    break;
                case 'U':
                    tokenStack.push(new Token(Token.Type.U));
                    break;
                case 'A':
                    tokenStack.push(new Token(Token.Type.A));
                    break;
                case 'E':
                    tokenStack.push(new Token(Token.Type.E));
                    break;
                case 'F':
                    tokenStack.push(new Token(Token.Type.F));
                    break;
                case 'G':
                    tokenStack.push(new Token(Token.Type.G));
                    break;
                case '(':
                    tokenStack.push(new Token(Token.Type.LB));
                    break;
                case ')':
                    tokenStack.push(new Token(Token.Type.RB));
                    break;
                default:
                    if (Pattern.matches("[a-z]", input.substring(i,i+1))) {
                        tokenStack.add(new Token(Token.Type.ATOM, input.substring(i, i + 1)));
                        break;
                    } else if (Pattern.matches("[ \t\f\r\n]", input.substring(i,i+1))) {
                        break;
                    } else {
                        throw new LexerException("Invalid input character " + input.charAt(i) + " at input position "
                        + i);
                    }
            }
        }
        return tokenStack;
    }

    public static void printTokenList() {
        System.out.println("Printing token list:");
        for (Token t: tokenStack) {
            System.out.println(t.getType() + " " + t.getId());
        }
        System.out.println();
    }
}
