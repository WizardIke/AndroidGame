package wizardike.assignment3.ai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.AnalogStick;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.talents.primary.PrimaryTalent;
import wizardike.assignment3.updating.PlayerAttackUpdater;
import wizardike.assignment3.updating.UpdatingSystem;

public class PlayerAttackController implements AnalogStick.OnRotationListener {
    private final PlayerAttackUpdater playerAttackUpdater;
    private UpdatingSystem updatingSystem;
    private int entity;

    public PlayerAttackController(int entity, UpdatingSystem updatingSystem, PrimaryTalent talent) {
        playerAttackUpdater = new PlayerAttackUpdater(talent);
        this.updatingSystem = updatingSystem;
        this.entity = entity;
    }

    public PlayerAttackController(DataInputStream save, UpdatingSystem updatingSystem, int entity,
                                  SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        playerAttackUpdater = new PlayerAttackUpdater(save, spriteSheetRemappingTable);
        this.updatingSystem = updatingSystem;
        this.entity = entity;
    }

    @Override
    public void start(float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
        updatingSystem.add(entity, playerAttackUpdater);
    }

    @Override
    public void move(float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
    }

    @Override
    public void stop(float directionX, float directionY) {
        playerAttackUpdater.directionX = directionX;
        playerAttackUpdater.directionY = directionY;
        updatingSystem.remove(entity, playerAttackUpdater);
    }

    public void save(DataOutputStream save, Level level,
                     IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable)
            throws IOException {

        if(level.getUpdatingSystem().indexOf(entity, playerAttackUpdater) != -1) {
            updatingSystem.remove(entity, playerAttackUpdater);
        }
        updatingSystem.save(save, spriteSheetRemappingTable);
    }
}
