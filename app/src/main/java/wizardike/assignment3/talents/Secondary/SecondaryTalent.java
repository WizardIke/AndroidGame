package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

public interface SecondaryTalent {
    boolean isReady();
    void activate(Level level, int entity);
    void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException;
    void update(Level level);
    void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException;
    int getId();
    float getCooldown();
    void setCooldown(float cooldown);
    float getRange();
}
