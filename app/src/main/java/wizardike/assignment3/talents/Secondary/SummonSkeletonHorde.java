package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.assemblies.Skeleton;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 16/12/2016.
 */
/*
public class SummonSkeletonHorde implements SecondaryTalent {
    private static final int id = 6;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new SummonSkeletonHorde(save);
            }
        });
    }

    protected int talentType;
    protected float castTime;
    protected float cooldown;
    protected int minSkeletonsPerGroup, skeletonsPerGroupRange, minNumGroups, numGroupsRange;

    protected SummonSkeletonHorde(float castTime, int minSkeletonsPerGroup, int skeletonsPerGroupRange,
                                  int minNumGroups, int numGroupsRange) {
        this.castTime = castTime;
        this.minSkeletonsPerGroup = minSkeletonsPerGroup;
        this.skeletonsPerGroupRange = skeletonsPerGroupRange;
        this.minNumGroups = minNumGroups;
        this.numGroupsRange = numGroupsRange;
        this.cooldown = 0.0f;
    }

    protected SummonSkeletonHorde(DataInputStream saveData) throws IOException {
        this.castTime = saveData.readFloat();
        this.cooldown = saveData.readFloat();
        this.minSkeletonsPerGroup = saveData.readInt();
        this.skeletonsPerGroupRange = saveData.readInt();
        this.minNumGroups = saveData.readInt();
        this.numGroupsRange = saveData.readInt();
    }

    @Override
    public float getCooldown() {
        return castTime;
    }

    @Override
    public void setCooldown(float castTime) {
        this.castTime = castTime;
    }

    @Override
    public float getRange() {
        return 0.0f;
    }

    @Override
    public boolean isReady() {
        return cooldown <= 0.0;
    }

    @Override
    public void activate(Level level, int entity) {
        int numGroups = main.randomNumberGenerator.nextInt(numGroupsRange) + minNumGroups;
        int numSkeletons;
        float posX, posY;
        for(int i = 0; i < numGroups; ++i) {
            posX = main.gameLevel.findRandomLocationX(main);
            posY = main.gameLevel.findRandomLocationY(main);
            numSkeletons = main.randomNumberGenerator.nextInt(skeletonsPerGroupRange) + minSkeletonsPerGroup;
            for(int j = 0; j < numSkeletons; ++j) {
                new Skeleton(main, caster, 8.0f, 8.0f,
                        posX + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f,
                        posY + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f,
                        0.15f);
            }
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
    public void save(DataOutputStream saveData, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        saveData.writeFloat(castTime);
        saveData.writeFloat(cooldown);
        saveData.writeInt(minSkeletonsPerGroup);
        saveData.writeInt(skeletonsPerGroupRange);
        saveData.writeInt(minNumGroups);
        saveData.writeInt(numGroupsRange);
    }

    @Override
    public int getId() {
        return id;
    }
}
*/