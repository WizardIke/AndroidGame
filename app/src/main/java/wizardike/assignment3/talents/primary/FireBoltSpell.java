package wizardike.assignment3.talents.primary;

import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.assemblies.FireBoltParticle;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.levels.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * Created by Isaac on 12/12/2016.
 */
public class FireBoltSpell implements PrimaryTalent {
    private static final int id = 0;
    protected static final float radius = 0.06f * 6.0f;

    static void registerLoader() {
        PrimaryTalentLoader.addLoader(id, new PrimaryTalentLoader.Loader() {
            @Override
            public PrimaryTalent load(DataInputStream save, Deserializer deserializer) throws IOException {
                return new FireBoltSpell(save, deserializer);
            }
        });
    }

    protected float speed;
    protected float castTime;
    protected float cooldown;
    protected float lifeTime;
    protected float damage;
    protected WalkingSpriteSheet spriteSheet;

    public FireBoltSpell(float speed, float castTime, float range, float damage, WalkingSpriteSheet spriteSheet) {
        this.speed = speed;
        this.castTime = castTime;
        this.lifeTime = range / speed;
        this.cooldown = 0.0f;
        this.damage = damage;
        this.spriteSheet = spriteSheet;
    }

    public FireBoltSpell(DataInputStream saveData, Deserializer deserializer) throws IOException {
        this.speed = saveData.readFloat();
        this.castTime = saveData.readFloat();
        this.lifeTime = saveData.readFloat();
        this.cooldown = saveData.readFloat();
        this.damage = saveData.readFloat();
        this.spriteSheet = deserializer.getObject(saveData.readInt());
    }

    @Override
    public void update(Level level) {
        float frameTime = level.getEngine().getFrameTimer().getFrameTime();
        cooldown -= frameTime;
    }

    @Override
    public boolean isReady() {
        return cooldown <= 0.0f;
    }

    @Override
    public void activate(Level level, int entity, float directionX, float directionY) {
        if(isReady()) {
            //main.soundFX.FireboltCastSound.play(6.0f);
            Vector2 position = level.getPositionSystem().getPosition(entity);
            FireBoltParticle.create(level, position.getX() + directionX * radius,
                    position.getY() + directionY * radius, radius, directionX, directionY,
                    speed, lifeTime, entity, damage, spriteSheet);
            cooldown = castTime;
        }
    }

    @Override
    public void handleMessage(Level level, DataInputStream networkIn, int thisEntity) throws IOException {

    }

    @Override
    public void setCooldown(float castTime) {
        this.castTime = castTime;
    }

    @Override
    public float getCooldown() {
        return this.castTime;
    }

    @Override
    public float getRange() {
        return 6.0f;
    }

    @Override
    public void save(DataOutputStream saveData, Serializer serializer) throws IOException {
        saveData.writeFloat(speed);
        saveData.writeFloat(castTime);
        saveData.writeFloat(lifeTime);
        saveData.writeFloat(cooldown);
        saveData.writeFloat(damage);
        saveData.writeInt(serializer.getId(spriteSheet));
    }

    @Override
    public int getId() {
        return id;
    }
}
