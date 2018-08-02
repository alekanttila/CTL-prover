package Prover;

import java.util.ArrayList;
import java.util.List;

import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.NONE;
import static Prover.StatusMessage.Level.SOME;

public class StatusMessage {
    public enum Level {
        NONE, SOME, MAX
    }

    public  enum Area {
        LEXER, PARSER, HUES, COLOURS, TABLEAU, LG
    }

    public static Level setLevel = MAX;
    public static List<Area> setAreas = new ArrayList<Area>();

    public static void statusPrint(Area a, Level l, String message) {
        if (setAreas.contains(a) && l.compareTo(setLevel) <= 0)  {
            System.out.println(message);
        }

    }
    public static void subSectionPrint(Area a, Level l, String message) {
        statusPrint(a, l, "\n\n\n\n\n" + message);
    }

    public static void sectionPrint(Area a, Level l, String message) {
        statusPrint(a, l, "\n\n\n\n\n"
                + new String(new char[100]).replace("\0", "-")
                + "\n" + message);
    }
    //stackoverflow.com/questions/2255500

}
