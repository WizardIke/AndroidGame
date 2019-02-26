package wizardike.assignment3.assemblies;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CircleHitBox;

/**
 * Created by Isaac on 13/12/2016.
 */
public class Tree {
    public static int create(Level level, Vector2 position, float radius) {
        int entity = level.getEngine().getEntityAllocator().allocate();
        level.getCollisionSystem().add(entity, new CircleHitBox(position, radius, Float.POSITIVE_INFINITY));
        return entity;
    }
}
