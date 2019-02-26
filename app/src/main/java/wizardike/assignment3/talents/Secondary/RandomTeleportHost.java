package wizardike.assignment3.talents.Secondary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.talents.UserInterfaceSystem;

/*
public class RandomTeleportHost extends RandomTeleport {
    private static final int id = 9;

    static void registerLoader() {
        SecondaryTalentLoader.addLoader(id, new SecondaryTalentLoader.Loader() {
            @Override
            public SecondaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new RandomTeleportHost(save);
            }
        });
    }

    public RandomTeleportHost(float castTime) {
        super(castTime);
    }

    private RandomTeleportHost(DataInputStream saveData) throws IOException {
        super(saveData);
    }

    @Override
    public void activate(Level level, int thisEntity) {
        Vector2 position = level.getPositionSystem().getPosition(thisEntity);
        float x = main.gameLevel.findRandomLocationX(main);
        float y = main.gameLevel.findRandomLocationY(main);
        position.setX(x);
        position.setY(y);
        cooldown = castTime;

        DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        try {
            networkOut.writeInt(28);
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            networkOut.writeInt(levelIndex);
            networkOut.writeInt(SystemIds.userInterfaceSystem);
            UserInterfaceSystem userInterfaceSystem = level.getUserInterfaceSystem();
            networkOut.writeInt(UserInterfaceSystem.secondary);
            networkOut.writeInt(userInterfaceSystem.indexOfSecondaryTalent(thisEntity, this));
            networkOut.writeFloat(x);
            networkOut.writeFloat(y);
        } catch (IOException e) {
            level.getEngine().onError();
        }
    }
}
*/