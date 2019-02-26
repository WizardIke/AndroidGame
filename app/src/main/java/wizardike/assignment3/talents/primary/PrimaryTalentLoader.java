package wizardike.assignment3.talents.primary;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.IOException;

import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;

public class PrimaryTalentLoader {
    interface Loader {
        PrimaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException;
    }
    private static final SparseArray<Loader> loaders = new SparseArray<>();

    static void addLoader(int id, Loader loader) {
        loaders.put(id, loader);
    }

    static {
        FireBoltSpell.registerLoader();
        FireBoltSpellClient.registerLoader();
        FireBoltSpellHost.registerLoader();
    }

    public static PrimaryTalent load(int id, DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        return loaders.get(id).load(save, spriteSheetRemappingTable);
    }
}
