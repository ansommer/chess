package ui;

import static ui.EscapeSequences.*;


public class BlackBoard implements Board {
    public void print(String boardColor, String oponentColor) {
        //I'm too committed now but this isn't very sustainable
        //I think in the future, I will have every piece have a position, and if we get to that position print the piece, if not print a blank
        int direction = 1;
        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    H   G   F  E   D  C   B   A    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 1 ");
        printPieces(oponentColor, 1);
        nextline(1, direction);
        printPawns(oponentColor, 1);

        nextline(2, direction);
        printBlanks(1, 3);

        printPawns(boardColor, 2);
        nextline(7, direction);
        printPieces(boardColor, 2);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " " + 8 + " " +
                RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_WHITE + "    H   G   F  E   D  C   B   A    " + RESET_BG_COLOR);

    }

}