package wizardike.assignment3;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.entities.EntityGenerator;
import wizardike.assignment3.graphics.UpdateListener;

public class LoadingScreen implements Startable, Entity, UpdateListener {
    public LoadingScreen(Engine engine, EntityGenerator.Callback callback) {
        callback.onLoadComplete(this);
    }

    @Override
    public void start(Engine engine) {

    }

    @Override
    public void stop(Engine engine) {

    }

    @Override
    public <T> T getComponent(Class<T> componentType) {
        return null;
    }

    @Override
    public void update() {

    }
}
