package ui;


import static ui.EscapeSequences.*;

public class WhiteBoard {
    public static void print(String boardColor, String oponentColor) {
        //I'm too committed now but this isn't very sustainable
        //I think in the future, I will have every piece have a position, and if we get to that position print the piece, if not print a blank

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 8 ");
        printPieces(oponentColor, 1);
        nextline(8);
        printPawns(oponentColor, 1);

        nextline(7);
        printBlanks();

        printPawns(boardColor, 2);
        nextline(2);
        printPieces(boardColor, 2);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + 1 + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_WHITE + "    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR);

    }//what if i join a game not there

    private static void printBlanks() {
        int lineNumber = 6;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                String bgColor = (j % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                String textColor = (j % 2 == 0) ? SET_TEXT_COLOR_DARK_PINK : SET_TEXT_COLOR_LIGHT_PINK;
                if (i % 2 == 0) {
                    bgColor = (j % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                    textColor = (j % 2 == 0) ? SET_TEXT_COLOR_LIGHT_PINK : SET_TEXT_COLOR_DARK_PINK;
                }

                System.out.print(bgColor + textColor);
                System.out.print(BLACK_PAWN);
            }
            nextline(lineNumber);
            lineNumber--;
        }
    }

    private static void printPieces(String color, int player) {
        System.out.print(color);
        String[] pieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        for (int i = 0; i < pieces.length; i++) {
            if (player == 1) {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                System.out.print(bgColor);
                System.out.print(pieces[i]);
            } else {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                System.out.print(bgColor);
                System.out.print(pieces[i]);
            }

        }
    }

    private static void printPawns(String color, int player) {
        System.out.print(color);
        for (int i = 0; i < 8; i++) {
            if (player == 1) {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                System.out.print(bgColor);
                System.out.print(BLACK_PAWN);
            } else {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                System.out.print(bgColor);
                System.out.print(BLACK_PAWN);
            }
        }
    }

    private static void nextline(int i) {
        int nextLine = i - 1;
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " " + nextLine + " ");
    }

}
