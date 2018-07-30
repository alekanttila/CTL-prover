package Prover;

import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.NONE;
import static Prover.StatusMessage.Level.SOME;

public class StatusMessage {
    public enum Level {
        NONE, SOME, MAX
    }
    public static Level level = MAX;
    public static void statusPrint(String message) {
        if (level.compareTo(NONE) > 0)  {
            System.out.println(message);
        }

    }
    public static void newSectionPrint(String message) {
        if (level.compareTo(NONE) > 0)  {
            System.out.println("\n\n\n" + message);
        }
    }
    public static void finePrint(String message) {
        if (level == MAX) {
            System.out.println(message);
        }
    }
    public static void fineNewSectionPrint(String message) {
        if (level ==  MAX)  {
            System.out.println("\n\n\n" + message);
        }
    }

}
