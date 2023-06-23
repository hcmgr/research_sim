package researchsim.entities;

import researchsim.logging.CollectEvent;
import researchsim.logging.MoveEvent;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;

import java.util.ArrayList;
import java.util.List;
import researchsim.util.*;


/**
 * Fauna is all the animal life present in a particular region or time.
 * Fauna can move around the scenario and be collected by the {@link User}.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 * @ass1_test
 */
public class Fauna extends Entity implements Movable, Collectable {

    /**
     * The habitat associated with the animal.
     * That is, what tiles an animal can exist in.
     */
    private final TileType habitat;

    /**
     * Creates a fauna (Animal) with a given size, coordinate and habitat.
     *
     * @param size       size associated with the animal
     * @param coordinate coordinate associated with the animal
     * @param habitat    habitat tiles associated with the animal
     * @throws IllegalArgumentException if habitat is not {@link TileType#LAND} or
     *                                  {@link TileType#OCEAN}
     * @ass1
     */
    public Fauna(Size size, Coordinate coordinate, TileType habitat)
        throws IllegalArgumentException {
        super(size, coordinate);
        if (habitat != TileType.LAND && habitat != TileType.OCEAN) {
            throw new IllegalArgumentException("Animal was created with a bad habitat: " + habitat);
        }
        this.habitat = habitat;
    }

    /**
     * Returns the animal's habitat.
     *
     * @return animal's habitat
     * @ass1
     */
    public TileType getHabitat() {
        return habitat;
    }

    /**
     * Returns the human-readable name of this animal.
     * The name is determined by the following table.
     * <p>
     * <table border="1">
     *     <caption>Human-readable names</caption>
     *     <tr>
     *         <td rowspan="2" colspan="2" style="background-color:#808080">&nbsp;</td>
     *         <td colspan="3">Habitat</td>
     *     </tr>
     *     <tr>
     *         <td>LAND</td>
     *         <td>OCEAN</td>
     *     </tr>
     *     <tr>
     *         <td rowspan="4">Size</td>
     *         <td>SMALL</td>
     *         <td>Mouse</td>
     *         <td>Crab</td>
     *     </tr>
     *     <tr>
     *         <td>MEDIUM</td>
     *         <td>Dog</td>
     *         <td>Fish</td>
     *     </tr>
     *     <tr>
     *         <td>LARGE</td>
     *         <td>Horse</td>
     *         <td>Shark</td>
     *     </tr>
     *     <tr>
     *         <td>GIANT</td>
     *         <td>Elephant</td>
     *         <td>Whale</td>
     *     </tr>
     * </table>
     * <p>
     * e.g. if this animal is {@code MEDIUM} in size and has a habitat of {@code LAND} then its
     * name would be {@code "Dog"}
     *
     * @return human-readable name
     * @ass1
     */
    @Override
    public String getName() {
        String name;
        switch (getSize()) {
            case SMALL:
                name = habitat == TileType.LAND ? "Mouse" : "Crab";
                break;
            case MEDIUM:
                name = habitat == TileType.LAND ? "Dog" : "Fish";
                break;
            case LARGE:
                name = habitat == TileType.LAND ? "Horse" : "Shark";
                break;
            case GIANT:
            default:
                name = habitat == TileType.LAND ? "Elephant" : "Whale";
        }
        return name;
    }

    /**
     * Returns the machine-readable string representation of the animal
     * The format of the string to return is:
     *      Fauna-size-coordinate-habitat
     * Where:
     *      -size is the animal's associated size
     *      -coordinate is the encoding of the animal's associated coordinate
     *      -habitat is the animal's associated habitat
     *
     * @return : encoded string representation of this animal
     */
    public String encode() {
        return super.encode() + "-" + this.getHabitat();
    }

    /**
     * Returns the hash code of this animal
     * NB: two animals that are equal according to the equals(Object) method should
     * have the same hash code
     *
     * @return : the hash code of this animal
     */
    @Override
    public int hashCode() {
        return super.hashCode() + this.getHabitat().hashCode();
    }

    /**
     * Returns true if and only if this animal is equal to the other given object
     *
     * NB: for two animals to be equal, they must have the same size,
     * coordinate and habitat
     *
     * @param other : the reference object with which to compare
     * @return : true if this animal is the same as the other argument,
     * false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Fauna)) {
            return false;
        }
        Fauna otherFauna = (Fauna) other;
        return (super.equals(otherFauna)
                && this.getHabitat() == otherFauna.getHabitat());
    }

    /**
     * Returns the human-readable string representation of this animal.
     * <p>
     * The format of the string to return is:
     * <pre>name [Fauna] at coordinate [habitat]</pre>
     * Where:
     * <ul>
     *   <li>{@code name} is the animal's human-readable name according to {@link #getName()}</li>
     *   <li>{@code coordinate} is the animal's associated coordinate in human-readable form</li>
     *   <li>{@code habitat} is the animal's associated habitat</li>
     *
     * </ul>
     * For example:
     *
     * <pre>Dog [Fauna] at (2,5) [LAND]</pre>
     *
     * @return human-readable string representation of this animal
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s [%s]",
            super.toString(),
            this.habitat);
    }

    /**
     * Returns a List of the possible coordinates that this animal can move to.
     * The animal can move to any coordinate in its range (given by Movable.CheckRange)
     * that also satisfies canMove(Coordinate).
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
     * Moves the animal to the new coordinate by:
     *      -setting the new tile's entity to be the animal;
     *      -setting the existing tile to be unoccupied AND;
     *      -creating a MoveEvent that is added to the Scenario logger
     *
     * @param coordinate : the new coordinate to move to
     * @requires : canMove(Coordinate) == true
     * @ensures : the states of both the new and existing tiles are updated
     *            and the event is logged
     */
    public void move(Coordinate coordinate) {
        Tile[] mapGrid = this.getScenario().getMapGrid();

        //create and add MoveEvent to Scenario log
        MoveEvent animalMove = new MoveEvent(this, coordinate);
        this.getScenario().getLog().add(animalMove);

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
     * Returns true if the current coordinate is different from
     * the new coordinate and false otherwise.
     *
     * @param currCoordinate : current coordinate
     * @param newCoordinate : coordinate to travel to
     * @return : true if the coordinates are equal (as per the Coordinate.equals()
     *           method), false otherwise
     */
    private boolean coordinatesNotEqual(Coordinate currCoordinate, Coordinate  newCoordinate) {
        return !currCoordinate.equals(newCoordinate);
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
     * Returns true if the distance (given as a Coordinate instance)
     * between the current and new coordinates is valid given the entity's
     * move distance, false otherwise.
     *
     * @param distance : instance of Coordinate corresponding to the distance
     *                   between the current and new coordinates
     * @return : true if the distance is valid, false otherwise
     */
    private boolean isValidDistance(Coordinate distance) {
        int absoluteDistance = this.absoluteDistance(distance);
        return this.getSize().moveDistance >= absoluteDistance;
    }

    /**
     * Returns true if the new tile type is appropriate for the animal,
     * false otherwise. A new tile is appropriate if NEITHER of the following
     * is true:
     *      -the animal's habitat is LAND and the new tile is OCEAN
     *      -the animal's habitat is OCEAN and the new tile is NOT OCEAN
     *
     * @param animalHabitat : moving animal's habitat
     * @param coordinate : new coordinate to move to
     * @param mapGrid : current map grid
     * @return : true if the new tile type is appropriate for the animal,
     *           false otherwise
     */
    private boolean checkOceanAndLand(TileType animalHabitat, Coordinate coordinate,
                                      Tile[] mapGrid) {
        TileType nextCoordinateType = mapGrid[coordinate.getIndex()].getType();
        //check ocean condition
        if (animalHabitat.equals(TileType.OCEAN)) {
            return nextCoordinateType.equals(TileType.OCEAN);
        }
        //check LAND condition
        if (animalHabitat.equals(TileType.LAND)) {
            return !nextCoordinateType.equals(TileType.OCEAN);
        }
        return true;
    }

    /**
     * Returns true if the new tile to move to is empty,
     * false otherwise
     *
     * @param coordinate : new coordinate to move to
     * @param mapGrid : current map grid
     * @return : true if tile is empty, false otherwise
     */
    private boolean newTileEmpty(Coordinate coordinate, Tile[] mapGrid) {

        return !mapGrid[coordinate.getIndex()].hasContents();
    }

    /**
     * Returns the X (horizontal) move direction for the traversal
     * ie: LEFT = -1 (decreasing X), RIGHT = 1 (increasing X)
     *
     * @param coordinate : the new coordinate to move to
     * @return : X (horizontal) move direction
     */
    private int getDx(Coordinate coordinate) {
        Coordinate distance = this.getDistance(this.getCoordinate(), coordinate);
        if (distance.getX() != 0) {
            return distance.getX() / Math.abs(distance.getX());
        }
        return distance.getX();
    }

    /**
     * Returns the Y (vertical) move direction for the traversal
     * ie: UP = -1 (decreasing Y), DOWN = 1 (increasing Y)
     *
     * @param coordinate : the new coordinate to move to
     * @return : Y (vertical) move direction
     */
    private int getDy(Coordinate coordinate) {
        Coordinate distance = this.getDistance(this.getCoordinate(), coordinate);
        if (distance.getY() != 0) {
            return distance.getY() / Math.abs(distance.getY());
        }
        return distance.getY();
    }

    /**
     * Checks all the conditions of an animal moving from one square
     * to another. Returns true if all the conditions are satisfied,
     * false if not.
     *
     * @param currCoordinate : current coordinate of the animal
     * @param newCoordinate : new coordinate to travel to
     * @param mapGrid : the current map grid
     * @return : true if the animal can validly move from the current coordinate
     *          to the new coordinate
     */
    private boolean checkConditions(Coordinate currCoordinate, Coordinate newCoordinate,
                                    Tile[] mapGrid) {
        Coordinate distance = this.getDistance(currCoordinate, newCoordinate);
        return (this.coordinatesNotEqual(currCoordinate, newCoordinate)
                && this.isValidDistance(distance)
                && this.checkOceanAndLand(this.getHabitat(), newCoordinate, mapGrid)
                && this.newTileEmpty(newCoordinate, mapGrid));
    }

    /**
     * This performs a traversal of tiles in the x direction, checking at
     * each tile whether the animal can move to the next tile. The method
     * returns true if the animal can reach the desired X coordinate
     * having satisfying all the conditions for each tile it traversed.
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
        //successful traversal in x direction
        return true;
    }

    /**
     * This performs a traversal of tiles in the y direction, checking at
     * each tile whether the animal can move to the next tile. The method
     * returns true if the animal can reach the desired X coordinate
     * having satisfying all the conditions for each tile it traversed.
     *
     * The method returns false if, at any point in the traversal, the
     * animal cannot traverse a tile.
     *
     * @param initCoordinate : initial coordinate of the animal (before move)
     * @param destination : coordinate the animal seeks (animal has free will I guess)
     *                      to move to (wow same joke again hey?)
     * @param mapGrid : the current map grid
     * @param dy : Y (horizontal) movement direction
     * @return : true if the animal could successfully traverse the required
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
        //successful traversal in y direction
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
     * @param initCoordinate : the initial coordinate of the animal (before move)
     * @param destination : the coordinate the animal seeks to move to
     * @param mapGrid : the current map grid
     * @param dx : X (horizontal) move direction
     * @param dy : Y (vertical) move direction
     * @param firstX : true if x is being traversed first, false if y is being
     *                traversed first
     * @return : true if the animal can traverse the necessary number of tiles
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

        //both traversals failed
        return false;
    }

    /**
     * Determines if the animal can move to the new coordinate
     * An animal can move to the new coordinate if all the following are satisfied:
     *      1: new coordinate != current coordinate
     *      2: new coordinate is on the scenario map
     *      3: the distance between the points is not greater than the animal's
     *      max distance
     *      4: If the animal's habitat is OCEAN, then the new coordinate must be of
     *      type OCEAN
     *      5: If the animal's habitat is LAND, then the new coordinate must not be
     *      of type OCEAN
     *      6: The tile at the new coordinate is empty
     *      7: All the above conditions are true for each tile it must traverse to
     *      reach the new coordinate
     *
     * NB: the animal can only turn once
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
        The following simulates a traversal of the tiles the animal must cross
        in order to move to the new coordinate. Given that the animal can only
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
     * A user interacts with and collects this animal.
     * Upon collection, the following occurs:
     *      -A CollectEvent is created with the animal and the coordinate
     *      -the tile the animal was occupying is emptied
     *      -the animal is removed from the current scenario's
     *      animal controller
     * @param user : the user that collects that entity
     * @return : points earned
     */
    public int collect(User user) {
        //create CollectEvent and add to Scenario's log
        CollectEvent collectEvent = new CollectEvent(user, this);
        this.getScenario().getLog().add(collectEvent);

        //clear tile the animal was occupying
        Tile[] mapGrid = this.getScenario().getMapGrid();
        mapGrid[collectEvent.getCoordinate().getIndex()].setContents(null);
        try {
            this.getScenario().setMapGrid(mapGrid);
        } catch (CoordinateOutOfBoundsException exp) {
            //do nothing
        }

        //animal removed from scenario's animal controller
        this.getScenario().getController().removeAnimal(this);
        return this.getSize().points;
    }
}