package logic;

/**
 * object that can save if a route has a crash in it and the new final position
 */
public class RouteResult {


    private final boolean crashFound;

    private final Position lastPointOnRoute;

    protected RouteResult(boolean crashFound, Position lastPointOnRoute) {
        this.crashFound = crashFound;
        this.lastPointOnRoute = lastPointOnRoute;
    }

    protected Position getLastPointOnRoute() {
        return lastPointOnRoute;
    }

    protected boolean isCrashFound() {
        return crashFound;
    }
}
