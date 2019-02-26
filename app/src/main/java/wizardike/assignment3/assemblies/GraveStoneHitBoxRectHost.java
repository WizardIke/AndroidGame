package wizardike.assignment3.assemblies;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.AlignedRectangleHitBox;

public class GraveStoneHitBoxRectHost {
    public static int create(Level level, float minPosX, float minPosY, float maxPosX, float maxPosY) {
        int entity = level.getEngine().getEntityAllocator().allocate();
        Vector2 position = new Vector2(minPosX, minPosY);
        level.getPositionHostSystem().addPosition(entity, position);
        level.getCollisionSystem().add(entity, new AlignedRectangleHitBox(position, maxPosX - minPosX,
                maxPosY - minPosY, Float.POSITIVE_INFINITY));
        return entity;
    }
}
