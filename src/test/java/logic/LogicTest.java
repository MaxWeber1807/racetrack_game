package logic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * test class
 */
public class LogicTest {

    @Test
    public void isBoardValid() {
        Board b = new Board(
                """
                        0000000000
                        0111111000
                        0100001000
                        0121111000
                        0000000000""",
                10, 5);
        assertEquals(ErrorCodes.NoError, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidNoRoundCourse() {
        Board b = new Board(
                """
                        0000000000
                        0111111000
                        0100000000
                        0121111000
                        0000000000""",
                10, 5);
        assertEquals(ErrorCodes.NoRoundCourse, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidNoStartingLine() {
        Board b = new Board(
                """
                        0000000000
                        0111111000
                        0100001000
                        0111111000
                        0000000000""",
                10, 5);
        assertEquals(ErrorCodes.StartMissing, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidComplicatedMap() {
        Board b;
        b = new Board(
                """
                        0000000000
                        0111111000
                        0100001100
                        0111000100
                        0100000100
                        0110000100
                        0010000100
                        0012110100
                        0012110100
                        0000011100
                        """,
                10, 10);
        assertEquals(ErrorCodes.NoError, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidComplicatedMap2() {
        Board b = new Board(
                "0000000000\n" +
                "0111111000\n" +
                "0100001100\n" +
                "0111000100\n" +
                "0100000100\n" +
                "0110101100\n" +
                "0010100100\n" +
                "0012110111\n" +
                "0012110100\n" +
                "0000011100\n",
                10, 10);
        assertEquals(ErrorCodes.NoError, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidSLHasRoadAtEnd() {
        Board b = new Board(
                     "0000000000\n" +
                        "0111111000\n" +
                        "0100001000\n" +
                        "0112111000\n" +
                        "0001000000\n",
                10, 5);
        assertEquals(ErrorCodes.StartNotComplete, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidDoubleSL() {
        Board b = new Board(
                     "0000000000\n" +
                        "0111111000\n" +
                        "0100002000\n" +
                        "0112111000\n" +
                        "0000000000\n",
                10, 5);
        assertEquals(ErrorCodes.StartNotComplete, b.isBoardValidForRace(b));
    }

    @Test
    public void isBoardValidSLEnterSLExit() {
        Board b = new Board(
                     "0000000000\n" +
                        "0111111000\n" +
                        "0110001100\n" +
                        "0111001100\n" +
                        "0110000100\n" +
                        "0110000100\n" +
                        "0012000100\n" +
                        "0002010100\n" +
                        "0002110100\n" +
                        "0000011100\n",
                10, 10);
        assertEquals(ErrorCodes.NoError, b.isBoardValidForRace(b));
    }

    @Test
    public void wrongMapSyntax() {
        Logic logic = new Logic(new FakeGUI());
        assertEquals(ErrorCodes.WrongMapSyntax,logic.getGameFromJson("maps/WrongMapSyntax.json"));
    }

    @Test
    public void wrongBoardContent() {
        Logic logic = new Logic(new FakeGUI());
        assertEquals(ErrorCodes.WrongBoardContents,logic.getGameFromJson("maps/WrongBoardContent.json"));
    }

    @Test
    public void wrongBoardSize() {
        Logic logic = new Logic(new FakeGUI());
        assertEquals(ErrorCodes.WrongBoardSize,logic.getGameFromJson("maps/WrongBoardSize.json"));
    }

    @Test
    public void wrongCPorDir() {
        Logic logic = new Logic(new FakeGUI());
        assertEquals(ErrorCodes.WrongCurrentPlayerOrDirection,logic.getGameFromJson("maps/WrongCPorDir.json"));
    }

    @Test
    public void wrongPlayerData() {
        Logic logic = new Logic(new FakeGUI());
        assertEquals(ErrorCodes.InvalidPLayerData,logic.getGameFromJson("maps/WrongPlayerData.json"));
    }



    @Test
    public void doesRouteWork() {
        Logic logic = new Logic(new FakeGUI());
        Position[] route = {new Position(10,2), new Position(11,2),
                new Position(12,2), new Position(13,2)};
        for (int i = 0; i < route.length; i++) {
            assertEquals(route[i].getPosX(),
                    logic.calcRoute(new Position(10,2), new Position(13,2))[i].getPosX());
            assertEquals(route[i].getPosY(),
                    logic.calcRoute(new Position(10,2), new Position(13,2))[i].getPosY());
        }

    }

    @Test
    public void doesRouteWork2() {
        Logic logic = new Logic(new FakeGUI());
        Position[] route = {new Position(1,1), new Position(2,1),
                new Position(3,0), new Position(4,0)};
        for (int i = 0; i < route.length; i++) {
            assertEquals(route[i].getPosX(),
                    logic.calcRoute(new Position(1,1), new Position(4,0))[i].getPosX());
            assertEquals(route[i].getPosY(),
                    logic.calcRoute(new Position(1,1), new Position(4,0))[i].getPosY());
        }

    }

    @Test
    public void isRouteValidLongRoute() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0110001100\n" +
                    "0111001100\n" +
                    "0110000100\n" +
                    "0110000100\n" +
                    "0012000100\n" +
                    "0002010100\n" +
                    "0002110100\n" +
                    "0000011100\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        Logic logic = new Logic(new FakeGUI(), s, 10, 10, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(2,6 ), new Position(2,1));
        RouteResult rr = logic.isRouteValid(route, new Position(2,1), false);
        assertFalse(rr.isCrashFound());
        assertTrue(new Position(2,1 ).isEqualTo(rr.getLastPointOnRoute()));
    }

    @Test
    public void isRouteValidLongRoute2() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0110001100\n" +
                    "0111001100\n" +
                    "0110000100\n" +
                    "0110000100\n" +
                    "0012000100\n" +
                    "0002010100\n" +
                    "0002110100\n" +
                    "0000011100\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        Logic logic = new Logic(new FakeGUI(), s, 10, 10, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(3,8 ), new Position(2,5));
        RouteResult rr = logic.isRouteValid(route, new Position(2,5), false);
        assertFalse(rr.isCrashFound());
        assertTrue(new Position(2,5).isEqualTo(rr.getLastPointOnRoute()));
    }

    @Test
    public void isRouteValidThroughCar() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[1].setCurrPos(new Position(5,3));
        players[1].setActive(true);
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(3,3 ), new Position(6,3));
        RouteResult rr = logic.isRouteValid(route, new Position(6,3), false);
        assertFalse(rr.isCrashFound());
        assertTrue(new Position(6,3 ).isEqualTo(rr.getLastPointOnRoute()));
    }

    @Test
    public void isRouteValidCarOnFinalPositionAndCrash() {
        String s =  "0111111000\n" +
                    "0000001000\n" +
                    "0000001000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0112111000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[1].setCurrPos(new Position(6,2));
        players[1].setActive(true);
        Logic logic = new Logic(new FakeGUI(), s, 10, 6, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(6,3 ), new Position(5,1));
        RouteResult rr = logic.isRouteValid(route, new Position(6,2), false);
        assertTrue(rr.isCrashFound());
        assertTrue(new Position(6,3 ).isEqualTo(rr.getLastPointOnRoute()));
    }

    @Test
    public void isRouteValidStartOnFinalPositionAndCrash() {
        String s =  "0111111000\n" +
                    "0000001000\n" +
                    "0000001000\n" +
                    "0100002000\n" +
                    "0111111000\n" +
                    "0111111000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        Logic logic = new Logic(new FakeGUI(), s, 10, 6, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(6,3 ), new Position(5,1));
        RouteResult rr = logic.isRouteValid(route, new Position(6,2), false);
        assertTrue(rr.isCrashFound());
        assertTrue(new Position(6,2).isEqualTo(rr.getLastPointOnRoute()));
    }
    @Test
    public void isRouteValidCarThroughGravel() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        Position[] route = logic.calcRoute(new Position(4,1 ), new Position(4,3));
        RouteResult rr = logic.isRouteValid(route, new Position(4,3), false);
        assertTrue(rr.isCrashFound());
        assertTrue(new Position(4,1 ).isEqualTo(rr.getLastPointOnRoute()));
    }

    @Test
    public void isPositionValidInPreparationTargetRoad() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setActive(true);
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        assertTrue(logic.getBoard().isValidPositionInGameMode(players, new Position(1,1),
                0, GameMode.Preparation_Mode));
    }

    @Test
    public void isPositionValidInPreparationTargetGravel() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setActive(true);
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        assertFalse(logic.getBoard().isValidPositionInGameMode(players, new Position(0,0),
               0, GameMode.Preparation_Mode));
    }

    @Test
    public void isPositionValidInRaceTargetPlayer() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0112111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setActive(true);
        players[1].setActive(true);
        players[1].setCurrPos(new Position(1,1));
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        assertFalse(logic.getBoard().isValidPositionInGameMode(players, new Position(1,1),
                0, GameMode.Race_Mode));
    }

    @Test
    public void isStartCrossed() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0121111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setActive(true);
        players[0].setCurrPos(new Position(1,3));
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.replayData.get(0).add(new Position(1,3));
        logic.replayData.get(0).add(new Position(2,3));
        logic.replayData.get(0).add(new Position(3,3));
        logic.handleStartCross(0, new Position(3,3));
        assertEquals(1, players[0].getLap());
    }

    @Test
    public void isStartCrossedOtherDirection() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n" +
                    "0121111000\n" +
                    "0000000000\n";
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setActive(true);
        players[0].setCurrPos(new Position(3,3));
        players[0].setLap(1);
        Logic logic = new Logic(new FakeGUI(), s, 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.replayData.get(0).add(new Position(3,3));
        logic.replayData.get(0).add(new Position(2,3));
        logic.replayData.get(0).add(new Position(1,3));
        logic.handleStartCross(0, new Position(1,3));
        assertEquals(0, players[0].getLap());
    }

    @Test
    public void isWonTrue() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setLap(2);
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.checkWon();
        assertTrue(logic.getIsGameWon());
    }

    @Test
    public void isWonFalse() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setLap(1);
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.checkWon();
        assertFalse(logic.getIsGameWon());
    }

    @Test
    public void areTargetPosValidStanding() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setCurrPos(new Position(1,1));
        players[0].setLastPos(new Position(1,1));
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.calculatePossibleFields(players[0]);
        Position[][] target = {{new Position(0,0), new Position(1, 0 ), new Position(2,0)},
                {new Position(0,1), new Position(1,1 ), new Position(2,1)},
                {new Position(0,2), new Position(1,2 ), new Position(2,2)}
        };
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[0].length; j++) {
                assertTrue(target[i][j].isEqualTo(logic.possiblePositions[i][j]));
            }
        }
    }
    @Test
    public void areTargetPosValidStraight() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setCurrPos(new Position(1,1));
        players[0].setLastPos(new Position(0,1));
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.calculatePossibleFields(players[0]);
        Position[][] target = {{new Position(1,0), new Position(2, 0 ), new Position(3,0)},
                {new Position(1,1), new Position(2,1 ), new Position(3,1)},
                {new Position(1,2), new Position(2,2 ), new Position(3,2)}
        };
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[0].length; j++) {
                assertTrue(target[i][j].isEqualTo(logic.possiblePositions[i][j]));
            }
        }
    }

    @Test
    public void areTargetPosValidOblique() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setCurrPos(new Position(1,1));
        players[0].setLastPos(new Position(0,0));
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.calculatePossibleFields(players[0]);
        Position[][] target = {{new Position(1,1), new Position(2, 1 ), new Position(3,1)},
                {new Position(1,2), new Position(2,2 ), new Position(3,2)},
                {new Position(1,3), new Position(2,3 ), new Position(3,3)}
        };
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[0].length; j++) {
                assertTrue(target[i][j].isEqualTo(logic.possiblePositions[i][j]));
            }
        }
    }

    @Test
    public void areTargetPosValidOutOfBounds() {
        Player[] players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        players[0].setCurrPos(new Position(0,0));
        players[0].setLastPos(new Position(3,3));
        Logic logic = new Logic(new FakeGUI(), "", 10, 5, StartCrossDirection.RIGHT, players, true);
        logic.calculatePossibleFields(players[0]);
        Position[][] target = {{new Position(-4,-4), new Position(-3, -4 ), new Position(-2,-4)},
                {new Position(-4,-3), new Position(-3,-3 ), new Position(-2,-3)},
                {new Position(-4,-2), new Position(-3,-2 ), new Position(-2,-2)}
        };
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[0].length; j++) {
                assertTrue(target[i][j].isEqualTo(logic.possiblePositions[i][j]));
            }
        }
    }

    @Test
    public void addLineTop() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n";
        Logic logic = new Logic(new FakeGUI(), s, 10, 3, StartCrossDirection.RIGHT, new Player[4], true);
        logic.addOrDeleteLine(StartCrossDirection.UP, true);
        String result = "0000000000\n" +
                        "0000000000\n" +
                        "0111111000\n" +
                        "0100001000\n";
        assertEquals(result, logic.getBoard().boardToString());
    }

    @Test
    public void addLineRight() {
        String s =  "0000000000\n" +
                    "0111111000\n" +
                    "0100001000\n";
        Logic logic = new Logic(new FakeGUI(), s, 10, 3, StartCrossDirection.RIGHT, new Player[4], true);
        logic.addOrDeleteLine(StartCrossDirection.RIGHT, true);
        String result = "00000000000\n" +
                        "01111110000\n" +
                        "01000010000\n";
        assertEquals(result, logic.getBoard().boardToString());
    }
    @Test
    public void addLineLeft() {
        String s     =  "0000000000\n" +
                        "0111111000\n" +
                        "0110001100\n" +
                        "0111001100\n" +
                        "0110000100\n" +
                        "0110000100\n" +
                        "0012000100\n" +
                        "0002010100\n" +
                        "0002110100\n" +
                        "0000011100\n";
        Logic logic = new Logic(new FakeGUI(), s, 10, 10, StartCrossDirection.RIGHT, new Player[4], true);
        logic.addOrDeleteLine(StartCrossDirection.LEFT, true);
        String result=  "00000000000\n" +
                        "00111111000\n" +
                        "00110001100\n" +
                        "00111001100\n" +
                        "00110000100\n" +
                        "00110000100\n" +
                        "00012000100\n" +
                        "00002010100\n" +
                        "00002110100\n" +
                        "00000011100\n";
        assertEquals(result, logic.getBoard().boardToString());
    }
    @Test
    public void addLineBottom() {
        String s     =  "0000000000\n" +
                        "0111111000\n" +
                        "0110001100\n" +
                        "0111001100\n" +
                        "0110000100\n" +
                        "0110000100\n" +
                        "0012000100\n" +
                        "0002010100\n" +
                        "0002110100\n" +
                        "0000011100\n";
        Logic logic = new Logic(new FakeGUI(), s, 10, 10, StartCrossDirection.RIGHT, new Player[4], true);
        logic.addOrDeleteLine(StartCrossDirection.DOWN, true);
        String result=  "0000000000\n" +
                        "0111111000\n" +
                        "0110001100\n" +
                        "0111001100\n" +
                        "0110000100\n" +
                        "0110000100\n" +
                        "0012000100\n" +
                        "0002010100\n" +
                        "0002110100\n" +
                        "0000011100\n" +
                        "0000000000\n";
        assertEquals(result, logic.getBoard().boardToString());
    }
}
