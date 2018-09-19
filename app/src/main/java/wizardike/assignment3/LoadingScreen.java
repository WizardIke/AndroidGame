package wizardike.assignment3;

import wizardike.assignment3.entities.Entity;
import wizardike.assignment3.entities.EntityGenerator;

public class LoadingScreen implements Startable, Entity {
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
}
