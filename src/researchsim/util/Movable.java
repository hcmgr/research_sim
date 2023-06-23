package researchsim.util;

import researchsim.entities.Size;
import researchsim.logging.MoveEvent;
import researchsim.map.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Denotes a specific type of entity that can move around in the simulation.
 * <p>
 * <b>NOTE:</b> <br> Read the documentation for these methods well.
 * This is one of the harder parts of A2.
 * <br> It is recommended that you create some private helper methods to assist with these
 * functions. <br> Some example methods might be: <br> checkTile - see if a Tile can be moved to
 * <br> checkTraversal - see if all tiles along a path can be moved to
 *
 * @ass2
 */
public interface Movable {

    /**
     * Returns a list of the possible coordinate the entity can move to.
     * These are defined as any coordinate in checkRange(int, Coordinate)
     * the entity can move to.
     *
     * @return : list of possible movements
     */
    List<Coordinate> getPossibleMoves();

    /**
     * Moves the entity from its current Coordinate to the given coordinate.
     * Additionally, a MoveEvent is created and added to the current Scenario
     * logger.
     *
     * @param coordinate : the new coordinate to move to
     */
    void move(Coordinate coordinate);

    /**
     * Determines if the entity can move to the specified coordinate.
     * An entity can move to the new coordinate if all following conditions are satisfied:
     *      -The coordinate given is on the scenario map.
     *      -the distance to the given coordinate is not greater than the distance
     *      the entity can move
     *      -The tile at the coordinate is NOT uninhabitable by the entity
     *      -The tile at the coordinate is not already occupied
     *      -The entity has an unimpeded path (meaning all the above conditions are true)
     *      for each tile it must traverse to reach the destination coordinate
     *
     * @param coordinate : coordinate to check
     * @return : true if the instance can move to the specified coordinate else false
     * @throws CoordinateOutOfBoundsException : if the given coordinate is out of bounds
     */
    boolean canMove(Coordinate coordinate) throws CoordinateOutOfBoundsException;

    /**
     * Return a list of coordinates that fall into the radius (range) of
     * the specified coordinate. A coordinate is in range if the distance
     * (number of tiles) required to travel to reach this new coordinate is
     * less than or equal (≤) to the radius.
     *
     * Also note that:
     *      -The Coordinates do not have to be in the bounds of the current scenario.
     *      -The returned list should include the initial Coordinate.
     *
     * @param radius : the number of tiles the entity can move
     * @param initialCoordinate : the starting coordinate of the entity
     * @return : a List of coordinates that the entity can move to
     */
    default List<Coordinate> checkRange(int radius, Coordinate initialCoordinate) {
        ArrayList<Coordinate> generatedList = new ArrayList<>();
        int centreX = initialCoordinate.getX();
        int centreY = initialCoordinate.getY();

        /*
        Creates a square of points around the given coordinate. This square
        has dimensions (2n+1) x (2n+1) where n = radius. Points are then
        added to the new points list only if they are within range.
         */
        for (int y = centreY - radius; y <= centreY + radius; y++) {
            for (int x = centreX - radius; x <= centreX + radius; x++) {
                Coordinate newCoordinate = new Coordinate(x, y);
                Coordinate distanceCoordinate = initialCoordinate.distance(newCoordinate);
                int absoluteDistance = distanceCoordinate.getAbsX() + distanceCoordinate.getAbsY();
                if (absoluteDistance <= radius) {
                    generatedList.add(newCoordinate);
                }
            }
        }
        return generatedList;
    }
}
