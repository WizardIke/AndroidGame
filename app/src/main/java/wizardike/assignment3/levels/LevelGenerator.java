package wizardike.assignment3.levels;

import wizardike.assignment3.Engine;

/**
 * Used to create Levels based on their id if they aren't in the save file.
 * Can be used to e.g. randomly generate a level when it is first needed.
 */
public class LevelGenerator {
    public interface Callback {
        void onLoadComplete(Level level);
    }

    public static void generate(int levelId, Engine engine, Callback callback) {
        final Level level = new Level(engine);
        callback.onLoadComplete(level);
    }
}
