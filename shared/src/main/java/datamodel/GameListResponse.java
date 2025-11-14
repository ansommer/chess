package datamodel;

import java.util.ArrayList;


public record GameListResponse(ArrayList<GameData> games) {
}
//maybe I'll make a tostring here to format it