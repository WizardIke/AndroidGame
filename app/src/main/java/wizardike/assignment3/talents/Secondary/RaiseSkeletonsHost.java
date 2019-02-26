package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import wizardike.assignment3.assemblies.SkeletonHost;
import wizardike.assignment3.graphics.SpriteSheets.SkeletonSpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;

/**
 * Created by Ike on 30/01/2017.
 */
/*
public class RaiseSkeletonsHost extends RaiseSkeletons {
    private static final int id = 4;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new RaiseSkeletonsHost(save, spriteSheetRemappingTable);
            }
        });
    }

    public RaiseSkeletonsHost(float castTime, int minSkeletons, int maxSkeletons, SkeletonSpriteSheet spriteSheet) {
        super(castTime, minSkeletons, maxSkeletons, spriteSheet);
    }

    public RaiseSkeletonsHost(DataInputStream saveData, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        super(saveData, spriteSheetRemappingTable);
    }

    @Override
    public void activate(Level level, int entity) {
        Random randomNumberGenerator = level.getEngine().getRandomNumberGenerator();
        int numSkeletons = randomNumberGenerator.nextInt(maxSkeletons + 1 - minSkeletons) + minSkeletons;
        final DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        try {
            networkOut.writeInt(10 + 8 * numSkeletons);
            networkOut.writeInt(caster.getType());
            networkOut.write(castSpell);
            networkOut.write(raiseSkeletons);
            networkOut.writeInt(numSkeletons);

            for(int j = 0; j < numSkeletons; ++j) {
                float x = posX + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f;
                float y = posY + main.randomNumberGenerator.nextFloat() * 0.1f - 0.05f;
                networkOut.writeFloat(x);
                networkOut.writeFloat(y);
                new SkeletonHost(main, caster, 8.0f, 8.0f,
                        x, y, 0.15f);
            }
            cooldown = castTime;
        } catch (IOException e) {
            level.getEngine().onError();
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
*/
