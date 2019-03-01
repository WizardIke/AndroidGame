package wizardike.assignment3.assemblies;

import wizardike.assignment3.Engine;
import wizardike.assignment3.R;
import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.awesomeness.Awesomeness;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.NecromancerSpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheet;
import wizardike.assignment3.graphics.SpriteSheets.SpriteSheetLoader;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.health.Health;
import wizardike.assignment3.health.PlayerHealth;
import wizardike.assignment3.health.Regeneration;
import wizardike.assignment3.health.Resistances;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CircleHitBox;
import wizardike.assignment3.userInterface.PlayerMovementController;

public class NecromancerPlayer {
    private static final float radius = 0.12f;
    private static final float mass = 70.0f;
    private static final float startingSpeed = 1.5f;
    private static final float armorToughness = 0.1f;
    private static final float startingHealthRegen = 0.9f;
    private static final float startingMaxHealth = 100.0f;
    private static final float startingFireResistance = 0.2f;
    private static final float startingColdResistance = 0.2f;
    private static final float startingLightningResistance = 0.2f;
    private static final float startingArcaneResistance = 0.2f;
    private static final float startingBludgeoningResistance = 0.2f;
    private static final float startingPiecingResistance = 0.2f;
    private static final float startingSlashingResistance = 0.2f;
    private static final float walkingAnimationLength = 0.4f;

    public static void create(final Engine engine, final Level level, final float posX, final float posY,
                              final EntityLoadedCallback callback) {
        new NecromancerSpriteSheet(R.drawable.necromancer_sprites, engine, new SpriteSheetLoader.Callback() {
            @Override
            public void onLoadComplete(SpriteSheet spriteSheet) {
                resourcesLoaded(level, posX, posY, (WalkingSpriteSheet)spriteSheet, callback);
            }
        });
    }

    private static void resourcesLoaded(final Level level, final float posX, final float posY,
                                      final WalkingSpriteSheet spriteSheet, final EntityLoadedCallback callback) {
        final int entity = level.getEngine().getEntityAllocator().allocate();
        level.getSpriteSheetSystem().addSpriteSheet(entity, spriteSheet);
        final Vector2 position = new Vector2(posX, posY);
        level.getPositionSystem().addPosition(entity, position);
        final Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                spriteSheet.xCoordinates[0], spriteSheet.yCoordinates[0], spriteSheet.spriteTextureWidth, spriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addSprite(entity, sprite);
        final PointLight light = new PointLight(position, 0.0f, 0.0f, 1.5f, radius, 0.6f, 0.7f, 0.8f);
        level.getLightingSystem().addPointLight(entity, light);
        final WalkingAnimation walkingAnimation = new WalkingAnimation(spriteSheet, sprite, walkingAnimationLength);
        level.getWalkingAnimationSystem().addWalkingAnimation(entity, walkingAnimation);
        final CircleHitBox circleHitBox = new CircleHitBox(position, radius, mass);
        level.getCollisionSystem().addCollidable(entity, circleHitBox);
        level.getFactionSystem().addFaction(entity, Faction.Mage);
        level.getCategorySystem().addCategory(entity, Category.Creature);
        final Health health = new PlayerHealth(new Resistances(startingFireResistance, startingColdResistance,
                startingLightningResistance, startingArcaneResistance, startingBludgeoningResistance,
                startingPiecingResistance, startingSlashingResistance), armorToughness, startingMaxHealth, startingMaxHealth);
        level.getHealthSystem().addHealth(entity, health);
        level.getRegenerationSystem().addRegeneration(entity, new Regeneration(startingHealthRegen, health));
        level.getAwesomenessSystem().addAwesomeness(entity, new Awesomeness(0));
        level.getCamera().position = position;

        final PlayerMovementController movementController = new PlayerMovementController(position, startingSpeed);
        level.getUserInterfaceSystem().addLeftAnalogStickListener(entity, movementController);
        //TODO add attack
        //level.getUserInterfaceSystem().addRightAnalogStickListener(entity, attackController);
        //TODO add spells
        callback.onLoadComplete(entity);
    }
}
