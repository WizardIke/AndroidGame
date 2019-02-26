package wizardike.assignment3.updating;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

public interface Updatable {
    void update(Level level, int entity);

    void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException;
    int getId();
}