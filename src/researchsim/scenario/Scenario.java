package researchsim.scenario;

import researchsim.entities.*;
import researchsim.map.Coordinate;
import researchsim.map.Tile;
import researchsim.map.TileType;
import researchsim.util.BadSaveException;
import researchsim.util.CoordinateOutOfBoundsException;
import researchsim.logging.Logger;
import researchsim.util.Encodable;
import researchsim.util.NoSuchEntityException;

import java.io.*;
import java.util.*;


/**
 * The scenario is the overriding class of the simulation.
 * It is similar to a level in a video game.
 * <p>
 * NOTE: Some methods in this class require interaction with the {@link ScenarioManager}. Only
 * interact with it when you need it.
 *
 * @ass1_partial
 */
public class Scenario extends Object implements Encodable {

    /**
     * The minimum dimensions of the map grid.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MIN_SIZE = 5;
    /**
     * The maximum dimensions of the map grid.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MAX_SIZE = 15;
    /**
     * Maximum number of tiles that the grid contains.
     * The value of this constant is {@value}
     *
     * @ass1
     */
    public static final int MAX_TILES = MAX_SIZE * MAX_SIZE;
    /**
     * The name of this scenario.
     */
    private final String name;
    /**
     * The width of the map in the scenario.
     */
    private final int width;
    /**
     * The height of the map in the scenario.
     */
    private final int height;
    /**
     * The tile grid for this scenario.
     */
    private Tile[] mapGrid;

    /**
     * The scenario's activity log
     */
    private Logger log;

    /**
     * the random seed used to initialise the Random instance of this scenario
     */
    private int seed;

    /**
     * The scenario's enemy manager
     */
    private AnimalController animalController;

    /**
     * instance of the Random class for this scenario
     */
    private Random randomInstance;

    /**
     * Creates a new Scenario with a given name, width, height and random seed. <br>
     * A one dimensional (1D) array of tiles is created as the board with the given width and
     * height. <br>
     * An empty Animal Controller and logger is also initialised. <br>
     * An instance of the {@link Random} class in initialised with the given seed.
     *
     * @param name   scenario name
     * @param width  width of the board
     * @param height height of the board
     * @param seed   the random seed for this scenario
     * @throws IllegalArgumentException if width &lt; {@value Scenario#MIN_SIZE} or width &gt;
     *                                  {@value Scenario#MAX_SIZE} or height
     *                                  &lt; {@value Scenario#MIN_SIZE} or height &gt;
     *                                  {@value Scenario#MAX_SIZE} or seed &lt; 0 or name is {@code
     *                                  null}
     * @ass1_partial
     * @see Random (<a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html">Link</a>)
     */
    public Scenario(String name, int width, int height, int seed)
        throws IllegalArgumentException {
        if (width > MAX_SIZE || width < MIN_SIZE) {
            throw new IllegalArgumentException("The given width does not conform to the "
                + "requirement: " + MIN_SIZE + " <= width <= " + MAX_SIZE + ".");
        }
        if (height > MAX_SIZE || height < MIN_SIZE) {
            throw new IllegalArgumentException("The given height does not conform to the "
                + "requirement: " + MIN_SIZE + " <= height <= " + MAX_SIZE + ".");
        }
        if (name == null) {
            throw new IllegalArgumentException("The given name does not conform to the "
                + "requirement: name != null.");
        }
        if (seed < 0) {
            throw new IllegalArgumentException("The given seed does not conform to the requirement:"
                    + "seed < 0");
        }

        this.name = name;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.mapGrid = new Tile[width * height];
        this.log = new Logger();
        this.animalController = new AnimalController();
        this.randomInstance = new Random(this.seed);

    }

    /**
     * Returns the name of the scenario.
     *
     * @return scenario name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the map grid for this scenario.
     * <p>
     * Adding or removing elements from the returned array should not affect the original array.
     *
     * @return map grid
     * @ass1
     */
    public Tile[] getMapGrid() {
        return Arrays.copyOf(mapGrid, getSize());
    }

    /**
     * Updates the map grid for this scenario.
     * <p>
     * Adding or removing elements from the array that was passed should not affect the class
     * instance array.
     *
     * @param map the new map
     * @throws CoordinateOutOfBoundsException (param) map length != size of current scenario map
     * @ass1_partial
     */
    public void setMapGrid(Tile[] map) throws CoordinateOutOfBoundsException {
        if (map.length != this.getSize()) {
            throw new CoordinateOutOfBoundsException("incorrect map size");
        }
        mapGrid = Arrays.copyOf(map, getSize());
    }


    /**
     * Returns the width of the map for this scenario.
     *
     * @return map width
     * @ass1
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map for this scenario.
     *
     * @return map height
     * @ass1
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the map in the scenario.<br>
     * The size of a map is the total number of tiles in the Tile array.
     *
     * @return map size
     * @ass1
     */
    public int getSize() {
        return width * height;
    }

    /**
     * Returns the scenarios random instance
     * @return : random
     */
    public Random getRandom() {
        return this.randomInstance;
    }

    /**
     * Returns the scenario's activity log
     * @return : game log
     */
    public Logger getLog() {
        return this.log;
    }

    /**
     * Returns the scenarios enemy manager
     * @return : enemy manager
     */
    public AnimalController getController() {
        return this.animalController;
    }

    /**
     * Creates a Scenario instance by reading information from the given reader.
     * The read is invalid if any of the following conditions are true:
     *      -the given reader is empty
     *      -the reader hits EOF before all the following is present:
     *          -ScenarioName
     *          -Width
     *          -Height
     *          -seed
     *          -map
     *      Nb: a separator must exist after the map
     *      -the required information above does not appear in the given order
     *      -If any of the Width, Height or Seed lines:
                     -Do not contain exactly one colon
                     -Do not have keys of "Width", "Height" and "Seed" respectively
     *               -Do not have parse-able integer values for Width, Height and
     *               Seed
     *      -If any of the ScenarioName, Width, Height or Seed values cause an
     *      IllegalArgumentException when used to create a Scenario()
     *      -The separator does not have Width-many equals ("=") signs
     *      -If any of the following is true of the map:
     *          -If the line width != Width
     *          -If the number of characters given is not equal to the Scenario
     *          size
     *          -If ANY character can't be parsed by TileType.decode(String)
     *      -If any of the following hold for true for the entities:
     *          -The line does not contain the correct number of hyphen ("-")
     *          characters for its respective encoding
     *          -The line does not start with "Fauna" or "Flora" or "User"
     *          -If the Coordinate component can not be decoded by
     *          Coordinate.decode(String)
     *          -If the Coordinate specified already has an Entity assigned.
     *          -If line starts with "Fauna" or "Flora" AND the Size component
     *          can not be decoded by Size.valueOf(String)
     *          -If line starts with "Fauna" AND the Habitat component can not
     *          be decoded by TileType.valueOf(String)
     *          -If line starts with "Fauna" AND the Habitat value causes an
     *          IllegalArgumentException to be thrown
     *          -If line starts with "Fauna" AND the Habitat at the Tile
     *          specified by the Coordinate is not suitable
     *          -If line starts with "Flora" AND the Tile specified by the
     *          Coordinate is not suitable
     *          -If line starts with "User" AND the Tile specified by the
     *          Coordinate is not suitable
     *
     * Additional notes:
     *      -if any of Width, Height and Seed is -1, they are assigned a default
     *      value of MIN_SIZE(5)
     *      -the created Scenario is added to the ScenarioManager
     *      -the created Scenario's map is set to the map described in the
     *      reader
     *      -after the Scenario's map is set, the created entities are set
     *      to inhabit their respective coordinates
     *
     * @param reader : reader from which to load all info (will not be null)
     * @return : scenario created by reading from the given reader
     * @throws IOException : if an IOException is encountered when reading from
     *                       the reader
     * @throws BadSaveException : if the reader contains a line that does not adhere
     *                            to the rules above (thus indicating that the contents
     *                            of the reader are invalid)
     */
    public static Scenario load(Reader reader) throws IOException, BadSaveException {

        Scenario newScenario;
        BufferedReader newReader = new BufferedReader(reader);

        //check if reader is empty
        String line = newReader.readLine();
        if (line == null) {
            throw new BadSaveException("No lines in file");
        }

        //read ScenarioName
        String scenarioName = line;

        //read Width, Height and Seed
        HashMap<String, Integer> keys = new LinkedHashMap<>();
        widthHeightSeedRead(keys, newReader);
        int width = keys.get("Width");
        int height = keys.get("Height");
        int seed = keys.get("Seed");

        //create scenario
        try {
            newScenario = new Scenario(scenarioName, width, height, seed);
        } catch (IllegalArgumentException exp) {
            throw new BadSaveException("Invalid Scenario parameters");
        }

        //read first separator
        readSeparator(width, newReader);

        //read map
        ArrayList<Tile> decodedTiles = readMap(newReader, width, height);

        //initialises the new scenario's map grid
        Tile[] mapGrid = initialiseMapGrid(decodedTiles, width, height);
        setNewMapGrid(newScenario, mapGrid);

        //read second separator
        readSeparator(width, newReader);

        //add new Scenario to ScenarioManager
        ScenarioManager.getInstance().addScenario(newScenario);

        //read entities
        ArrayList<Entity> entities = readEntities(newReader, mapGrid);

        //add created entities to their respective tiles
        addEntities(entities, newScenario);

        /*
        The scenario is added to ScenarioManager a second time
        given that adding the entities to their respective tiles
        changed the map grid.
         */
        ScenarioManager.getInstance().addScenario(newScenario);

        return newScenario;
    }

    /**
     * Returns true if an entity has already been assigned to the
     * given coordinate, false otherwise.
     *
     * @param entities : list of entities already added from the scenario
     * @param assignedCoordinate : coordinate of entity to add
     * @return : true if an entity occupies the coordinate already, false otherwsie
     */
    private static boolean coordinateAlreadyAssigned(ArrayList<Entity> entities,
                                                     Coordinate assignedCoordinate) {

        for (Entity entity : entities) {
            if (entity.getCoordinate().equals(assignedCoordinate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads a separator line and throws a BadSaveException if it is
     * incorrectly configured.
     *
     * @param width : scenario width
     * @param newReader : file reader
     * @throws IOException : if the reader is empty
     * @throws BadSaveException : if the separator is either the incorrect length
     *                            or contains incorrect characters
     */
    private static void readSeparator(int width, BufferedReader newReader)
            throws IOException, BadSaveException {

        String separatorLine = newReader.readLine();
        //separator incorrect length
        if (separatorLine.length() != width) {
            throw new BadSaveException("Separator is of incorrect length");
        }

        //separator includes non '=' characters
        for (int i = 0; i < separatorLine.length(); i++) {
            if (separatorLine.charAt(i) != '=') {
                throw new BadSaveException("Separator has non '=' characters");
            }
        }
    }

    /**
     * Reads an entity line and throws a BadSaveException if either:
     *      -it does not start with Fauna, Flora or User OR;
     *      -it does not contain the right number of elements
     *      given the type
     *
     * @param line : line to read
     * @param components : elements of the entity encoding
     * @throws BadSaveException : if the entity encoding is configured incorrectly
     */
    private static void lineConfiguredCorrectly(String line, String[] components)
            throws BadSaveException {
        if (!(line.startsWith("Fauna") && components.length == 4
            || line.startsWith("Flora") && components.length == 3
            || line.startsWith("User") && components.length == 3)) {

            throw new BadSaveException("Entity line either doesn't start with"
                     + "a valid entity OR is configured incorrectly");
        }
    }

    /**
     * Returns the decoded Coordinate instance corresponding to the given
     * encoding.
     *
     * @param coordinateEncoding : coordinate encoding
     * @param entities : list of entities already added
     * @return : entity's decoded coordinate
     * @throws BadSaveException : if an entity already occupies the coordinate
     */
    private static Coordinate decodeCoordinate(String coordinateEncoding,
                                               ArrayList<Entity> entities)
                                    throws BadSaveException {

        Coordinate entityCoordinate = Coordinate.decode(coordinateEncoding);
        if (coordinateAlreadyAssigned(entities, entityCoordinate)) {
            throw new BadSaveException("Coordinate already assigned");
        }
        return entityCoordinate;
    }

    /**
     * Returns the Size of the entity given the particular encoding.
     *
     * @param sizeEncoding : given size encoding
     * @return : Size of entity corresponding to the given encoding
     * @throws BadSaveException : if the Size encoding cannot be decoded
     */
    private static Size decodeSize(String sizeEncoding) throws BadSaveException {
        Size entitySize;
        try {
            entitySize = Size.valueOf(sizeEncoding);
        } catch (IllegalStateException exp) {
            throw new BadSaveException("Size component invalid");
        }
        return entitySize;
    }

    /**
     * Reads the given Fauna line and adds it to the given entities
     * list if it is correctly configured.
     *
     * @param line : line of file
     * @param components : elements of the User encoding
     * @param mapGrid : new scenario's map grid
     * @param entities : list of entities
     * @throws BadSaveException : if the Fauna line is incorrectly configured
     *                            as per Fauna.encode()
     */
    private static void checkFauna(String line, String[] components,
                                   Tile[] mapGrid, ArrayList<Entity> entities)
            throws BadSaveException {

        //encodings
        String sizeEncoding = components[1];
        String coordinateEncoding = components[2];
        String habitatEncoding = components[3];
        //values
        Size entitySize;
        Coordinate entityCoordinate;
        TileType entityHabitat;

        entityCoordinate = decodeCoordinate(coordinateEncoding, entities);
        entitySize = decodeSize(sizeEncoding);

        //habitat of tile entity will be inhabiting
        TileType tileHabitat = mapGrid[entityCoordinate.getIndex()].getType();

        //check number of hyphens
        if (characterCount(line, '-') != 3) {
            throw new BadSaveException("Wrong number of hyphens dude");
        }

        //check that can decode Fauna habitat
        try {
            entityHabitat = TileType.valueOf(habitatEncoding);
        } catch (IllegalArgumentException exp) {
            throw new BadSaveException("Habitat component invalid");
        }

        //check that Fauna habitat is suitable
        if (entityHabitat.equals(TileType.OCEAN)) {
            if (!(tileHabitat.equals(TileType.OCEAN))) {
                throw new BadSaveException("Invalid habitat type");
            }
        }
        if (entityHabitat.equals(TileType.LAND)) {
            if (tileHabitat.equals(TileType.OCEAN)) {
                throw new BadSaveException("Invalid habitat type");
            }
        }

        //create entity and add to entities list
        Fauna newEntity = new Fauna(entitySize, entityCoordinate, entityHabitat);
        entities.add(newEntity);
    }

    /**
     * Reads the given Flora line and adds it to the given entities
     * list if it is correctly configured.
     *
     * @param line : line of file
     * @param components : elements of the User encoding
     * @param mapGrid : new scenario's map grid
     * @param entities : list of entities
     * @throws BadSaveException : if the Flora line is incorrectly configured
     *                            as per Flora.encode()
     */
    private static void checkFlora(String line, String[] components,
                                   Tile[] mapGrid, ArrayList<Entity> entities)
            throws BadSaveException {

        //encodings
        String sizeEncoding = components[1];
        String coordinateEncoding = components[2];
        //values
        Size entitySize;
        Coordinate entityCoordinate;

        entityCoordinate = decodeCoordinate(coordinateEncoding, entities);
        entitySize = decodeSize(sizeEncoding);

        //habitat of tile entity will be inhabiting
        TileType tileHabitat = mapGrid[entityCoordinate.getIndex()].getType();

        //check number of hyphens
        if (characterCount(line, '-') != 2) {
            throw new BadSaveException("Wrong number of hyphens dude");
        }

        //check that Flora habitat is suitable
        if (tileHabitat.equals(TileType.OCEAN)) {
            throw new BadSaveException("Invalid habitat type");
        }
        //create entity
        Flora newEntity = new Flora(entitySize, entityCoordinate);
        entities.add(newEntity);
    }

    /**
     * Reads the given User line and adds it to the given entities
     * list if it is correctly configured.
     *
     * @param line : line of file
     * @param components : elements of the User encoding
     * @param mapGrid : new scenario's map grid
     * @param entities : list of entities
     * @throws BadSaveException : if the User line is incorrectly configured
     *                            as per User.encode()
     */
    private static void checkUser(String line, String[] components,
                                  Tile[] mapGrid, ArrayList<Entity> entities)
            throws BadSaveException {

        String userCoordinateEncoding = components[1];
        String name = components[2];
        Coordinate userCoordinate = decodeCoordinate(userCoordinateEncoding, entities);

        //check number of hyphens
        if (characterCount(line, '-') != 2) {
            throw new BadSaveException("Wrong number of hyphens dude");
        }

        //check if User's tile habitat is not suitable
        TileType tileHabitat = mapGrid[userCoordinate.getIndex()].getType();
        if (tileHabitat.equals(TileType.OCEAN)
                || tileHabitat.equals(TileType.MOUNTAIN)) {

            throw new BadSaveException("Invalid habitat type");
        }

        //create User entity
        User newEntity = new User(userCoordinate, name);
        entities.add(newEntity);
    }

    /**
     * Returns a list of the decoded entities to add to the new
     * scenario's map grid.
     *
     * @param newReader : file reader
     * @param mapGrid : new map grid
     * @return : a List of the decoded entities to add to the map grid
     * @throws BadSaveException : if any of the entity encodings are incorrectly
     *                            configured
     * @throws IOException : if the file reader is empty
     */
    private static ArrayList<Entity> readEntities(BufferedReader newReader,
                                              Tile[] mapGrid)
            throws BadSaveException, IOException {

        ArrayList<Entity> entities = new ArrayList<>();
        String line;
        while ((line = newReader.readLine()) != null) {
            String[] components = line.split("-");

            //general check of Flora/Fauna/User line configuration
            lineConfiguredCorrectly(line, components);

            /*
             * Checks specific conditions of the entity being
             * added depending on its type (Flora, Fauna or User).
             * Additionally, if all conditions are satisfied, the entity
             * is added to the entities list
             */
            if (line.startsWith("Flora")) {
                checkFlora(line, components, mapGrid, entities);
            }

            if (line.startsWith("Fauna")) {
                checkFauna(line, components, mapGrid, entities);
            }

            if (line.startsWith("User")) {
                checkUser(line, components, mapGrid, entities);
            }
        }
        return entities;
    }

    /**
     * Adds all entities in the given list to the given Scenario's map grid.
     * NB: all animals (Fauna are added to AnimalController
     *
     * @param entities : list of entities
     * @param newScenario : new scenario
     */
    private static void addEntities(ArrayList<Entity> entities, Scenario newScenario) {
        for (Entity entity : entities) {
            int coordinateIndex = entity.getCoordinate().getIndex();
            Tile entityTile = newScenario.getMapGrid()[coordinateIndex];
            entityTile.setContents(entity);

            //adds entity to AnimalController if its of type Fauna
            if (entity instanceof Fauna) {
                Fauna addedAnimal = (Fauna) entity;
                newScenario.getController().addAnimal(addedAnimal);
            }
        }
    }

    /**
     * Initialises the new scenario's map grid using the given list of
     * tiles to generate it.
     *
     * @param decodedTiles : list of tiles
     * @param width : width of new scenario
     * @param height : height of new scenario
     * @return : the newly generated map grid
     */
    private static Tile[] initialiseMapGrid(ArrayList<Tile> decodedTiles,
                                            int width, int height) {

        //loops through the map grid and adds the necessary tile at each position
        Tile[] mapGrid = new Tile[width * height];
        for (int i = 0; i < mapGrid.length; i++) {
            mapGrid[i] = decodedTiles.get(i);
        }
        return mapGrid;
    }

    /**
     * Sets the given map grid as the given scenario's new map grid.
     *
     * @param newScenario : new scenario
     * @param newMapGrid : new map grid
     * @throws BadSaveException : if the map grid is the incorrect size
     *                            given the scenario's dimensions
     */
    private static void setNewMapGrid(Scenario newScenario, Tile[] newMapGrid)
            throws BadSaveException {

        try {
            newScenario.setMapGrid(newMapGrid);
        } catch (CoordinateOutOfBoundsException exp) {
            throw new BadSaveException("Map is the incorrect size given the "
                    + "height and width");
        }
    }

    /**
     * Reads the encoded map from the file and returns a List of Tiles
     * from which the new scenario's map grid can be generated.
     *
     * @param reader : file reader
     * @param width : width of scenario
     * @param height : height of scenario
     * @return : list of decoded tiles to form the map grid from
     * @throws IOException : if there is a problem with the reader
     * @throws BadSaveException : if the map is incorrectly configured
     */
    private static ArrayList<Tile> readMap(BufferedReader reader, int width, int height)
            throws IOException, BadSaveException {

        ArrayList<Tile> decodedTiles = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            //checks number of characters in each line
            String line = reader.readLine();
            if (line.length() != width) {
                throw new BadSaveException("Map line is not specified width");
            }

            /*
            Checks that each character is a valid TileType encoding. Additionally,
            a new tile is created and added to the list of decoded tiles.
             */
            for (int col = 0; col < width; col++) {
                try {
                    TileType tileType = TileType.decode(String.valueOf(line.charAt(col)));
                    Tile tile = new Tile(tileType);
                    decodedTiles.add(tile);
                } catch (BadSaveException exp) {
                    throw new BadSaveException("Invalid tile type found in map");
                }
            }
        }
        //checks that the map is the correct size
        if (decodedTiles.size() != width * height) {
            throw new BadSaveException("Map is the incorrect size given "
                    + "the height and width");
        }
        return decodedTiles;
    }

    /**
     * Reads the width, height and seed values and throws a BadSaveException
     * if any of the three lines are incorrectly configured.
     *
     * @param keys : hash map storing key value pairs of {"Width", "Height", "Seed}
     *              and their corresponding values
     * @param newReader : file reader
     * @throws BadSaveException : if any of the width, height or seed is incorrectly
     *                            configured
     * @throws IOException : if the file reader is incorrect
     */
    private static void widthHeightSeedRead(HashMap<String, Integer> keys,
                                             BufferedReader newReader)
            throws BadSaveException, IOException {

        //default values
        int width = -1;
        int height = -1;
        int seed = -1;

        keys.put("Width", width);
        keys.put("Height", height);
        keys.put("Seed", seed);
        for (Map.Entry<String, Integer> entry : keys.entrySet()) {
            String line = newReader.readLine();
            widthHeightSeedHelper(line, entry.getKey(), keys);
        }
    }

    /**
     * Reads the given line (either Width, Height or Seed) and stores the value
     * in the given hash map.
     *
     * @param line : line of file
     * @param key : "Width", "Height" or "Seed"
     * @param keys : hash map storing key value pairs of {"Width", "Height", "Seed}
     *               and their corresponding values
     * @throws BadSaveException : if given line is incorrectly configured
     *                            as per the requirements for width, height
     *                            and seed encodings.
     */
    private static void widthHeightSeedHelper(String line, String key,
                                             HashMap<String, Integer> keys)
            throws BadSaveException {

        //checks if too many colons
        if (characterCount(line, ':') > 1) {
            throw new BadSaveException("Too many colons");
        }

        //checks if line is incorrectly configured
        String[] components = line.split(":");
        if (components.length != 2) {
            throw new BadSaveException();
        }

        //check if key is incorrect
        if (!(components[0].equals(key))) {
            throw new BadSaveException("Wrong " + key + " key");
        }

        //check if the value is a parse-able int
        try {
            int newValue = Integer.parseInt(components[1]);
            if (newValue == -1) {
                newValue = MIN_SIZE;
            }
            keys.put(key, newValue);
        } catch (NumberFormatException exp) {
            throw new BadSaveException("Bad width value format");
        }
    }

    /**
     * Returns the number of times the given character
     * occurs in the given string encoding.
     *
     * @param encoding : string encoding
     * @return : character count
     */
    private static int characterCount(String encoding, char targetChar) {
        int count = 0;
        for (int i = 0; i < encoding.length(); i++) {
            if (encoding.charAt(i) == targetChar) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the hash code of this scenario
     * NB: two scenarios that are equal according to the equals(Object) should
     * have the same hash code
     *
     * @return : hash code of this scenario
     */
    @Override
    public int hashCode() {
        return (this.getName().hashCode()
                + Integer.hashCode(this.getWidth())
                + Integer.hashCode(this.getHeight())
                + Arrays.hashCode(this.getMapGrid()));
    }

    /**
     * Returns true if and only if this scenario is equal to the other given object
     *
     * For two scenarios to be equal, they must have the same:
     *      -name
     *      -width
     *      -height
     *      -map contents (Tile array)
     *
     * @param other : the reference object with which to compare
     * @return : true if this scenario is the same as the other argument; false
     *           otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Scenario)) {
            return false;
        }
        Scenario otherScenario = (Scenario) other;
        return (this.getName().equals(otherScenario.getName())
                && this.getWidth() == otherScenario.getWidth()
                && this.getHeight() == otherScenario.getHeight()
                && Arrays.equals(this.getMapGrid(), otherScenario.getMapGrid()));
    }

    /**
     * Returns the human-readable string representation of this scenario.
     * <p>
     * The format of the string to return is:
     * <pre>
     *     (name)
     *     Width: (width), Height: (height)
     *     Entities: (entities)
     * </pre>
     * Where:
     * <ul>
     *   <li>{@code (name)} is the scenario's name</li>
     *   <li>{@code (width)} is the scenario's width</li>
     *   <li>{@code (height)} is the scenario's height</li>
     *   <li>{@code (entities)} is the number of entities currently on the map in the scenario</li>
     * </ul>
     * For example:
     *
     * <pre>
     *     Beach retreat
     *     Width: 6, Height: 5
     *     Entities: 4
     * </pre>
     * <p>
     * Each line should be separated by a system-dependent line separator.
     *
     * @return human-readable string representation of this scenario
     * @ass1
     */
    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(System.lineSeparator());
        result.add(name);
        result.add(String.format("Width: %d, Height: %d", width, height));
        result.add(String.format("Entities: %d",
            Arrays.stream(mapGrid).filter(Objects::nonNull).filter(Tile::hasContents).count()));
        return result.toString();
    }

    /**
     * Generates the encoded map string from which the Scenario's map
     * grid will be derived. Each row is separated by a system- dependent
     * line-separator.
     *
     * @return : encoded map string
     */
    private String generateMapString() {
        StringBuilder mapString = new StringBuilder();

        /*
        Loops through every tile in the map grid and adds the specific tile
        encoding to the encoded map string.
         */
        int i = 0;
        for (Tile tile : this.getMapGrid()) {
            if (tile != null) {
                String stringEncoding = tile.getType().encode();

                /*
                System-dependent line separator placed after every 'Width'-many
                tiles and there is no separator after the last tile.
                 */
                if ((i + 1) % this.getWidth() == 0 && i != this.getSize() - 1) {
                    stringEncoding += System.lineSeparator();
                }
                mapString.append(stringEncoding);
                i++;
            }
        }
        return mapString.toString();
    }

    /**
     * Generates the list of encoded entities that will populate the Scenario
     * map. Each entity encoding is given by its specific encode() method. Each
     * entity encoding is separated by a system-independent line-separator.
     *
     * @return : list of encoded entities
     */
    private String generateEntitiesString() {
        StringBuilder entityString = new StringBuilder();
        /*
        Loops through the map grid and, if a tile has an entity,
        adds the entity's encoding to the combined entities string.
         */
        for (Tile tile : this.getMapGrid()) {
            if (tile != null) {
                try {
                    entityString.append(tile.getContents().encode());
                    entityString.append(System.lineSeparator());
                } catch (NoSuchEntityException exp) {
                /*
                tile does not have an entity, so nothing is added to the
                entityString
                 */
                }
            }
        }
        if (entityString.toString().equals("")) {
            return null;
        }
        //remove final line separator (which is the last character)
        return entityString.deleteCharAt(entityString.length() - 1).toString();
    }

    /**
     * Returns the machine-readable string representation of this Scenario
     *
     * The format of the string to return is
     *
     *      {ScenarioName}
     *      Width:{Width}
     *      Height:{Height}
     *      Seed:{Seed}
     *      {Separator}
     *      {map}
     *      {Separator}
     *      {entity}
     *      {entity...}
     *
     * Where:
     *      -{ScenarioName} is the name of the scenario
     *      -{Width} is the width of the scenario
     *      -{Height} is the height of the scenario
     *      -{Seed} is the seed of the scenario
     *      -{Separator} = "=====" (a string of 5 'equals' signs)
     *      -{map} is the tile map where:
     *          -each tile is represented by its TileType encoding AND;
     *          -a system-dependent line separator is added after Width-many
     *          characters are written
     *      -{entity} is the Entity encoding of each entity found on the map.
                NB: entities are added in the order of their index in the Tile map
     * @return : encoded string representation of this Scenario
     */
    public String encode() {
        StringJoiner returnString = new StringJoiner(System.lineSeparator());
        String widthString = Integer.toString(this.getWidth());
        String heightString = Integer.toString(this.getHeight());
        String separator = "=====";

        returnString.add(this.getName());
        returnString.add("Width:" + widthString);
        returnString.add("Height:" + heightString);
        returnString.add("Seed:" + this.seed);
        returnString.add(separator);
        returnString.add(this.generateMapString());
        returnString.add(separator);

        if (this.generateEntitiesString() != null) {
            returnString.add(this.generateEntitiesString());
        }
        return returnString.toString();
    }
}