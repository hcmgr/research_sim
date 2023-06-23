package researchsim.map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;

import java.util.Arrays;

import static org.junit.Assert.*;


public class CoordinateTest {

    private Coordinate coordinate11;
    private Coordinate coordinate11Dup;
    private Coordinate coordinate21;
    private Coordinate coordinateneg21;
    private Coordinate coordinateneg11;

    @Before
    public void setUp() throws Exception {
        coordinate11 = new Coordinate(1, 1);
        coordinate11Dup = new Coordinate(1, 1);
        coordinate21 = new Coordinate(2, 1);
        coordinateneg21 = new Coordinate(-2, -1);
        coordinateneg11 = new Coordinate(-1, -1);
    }

    @After
    public void tearDown() throws Exception {
        ScenarioManager.getInstance().reset();
    }

    @Test
    public void testDefaultConstructor() {
        Coordinate origin = new Coordinate();
        assertEquals("Incorrect value was returned.", 0, origin.getX());
        assertEquals("Incorrect value was returned.", 0, origin.getY());
    }

    @Test
    public void testIndexConstructor() {
        createSafeTestScenario("testIndexConstructor", 5, 5);
        Coordinate origin = new Coordinate(12);
        assertEquals("Incorrect value was returned.", 2, origin.getX());
        assertEquals("Incorrect value was returned.", 2, origin.getY());
    }

    @Test
    public void testGetX() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getX());
        assertEquals("Incorrect value was returned.", -2, coordinateneg21.getX());

    }

    @Test
    public void testGetY() {
        assertEquals("Incorrect value was returned.", 1, coordinate11.getY());
        assertEquals("Incorrect value was returned.", -1, coordinateneg21.getY());
    }

    @Test
    public void testGetIndex() {
        createSafeTestScenario("testGetIndex", 5, 5);
        assertEquals("Incorrect value was returned.",
            6, coordinate11.getIndex());
        assertEquals("Incorrect value was returned.",
            7, coordinate21.getIndex());
        assertEquals("Incorrect value was returned.",
            -7, coordinateneg21.getIndex());
    }

    @Test
    public void testIsInBounds() {
        createSafeTestScenario("testIsInBounds", 10, 10);
        assertTrue("Incorrect value was returned.", coordinate11.isInBounds());
        assertFalse("Incorrect value was returned.", coordinateneg21.isInBounds());
    }

    @Test
    public void testConvert() {
        createSafeTestScenario("testConvert", 10, 10);
        assertEquals("Incorrect value was returned.",
            55, Coordinate.convert(5, 5));
        createSafeTestScenario("testConvert2", 5, 7);
        assertEquals("Incorrect value was returned.",
            30, Coordinate.convert(5, 5));
    }

    @Test
    public void testToString() {
        assertEquals("Incorrect value was returned.",
            "(1,1)", coordinate11.toString());
        assertEquals("Incorrect value was returned.",
            "(0,0)", new Coordinate().toString());
        assertEquals("Incorrect value was returned.",
            "(-2,-1)", coordinateneg21.toString());
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a 5x5 map of LAND. A Seed of 0.
     *
     * @param name of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[])
     */
    public static Scenario createSafeTestScenario(String name) {
        return createSafeTestScenario(name, new TileType[] {
            TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
            TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
            TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
            TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
            TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND
        });
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a 5x5 map with the array of tiles based on the array provided. A
     * Seed of 0.
     *
     * @param name  of the scenario
     * @param tiles the map of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[], int, int)
     */
    public static Scenario createSafeTestScenario(String name, TileType[] tiles) {
        return createSafeTestScenario(name, tiles, 5, 5);
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has an n x m map with the array of LAND tiles. A
     * Seed of 0.
     *
     * @param name  of the scenario
     * @param width  the width of the scenario
     * @param height the height of the scenario
     * @return generated scenario
     * @see #createSafeTestScenario(String, TileType[], int, int)
     */
    public static Scenario createSafeTestScenario(String name, int width, int height) {
        int size = width * height;
        TileType[] tiles = new TileType[size];
        Arrays.fill(tiles,0,size,TileType.LAND);
        return createSafeTestScenario(name, tiles, width, height);
    }

    /**
     * Creates a new scenario and adds it to the scenario manager.
     * The scenario created has a n x m map with the array of tiles based on the array provided. A
     * Seed of 0.
     *
     * @param name   of the scenario
     * @param tiles  the map of the scenario
     * @param width  the width of the scenario
     * @param height the height of the scenario
     * @return generated scenario
     */
    public static Scenario createSafeTestScenario(String name, TileType[] tiles,
                                                  int width, int height) {
        Scenario s = new Scenario(name, width, height, 0);
        Tile[] map = Arrays.stream(tiles).map(Tile::new).toArray(Tile[]::new);
        try {
            s.setMapGrid(map);
        } catch (CoordinateOutOfBoundsException error) {
            fail("Failed to update a scenario map for test: " + name + "\n "
                + error.getMessage());
        }
        ScenarioManager.getInstance().addScenario(s);
        try {
            ScenarioManager.getInstance().setScenario(name);
        } catch (BadSaveException error) {
            fail("Failed to update a scenario map for test: " + name + "\n "
                + error.getMessage());
        }
        return s;
    }

    @Test
    public void getAbsXTest(){
        assertEquals(coordinate11.getAbsX(), 1);
        assertEquals(coordinateneg21.getAbsX(), 2);
    }

    @Test
    public void getAbsYTest(){
        assertEquals(coordinate11.getAbsY(), 1);
        assertEquals(coordinateneg21.getAbsY(), 1);
    }

    @Test
    public void testEncode(){
        assertEquals(coordinate11.encode(), "1,1");
        assertEquals(coordinateneg21.encode(), "-2,-1");
    }

    @Test
    public void testDistance(){
        Coordinate distance1 = coordinate11.distance(coordinateneg21);
        Coordinate expectedDistance1 = new Coordinate(-3, -2);
        Coordinate distance2 = coordinate11.distance(coordinate21);
        Coordinate expectedDistance2 = new Coordinate(1, 0);
        Coordinate distance3 = coordinate11.distance(coordinate11);
        Coordinate expectedDistance3 = new Coordinate(0, 0);
        assertEquals(expectedDistance1, distance1);
        assertEquals(expectedDistance2, distance2);
        assertEquals(expectedDistance3, distance3);
    }

    @Test
    public void testTranslate(){
        Coordinate translation1 = coordinate11.translate(0, 0);
        Coordinate expectedTranslation1 = new Coordinate(1, 1);
        assertEquals(translation1, expectedTranslation1);
        Coordinate translation2 = coordinate11.translate(-1, -2);
        Coordinate expectedTranslation2 = new Coordinate(0, -1);
        assertEquals(translation2, expectedTranslation2);
    }

    @Test
    public void testHashCode(){
        assertEquals(coordinate11.hashCode(), coordinate11Dup.hashCode());
        assertNotEquals(coordinate11.hashCode(), coordinate21.hashCode());
    }

    @Test
    public void testEquals(){
        assertEquals(coordinate11, coordinate11Dup);
        assertNotEquals(coordinate11, coordinate21);
        Object somethingRandom = new Object();
        assertNotEquals(somethingRandom, coordinate11);
        assertNotEquals(coordinate11, "(1,1)");
    }


    @Test(expected = BadSaveException.class)
    public void testDecodeTooManyCommas1() throws BadSaveException {
        Coordinate.decode("1,2,");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeTooFewCommas1() throws BadSaveException {
        Coordinate.decode("12");
    }


    @Test(expected = BadSaveException.class)
    public void testDecodeXParse1() throws BadSaveException {
        Coordinate.decode("blah, 2");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeXParse2() throws BadSaveException {
        Coordinate.decode("1.1, 2");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeXParse3() throws BadSaveException {
        Coordinate.decode(", 2");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeYParse1() throws BadSaveException {
        Coordinate.decode("1, bigmfsmoke");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeYParse2() throws BadSaveException {
        Coordinate.decode("1, 6.9");
    }

    @Test(expected = BadSaveException.class)
    public void testDecodeYParse3() throws BadSaveException {
        Coordinate.decode("1,");
    }
}