package wizardike.assignment3.entities;

import wizardike.assignment3.Engine;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.levels.Level;

public class Player {
    public static int create(Engine engine, Level level) {
        final int entity = engine.getEntityAllocator().allocate();
        level.getLightingSystem().addPointLight(entity, new PointLight(0.0f, 0.0f,
                1.5f, 6.4f, 1.0f, 1.0f, 1.0f));
        return entity;
    }
}
