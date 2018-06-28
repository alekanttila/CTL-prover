package Prover;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Parser {
    private enum TokenType {
        ATOM, NOT, AND,
        OR, IFTHEN, IFF, TRUE, FALSE,
        X, U, A,
        E, F, G,
        LB, RB
    }
    private class Token {
        private TokenType type;
        private String id;
        public Token(TokenType type, String id){
            this.type = type;
            this.id = id;
        }
        public Token(TokenType type){
            this(type, null);
        }
    }

    private List<Token> tokenList= new ArrayList<Token>();

    public void scan() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        for (int i = 0; i < input.length(); i++) {
            System.out.println("testing " + input.charAt(i));
            switch (input.charAt(i)) {
                case '~':
                    tokenList.add(new Token(TokenType.NOT));
                    break;
                case '&':
                    tokenList.add(new Token(TokenType.AND));
                    break;
                case '|':
                    tokenList.add(new Token(TokenType.OR));
                    break;
                case '>':
                    tokenList.add(new Token(TokenType.IFTHEN));
                    break;
                case '=':
                    tokenList.add(new Token(TokenType.IFF));
                    break;
                case '1':
                    tokenList.add(new Token(TokenType.TRUE));
                    break;
                case '0':
                    tokenList.add(new Token(TokenType.FALSE));
                    break;
                case 'X':
                    tokenList.add(new Token(TokenType.X));
                    break;
                case 'U':
                    tokenList.add(new Token(TokenType.U));
                    break;
                case 'A':
                    tokenList.add(new Token(TokenType.A));
                    break;
                case 'E':
                    tokenList.add(new Token(TokenType.E));
                    break;
                case 'F':
                    tokenList.add(new Token(TokenType.F));
                    break;
                case 'G':
                    tokenList.add(new Token(TokenType.G));
                    break;
                case '(':
                    tokenList.add(new Token(TokenType.LB));
                    break;
                case ')':
                    tokenList.add(new Token(TokenType.RB));
                    break;
                default:
                    if (Pattern.matches("[a-z]", input.substring(i,i+1))) {
                        System.out.println("match " + input.substring(i, i + 1));
                        tokenList.add(new Token(TokenType.ATOM, input.substring(i, i + 1)));
                        break;
                    } else if (Pattern.matches("[ \t\f\r\n]", input.substring(i,i+1))) {
                        break;
                    } else {
                        System.out.println("Scan error");
                        System.err.println("Scan error");
                    }
            }
        }
        System.out.println(input);
    }

    public void parse() {
        
    }

    public void printTokenList() {
        for (Token t: this.tokenList) {
            System.out.println(t.type + " " + t.id);
        }
    }
}
