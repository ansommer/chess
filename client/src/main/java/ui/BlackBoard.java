package ui;

import chess.ChessGame;

import static ui.EscapeSequences.*;


public class BlackBoard implements Board {

    private BoardPrint boardPrint = new BoardPrint();

    public void print(String boardColor, String oponentColor) {
        //I'm too committed now but this isn't very sustainable
        //I think in the future, I will have every piece have a position, and if we get to that position print the piece, if not print a blank


        boardPrint.print(ChessGame.TeamColor.BLACK);

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " " + 8 + " " +
                RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    H   G   F  E   D  C   B   A    " + RESET_BG_COLOR);

    }

}