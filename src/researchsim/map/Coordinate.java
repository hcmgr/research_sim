package researchsim.map;

import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;

import java.util.Arrays;

/**
 * A coordinate is a representation of the  X and Y positions on a graphical map.<br>
 * This X, Y position can be used to calculate the index of a Tile in the scenario tile map
 * depending on the currently active scenario. <br>
 * The X and Y positions will not change but the index will depending on the current scenario.
 * <p>
 * A coordinate is similar to a point on the cartesian plane.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 * @ass1_test_partial
 */
public class Coordinate {

    /**
     * The position in the Horizontal plane (Left-Right).
     */
    private final int xcoord;

    /**
     * The position in the Vertical plane (Up-Down).
     */
    private final int ycoord;

    /**
     * Creates a new coordinate at the top left position (0,0), index 0 (zero).
     *
     * @ass1
     */
    public Coordinate() {
        this(0, 0);
    }

    /**
     * Creates a new coordinate at the specified (x,y) position.
     *
     * @param xcoord horizontal position
     * @param ycoord vertical position
     * @ass1
     */
    public Coordinate(int xcoord, int ycoord) {
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }

    /**
     * Creates a new coordinate at the specified index.
     *
     * @param index index in the tile grid
     * @ass1
     */
    public Coordinate(int index) {
        int width = ScenarioManager.getInstance().getScenario().getWidth();
        this.xcoord = index % width;
        this.ycoord = index / width;
    }

    /**
     * The position in the Horizontal plane (Left-Right)
     *
     * @return the horizontal position
     * @ass1
     */
    public int getX() {
        return xcoord;
    }

    /**
     * The position in the Horizontal plane (Left-Right) absolute value
     * @return : the absolute horizontal position
     */
    public int getAbsX() {
        return Math.abs(this.getX());
    }

    /**
     * The position in the Vertical plane (Up-Down)
     *
     * @return the vertical position
     * @ass1
     */
    public int getY() {
        return ycoord;
    }

    /**
     * The position in the Vertical plane (Up-Down) absolute value
     * @return : the absolute vertical position
     */
    public int getAbsY() {
        return Math.abs(this.getY());
    }

    /**
     * The index in the tile grid of this coordinate.
     *
     * @return the grid index
     * @ass1
     */
    public int getIndex() {
        return Coordinate.convert(xcoord, ycoord);
    }

    /**
     * Determines if the coordinate in the bounds of the current scenario map
     *
     * @return true, if 0 &le; coordinate's x position &lt; current scenarios' width AND 0 &le;
     * coordinate's y position &lt; current scenarios' height
     * else, false
     * @ass1
     */
    public boolean isInBounds() {
        Scenario scenario = ScenarioManager.getInstance().getScenario();
        return xcoord < scenario.getWidth() && xcoord >= 0
            && ycoord < scenario.getHeight() && ycoord >= 0;
    }

    /**
     * Utility method to convert an (x,y) integer pair to an array index location.
     *
     * @param xcoord the x portion of a coordinate
     * @param ycoord the y portion of a coordinate
     * @return the converted index
     * @ass1
     */
    public static int convert(int xcoord, int ycoord) {
        return xcoord + ycoord * ScenarioManager.getInstance().getScenario().getWidth();
    }

    /**
     * Returns a new Coordinate from the given encoded string
     *
     * @param encoded : the encoded coordinate string
     * @return : decoded coordinate
     * @throws BadSaveException - if the format of the given string is invalid
     *                            according to the encoded representation specified
     *                            in encode()
     *
     */
    public static Coordinate decode(String encoded) throws BadSaveException {
        String[] encodingComponents = encoded.split(",");
        if (encodingComponents.length != 2) {
            throw new BadSaveException();
        }

        //count number of commas in the encoding
        int commaCount = 0;
        for (int i = 0; i < encoded.length(); i++) {
            if (encoded.charAt(i) == ',') {
                commaCount++;
            }
        }
        if (commaCount > 1) {
            throw new BadSaveException("More than 2 commas in encoding");
        }

        //try and parse the x and y components of the encoding
        int x;
        int y;
        try {
            x = Integer.parseInt(encodingComponents[0]);
            y = Integer.parseInt(encodingComponents[1]);
        } catch (NumberFormatException exp) {
            throw new BadSaveException("x and y are not both parse-able integers");
        }

        return new Coordinate(x, y);
    }

    /**
     * Returns a special Coordinate pair showing the difference between the
     * current instance and the other coordinate.
     *
     * @param other : coordinate to compare
     * @return : special difference coordinate pair
     */
    public Coordinate distance(Coordinate other) {
        int dx = other.getX() - this.getX();
        int dy = other.getY() - this.getY();
        return new Coordinate(dx, dy);
    }

    /**
     * Translate the coordinate given the amount of tiles in the x and y direction
     *
     * @param x : translation in x-axis
     * @param y : translation in y-axis
     * @return : new coordinate location
     */
    public Coordinate translate(int x, int y) {
        int translatedX = this.getX() + x;
        int translatedY = this.getY() + y;
        return new Coordinate(translatedX, translatedY);
    }

    /**
     * Returns the machine-readable string representation of this Coordinate.
     * The format of the string to return is:
     *
     *      x,y
     *
     * where:
     *      -x is the position in the Horizontal plane (Left-Right)
     *      -y is the position in the Vertical plane (Up-Down)
     *
     * @return : encoded string representation of this Coordinate
     */
    public String encode() {
        return this.getX() + "," + this.getY();
    }

    /**
     * Returns the hash code of this coordinate.
     * NB: Two coordinates that are equal according to equals(Object) will have
     * the same hash code
     *
     * @return : hash code of this coordinate
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(this.getX()) + Integer.hashCode(this.getY());
    }

    /**
     * Returns true if and if this coordinate is equal to the other given coordinate.
     * For two coordinates to be equal, they must have the same x and y position.
     *
     * @param other : the reference object with which to compare
     * @return : true if this coordinate is the same as the other argument;
     *           false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Coordinate)) {
            return false;
        }
        Coordinate otherCoordinate = (Coordinate) other;
        return (this.getX() == otherCoordinate.getX()
                && this.getY() == otherCoordinate.getY());
    }

    /**
     * Returns the human-readable string representation of this Coordinate.
     * <p>
     * The format of the string to return is:
     * <pre>(x,y)</pre>
     * Where:
     * <ul>
     *   <li>{@code x} is the position in the Horizontal plane (Left-Right)</li>
     *   <li>{@code y} is the position in the Vertical plane (Up-Down)</li>
     * </ul>
     * For example:
     *
     * <pre>(1,3)</pre>
     *
     * @return human-readable string representation of this Coordinate.
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)",
            this.xcoord, this.ycoord);
    }
}
