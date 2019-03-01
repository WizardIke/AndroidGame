package wizardike.assignment3.physics.Collision.CollisionHandlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.animation.FireBoltAnimation;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.Collidable;

import static wizardike.assignment3.networking.NetworkMessageTypes.die;

public class ExplodeClient implements CollisionHandler {
    private static final int id = 4;

    static void registerLoader() {
        CollisionHandlerLoader.addLoader(id, new CollisionHandlerLoader.Loader() {
            @Override
            public CollisionHandler load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new ExplodeClient(save, deserializer);
            }
        });
    }


    protected Vector2 position;
    public boolean exploding;
    public float vX, vY;

    public ExplodeClient(Vector2 position, float vX, float vY) {
        this.position = position;
        exploding = false;
        this.vX = vX;
        this.vY = vY;
    }

    private ExplodeClient(DataInputStream saveData, Deserializer deserializer) throws IOException {
        position = deserializer.getObject(saveData.readInt());
        this.vX = saveData.readFloat();
        this.vY = saveData.readFloat();
    }

    @Override
    public void run(Level level, Collidable thisCollidable, int thisEntity, Collidable other, int otherEntity) {
        Faction thisFaction = level.getFactionSystem().getFaction(thisEntity);
        Faction targetFaction = level.getFactionSystem().getFaction(otherEntity);
        if (!exploding && !thisFaction.isAlly(targetFaction)) {
            //main.soundFX.explosion.play(5.0f);
            FireBoltAnimation fireBoltAnimation = level.getFireBoltAnimationSystem().getFireBoltAnimation(thisEntity);
            if(fireBoltAnimation != null) {
                fireBoltAnimation.explode();
            }
            exploding = true;
            vX = 0.0f;
            vY = 0.0f;
        }
    }

    @Override
    public void update(Level level, Collidable thisCollidable, int thisEntity) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        position.setX(position.getX() + vX * frameTime);
        position.setY(position.getY() + vY * frameTime);
    }

    @Override
    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeInt(serializer.getId(position));
        saveData.writeFloat(vX);
        saveData.writeFloat(vY);
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {
        int message = networkIn.readInt();
        switch (message) {
            case die:
                level.getDestructionSystem().delayedDestroy(thisEntity);
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
