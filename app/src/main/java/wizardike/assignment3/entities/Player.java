package wizardike.assignment3.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.Savable;
import wizardike.assignment3.Startable;
import wizardike.assignment3.graphics.PointLight;

public class Player implements Entity, Startable, Savable {
    public static final int id = 4;

    static {
        EntityLoader.addEntityLoader(id, new EntityLoader.IEntityLoader() {
            @Override
            public void loadEntity(DataInputStream save, Engine engine,
                                     EntityLoader.EntityLoadedCallback callback) throws IOException {
                new Player(save, engine, callback);
            }
        });
    }

    private PointLight light;

    private Player(DataInputStream save, Engine engine,
                   final EntityLoader.EntityLoadedCallback callback) throws IOException {
        light = new PointLight(save);
        callback.onLoadComplete(this);
    }

    @Override
    public <T> T getComponent(Class<T> componentType) {
        if(componentType == PointLight.class) {
            return componentType.cast(light);
        }
        return null;
    }

    public static int saveLength() {
        return 32;
    }

    public static void generateSave(DataOutputStream save) throws IOException {
        PointLight light = new PointLight(0.0f, 0.0f, 1.5f, 5.0f,
                1.0f, 1.0f, 1.0f);
        light.save(save);
    }

    @Override
    public void save(DataOutputStream save) throws IOException {
        save.writeInt(id);
        light.save(save);
    }

    @Override
    public void start(Engine engine) {
        engine.getGraphicsManager().addPointLight(light);
    }

    @Override
    public void stop(Engine engine) {
        engine.getGraphicsManager().removePointLight(light);
    }
}
