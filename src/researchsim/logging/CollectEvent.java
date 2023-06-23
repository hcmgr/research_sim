package researchsim.logging;

import researchsim.entities.User;
import researchsim.entities.Entity;

/**
 * The collection of an entity that implemented {@link researchsim.util.Collectable} by a
 * {@link User}.
 *
 * @ass2
 */
public class CollectEvent extends Event {

    /** the user collecting a target entity */
    private User user;
    /** the target entity that is being collected */
    private final Entity targetEntity;

    /**
     * Creates a new collect event whereby a user collects research on
     * an entity
     *
     * @param user : the user collecting a target entity
     * @param target : the target entity that is being collected
     */
    public CollectEvent(User user, Entity target) {
        super(user, target.getCoordinate());
        this.targetEntity = target;
    }

    /**
     * Returns the target that was collected
     * @return : event target
     */
    public Entity getTarget() {
        return this.targetEntity;
    }

    /**
     * Returns the string representation of the collect event
     * The format of the string return is:
     *
     *      user
     *      COLLECTED
     *      entity
     *
     * where:
     *      -user is the toString() of the user that the entity
     *      -entity is the toString() of the collected entity
     *
     * @return : human-readable string representation of this collect event
     */
    public String toString() {
        return this.getEntity().toString()
                + System.lineSeparator()
                + "COLLECTED"
                + System.lineSeparator()
                + this.getTarget().toString()
                + System.lineSeparator()
                + "-----";
    }
}
