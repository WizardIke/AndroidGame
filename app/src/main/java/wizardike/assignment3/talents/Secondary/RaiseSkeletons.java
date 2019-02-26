package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Random;

import wizardike.assignment3.assemblies.Skeleton;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.SpriteSheets.SkeletonSpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 16/12/2016.
 */
public class RaiseSkeletons implements SecondaryTalent {
    private static final int id = 2;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new RaiseSkeletons(save, spriteSheetRemappingTable);
            }
        });
    }

    protected float cooldown;
    protected float castTime;
    protected int minSkeletons;
    protected int maxSkeletons;
    protected SkeletonSpriteSheet spriteSheet;

    public RaiseSkeletons(float castTime, int minSkeletons, int maxSkeletons, SkeletonSpriteSheet spriteSheet) {
        this.castTime = castTime;
        this.maxSkeletons = maxSkeletons;
        this.minSkeletons = minSkeletons;
        cooldown = 0.0f;
        this.spriteSheet = spriteSheet;
    }

    public RaiseSkeletons(DataInputStream saveData, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        this.castTime = saveData.readFloat();
        this.cooldown = saveData.readFloat();
        this.minSkeletons = saveData.readInt();
        this.maxSkeletons = saveData.readInt();
        this.spriteSheet = (SkeletonSpriteSheet) spriteSheetRemappingTable[saveData.readInt()];
    }

    @Override
    public boolean isReady() {
        return cooldown <= 0.0;
    }

    @Override
    public void activate(Level level, int entity) {
        Random randomNumberGenerator = level.getEngine().getRandomNumberGenerator();
        int numSkeletons = randomNumberGenerator.nextInt(maxSkeletons + 1 - minSkeletons) + minSkeletons;
        Vector2 position = level.getPositionSystem().getPosition(entity);

        for(int j = 0; j < numSkeletons; ++j) {
            Skeleton.create(level, entity, 8.0f, 8.0f, position.getX() + randomNumberGenerator.nextFloat() * 2.0f - 1.0f,
                    position.getY() + randomNumberGenerator.nextFloat() * 2.0f - 1.0f, 0.15f * 6.0f, spriteSheet);
        }
        cooldown = castTime;
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {

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
    public void setCooldown(float time) {
        this.castTime = time;
    }

    @Override
    public float getRange() {
        return 0.9f;
    }

    @Override
    public void save(DataOutputStream saveData, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        saveData.writeFloat(castTime);
        saveData.writeFloat(cooldown);
        saveData.writeInt(minSkeletons);
        saveData.writeInt(maxSkeletons);
        saveData.writeInt(spriteSheetRemappingTable.get(spriteSheet));
    }

    @Override
    public int getId() {
        return id;
    }
}
