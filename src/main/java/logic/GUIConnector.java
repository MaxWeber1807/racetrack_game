package logic;

import java.util.ArrayList;

/**
 * interface that is implemented by JavaFXGUI for output and FakeGui for testing
 */
public interface GUIConnector {

    default void initializeGame(Board b, StartCrossDirection dir, boolean inRace, Player[] players) {
    }

    default void changeGridPaneCell(int i, int j, Surfaces sur, StartCrossDirection dir, boolean isStart) {
    }
    default void initializePaneStyles(Board b, StartCrossDirection dir) {
    }
    default void outputErrorMessage(ErrorCodes errorCode, String currActPl) {
    }
    default void updatePlayerPosGUI(Logic logic, Player[] players, boolean isAI, Position oldPos,
                                           Position newPos, int player_number, boolean crashFound) {
    }
    default void updatePlayerInfoTextField(String s) {
    }
    default void showPossibleField(Position pos, Surfaces sur, StartCrossDirection dir) {
    }
    default void playReplayMove(Logic logic, ArrayList<ArrayList<Position>> replayData, int moveCount, int animationSpeed) {
    }
}

