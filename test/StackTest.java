import Prover.Stack;
import Prover.StackArray;
import Prover.StackException;

public class StackTest {
    public static void main(String[] args) {
        try {
            Stack stacka = new StackArray();
            stacka.push("SSSS");
            System.out.println("ASAS");
        }
        catch (StackException e){
            e.printStackTrace();
        }
    }
}
