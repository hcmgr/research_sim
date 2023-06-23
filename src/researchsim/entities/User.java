package researchsim.entities;

import researchsim.logging.MoveEvent;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.map.Coordinate;
import researchsim.util.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User is the player controlled character in the simulation.
 * A user can {@code collect} any class that implements the {@link researchsim.util.Collectable}
 * interface.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass2
 * @ass2_test
 */
public class User extends Entity implements Movable {

    /** name of user */
    private final String userName;


    /**
     * Creates a user with a given coordinate and name. A user is a MEDIUM
     * sized entity
     *
     * @param coordinate : coordinate associated with the user
     * @param name : the name of this user
     */
    public User(Coordinate coordinate, String name) {
        super(Size.MEDIUM, coordinate);
        this.userName = name;
    }

    /**
     * Returns the name of this user
     * @return : human-readable name
     */
    public String getName() {
        return this.userName;
    }

    /**
     * Returns the machine-readable string representation of this user
     * The format of the string is:
     *
     *      User-coordinate-name
     *
     * where:
     *      -coordinate is the encoding of the user's associated coordinate
     *      -name is the user's name
     *
     * @return encoded string representation of this user
     */
    public String encode() {
        return "User-" + this.getCoordinate().encode() + "-" + this.getName();
    }

    /**
     * Returns the hash code of this user
     * NB: Two users that are equal according to the equals(Object) method will
     * have the same hash code
     *
     * @return : hash code of this user
     */
    public int hashCode() {
        return this.getCoordinate().hashCode() + this.getName().hashCode();
    }

    /**
     * Returns true if and only if this user is equal to the other given object.
     * NB: For two users to be equal, they must have the same coordinate and name.
     *
     * @param other : the reference object with which to compare
     * @return : true if this user is the name as the other argument; false otherwise
     */
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User otherUser = (User) other;
        return (this.getName().equals(otherUser.getName())
                && this.getCoordinate().equals(otherUser.getCoordinate()));
    }

    /**
     * Returns a List of the possible coordinate that this animal can move to.
     * The animal can move to any coordinate in its range (given by
     * Movable.CheckRange) that also satisfied canMove(Coordinate).
     *
     * @return : list of possible movements
     */
    public List<Coordinate> getPossibleMoves() {
        /*
        Take all coordinates in checkRange and filter by canMove
        (where canMove takes coordinate from checkRange)
         */
        ArrayList<Coordinate> possibleMoves = new ArrayList<>();
        for (Coordinate point : checkRange(this.getSize().moveDistance,
                this.getCoordinate())) {
            try {
                if (this.canMove(point)) {
                    possibleMoves.add(point);
                }
            } catch (CoordinateOutOfBoundsException exp) {
                //do nothing
            }
        }
        return possibleMoves;

    }

    /**
     * Moves the user to the new coordinate by:
     *      -setting the new tile's entity to be the user;
     *      -setting the existing tile to unoccupied;
     *      -creating a moveEvent with the user and the new coordinate AND;
     *      -if the new tile has a collectable entity, then the entity is
     *      collected
     *
     * NB: any exceptions arising from collect are squashed
     *
     * @param coordinate : the new coordinate to move to
     * @requires : canMove(Coordinate) == true
     * @ensures : the states of both the new and existing tiles are updated
     *            and the event is logged
     */
    public void move(Coordinate coordinate) {
        Tile[] mapGrid = this.getMapGrid();

        //create and add MoveEvent to Scenario log
        MoveEvent animalMove = new MoveEvent(this, coordinate);
        this.getScenario().getLog().add(animalMove);

        //collect entity if necessary
        try {
            this.collect(coordinate);
        } catch (NoSuchEntityException | CoordinateOutOfBoundsException exp) {
            //do nothing
        }

        //update map grid
        mapGrid[coordinate.getIndex()].setContents(this);
        mapGrid[this.getCoordinate().getIndex()].setContents(null);

        try {
            this.getScenario().setMapGrid(mapGrid);
        } catch (CoordinateOutOfBoundsException exp) {
            //do nothing
        }

        //change entity's own coordinate
        this.setCoordinate(coordinate);
    }

    /**
     * Returns the current scenario.
     * @return : the current Scenario
     */
    private Scenario getScenario() {
        return ScenarioManager.getInstance().getScenario();
    }

    /**
     * Returns the current map grid.
     * @return : the current map grid
     */
    private Tile[] getMapGrid() {
        return this.getScenario().getMapGrid();
    }


    /**
     * Returns the instance of Coordinate corresponding to the distance between
     * the current and new coordinate.
     *
     * @param currCoordinate : current coordinate
     * @param newCoordinate : coordinate to travel to
     * @return : distance Coordinate instance
     */
    private Coordinate getDistance(Coordinate currCoordinate, Coordinate newCoordinate) {
        return currCoordinate.distance(newCoordinate);
    }

    /**
     * Returns the distance in tiles given a distance coordinate.
     *
     * @param distanceCoordinate : given distance coordinate
     * @return : absolute distance in tiles
     */
    private int absoluteDistance(Coordinate distanceCoordinate) {
        return distanceCoordinate.getAbsX() + distanceCoordinate.getAbsY();
    }

    /**
     * Returns the X (horizontal) move direction for the traversal
     * ie: LEFT = -1 (decreasing X), RIGHT = 1 (increasing X)
     *
     * @param otherCoordinate : the new coordinate to move to
     * @return : X (horizontal) move direction
     */
    private int getDx(Coordinate otherCoordinate) {
        Coordinate distance = this.getDistance(this.getCoordinate(), otherCoordinate);
        if (distance.getX() != 0) {
            return distance.getX() / Math.abs(distance.getX());
        }
        return distance.getX();
    }


    /**
     * Returns the Y (vertical) move direction for the traversal
     * ie: UP = -1 (decreasing Y), DOWN = 1 (increasing Y)
     *
     * @param otherCoordinate : the new coordinate to move to
     * @return : Y (vertical) move direction
     */
    private int getDy(Coordinate otherCoordinate) {
        Coordinate distance = this.getDistance(this.getCoordinate(), otherCoordinate);
        if (distance.getY() != 0) {
            return distance.getY() / Math.abs(distance.getY());
        }
        return distance.getY();
    }

    /**
     * Returns true if the current coordinate is different from
     * the new coordinate and false otherwise.
     *
     * @param currCoordinate : current coordinate
     * @param newCoordinate : coordinate to travel to
     * @return : true if the coordinates are equal (as per the Coordinate.equals()
     *           method), false otherwise
     */
    private boolean coordinateNotEqual(Coordinate currCoordinate, Coordinate newCoordinate) {
        return !currCoordinate.equals(newCoordinate);
    }

    /**
     * Returns true if the distance (given as a Coordinate instance)
     * between the current and new coordinates is not greater than
     * 4 (maximum distance a user can travel per move).

     *
     * @param distance : instance of Coordinate corresponding to the distance
     *                   between the current and new coordinates
     * @return : true if the distance is valid, false otherwise
     */
    private boolean isValidDistance(Coordinate distance) {
        int absoluteDistance = this.absoluteDistance(distance);
        return absoluteDistance <= 4;
    }

    /**
     * Returns true if the coordinate to move to is on the scenario map,
     * false otherwise
     *
     * @param coordinate : the coordinate to move to
     * @return : true if on the scenario map, false if not
     */
    private boolean isOnScenarioMap(Coordinate coordinate) {
        return coordinate.isInBounds();
    }

    /**
     * Returns true if the new tile type is appropriate for the user,
     * false otherwise. A new tile is appropriate if it is NOT of type
     * OCEAN or MOUNTAIN.
     *
     * @param mapGrid : current map grid
     * @param coordinate : new coordinate to move to
     * @return : true if the new tile type is appropriate for the user,
     *           false otherwise
     */
    private boolean checkOceanAndMountain(Tile[] mapGrid, Coordinate coordinate) {
        TileType habitat = mapGrid[coordinate.getIndex()].getType();
        return (!(habitat.equals(TileType.MOUNTAIN)
                || habitat.equals(TileType.OCEAN)));
    }

    /**
     * Checks all the conditions of a user moving from one square
     * to another. Returns true if all the conditions are satisfied,
     * false if not.
     *
     * @param currCoordinate : current coordinate of the user
     * @param newCoordinate : new coordinate to travel to
     * @param mapGrid : the current map grid
     * @return : true if the user can validly move from the current coordinate
     *          to the new coordinate
     */
    private boolean checkConditions(Coordinate currCoordinate, Coordinate newCoordinate,
                                    Tile[] mapGrid) {

        Coordinate distance = this.getDistance(currCoordinate, newCoordinate);
        return (this.coordinateNotEqual(currCoordinate, newCoordinate)
                && this.isValidDistance(distance)
                && this.checkOceanAndMountain(mapGrid, newCoordinate));
    }

    /**
     * This performs a traversal of tiles in the x direction, checking at
     * each tile whether the user can move to the next tile. The method
     * returns true if the user can reach the desired X coordinate
     * having satisfying all the conditions for each tile they traversed.
     *
     * The method returns false if, at any point in the traversal, the
     * animal cannot traverse a tile.
     *
     * @param initCoordinate : initial coordinate of animal (before move)
     * @param destination : coordinate the animal seeks (animal has free will I guess)
     *                      to move to
     * @param mapGrid : the current map grid
     * @param dx : X (horizontal) movement direction
     * @return : true if the animal could successfully traverse the required
     *          number of X tiles, false otherwise.
     */
    private boolean xtraversal(Coordinate initCoordinate, Coordinate destination,
                               Tile[] mapGrid, int dx) {

        Coordinate currCoordinate = initCoordinate;
        for (int x = currCoordinate.getX(); x != destination.getX(); x += dx) {
            //check if next coordinate in x direction is valid
            Coordinate newCoordinate = new Coordinate(x + dx, currCoordinate.getY());
            if (!(this.checkConditions(currCoordinate, newCoordinate, mapGrid))) {
                return false;
            }
            currCoordinate = newCoordinate;
        }
        //successful traversal
        return true;
    }

    /**
     * This performs a traversal of tiles in the y direction, checking at
     * each tile whether the user can move to the next tile. The method
     * returns true if the user can reach the desired X coordinate
     * having satisfying all the conditions for each tile they traversed.
     *
     * The method returns false if, at any point in the traversal, the
     * user cannot traverse a tile.
     *
     * @param initCoordinate : initial coordinate of the user (before move)
     * @param destination : coordinate the user seeks to move to
     * @param mapGrid : the current map grid
     * @param dy : Y (horizontal) movement direction
     * @return : true if the user could successfully traverse the required
     *          number of Y tiles, false otherwise.
     */
    private boolean ytraversal(Coordinate initCoordinate, Coordinate destination,
                               Tile[] mapGrid, int dy) {

        Coordinate currCoordinate = initCoordinate;
        for (int y = currCoordinate.getY(); y != destination.getY(); y += dy) {
            //check if next coordinate in y direction is valid
            Coordinate newCoordinate = new Coordinate(currCoordinate.getX(), y + dy);
            if (!(this.checkConditions(currCoordinate, newCoordinate, mapGrid))) {
                return false;
            }
            currCoordinate = newCoordinate;
        }
        //successful traversal in x direction
        return true;
    }

    /**
     * This is the main traversal method. Either X or Y is traversed first depending
     * on the firstX value. If the first traversal succeeds, the second direction is
     * traversed starting from the coordinate the first traversal finished at.
     *
     * The method returns true if both the first and second traversal succeeds and
     * false otherwise.
     *
     * @param initCoordinate : the initial coordinate of the user (before move)
     * @param destination : the coordinate the user seeks to move to
     * @param mapGrid : the current map grid
     * @param dx : X (horizontal) move direction
     * @param dy : Y (vertical) move direction
     * @param firstX : true if x is being traversed first, false if y is being
     *                traversed first
     * @return : true if the user can traverse the necessary number of tiles
     *           in th direction specified by firstX, false otherwise
     */
    private boolean generalTraversal(Coordinate initCoordinate, Coordinate destination,
                                     Tile[] mapGrid, int dx, int dy, boolean firstX) {

        boolean firstTraversal;
        Coordinate currCoordinate = initCoordinate;

        //traverse x first
        if (firstX) {
            //result of x traversal
            firstTraversal = this.xtraversal(initCoordinate, destination, mapGrid, dx);
            if (firstTraversal) {
                currCoordinate = new Coordinate(destination.getX(), currCoordinate.getY());
                //result of y traversal
                return this.ytraversal(currCoordinate, destination, mapGrid, dy);
            }

            //traverse y first
        } else {
            //result of y traversal
            firstTraversal = this.ytraversal(initCoordinate, destination, mapGrid, dy);
            if (firstTraversal) {
                currCoordinate = new Coordinate(currCoordinate.getX(), destination.getY());
                //result of x traversal
                return this.xtraversal(currCoordinate, destination, mapGrid, dx);
            }
        }

        //the particular traversal failed
        return false;
    }

    /**
     * Determines if the user can move to the new coordinate
     * A user can move to the new coordinate if all the following are satisfied:
     *      1: new coordinate != current coordinate
     *      2: new coordinate is on the scenario map
     *      3: The distance from the given coordinate to the current coordinate is
     *      not greater than four (4)
            4: The tile at the coordinate is NOT OCEAN or MOUNTAIN
     *      5: All the above conditions are true for each tile they must traverse to
     *      reach the new coordinate
     *
     * NB: the user can only turn once
     *
     * @param coordinate : the new coordinate
     * @return : true if the above conditions are all satisfied, false otherwise
     * @throws CoordinateOutOfBoundsException : if the new coordinate is out of bounds
     */
    public boolean canMove(Coordinate coordinate) throws CoordinateOutOfBoundsException {
        Coordinate currCoordinate = this.getCoordinate();
        Tile[] mapGrid = this.getMapGrid();

        //initially check conditions before traversing any tiles
        if (!this.isOnScenarioMap(coordinate)) {
            throw new CoordinateOutOfBoundsException();
        }

        if (!(this.checkConditions(currCoordinate, coordinate, mapGrid))) {
            return false;
        }

        /*
        The following simulates a traversal of the tiles the user must cross
        in order to move to the new coordinate. Given that the user can only
        turn once there are two path combinations: (x direction first then y) OR
        (y direction first then x).

        The necessary conditions are checked at each tile of the traversal
         */

        //x and y directions to traverse
        int dx = this.getDx(coordinate);
        int dy = this.getDy(coordinate);

        //traverse x first, then y
        if (generalTraversal(currCoordinate, coordinate, mapGrid, dx, dy, true)) {
            return true;
        //traverse y first, then x
        } else {
            return generalTraversal(currCoordinate, coordinate, mapGrid, dx, dy, false);
        }
    }

    /**
     * Returns a list of all the possible coordinates the user can collect from.
     * A user can only collect from tiles that are 1 tile away that:
     *      -are in bounds of the current scenario;
     *      -have contents AND;
     *      -have contents that are collectable (as per the Collectable interface)
     *
     * @return : list of possible collections
     */
    public List<Coordinate> getPossibleCollection() {
        ArrayList<Coordinate> possibleCollections = new ArrayList<>();
        /*
        Loop through all coordinates exactly 1 tile away and check the
        aforementioned conditions are satisfied.
         */
        for (Coordinate coordinate : checkRange(1, this.getCoordinate())) {
            if (coordinate.isInBounds()) {
                Tile newTile = this.getMapGrid()[coordinate.getIndex()];
                try {
                    if (!(coordinate.equals(this.getCoordinate()))
                            && newTile.hasContents()
                            && newTile.getContents() instanceof Collectable) {

                        possibleCollections.add(coordinate);
                    }
                } catch (NoSuchEntityException exp) {
                    //do nothing
                }
            }
        }
        return possibleCollections;
    }

    /**
     * Collects an entity from the specified coordinate
     *
     * @param coordinate : the coordinate to collect from
     * @throws NoSuchEntityException : if the given coordinate is empty
     * @throws CoordinateOutOfBoundsException : if the given coordinate is not
     *                                          in the map bounds
     */
    public void collect(Coordinate coordinate) throws NoSuchEntityException,
            CoordinateOutOfBoundsException {

        //coordinate not in map bounds
        if (!coordinate.isInBounds()) {
            throw new CoordinateOutOfBoundsException("coordinate not in map bounds");
        }

        Tile collectingTile = this.getMapGrid()[coordinate.getIndex()];

        //collect the entity if it's in the range of possible moves for the user
        if (this.getPossibleMoves().contains(coordinate)) {
            Entity entityAtTile = collectingTile.getContents();
            if (entityAtTile instanceof Fauna) {
                Fauna faunaEntity = (Fauna) entityAtTile;
                faunaEntity.collect(this);
            }
            if (entityAtTile instanceof Flora) {
                Flora floraEntity = (Flora) entityAtTile;
                floraEntity.collect(this);
            }
        }
    }
}
