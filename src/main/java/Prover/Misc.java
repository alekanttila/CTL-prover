package Prover;

public class Misc {

    public static void printRelation(boolean[][] r, String memberInitial, String rName) {
        boolean printed = false;
        for (int i = 0; i <  r.length; i++) {
            for (int j = 0; j < r[i].length; j++) {
                if (r[i][j]) {
                    System.out.print(memberInitial + i + rName + memberInitial + j + " ");
                    printed = true;
                }
            }
            if (printed) {
                System.out.println();
                printed = false;
            }
        }
    }
}
