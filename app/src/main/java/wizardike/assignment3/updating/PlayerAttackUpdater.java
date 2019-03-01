package wizardike.assignment3.updating;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.talents.primary.PrimaryTalent;
import wizardike.assignment3.talents.primary.PrimaryTalentLoader;

public class PlayerAttackUpdater implements Updatable {
    private static final int id = 0;

    static void registerLoader() {
        UpdatableLoader.addLoader(id, new UpdatableLoader.Loader() {
            @Override
            public Updatable load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new PlayerAttackUpdater(save, deserializer);
            }
        });
    }

    public float directionX;
    public float directionY;
    private PrimaryTalent talent;

    public PlayerAttackUpdater(PrimaryTalent talent) {
        this.talent = talent;
    }

    public PlayerAttackUpdater(DataInputStream save, Deserializer deserializer) throws IOException {
        directionX = save.readFloat();
        directionY = save.readFloat();
        final int talentId = save.readInt();
        talent = PrimaryTalentLoader.load(talentId, save, deserializer);
    }

    @Override
    public void update(Level level, int entity) {
        talent.update(level);
        if(talent.isReady()) {
            talent.activate(level, entity, directionX, directionY);
        }
    }

    @Override
    public void save(DataOutputStream save, Serializer serializer) throws IOException {
        save.writeFloat(directionX);
        save.writeFloat(directionY);
        save.writeInt(talent.getId());
        talent.save(save, serializer);
    }

    @Override
    public int getId() {
        return id;
    }
}
