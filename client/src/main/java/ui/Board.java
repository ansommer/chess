package ui;


import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

public class Board {
    public static void main(String[] args) {

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 8 ");
        printPieces(SET_TEXT_COLOR_BLACK);
        nextline(8);
        printPawns(SET_TEXT_COLOR_BLACK);
        nextline(7);
        printBlanks();
        printPieces(SET_TEXT_COLOR_WHITE);
        nextline(2);
        printPawns(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + 1 + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
        System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR);

    }

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

    private static void printPieces(String color) {
        System.out.print(color);
        String[] pieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        for (int i = 0; i < pieces.length; i++) {
            String bgColor = (i % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
            System.out.print(bgColor);
            System.out.print(pieces[i]);
        }
    }

    private static void printPawns(String color) {
        System.out.print(color);
        for (int i = 0; i < 8; i++) {
            String bgColor = (i % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
            System.out.print(bgColor);
            System.out.print(BLACK_PAWN);
        }
    }

    private static void nextline(int i) {
        int nextLine = i - 1;
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " " + nextLine + " ");
    }

}
