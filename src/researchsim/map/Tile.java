package researchsim.map;

import researchsim.entities.Entity;
import researchsim.util.NoSuchEntityException;

/**
 * A tile on the scenario map that entities' items operate on.
 *
 * @ass1_partial
 * @ass1_test
 */
public class Tile {

    /**
     * The type of the tile
     */
    private final TileType type;
    /**
     * The entity that is occupying the tile (if any)
     */
    private Entity contents;

    /**
     * Creates a new tile with a given type.
     * Every new tile is initialised as unoccupied (empty).
     *
     * @param type tile type
     * @ass1
     */
    public Tile(TileType type) {
        this.type = type;
        this.contents = null;
    }

    /**
     * Returns the type of the tile.
     *
     * @return tile type
     * @ass1
     */
    public TileType getType() {
        return type;
    }

    /**
     * Returns the contents of the tile.
     *
     * @return tile contents
     * @throws NoSuchEntityException if the tile is empty
     * @ass1
     */
    public Entity getContents() throws NoSuchEntityException {
        if (!hasContents()) {
            throw new NoSuchEntityException("Attempted to get the entity at an empty Tile.");
        }
        return contents;
    }

    /**
     * Updates the contents of the tile.
     *
     * @param item new tile contents
     * @ass1
     */
    public void setContents(Entity item) {
        this.contents = item;
    }

    /**
     * Checks if the tile is currently occupied.
     *
     * @return true if occupied (has contents), else false
     * @ass1
     */
    public boolean hasContents() {
        return contents != null;
    }

    /**
     * Returns the hash code of this tile
     * @return : hash code of this tile
     */
    public int hashCode() {
        try {
            return this.getContents().hashCode() + this.getType().hashCode();
        //tile empty
        } catch (NoSuchEntityException exp) {
            return this.getType().hashCode();
        }
    }

    /**
     * Returns true if and only if this tile is equal to the other given tile
     *
     * @param other : the reference object with which to compare
     * @return : true if this tile is the same as the other argument; false otherwise
     */
    public boolean equals(Object other) {
        if (!(other instanceof Tile)) {
            return false;
        }
        Tile otherTile = (Tile) other;

        /*
        If the tiles are of the same type, there are equal if:
            -both tiles have contents and these contents are equal OR;
            -both tiles are empty
         */
        try {
            if (this.getType() == otherTile.getType()) {
                if (this.hasContents() && otherTile.hasContents()) {
                    return this.getContents().equals(otherTile.getContents());
                }
                if (!this.hasContents() && !otherTile.hasContents()) {
                    return true;
                }
            }
        } catch (NoSuchEntityException e) {
            //do nothing
        }
        return false;
    }
}
