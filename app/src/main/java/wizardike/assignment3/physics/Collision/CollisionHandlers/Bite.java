package wizardike.assignment3.physics.Collision.CollisionHandlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.health.Health;
import wizardike.assignment3.health.Resistances;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.Collidable;

/**
 * Created by Isaac on 14/10/2017.
 */
public class Bite implements CollisionHandler {
    private static final int id = 0;

    private float castTime;
    private float coolDown;
    private float damage;

    static void registerLoader() {
        CollisionHandlerLoader.addLoader(id, new CollisionHandlerLoader.Loader() {
            @Override
            public CollisionHandler load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new Bite(save);
            }
        });
    }

    public Bite(float castTime, float damage) {
        this.castTime = castTime;
        this.coolDown = 0f;
        this.damage = damage;
    }

    private Bite(DataInputStream saveData) throws IOException {
        castTime = saveData.readFloat();
        coolDown = saveData.readFloat();
        damage = saveData.readFloat();
    }

    @Override
    public void update(Level level, Collidable thisCollidable, int thisEntity) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        if(coolDown > 0f) coolDown -= frameTime;
    }

    @Override
    public void run(Level level, Collidable thisCollidable, int thisEntity, Collidable other, int otherEntity) {
        Integer otherCategory = level.getCategorySystem().getCategory(otherEntity);
        if(otherCategory != null && otherCategory == Category.Creature) {
            Faction thisFaction = level.getFactionSystem().getFaction(thisEntity);
            Faction otherFaction = level.getFactionSystem().getFaction(otherEntity);
            if(coolDown <= 0.0f && thisFaction.isEnemy(otherFaction)) {
                //main.soundFX.bite.play();
                Health targetHealth = level.getHealthSystem().getHealth(otherEntity);
                if(targetHealth != null) {
                    targetHealth.takeDamage(level, thisEntity, otherEntity, damage, Resistances.Type.piecing);
                }
                //caster.gainXp(damage * 0.1f);
                coolDown = castTime;
            }
        }
    }

    @Override
    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeFloat(castTime);
        saveData.writeFloat(coolDown);
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
