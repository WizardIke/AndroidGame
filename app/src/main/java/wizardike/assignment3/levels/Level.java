package wizardike.assignment3.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wizardike.assignment3.Engine;
import wizardike.assignment3.Serialization.Deserializer;
import wizardike.assignment3.Serialization.Serializer;
import wizardike.assignment3.ai.BasicAIControllerHostSystem;
import wizardike.assignment3.ai.BasicAIControllerSystem;
import wizardike.assignment3.animation.FireBoltAnimationSystem;
import wizardike.assignment3.animation.WalkingAnimationSystem;
import wizardike.assignment3.awesomeness.AwesomenessSystem;
import wizardike.assignment3.category.CategorySystem;
import wizardike.assignment3.destruction.DestructionSystem;
import wizardike.assignment3.faction.FactionSystem;
import wizardike.assignment3.graphics.Camera;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheetSystem;
import wizardike.assignment3.health.HealthSystem;
import wizardike.assignment3.health.RegenerationHostSystem;
import wizardike.assignment3.health.RegenerationSystem;
import wizardike.assignment3.networking.NetworkConnection;
import wizardike.assignment3.networking.Server;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.physics.velocity.VelocityHostSystem;
import wizardike.assignment3.physics.velocity.VelocitySystem;
import wizardike.assignment3.position.PositionSystem;
import wizardike.assignment3.updating.UpdatingSystem;
import wizardike.assignment3.graphics.GeometrySystem;
import wizardike.assignment3.graphics.LightingSystem;
import wizardike.assignment3.physics.Collision.CollisionSystem;
import wizardike.assignment3.userInterface.UserInterfaceSystem;

/**
 * A game level. All levels should be an instance of this.
 * An entity cannot have components in more than one Level.
 */
public class Level {
    public interface Callback {
        void onLoadComplete(Level level);
    }

    private Engine engine;

    private DestructionSystem destructionSystem;
    private UpdatingSystem updatingSystem;
    private UserInterfaceSystem userInterfaceSystem;
    private PositionSystem positionSystem;
    private CollisionSystem collisionSystem;
    private GeometrySystem geometrySystem;
    private LightingSystem lightingSystem;
    private AwesomenessSystem awesomenessSystem;
    private CategorySystem categorySystem;
    private FactionSystem factionSystem;
    private BasicAIControllerSystem basicAIControllerSystem = null;
    private BasicAIControllerHostSystem basicAIControllerHostSystem = null;
    private HealthSystem healthSystem;
    private RegenerationSystem regenerationSystem = null;
    private RegenerationHostSystem regenerationHostSystem = null;
    private VelocitySystem velocitySystem = null;
    private VelocityHostSystem velocityHostSystem = null;
    private SpriteSheetSystem spriteSheetSystem;
    private WalkingAnimationSystem walkingAnimationSystem;
    private FireBoltAnimationSystem fireBoltAnimationSystem;
    private Camera camera;

    /**
     * Makes an empty level
     */
    Level(Engine engine) {
        this.engine = engine;
        NetworkConnection connection = engine.getNetworkConnection();
        boolean host = connection != null && connection.getClass() == Server.class;

        destructionSystem = new DestructionSystem();
        updatingSystem = new UpdatingSystem();
        userInterfaceSystem = new UserInterfaceSystem(this);
        positionSystem = new PositionSystem();
        collisionSystem = new CollisionSystem();
        geometrySystem = new GeometrySystem();
        lightingSystem = new LightingSystem();
        awesomenessSystem = new AwesomenessSystem();
        categorySystem = new CategorySystem();
        factionSystem = new FactionSystem();
        healthSystem = new HealthSystem();
        if(host) {
            basicAIControllerHostSystem = new BasicAIControllerHostSystem();
            regenerationHostSystem = new RegenerationHostSystem();
            velocityHostSystem = new VelocityHostSystem();
        } else {
            basicAIControllerSystem = new BasicAIControllerSystem();
            regenerationSystem = new RegenerationSystem();
            velocitySystem = new VelocitySystem();
        }
        camera = new Camera(null, 1.0f);
        spriteSheetSystem = new SpriteSheetSystem();
        walkingAnimationSystem = new WalkingAnimationSystem();
        fireBoltAnimationSystem = new FireBoltAnimationSystem();
    }

    /**
     * Loads a level. The level must not be used until the callback is called
     */
    Level(final DataInputStream save, final Engine engine, final Callback callback) throws IOException {
        this.engine = engine;
        NetworkConnection connection = engine.getNetworkConnection();
        boolean host = connection != null && connection.getClass() == Server.class;

        final Deserializer deserializer = new Deserializer(engine.getEntityAllocator());
        destructionSystem = new DestructionSystem(); //There shouldn't have been any entities pending destruction when we saved.
        positionSystem = new PositionSystem(save, deserializer);
        collisionSystem = new CollisionSystem(save, deserializer);
        geometrySystem = new GeometrySystem(save, deserializer);
        lightingSystem = new LightingSystem(save, deserializer);
        awesomenessSystem = new AwesomenessSystem(save, deserializer);
        categorySystem = new CategorySystem(save, deserializer);
        factionSystem = new FactionSystem(save, deserializer);
        healthSystem = new HealthSystem(save, deserializer);
        if(host) {
            regenerationHostSystem = new RegenerationHostSystem(save, deserializer);
            velocityHostSystem = new VelocityHostSystem(save, deserializer);
            basicAIControllerHostSystem = new BasicAIControllerHostSystem(save, deserializer);
        } else {
            regenerationSystem = new RegenerationSystem(save, deserializer);
            velocitySystem = new VelocitySystem(save, deserializer);
            basicAIControllerSystem = new BasicAIControllerSystem(save, deserializer);
        }
        camera = new Camera(save, deserializer);
        Sprite[][] spritesToRemap = new Sprite[2][];
        spritesToRemap[0] = geometrySystem.getSprites();
        spritesToRemap[1] = geometrySystem.getTransparentSprites();
        final int[] spritesToRemapLength = new int[2];
        spritesToRemapLength[0] = geometrySystem.getSpriteCount();
        spritesToRemapLength[1] = geometrySystem.getTransparentSpriteCount();
        spriteSheetSystem = new SpriteSheetSystem(save, engine, deserializer, spritesToRemap,
                spritesToRemapLength, new SpriteSheetSystem.Callback() {
            @Override
            public void onLoadComplete(SpriteSheetSystem spriteSheetSystem) {
                try {
                    walkingAnimationSystem = new WalkingAnimationSystem(save, deserializer);
                    fireBoltAnimationSystem = new FireBoltAnimationSystem(save, deserializer);
                    updatingSystem = new UpdatingSystem(save, deserializer);
                    userInterfaceSystem = new UserInterfaceSystem(save, Level.this, deserializer);

                    callback.onLoadComplete(Level.this);
                } catch (IOException e) {
                    engine.onError();
                }
            }
        });
    }

    public void save(DataOutputStream save) throws IOException {
        final Serializer serializer = new Serializer();

        positionSystem.save(save, serializer);
        collisionSystem.save(save, serializer);
        geometrySystem.save(save, serializer);
        lightingSystem.save(save, serializer);
        awesomenessSystem.save(save, serializer);
        categorySystem.save(save, serializer);
        factionSystem.save(save, serializer);
        healthSystem.save(save, serializer);
        if(regenerationHostSystem != null) {
            regenerationHostSystem.save(save, serializer);
            velocityHostSystem.save(save, serializer);
            basicAIControllerHostSystem.save(save, serializer);
        } else {
            regenerationSystem.save(save, serializer);
            velocitySystem.save(save, serializer);
            basicAIControllerSystem.save(save, serializer);
        }
        camera.save(save, serializer);
        spriteSheetSystem.save(save, serializer);
        walkingAnimationSystem.save(save, serializer);
        fireBoltAnimationSystem.save(save, serializer);
        updatingSystem.save(save, serializer);
        userInterfaceSystem.save(save, serializer);
    }

    public Engine getEngine() {
        return engine;
    }

    public DestructionSystem getDestructionSystem() {
        return destructionSystem;
    }

    public UpdatingSystem getUpdatingSystem() {
        return updatingSystem;
    }

    public UserInterfaceSystem getUserInterfaceSystem() {
        return userInterfaceSystem;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public GeometrySystem getGeometrySystem() {
        return geometrySystem;
    }

    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }

    public AwesomenessSystem getAwesomenessSystem() {
        return awesomenessSystem;
    }

    public CategorySystem getCategorySystem() {
        return categorySystem;
    }

    public FactionSystem getFactionSystem() {
        return factionSystem;
    }

    public PositionSystem getPositionSystem() {
        return positionSystem;
    }

    public BasicAIControllerSystem getBasicAIControllerSystem() {
        return basicAIControllerSystem;
    }

    public BasicAIControllerHostSystem getBasicAIControllerHostSystem() {
        return basicAIControllerHostSystem;
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    public RegenerationSystem getRegenerationSystem() {
        return regenerationSystem;
    }

    public RegenerationHostSystem getRegenerationHostSystem() {
        return regenerationHostSystem;
    }

    public VelocitySystem getVelocitySystem() {
        return velocitySystem;
    }

    public VelocityHostSystem getVelocityHostSystem() {
        return velocityHostSystem;
    }

    public Camera getCamera() {
        return camera;
    }

    public SpriteSheetSystem getSpriteSheetSystem() {
        return spriteSheetSystem;
    }

    public WalkingAnimationSystem getWalkingAnimationSystem() {
        return walkingAnimationSystem;
    }

    public FireBoltAnimationSystem getFireBoltAnimationSystem() {
        return fireBoltAnimationSystem;
    }

    public void start() {
        userInterfaceSystem.start(engine.getUserInterface());
    }

    public void update() {
        if(regenerationHostSystem != null) {
            regenerationHostSystem.update(this);
            basicAIControllerHostSystem.update(this);
        } else {
            regenerationSystem.update(this);
            basicAIControllerSystem.update(this);
        }

        collisionSystem.update(this); //handle collisions between objects
        if(velocityHostSystem != null) {
            velocityHostSystem.update(this);
        } else {
            velocitySystem.update(this);
        }
        walkingAnimationSystem.update(this);
        fireBoltAnimationSystem.update(this);
        updatingSystem.update(this); //update anything that needs updating
        geometrySystem.update(this); //render geometry to a buffer
        lightingSystem.update(this); //apply lighting and shadows
        destructionSystem.update(this);
    }

    public void stop() {
        userInterfaceSystem.stop(engine.getUserInterface());
    }

    public void handleMessage(DataInputStream networkIn) throws IOException {
        int systemId = networkIn.readInt();
        switch(systemId) {
            case SystemIds.healthSystem: {
                healthSystem.handleMessage(this, networkIn);
                break;
            }
            case SystemIds.positionSystem: {
                positionSystem.handleMessage(networkIn);
            }
            case SystemIds.collisionSystem: {
                collisionSystem.handleMessage(this, networkIn);
            }
            default: {
                engine.onError();
            }
        }
    }

    /**
     * Removes all components from an entity. The entity can then be reused if needed.
     * @param entity The entity to destroy
     */
    public void destroyEntity(int entity) {
        if(regenerationHostSystem != null) {
            regenerationHostSystem.removeRegenerations(entity);
            velocityHostSystem.removeMovements(entity);
            basicAIControllerHostSystem.removeBasicAIControllers(entity);
        } else {
            regenerationSystem.removeRegenerations(entity);
            velocitySystem.removeMovements(entity);
            basicAIControllerSystem.removeBasicAIControllers(entity);
        }
        updatingSystem.removeUpdatables(entity);
        userInterfaceSystem.removeLeftAnalogStickListeners(entity);
        userInterfaceSystem.removeRightAnalogStickListeners(entity);
        collisionSystem.removeCollidables(entity);
        positionSystem.removePositions(entity);
        geometrySystem.removeSprites(entity);
        geometrySystem.removeTransparentSprites(entity);
        lightingSystem.removeCircleShadowCasters(entity);
        lightingSystem.removeLineShadowCasters(entity);
        lightingSystem.removePointLights(entity);
        awesomenessSystem.removeAwesomenesses(entity);
        categorySystem.removeCategories(entity);
        factionSystem.removeFactions(entity);
        healthSystem.removeHealths(entity);
        spriteSheetSystem.removeSpriteSheets(entity);
        walkingAnimationSystem.removeWalkingAnimations(entity);
        fireBoltAnimationSystem.removeFireBoltAnimations(entity);
    }
}
