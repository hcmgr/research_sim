package researchsim.logging;

import researchsim.map.Coordinate;
import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * A detailed log that contains a record of {@link Event}s and contains some event statistics.
 *
 * @ass2
 */
public class Logger {

    /** list of events that have occurred in the given scenario */
    private ArrayList<Event> eventList;
    /** number of entities that have been collected by the user */
    private int numEntitiesCollected;
    /** number of tiles that have been traversed by entities */
    private int numTilesTravelled;
    /** number of points earned in the given scenario */
    private int numPointsEarned;

    /**
     * Creates a new logger to maintain a list of events that occur in a scenario
     */
    public Logger() {
        this.eventList = new ArrayList<Event>();
        this.numEntitiesCollected = 0;
        this.numTilesTravelled = 0;
        this.numPointsEarned = 0;
    }

    /**
     * Returns how many entities have been collected by a user
     * @return : number of entities collected
     */
    public int getEntitiesCollected() {
        return this.numEntitiesCollected;
    }

    /**
     * Returns how many tiles have been traversed by entities
     * @return : number of tiles traversed
     */
    public int getTilesTraversed() {
        return this.numTilesTravelled;
    }

    /**
     * Returns the number of points earned in a scenario
     * @return : number of points earned
     */
    public int getPointsEarned() {
        return this.numPointsEarned;
    }

    /**
     * Return the list of all logged events
     * @return : all logged events
     */
    public List<Event> getEvents() {
        return this.eventList;
    }

    /**
     * Adds an event to the log.
     * If the event is a CollectEvent:
     *      -the number of entities collected is incremented AND
     *      -the number of points for collecting the entity is recorded
     *
     * If the event is a MoveEvent:
     *      -the number of tiles traversed is incremented by the distance travelled
     *      in the event
     *
     * @param event : the new event
     */
    public void add(Event event) {
        this.getEvents().add(event);
        if (event instanceof CollectEvent) {
            //increment points and number of entities collected
            CollectEvent collectEvent = (CollectEvent) event;
            this.numEntitiesCollected++;
            this.numPointsEarned += collectEvent.getTarget().getSize().points;
        }
        if (event instanceof MoveEvent) {
            //increment the number of tiles travelled by entities
            MoveEvent moveEvent = (MoveEvent) event;
            Coordinate entityCoordinate = moveEvent.getEntity().getCoordinate();
            Coordinate targetCoordinate = moveEvent.getCoordinate();
            Coordinate distance = entityCoordinate.distance(targetCoordinate);
            int absoluteDistance = distance.getAbsX() + distance.getAbsY();
            this.numTilesTravelled += absoluteDistance;

        }
    }

    /**
     * Returns the string representation of the event log.
     * The format of the string to return is:
     *
     *      logEntry 1
     *      logEntry 2
     *      ...
     *
     * where:
     *      logEntry is the Event.toString() of an event in the log
     *
     * NB: log entries appear in the order in which they were added
     * @return : human-readable string representation of log
     */
    @Override
    public String toString() {
        StringJoiner returnString = new StringJoiner(System.lineSeparator());
        for (Event event : this.getEvents()) {
            returnString.add(event.toString());
        }
        return returnString.toString();
    }
}
