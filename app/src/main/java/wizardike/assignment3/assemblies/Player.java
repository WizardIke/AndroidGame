package wizardike.assignment3.assemblies;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.levels.Level;

public class Player {
    public static int create(Engine engine, Level level) {
        final int entity = engine.getEntityAllocator().allocate();
        Vector2 position = new Vector2(0.0f, 0.0f);
        level.getPositionSystem().addPosition(entity, position);
        level.getLightingSystem().addPointLight(entity, new PointLight(position, 0.0f, 0.0f,
                1.5f, 6.4f, 1.0f, 1.0f, 1.0f));
        return entity;
    }
}
