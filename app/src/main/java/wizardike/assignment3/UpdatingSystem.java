package wizardike.assignment3;

import java.util.ArrayList;

import wizardike.assignment3.Updatable;
import wizardike.assignment3.graphics.UpdateListener;
import wizardike.assignment3.Engine;

public class UpdatingSystem implements UpdateListener{
    private ArrayList<Updatable> updatables = new ArrayList<>();
    private Engine world;

    public UpdatingSystem(Engine world) {
        this.world = world;
    }

    @Override
    public void update() {
        for(Updatable updatable : updatables) {
            updatable.update(world);
        }
    }

    public void add(Updatable updatable) {
        updatables.add(updatable);
    }

    public void remove(Updatable updatable) {
        int index = updatables.indexOf(updatable);
        updatables.set(index, updatables.get(updatables.size() - 1));
        updatables.remove(updatables.size() - 1);
    }
}
