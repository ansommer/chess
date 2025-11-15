package ui;


import static ui.EscapeSequences.*;

public class WhiteBoard implements Board {
    public void print(String boardColor, String oponentColor) {
        //I'm too committed now but this isn't very sustainable
        //I think in the future, I will have every piece have a position, and if we get to that position print the piece, if not print a blank
        int direction = -1;
        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 8 ");
        printPieces(oponentColor, 1);
        nextline(8, direction);
        printPawns(oponentColor, 1);

        nextline(7, direction);
        printBlanks(-1, 6);

        printPawns(boardColor, 2);
        nextline(2, direction);
        printPieces(boardColor, 2);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + 1 + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_WHITE + "    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR);

    }
}
