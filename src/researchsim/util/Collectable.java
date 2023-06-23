package researchsim.util;

import researchsim.entities.User;

/**
 * Denotes an entity that can be collected by the {@link researchsim.entities.User} in the
 * simulation.
 *
 * @ass2
 */
public interface Collectable {

    /**
     * A User interacts and collects this instance
     *
     * @param user : the user that collects that entity
     * @return : the amount of points gained by that user
     */
    int collect(User user);
}

/*
TODO:
    general:
        -docstring private methods
        -look for uses of hungarian notation usage
            -squash an exception if a method doesn't say it throws
            it (ScenarioManager.addScenario)
    tests:
        -UserTest
        -CoordinateTest

QUESTIONS:
    -do we collect entities on the way? (NO)
    -should we check all the things in move attached to Move?
        -all the collect stuff of Fauna etc.
    -how long is too long for Scenario.load?

    -at each separate line of Scenario.load, do we need to check
    if the line is not null?
    -in Scenario.load, do we have to throw a different badSaveException
    with a different message for each incorrect encoding
    (or can we throw a general catch all one)
 */
