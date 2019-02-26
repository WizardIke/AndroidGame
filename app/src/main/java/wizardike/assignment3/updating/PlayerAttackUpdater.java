package wizardike.assignment3.updating;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.talents.primary.PrimaryTalent;
import wizardike.assignment3.talents.primary.PrimaryTalentLoader;

public class PlayerAttackUpdater implements Updatable {
    private static final int id = 0;

    static void registerLoader() {
        UpdatableLoader.addLoader(id, new UpdatableLoader.Loader() {
            @Override
            public Updatable load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new PlayerAttackUpdater(save, spriteSheetRemappingTable);
            }
        });
    }

    public float directionX;
    public float directionY;
    private PrimaryTalent talent;

    public PlayerAttackUpdater(PrimaryTalent talent) {
        this.talent = talent;
    }

    public PlayerAttackUpdater(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        directionX = save.readFloat();
        directionY = save.readFloat();
        final int talentId = save.readInt();
        talent = PrimaryTalentLoader.load(talentId, save, spriteSheetRemappingTable);
    }

    @Override
    public void update(Level level, int entity) {
        talent.update(level);
        if(talent.isReady()) {
            talent.activate(level, entity, directionX, directionY);
        }
    }

    @Override
    public void save(DataOutputStream save, IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable) throws IOException {
        save.writeFloat(directionX);
        save.writeFloat(directionY);
        save.writeInt(talent.getId());
        talent.save(save, spriteSheetRemappingTable);
    }

    @Override
    public int getId() {
        return id;
    }
}
