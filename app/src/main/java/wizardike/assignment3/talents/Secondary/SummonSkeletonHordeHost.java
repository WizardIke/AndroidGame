package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.assemblies.SkeletonHost;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Ike on 30/01/2017.
 */
/*
public class SummonSkeletonHordeHost extends SummonSkeletonHorde {
    private static final int id = 8;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new SummonSkeletonHordeHost(save);
            }
        });
    }

    public SummonSkeletonHordeHost(float castTime, int minSkeletonsPerGroup, int skeletonsPerGroupRange,
                                   int minNumGroups, int numGroupsRange){
        super(castTime, minSkeletonsPerGroup, skeletonsPerGroupRange, minNumGroups, numGroupsRange);
    }

    public SummonSkeletonHordeHost(DataInputStream saveData) throws IOException {
        super(saveData);
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

            final DataOutputStream networkOut = main.networkConnection.getNetworkOut();
            try {
                networkOut.writeInt(10 + numSkeletons * 8);
                networkOut.writeInt(caster.getType());
                networkOut.write(castSpell);
                networkOut.write(summonSkeletonHorde);
                networkOut.writeInt(numSkeletons);
            } catch (IOException e) {
                main.connectionLost();
            }
            for(int j = 0; j < numSkeletons; ++j) {
                float x = posX + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f;
                float y = posY + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f;
                try {
                    networkOut.writeFloat(x);
                    networkOut.writeFloat(y);
                } catch (IOException e) {
                    main.connectionLost();
                }
                new SkeletonHost(main, caster, 8.0f, 8.0f, x, y, 0.15f);
            }
        }
        cooldown = castTime;
    }

    @Override
    public int getId() {
        return id;
    }
}
*/