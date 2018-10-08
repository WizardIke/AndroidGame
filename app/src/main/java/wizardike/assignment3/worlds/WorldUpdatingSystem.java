package wizardike.assignment3.worlds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import wizardike.assignment3.Engine;

public class WorldUpdatingSystem {
    public interface Callback {
        void onLoadComplete(WorldUpdatingSystem worldUpdatingSystem);
    }
    private final List<World> worlds = new ArrayList<>();

    public WorldUpdatingSystem() {}

    public WorldUpdatingSystem(DataInputStream save, final Engine engine,
                               final Callback callback) throws IOException {
        final int worldCount = save.readInt();

        final WorldLoader.Callback worldLoadedCallback = new WorldLoader.Callback() {
            private AtomicInteger worldsLoadedCount = new AtomicInteger(0);

            @Override
            public void onLoadComplete(World updatable) {
                if(worldsLoadedCount.incrementAndGet() == worldCount) {
                    callback.onLoadComplete(WorldUpdatingSystem.this);
                }
            }
        };

        for(int i = 0; i != worldCount; ++i) {
            final int id = save.readInt();
            WorldLoader.load(id, save, engine, worldLoadedCallback);
        }
    }

    public void addWorld(World world) {
        worlds.add(world);
    }

    public void removeWorld(World world) {
        worlds.remove(world);
    }

    public void removeAllWorlds() {
        worlds.clear();
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public void update(Engine engine) {
        for(World world : worlds) {
            world.update(engine);
        }
    }

    public void save(DataOutputStream save) throws IOException {
        save.writeInt(worlds.size());
        for(World world : worlds) {
            save.writeInt(world.getId());
            world.save(save);
        }
    }
}
