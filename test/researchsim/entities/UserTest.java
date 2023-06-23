package researchsim.entities;

import org.junit.Before;
import org.junit.Test;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.scenario.Scenario;
import researchsim.scenario.ScenarioManager;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.util.NoSuchEntityException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest {

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

    private Scenario getScenario(){
        return ScenarioManager.getInstance().getScenario();
    }

    Coordinate testCoordinate0_0;
    Coordinate testCoordinate5_10;
    Coordinate testCoordinate2_1;
    Coordinate testCoordinate_1_1;
    Coordinate testCoordinate2_3;
    Coordinate testCoordinate4_0;
    Coordinate testCoordinate4_2;
    Coordinate testCoordinate1_2;
    Coordinate testCoordinate3_2;
    Coordinate testCoordinate1_1;
    Coordinate testCoordinate2_2;
    Coordinate testCoordinate1_0;
    Coordinate testCoordinate_1_0;
    Coordinate testCoordinate2_0;
    Coordinate testCoordinate3_0;
    Coordinate testCoordinate1_3;
    Coordinate testCoordinate0_1;
    Coordinate testCoordinate0_2;
    Coordinate testCoordinate3_1;
    User testUser1;
    User testUser2;
    User testUser3;
    User testUser1Copy;
    User testUser4;
    User testCollectableUser;
    User testUser5;
    User testUser2_1;
    Fauna testFauna1;
    Fauna testFauna2;
    Fauna testFauna3;
    Fauna testFauna4;
    Fauna testFauna5;
    Flora testFlora;
    Scenario testScenAllLand;
    Scenario testScenMountain;
    Scenario testScenOcean;
    Scenario testScenMountainAndOcean;
    Scenario testScenDestinationMountain;
    Scenario testScenDestinationOcean;
    Scenario testScenCantTurnMoreThanOnce;
    Scenario testScenStraightLine;
    Tile[] mapGrid1 = new Tile[5*5];
    Tile[] mapGrid2 = new Tile[5*5];
    Tile[] mapGrid3 = new Tile[5*5];

    @Before
    public void setup(){
        //setup users
        testCoordinate0_0 = new Coordinate();
        testCoordinate5_10 = new Coordinate(5,10);
        testCoordinate2_1 = new Coordinate(2,1);
        testCoordinate_1_1 = new Coordinate(-1, -1);
        testCoordinate2_3 = new Coordinate(2, 3);
        testCoordinate4_0 = new Coordinate(4, 0);
        testCoordinate4_2 = new Coordinate(4, 2);
        testCoordinate1_2 = new Coordinate(1, 2);
        testCoordinate3_2 = new Coordinate(3, 2);
        testCoordinate1_1 = new Coordinate(1, 1);
        testCoordinate2_2 = new Coordinate(2, 2);
        testCoordinate1_0 = new Coordinate(1, 0);
        testCoordinate_1_0 = new Coordinate(-1, 0);
        testCoordinate2_0 = new Coordinate(2, 0);
        testCoordinate3_0 = new Coordinate(3, 0);
        testCoordinate3_1 = new Coordinate(3, 1);
        testCoordinate0_1 = new Coordinate(0, 1);
        testCoordinate0_2 = new Coordinate(0, 2);
        testCoordinate1_3 = new Coordinate(1, 3);
        testUser1 = new User(testCoordinate0_0, "GETSOMEBABY" );
        testUser2 = new User(testCoordinate5_10, "PRESIDENTMAO");
        testUser1Copy= new User(testCoordinate0_0, "GETSOMEBABY");
        testUser3 = new User(testCoordinate4_0, "manIsThisAssignmentAFunOne");
        testUser4 = new User(testCoordinate2_2, "bropleasebro");
        testUser5 = new User(testCoordinate1_0, "tutorMateHowAreWE");
        testUser2_1 = new User(testCoordinate2_1, "tutorMateHowAREWE");
        testCollectableUser = new User(testCoordinate1_2, "stop");
        testFauna1 = new Fauna(Size.MEDIUM, testCoordinate2_1, TileType.LAND);
        testFauna2 = new Fauna(Size.MEDIUM, testCoordinate2_3, TileType.LAND);
        testFauna3 = new Fauna(Size.MEDIUM, testCoordinate1_0, TileType.LAND);
        testFauna4 = new Fauna(Size.MEDIUM, testCoordinate3_2, TileType.LAND);
        testFauna5 = new Fauna(Size.MEDIUM, testCoordinate1_1, TileType.LAND);
        testFlora = new Flora(Size.MEDIUM, testCoordinate2_1);

        //set up map grid
        TileType[] mountain = {
                TileType.LAND, TileType.MOUNTAIN, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] ocean = {
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.OCEAN, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] moutainAndOcean = {
                TileType.LAND, TileType.MOUNTAIN, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.OCEAN, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] destinationInvalidMountain = {
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.OCEAN, TileType.LAND, TileType.MOUNTAIN, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] destinationInvalidOcean = {
                TileType.LAND, TileType.LAND, TileType.OCEAN, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.OCEAN, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] canTurnMoreThanOnce = {
                TileType.LAND, TileType.LAND, TileType.OCEAN, TileType.LAND, TileType.LAND,
                TileType.OCEAN, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        TileType[] straightLine = {
                TileType.LAND, TileType.LAND, TileType.OCEAN, TileType.LAND, TileType.LAND,
                TileType.OCEAN, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND, TileType.LAND,
                TileType.LAND, TileType.OCEAN, TileType.LAND, TileType.LAND, TileType.LAND,
        };

        //set up scenarios
        testScenAllLand = createSafeTestScenario("allLand");
        testScenMountain = createSafeTestScenario("mountain", mountain);
        testScenOcean = createSafeTestScenario("ocean", ocean);
        testScenMountainAndOcean = createSafeTestScenario("mountainAndOcean", moutainAndOcean);
        testScenDestinationMountain = createSafeTestScenario("destinationMountain",
                destinationInvalidMountain);
        testScenDestinationOcean = createSafeTestScenario("destinationOcean",
                destinationInvalidOcean);
        testScenCantTurnMoreThanOnce = createSafeTestScenario("cantTurnMoreThanOnce",
                canTurnMoreThanOnce);
        testScenStraightLine = createSafeTestScenario("straightLine");

        ScenarioManager.getInstance().addScenario(testScenAllLand);
    }

    @Test
    public void encodeTest(){
        assertEquals(testUser1.encode(), "User-0,0-GETSOMEBABY");
    }

    @Test
    public void getNameTest(){
        assertEquals(testUser1.getName(), "GETSOMEBABY");
    }

    @Test
    public void equalsTest(){
        assertEquals(testUser1, testUser1Copy);
        assertNotEquals(testUser1, testUser2);
    }

    @Test
    public void hashCodeTest(){
        assertEquals(testUser1.hashCode(), testUser1Copy.hashCode());
        assertNotEquals(testUser1.hashCode(), testUser2.hashCode());
    }

    @Test
    public void canMoveCoordinatesDifferTest() throws CoordinateOutOfBoundsException {
        assertFalse(testUser1.canMove(testCoordinate0_0));
    }

    @Test (expected = CoordinateOutOfBoundsException.class)
    public void canMoveCoordinateNotOnMapTest() throws CoordinateOutOfBoundsException {
        //negative coordinate
        testUser1.canMove(testCoordinate_1_1);
        //outside map width and height
        testUser1.canMove(testCoordinate5_10);

    }

    @Test
    public void canMoveCoordinateOnMapTest() throws CoordinateOutOfBoundsException {
        //inside map bounds
        assertTrue(testUser1.canMove(testCoordinate2_1));
    }

    @Test
    public void canMoveDistanceTest() throws CoordinateOutOfBoundsException {
        assertFalse(testUser1.canMove(testCoordinate2_3));
        assertTrue(testUser1.canMove(testCoordinate2_2));
        assertTrue(testUser1.canMove(testCoordinate2_1));
    }

    @Test
    public void canMoveOceanAndMountainTest() throws BadSaveException, CoordinateOutOfBoundsException {
        ScenarioManager.getInstance().setScenario("mountain");
        assertTrue(testUser1.canMove(testCoordinate2_1));
        ScenarioManager.getInstance().setScenario("ocean");
        assertTrue(testUser1.canMove(testCoordinate2_1));
        ScenarioManager.getInstance().setScenario("mountainAndOcean");
        assertFalse(testUser1.canMove(testCoordinate2_1));
        ScenarioManager.getInstance().setScenario("destinationMountain");
        assertFalse(testUser1.canMove(testCoordinate2_1));
        ScenarioManager.getInstance().setScenario("destinationOcean");
        assertFalse(testUser1.canMove(testCoordinate2_1));
    }

    @Test
    public void canMoveCantTurnMoreOnceTest() throws BadSaveException, CoordinateOutOfBoundsException {
        ScenarioManager.getInstance().setScenario("cantTurnMoreThanOnce");
        assertFalse(testUser1.canMove(testCoordinate2_1));
        ScenarioManager.getInstance().setScenario("straightLine");
        assertFalse(testUser1.canMove(testCoordinate4_2));
    }

    @Test (expected = CoordinateOutOfBoundsException.class)
    public void canMoveThrowsException1() throws CoordinateOutOfBoundsException {
        testUser1.canMove(testCoordinate_1_0);
    }

    @Test (expected = CoordinateOutOfBoundsException.class)
    public void canMoveThrowsException2() throws CoordinateOutOfBoundsException {
        testUser1.canMove(testCoordinate5_10);
    }

    @Test
    public void moveOldTileEmptyTest() throws BadSaveException, NoSuchEntityException {
        testUser1.move(testCoordinate2_1);
        assertFalse(getScenario().getMapGrid()[0].hasContents());
    }

    @Test
    public void moveNewTileContentsTest() throws NoSuchEntityException {
        testUser1.move(testCoordinate2_1);
        assertEquals(testUser1, getScenario().getMapGrid()[7].getContents());
    }

    @Test
    public void moveUserCoordinateChangedTest(){
        assertEquals(testUser1.getCoordinate(), testCoordinate0_0);
        testUser1.move(testCoordinate2_1);
        assertEquals(testUser1.getCoordinate(), testCoordinate2_1);
    }

    @Test
    public void moveEntitySquareEmptyAfterCollect() throws NoSuchEntityException {
        getScenario().getMapGrid()[7].setContents(testFauna1);
        testUser1.move(testCoordinate2_1);
        assertNotEquals(getScenario().getMapGrid()[7].getContents(), testFauna1);
    }

    @Test
    public void getPossibleCollection() throws CoordinateOutOfBoundsException {
        Tile[] mapGrid = getScenario().getMapGrid();
        mapGrid[7].setContents(testFlora);
        mapGrid[17].setContents(testFauna2);
        mapGrid[11].setContents(testCollectableUser);
        mapGrid[6].setContents(testFauna5);

        getScenario().setMapGrid(mapGrid);

        ArrayList<Coordinate> correctList = new ArrayList<>();
        correctList.add(testCoordinate2_3);
        correctList.add(testCoordinate2_1);

        ArrayList<Coordinate> first = (ArrayList<Coordinate>) testUser4.getPossibleCollection();
        assertTrue(first.size() == correctList.size()
                && first.containsAll(correctList) && correctList.containsAll(first));
    }

    @Test (expected = NoSuchEntityException.class)
    public void collectCoordinateEmptyTest()
            throws CoordinateOutOfBoundsException, NoSuchEntityException {
        testUser1.collect(testCoordinate2_1);
    }

    @Test (expected = CoordinateOutOfBoundsException.class)
    public void collectCoordinateCoordinateOutOfBoundsTest()
            throws CoordinateOutOfBoundsException, NoSuchEntityException {
        testUser1.collect(testCoordinate5_10);
    }

    @Test
    public void collectTileEmptiedTest()
            throws CoordinateOutOfBoundsException, NoSuchEntityException {
        getScenario().getMapGrid()[7].setContents(testFauna1);
        testUser1.collect(testCoordinate2_1);
        assertFalse(getScenario().getMapGrid()[7].hasContents());
    }


    @Test
    public void getPossibleMovesTest() throws BadSaveException {

        List<Coordinate> userMoves = testUser5.getPossibleMoves();
        ArrayList<Coordinate> correctList = new ArrayList<>();
        correctList.add(testCoordinate0_0);
        correctList.add(testCoordinate2_0);
        correctList.add(testCoordinate3_0);
        correctList.add(testCoordinate4_0);
        correctList.add(testCoordinate2_1);
        correctList.add(testCoordinate1_1);
        correctList.add(testCoordinate1_2);
        correctList.add(testCoordinate1_3);
        correctList.add(testCoordinate0_1);
        correctList.add(testCoordinate0_2);
        correctList.add(testCoordinate3_1);
        correctList.add(testCoordinate2_2);

        assertTrue(userMoves.size() == correctList.size()
        && userMoves.containsAll(correctList) && correctList.containsAll(userMoves));

        ScenarioManager.getInstance().setScenario("ocean");
        correctList.remove(testCoordinate0_1);
        userMoves = testUser5.getPossibleMoves();

        assertTrue(userMoves.size() == correctList.size()
                && userMoves.containsAll(correctList) && correctList.containsAll(userMoves));
    }
}