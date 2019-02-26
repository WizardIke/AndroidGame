package wizardike.assignment3.worlds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;

public class FrameTimer implements World {
    private static final int id = 1;

    private long oldTime;
    private float frameTime;

    static void registerLoader() {
        WorldLoader.addLoader(id, new WorldLoader.Loader() {
            @Override
            public World load(DataInputStream save, final Engine engine,
                              final WorldLoader.Callback callback) {
                return new FrameTimer();
            }
        });
    }

    public void start() {
        oldTime = System.nanoTime();
        frameTime = 0.0f;
    }

    @Override
    public void update(Engine engine) {
        long time = System.nanoTime();
        long delta = time - oldTime;
        oldTime = time;
        if(delta > 50000000) {
            delta = 50000000;
        }
        frameTime = delta / 1000000000.0f;
    }

    public float getFrameTime() {
        return frameTime;
    }


    @Override
    public void save(DataOutputStream save) throws IOException {
        //do nothing
    }

    @Override
    public int getId() {
        return id;
    }
}
