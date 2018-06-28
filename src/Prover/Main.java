    package Prover;

public class Main {
    public static void main(String args[]){
        System.out.println('A');
        Parser parser = new Parser();
        parser.scan();
        parser.printTokenList();
        System.out.println('B');
    }
}
