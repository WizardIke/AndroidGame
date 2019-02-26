package wizardike.assignment3.assemblies;

import android.os.Handler;
import android.os.Looper;

import wizardike.assignment3.Engine;
import wizardike.assignment3.R;
import wizardike.assignment3.ai.PlayerAttackController;
import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.FireMageSpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheetLoader;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.health.Health;
import wizardike.assignment3.health.PlayerHealth;
import wizardike.assignment3.health.Regeneration;
import wizardike.assignment3.health.Resistances;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CircleHitBox;
import wizardike.assignment3.physics.movement.Movement;
import wizardike.assignment3.ai.PlayerMovementController;
import wizardike.assignment3.talents.primary.FireBoltSpell;

public class FireMagePlayer {
    private static final float startingMaxHealth = 100.0f;
    private static final float startingSpeed = 0.25f * 6.0f;
    private static final float startingFireBoltSpellDamage = 10.0f;
    private static final float radius = 0.02f * 6.0f;
    private static final float mass = 70.0f;
    private static final float armorToughness = 0.1f;
    private static final float startingHealthRegen = 0.9f;
    private static final float startingResistance = 0.2f;
    private static final float walkingAnimationLength = 0.4f;

    public static void create(final Engine engine, final Level level, final float posX, final float posY,
                              final EntityLoadedCallback callback) {
        new FireMageSpriteSheet(R.drawable.fire_mage_sprites, engine, new SpriteSheetLoader.Callback() {
            @Override
            public void onLoadComplete(SpriteSheet spriteSheet) {
                resourcesLoaded(engine, level, posX, posY, (WalkingSpriteSheet)spriteSheet, callback);
            }
        });
    }

    private static void resourcesLoaded(final Engine engine, final Level level, final float posX, final float posY,
                                       final WalkingSpriteSheet walingSpriteSheet, final EntityLoadedCallback callback) {
        final int entity = level.getEngine().getEntityAllocator().allocate();
        level.getSpriteSheetSystem().addSpriteSheet(entity, walingSpriteSheet);
        final Vector2 position = new Vector2(posX, posY);
        level.getPositionSystem().addPosition(entity, position);
        final Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                walingSpriteSheet.xCoordinates[0], walingSpriteSheet.yCoordinates[0],
                walingSpriteSheet.spriteTextureWidth, walingSpriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addSprite(entity, sprite);
        final PointLight light = new PointLight(position, 0.0f, 0.0f, 1.5f, radius, 0.8f, 0.7f, 0.6f);
        level.getLightingSystem().addPointLight(entity, light);
        final Movement movement = new Movement(position);
        level.getMovementSystem().addMovement(entity, movement);
        final WalkingAnimation walkingAnimation = new WalkingAnimation(walingSpriteSheet, movement, sprite, walkingAnimationLength);
        level.getWalkingAnimationSystem().addWalkingAnimation(entity, walkingAnimation);
        final CircleHitBox circleHitBox = new CircleHitBox(position, radius, mass);
        level.getCollisionSystem().addCollidable(entity, circleHitBox);
        level.getFactionSystem().addFaction(entity, Faction.Mage);
        level.getCategorySystem().addCategory(entity, Category.Creature);
        final Health health = new PlayerHealth(new Resistances(startingResistance, startingResistance,
                startingResistance, startingResistance, startingResistance,
                startingResistance, startingResistance), armorToughness, startingMaxHealth, startingMaxHealth);
        level.getHealthSystem().addHealth(entity, health);
        level.getRegenerationSystem().addRegeneration(entity, new Regeneration(startingHealthRegen, health));
        level.getAwesomenessSystem().addAwesomeness(entity, new Awesomeness(0));
        level.getCamera().position = position;

        final PlayerAttackController attackController = new PlayerAttackController(entity, level.getUpdatingSystem(),
                new FireBoltSpell(0.4f * 6.0f, 0.5f, 2.0f * 6.0f, startingFireBoltSpellDamage, walingSpriteSheet));
        final PlayerMovementController movementController = new PlayerMovementController(movement, startingSpeed);

        new Handler(Looper.getMainLooper()).post(new Runnable() { //needs to run on ui thread
            @Override
            public void run() {
                engine.getUserInterface().setRightAnalogStickOnRotationListener(attackController);
                engine.getUserInterface().setLeftAnalogStickOnRotationListener(movementController);
                callback.onLoadComplete(entity);
            }
        });
    }
}
