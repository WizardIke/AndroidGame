package wizardike.assignment3.talents.primary;

import wizardike.assignment3.assemblies.FireBoltParticleHost;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.networking.SystemIds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static wizardike.assignment3.networking.NetworkMessageTypes.activate;
import static wizardike.assignment3.networking.NetworkMessageTypes.activateSuccessful;

/**
 * Created by Ike on 30/01/2017.
 */
public class FireBoltSpellHost extends FireBoltSpell {
    private static final int id = 2;

    static void registerLoader() {
        PrimaryTalentLoader.addLoader(id, new PrimaryTalentLoader.Loader() {
            @Override
            public PrimaryTalent load(DataInputStream save, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
                return new FireBoltSpellHost(save, spriteSheetRemappingTable);
            }
        });
    }

    public FireBoltSpellHost(float speed, float castTime, float range, float damage, WalkingSpriteSheet spriteSheet) {
        super(speed, castTime, range, damage, spriteSheet);
    }

    public FireBoltSpellHost(DataInputStream saveData, SpriteSheet[] spriteSheetRemappingTable) throws IOException {
        super(saveData, spriteSheetRemappingTable);
    }

    @Override
    public void activate(Level level, int entity, float directionX, float directionY) {
        /*
        //main.soundFX.FireboltCastSound.play(6.0f);
        Vector2 position = level.getPositionSystem().getPosition(entity);
        FireBoltParticleHost.create(level, position.getX() + directionX * radius,
                position.getY() + directionY * radius, radius, directionX, directionY, speed,
                lifeTime, entity, damage, spriteSheet);

        cooldown = castTime;

        DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
        try {
            networkOut.writeInt(16);
            int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
            networkOut.writeInt(levelIndex);
            networkOut.writeInt(SystemIds.TalentSystem);
            TalentSystem talentSystem = level.getTalentSystem();
            networkOut.writeInt(talentSystem.indexOf(entity, this));
            networkOut.writeInt(activate);
            networkOut.writeFloat(directionX);
            networkOut.writeFloat(directionY);
        } catch (IOException e) {
            level.getEngine().onError();
        }
        */
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {
        int message = networkIn.readInt();
        switch (message) {
            case activate: {
                if(isReady()) {
                    //main.soundFX.FireboltCastSound.play(6.0f);
                    cooldown = castTime;
                    float directionX = networkIn.readFloat();
                    float directionY = networkIn.readFloat();
                    Vector2 position = level.getPositionSystem().getPosition(thisEntity);
                    FireBoltParticleHost.create(level, position.getX() + directionX * radius,
                            position.getY() + directionY * radius, radius, directionX, directionY, speed,
                            lifeTime, thisEntity, damage, spriteSheet);

                    DataOutputStream networkOut = level.getEngine().getNetworkConnection().getNetworkOut();
                    try {
                        networkOut.writeInt(28);
                        int levelIndex = level.getEngine().getMainWorld().getIdOfLevel(level);
                        networkOut.writeInt(levelIndex);
                        networkOut.writeInt(SystemIds.userInterfaceSystem);
                        //UserInterfaceSystem userInterfaceSystem = level.getUserInterfaceSystem();
                        //networkOut.writeInt(UserInterfaceSystem.attack);
                        //networkOut.writeInt(userInterfaceSystem.indexOfAttackTalent(thisEntity, this));
                        networkOut.writeInt(activateSuccessful);
                        networkOut.writeFloat(directionX);
                        networkOut.writeFloat(directionY);
                    } catch (IOException e) {
                        level.getEngine().onError();
                    }
                }
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
