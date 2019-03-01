package wizardike.assignment3.physics.Collision.CollisionHandlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.Collidable;

/**
 * Created by Isaac on 14/10/2017.
 */
public class BiteClient implements CollisionHandler {
    private static final int id = 1;

    static void registerLoader() {
        CollisionHandlerLoader.addLoader(id, new CollisionHandlerLoader.Loader() {
            @Override
            public CollisionHandler load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new BiteClient(save);
            }
        });
    }

    private  float castTime;
    private float coolDown;

    public BiteClient(float castTime) {
        this.castTime = castTime;
        this.coolDown = 0f;
    }

    private BiteClient(DataInputStream saveData) throws IOException {
        castTime = saveData.readFloat();
        coolDown = saveData.readFloat();
    }

    @Override
    public void update(Level level, Collidable thisCollidable, int thisEntity) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        if(coolDown > 0f) coolDown -= frameTime;
    }

    @Override
    public void run(Level level, Collidable thisCollidable, int thisEntity, Collidable other, int otherEntity) {
        Integer targetCategory = level.getCategorySystem().getCategory(otherEntity);
        if(targetCategory != null && targetCategory == Category.Creature) {
            Faction thisFaction = level.getFactionSystem().getFaction(thisEntity);
            Faction targetFaction = level.getFactionSystem().getFaction(otherEntity);
            if(coolDown <= 0.0f && thisFaction.isEnemy(targetFaction)) {
                //main.soundFX.bite.play();
                coolDown = castTime;
            }
        }
    }

    @Override
    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeFloat(castTime);
        saveData.writeFloat(coolDown);
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) {

    }

    @Override
    public int getId() {
        return id;
    }
}
