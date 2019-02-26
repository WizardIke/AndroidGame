package wizardike.assignment3.talents.Secondary;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * Created by Isaac on 15/12/2016.
 */
/*
public class RandomTeleport implements SecondaryTalent {
    private static final int id = 5;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new RandomTeleport(save);
            }
        });
    }

    protected float cooldown;
    protected float castTime;

    public RandomTeleport(float castTime) {
        this.castTime = castTime;
        cooldown = 0.0f;
    }

    public RandomTeleport(DataInputStream saveData) throws IOException {
        this.castTime = saveData.readFloat();
        this.cooldown = saveData.readFloat();
    }

    @Override
    public boolean isReady() {
        return cooldown <= 0.0;
    }

    @Override
    public void activate(Level level, int entity) {
        Vector2 position = level.getPositionSystem().getPosition(entity);
        position.setX(main.gameLevel.findRandomLocationX(main));
        position.setY(main.gameLevel.findRandomLocationY(main));
        cooldown = castTime;
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {
        Vector2 position = level.getPositionSystem().getPosition(thisEntity);
        position.setX(networkIn.readFloat());
        position.setY(networkIn.readFloat());
    }

    @Override
    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        cooldown -= frameTime;
    }

    @Override
    public float getCooldown() {
        return castTime;
    }

    @Override
    public float getRange() {
        return 0.0f;
    }

    @Override
    public void setCooldown(float castTime) {
        this.castTime = castTime;
    }

    @Override
    public void save(DataOutputStream saveData, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        saveData.writeFloat(castTime);
        saveData.writeFloat(cooldown);
    }

    @Override
    public int getId() {
        return id;
    }
}
*/