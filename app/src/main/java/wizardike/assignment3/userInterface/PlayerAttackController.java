package wizardike.assignment3.userInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.talents.primary.PrimaryTalent;
import wizardike.assignment3.updating.PlayerAttackUpdater;

public class PlayerAttackController implements AnalogStickListener {
    private static final int id = 1;

    static void registerLoader() {
        AnalogStickListenerLoader.addLoader(id, new AnalogStickListenerLoader.Loader() {
            @Override
            public AnalogStickListener load(DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException {
                return new PlayerAttackController(save, entity, level, deserializer);
            }
        });
    }

    private final PlayerAttackUpdater playerAttackUpdater;

    public PlayerAttackController(PrimaryTalent talent) {
        playerAttackUpdater = new PlayerAttackUpdater(talent);
    }

    private PlayerAttackController(DataInputStream save, int entity, Level level, Deserializer deserializer) throws IOException {
        boolean wasAttacking = save.readBoolean();
        if(wasAttacking) {
            playerAttackUpdater = deserializer.getObject(save.readInt());
            level.getUpdatingSystem().remove(entity, playerAttackUpdater);
        } else {
            playerAttackUpdater = new PlayerAttackUpdater(save, deserializer);
        }
    }

    @Override
    public void save(DataOutputStream save, int entity, Level level, Serializer serializer) throws IOException {
        final int index = level.getUpdatingSystem().indexOf(entity, playerAttackUpdater);
        if(index == -1) {
            save.writeBoolean(false);
            playerAttackUpdater.save(save, serializer);
        } else {
            save.writeBoolean(true);
            save.writeInt(serializer.getId(playerAttackUpdater));
        }
    }

    @Override
    public void start(int entity, Level level, float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
        level.getUpdatingSystem().add(entity, playerAttackUpdater);
    }

    @Override
    public void move(int entity, Level level, float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
    }

    @Override
    public void stop(int entity, Level level, float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
        level.getUpdatingSystem().remove(entity, playerAttackUpdater);
    }

    @Override
    public int getId() {
        return id;
    }
}
