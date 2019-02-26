package wizardike.assignment3.talents.Secondary;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;

public class SecondaryTalentLoader {
    interface Loader {
        SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        RaiseSkeletons.registerLoader();
        RaiseSkeletonsClient.registerLoader();
        //RaiseSkeletonsHost.registerLoader();
        //RandomTeleport.registerLoader();
        //SummonSkeletonHorde.registerLoader();
        //SummonSkeletonHordeClient.registerLoader();
        //SummonSkeletonHordeHost.registerLoader();
    }

    public static SecondaryTalent load(int id, DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        return loaders.get(id).load(save, spriteSheetRemappingTable);
    }
}
