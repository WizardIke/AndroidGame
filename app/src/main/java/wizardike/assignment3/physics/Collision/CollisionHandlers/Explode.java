package wizardike.assignment3.physics.Collision.CollisionHandlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.animation.FireBoltAnimation;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.health.Health;
import wizardike.assignment3.health.Resistances;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.Collidable;

/**
 * Created by Isaac on 14/10/2017.
 */
public class Explode implements CollisionHandler {
    private static final int id = 2;

    static void registerLoader() {
        CollisionHandlerLoader.addLoader(id, new CollisionHandlerLoader.Loader() {
            @Override
            public CollisionHandler load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new Explode(save, deserializer);
            }
        });
    }

    protected Vector2 position;
    public boolean exploding;
    protected float damage;
    public float timeLeft;
    public float vX, vY;

    public Explode(Vector2 position, float damage, float lifeTime, float vX, float vY) {
        this.position = position;
        exploding = false;
        this.damage = damage;
        this.timeLeft = lifeTime;
        this.vX = vX;
        this.vY = vY;
    }

    Explode(DataInputStream saveData, Deserializer deserializer) throws IOException {
        position = deserializer.getObject(saveData.readInt());
        this.vX = saveData.readFloat();
        this.vY = saveData.readFloat();
        this.timeLeft = saveData.readFloat();
        this.damage = saveData.readFloat();
    }

    @Override
    public void run(Level level, Collidable thisCollidable, int thisEntity, Collidable other, int otherEntity) {
        Faction thisFaction = level.getFactionSystem().getFaction(thisEntity);
        Faction targetFaction = level.getFactionSystem().getFaction(otherEntity);
        if (!exploding && !thisFaction.isAlly(targetFaction)) {
            //main.soundFX.explosion.play(5.0f);
            Health health = level.getHealthSystem().getHealth(otherEntity);
            if(health != null) {
                health.takeDamage(level, thisEntity, otherEntity, damage, Resistances.Type.fire);
                //caster.gainXp(damage * 0.1f);
            }
            FireBoltAnimation fireBoltAnimation = level.getFireBoltAnimationSystem().getFireBoltAnimation(thisEntity);
            if(fireBoltAnimation != null) {
                fireBoltAnimation.explode();
            }
            timeLeft = 0.2f;
            exploding = true;
            vX = 0.0f;
            vY = 0.0f;
        }
    }

    @Override
    public void update(Level level, Collidable thisCollidable, int thisEntity) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        timeLeft -= frameTime;
        if(timeLeft < 0.0) {
            level.getDestructionSystem().delayedDestroy(thisEntity);
        }
        position.setX(position.getX() + vX * frameTime);
        position.setY(position.getY() + vY * frameTime);
    }

    @Override
    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeInt(serializer.getId(position));
        saveData.writeFloat(vX);
        saveData.writeFloat(vY);
        saveData.writeFloat(timeLeft);
        saveData.writeFloat(damage);
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) {

    }

    @Override
    public int getId() {
        return id;
    }
}
