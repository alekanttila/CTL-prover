package Prover.Formula;

/*Token class used by Lexer and Parser
 */
public class Token {
    //Token types recognized by the scanner
    public enum Type {
        ATOM, NOT, AND,
        OR, IFTHEN, IFF, TRUE, FALSE,
        X, U, A,
        E, F, G,
        LB, RB,
        // Extra parser symbols included for convenience
        //(end symbol and non-terminals)
        END, ALPHA, BETA, GAMMA, DELTA
    }

    private Type type;
    private String id;

    public Token(Type type, String id){
        this.type = type;
        this.id = id;
    }
    public Token(Type type)
    {
        this(type, null);
    }
    public Type getType() {
        return this.type;
    }
    public String getId() {
        return this.id;
    }

}
