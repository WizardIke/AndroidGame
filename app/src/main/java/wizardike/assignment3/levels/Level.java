package wizardike.assignment3.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.Engine;
import wizardike.assignment3.ai.BasicAIControllerSystem;
import wizardike.assignment3.animation.FireBoltAnimationSystem;
import wizardike.assignment3.animation.WalkingAnimationSystem;
import wizardike.assignment3.awesomeness.AwesomenessSystem;
import wizardike.assignment3.category.CategorySystem;
import wizardike.assignment3.destruction.DestructionSystem;
import wizardike.assignment3.faction.FactionSystem;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.Camera;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheetSystem;
import wizardike.assignment3.health.Health;
import wizardike.assignment3.health.HealthHostSystem;
import wizardike.assignment3.health.HealthSystem;
import wizardike.assignment3.health.RegenerationSystem;
import wizardike.assignment3.networking.NetworkConnection;
import wizardike.assignment3.networking.Server;
import wizardike.assignment3.networking.SystemIds;
import wizardike.assignment3.physics.movement.Movement;
import wizardike.assignment3.physics.movement.MovementSystem;
import wizardike.assignment3.position.PositionHostSystem;
import wizardike.assignment3.position.PositionSystem;
import wizardike.assignment3.talents.UserInterfaceSystem;
import wizardike.assignment3.updating.UpdatingSystem;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.graphics.GeometrySystem;
import wizardike.assignment3.graphics.LightingSystem;
import wizardike.assignment3.physics.Collision.CollisionSystem;

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
    private PositionSystem positionSystem = null;
    private PositionHostSystem positionHostSystem = null;
    private CollisionSystem collisionSystem;
    private GeometrySystem geometrySystem;
    private LightingSystem lightingSystem;
    private AwesomenessSystem awesomenessSystem;
    private CategorySystem categorySystem;
    private FactionSystem factionSystem;
    private BasicAIControllerSystem basicAIControllerSystem;
    private HealthSystem healthSystem = null;
    private HealthHostSystem healthHostSystem = null;
    private RegenerationSystem regenerationSystem;
    private MovementSystem movementSystem;
    private Camera camera;
    private SpriteSheetSystem spriteSheetSystem;
    private WalkingAnimationSystem walkingAnimationSystem;
    private FireBoltAnimationSystem fireBoltAnimationSystem;
    private UserInterfaceSystem userInterfaceSystem;

    /**
     * Makes an empty level
     */
    Level(Engine engine) {
        this.engine = engine;
        NetworkConnection connection = engine.getNetworkConnection();
        boolean host = connection != null && connection.getClass() == Server.class;

        destructionSystem = new DestructionSystem();
        updatingSystem = new UpdatingSystem();
        if(host) {
            positionHostSystem = new PositionHostSystem();
        } else {
            positionSystem = new PositionSystem();
        }
        collisionSystem = new CollisionSystem();
        geometrySystem = new GeometrySystem();
        lightingSystem = new LightingSystem();
        awesomenessSystem = new AwesomenessSystem();
        categorySystem = new CategorySystem();
        factionSystem = new FactionSystem();
        basicAIControllerSystem = new BasicAIControllerSystem();
        if(host) {
            healthHostSystem = new HealthHostSystem();
        } else {
            healthSystem = new HealthSystem();
        }
        regenerationSystem = new RegenerationSystem();
        movementSystem = new MovementSystem();
        camera = new Camera(null, 1.0f);
        spriteSheetSystem = new SpriteSheetSystem();
        walkingAnimationSystem = new WalkingAnimationSystem();
        fireBoltAnimationSystem = new FireBoltAnimationSystem();
        userInterfaceSystem = new UserInterfaceSystem();
    }

    /**
     * Loads a level. The level must not be used until the callback is called
     */
    Level(final DataInputStream save, final Engine engine, final Callback callback) throws IOException {
        this.engine = engine;
        NetworkConnection connection = engine.getNetworkConnection();
        boolean host = connection != null && connection.getClass() == Server.class;

        final EntityUpdater entityUpdater = new EntityUpdater();
        destructionSystem = new DestructionSystem(); //There shouldn't have been any entities pending destruction when we saved.
        updatingSystem = new UpdatingSystem(save, engine, entityUpdater);
        Vector2[] positionRemappingTable;
        if(host) {
            positionHostSystem = new PositionHostSystem(save, engine, entityUpdater);
            positionRemappingTable = positionHostSystem.getPositions();
        } else {
            positionSystem = new PositionSystem(save, engine, entityUpdater);
            positionRemappingTable = positionSystem.getPositions();
        }
        collisionSystem = new CollisionSystem(save, engine, entityUpdater, positionRemappingTable);
        geometrySystem = new GeometrySystem(save, engine, entityUpdater, positionRemappingTable);
        lightingSystem = new LightingSystem(save, engine, entityUpdater, positionRemappingTable);
        awesomenessSystem = new AwesomenessSystem(save, engine, entityUpdater);
        categorySystem = new CategorySystem(save, engine, entityUpdater);
        factionSystem = new FactionSystem(save, engine, entityUpdater);
        basicAIControllerSystem = new BasicAIControllerSystem(save, engine, entityUpdater);
        Health[] healthRemappingTable;
        if(host) {
            healthHostSystem = new HealthHostSystem(save, engine, entityUpdater);
            healthRemappingTable = healthHostSystem.getHealths();
        } else {
            healthSystem = new HealthSystem(save, engine, entityUpdater);
            healthRemappingTable = healthSystem.getHealths();
        }
        regenerationSystem = new RegenerationSystem(save, engine, entityUpdater, healthRemappingTable);
        movementSystem = new MovementSystem(save, engine, entityUpdater, positionRemappingTable);
        camera = new Camera(save, positionRemappingTable);

        Sprite[][] spritesToRemap = new Sprite[2][];
        spritesToRemap[0] = geometrySystem.getSprites();
        spritesToRemap[1] = geometrySystem.getTransparentSprites();
        final int[] spritesToRemapLength = new int[2];
        spritesToRemapLength[0] = geometrySystem.getSpriteCount();
        spritesToRemapLength[1] = geometrySystem.getTransparentSpriteCount();
        spriteSheetSystem = new SpriteSheetSystem(save, engine, entityUpdater, spritesToRemap,
                spritesToRemapLength, new SpriteSheetSystem.Callback() {
            @Override
            public void onLoadComplete(SpriteSheetSystem spriteSheetSystem) {
                try {
                    SpriteSheet[] spriteSheets = spriteSheetSystem.getSpriteSheets();
                    walkingAnimationSystem = new WalkingAnimationSystem(save, engine, entityUpdater,
                            spriteSheets,
                            movementSystem.getMovements(),
                            geometrySystem.getSprites());
                    fireBoltAnimationSystem = new FireBoltAnimationSystem(save, engine, entityUpdater,
                            spriteSheets,
                            geometrySystem.getTransparentSprites());
                    userInterfaceSystem = new UserInterfaceSystem(save, engine, entityUpdater,
                            spriteSheets);
                } catch (IOException e) {
                    engine.onError();
                }

                callback.onLoadComplete(Level.this);
            }
        });
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

    public PositionHostSystem getPositionHostSystem() {
        return positionHostSystem;
    }

    public BasicAIControllerSystem getBasicAIControllerSystem() {
        return basicAIControllerSystem;
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    public HealthHostSystem getHealthHostSystem() {
        return healthHostSystem;
    }

    public RegenerationSystem getRegenerationSystem() {
        return regenerationSystem;
    }

    public MovementSystem getMovementSystem() {
        return movementSystem;
    }

    public Camera getCamera() {
        return camera;
    }

    public UserInterfaceSystem getUserInterfaceSystem() {
        return userInterfaceSystem;
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

    public void update(Engine engine) {
        basicAIControllerSystem.update(this);
        collisionSystem.update(this); //handle collisions between objects
        if(healthHostSystem != null) {
            healthHostSystem.update(this);
        }
        movementSystem.update(this);
        walkingAnimationSystem.update(this);
        fireBoltAnimationSystem.update(this);
        if(positionHostSystem != null) {
            positionHostSystem.update(this);
        }
        userInterfaceSystem.update(this);
        updatingSystem.update(this); //update anything that needs updating
        geometrySystem.update(this); //render geometry to a buffer
        lightingSystem.update(this); //apply lighting and shadows
        destructionSystem.update(this);
        regenerationSystem.update(this);
    }

    public void save(DataOutputStream save) throws IOException {
        updatingSystem.save(save);
        IdentityHashMap<Vector2, Integer> positionRemappingTable;
        if(positionSystem != null) {
            positionSystem.save(save);
            positionRemappingTable = positionSystem.getRemappingTable();
        } else {
            positionHostSystem.save(save);
            positionRemappingTable = positionSystem.getRemappingTable();
        }
        collisionSystem.save(save, positionRemappingTable);
        geometrySystem.save(save, positionRemappingTable);
        IdentityHashMap<Sprite, Integer> spriteRemappingTable = geometrySystem.getSpriteRemappingTable();
        lightingSystem.save(save, positionRemappingTable);
        awesomenessSystem.save(save);
        categorySystem.save(save);
        factionSystem.save(save);
        basicAIControllerSystem.save(save);
        IdentityHashMap<Health, Integer> healthRemappingTable;
        if(healthSystem != null) {
            healthSystem.save(save);
            healthRemappingTable = healthSystem.getHealthRemappingTable();
        } else {
            healthHostSystem.save(save);
            healthRemappingTable = healthHostSystem.getHealthRemappingTable();
        }
        regenerationSystem.save(save, healthRemappingTable);
        movementSystem.save(save, positionRemappingTable);
        IdentityHashMap<Movement, Integer> movementRemappingTable = movementSystem.getRemappingTable();
        camera.save(save, positionRemappingTable);
        spriteSheetSystem.save(save);
        IdentityHashMap<SpriteSheet, Integer> spriteSheetRemappingTable = spriteSheetSystem.getRemappingTable();
        walkingAnimationSystem.save(save, spriteSheetRemappingTable, movementRemappingTable,
                spriteRemappingTable);
        IdentityHashMap<Sprite, Integer> transparentSpriteRemappingTable
                = geometrySystem.getTransparentSpriteRemappingTable();
        fireBoltAnimationSystem.save(save, spriteSheetRemappingTable, transparentSpriteRemappingTable);
        userInterfaceSystem.save(save, spriteSheetRemappingTable);
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
            case SystemIds.userInterfaceSystem: {
                userInterfaceSystem.handleMessage(this, networkIn);
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
        updatingSystem.removeAll(entity);
        collisionSystem.removeAll(entity);
        if(positionSystem != null) {
            positionSystem.removePositions(entity);
        } else {
            positionHostSystem.removePositions(entity);
        }
        geometrySystem.removeSprites(entity);
        geometrySystem.removeTransparentSprites(entity);
        lightingSystem.removeCircleShadowCasters(entity);
        lightingSystem.removeLineShadowCasters(entity);
        lightingSystem.removePointLights(entity);
        awesomenessSystem.removeAwesomenesses(entity);
        categorySystem.removeCategories(entity);
        factionSystem.removeFactions(entity);
        if(healthSystem != null) {
            healthSystem.removeHealths(entity);
        } else {
            healthHostSystem.removeHealths(entity);
        }
        regenerationSystem.removeRegenerations(entity);
        movementSystem.removeMovements(entity);
        userInterfaceSystem.removeMoveTalents(entity);
        userInterfaceSystem.removeAttackTalents(entity);
        spriteSheetSystem.removeSpriteSheets(entity);
        walkingAnimationSystem.removeWalkingAnimations(entity);
        fireBoltAnimationSystem.removeFireBoltAnimations(entity);
    }
}
