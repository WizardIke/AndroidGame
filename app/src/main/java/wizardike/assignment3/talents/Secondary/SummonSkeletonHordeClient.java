package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.assemblies.SkeletonClient;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Ike on 30/01/2017.
 */
/*
public class SummonSkeletonHordeClient extends SummonSkeletonHorde {
    private static final int id = 7;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new SummonSkeletonHordeClient(save);
            }
        });
    }

    public SummonSkeletonHordeClient(float castTime, int minSkeletonsPerGroup, int skeletonsPerGroupRange,
                                     int minNumGroups, int numGroupsRange){
        super(castTime, minSkeletonsPerGroup, skeletonsPerGroupRange, minNumGroups, numGroupsRange);
    }

    public SummonSkeletonHordeClient(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public void handleMessage(Level main, DataInputStream networkData, int caster) throws java.io.IOException{
        int numSkeletons = networkData.readInt();
        for(int i = 0; i < numSkeletons; ++i){
            new SkeletonClient(main, caster, 8.0f, 8.0f, networkData.readFloat(),
                    networkData.readFloat(), 0.15f);
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
*/