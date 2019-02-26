package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.assemblies.SkeletonClient;
import wizardike.assignment3.graphics.SpriteSheets.SkeletonSpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Ike on 30/01/2017.
 */
public class RaiseSkeletonsClient extends RaiseSkeletons {
    private static final int id = 3;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new RaiseSkeletonsClient(save, spriteSheetRemappingTable);
            }
        });
    }

    public RaiseSkeletonsClient(float castTime, int minSkeletons, int maxSkeletons, SkeletonSpriteSheet spriteSheet) {
        super(castTime, minSkeletons, maxSkeletons, spriteSheet);
    }

    public RaiseSkeletonsClient(DataInputStream saveData, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        super(saveData, spriteSheetRemappingTable);
    }

    @Override
    public void activate(Level level, int entity) {}

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {
        int numSkeletons = networkIn.readInt();
        for(int i = 0; i < numSkeletons; ++i){
            SkeletonClient.create(level, thisEntity, 8.0f, 8.0f,
                    networkIn.readFloat(), networkIn.readFloat(), 0.15f * 6.0f, spriteSheet);
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
