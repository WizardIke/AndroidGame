package wizardike.assignment3.assemblies;

import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.health.HealthHost;
import wizardike.assignment3.health.PlayerHealthHost;
import wizardike.assignment3.health.Regeneration;
import wizardike.assignment3.health.Resistances;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CircleHitBox;
import wizardike.assignment3.userInterface.PlayerMovementControllerHost;

/**
 * Created by Isaac on 25/01/2017.
 */
public class NecromancerHostPlayer {
    private static final float startingMaxHealth = 100.0f;
    private static final float startingSpeed = 1.5f;
    private static final float radius = 0.12f;
    private static final float mass = 70.0f;
    private static final float armorToughness = 0.1f;
    private static final float startingHealthRegen = 0.9f;
    private static final float startingFireResistance = 1.0f;
    private static final float startingColdResistance = 1.0f;
    private static final float startingLightningResistance = 1.0f;
    private static final float startingArcaneResistance = 1.0f;
    private static final float startingBludgeoningResistance = 1.0f;
    private static final float startingPiecingResistance = 1.0f;
    private static final float startingSlashingResistance = 1.0f;
    private static final float walkingAnimationLength = 0.4f;

    public static int create(Level level, final float posX, final float posY, WalkingSpriteSheet spriteSheet) {
        int entity = level.getEngine().getEntityAllocator().allocate();
        Vector2 position = new Vector2(posX, posY);
        level.getPositionSystem().addPosition(entity, position);
        Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                spriteSheet.xCoordinates[0], spriteSheet.yCoordinates[0], spriteSheet.spriteTextureWidth, spriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addSprite(entity, sprite);
        level.getLightingSystem().addPointLight(entity, new PointLight(position, 0.0f, 0.0f, 1.5f, radius, 0.8f, 0.7f, 0.7f));
        WalkingAnimation walkingAnimation = new WalkingAnimation(spriteSheet, sprite, walkingAnimationLength);
        level.getWalkingAnimationSystem().addWalkingAnimation(entity, walkingAnimation);
        CircleHitBox circleHitBox = new CircleHitBox(position, radius, mass);
        level.getCollisionSystem().addCollidable(entity, circleHitBox);
        level.getFactionSystem().addFaction(entity, Faction.Mage);
        level.getCategorySystem().addCategory(entity, Category.Creature);
        HealthHost health = new PlayerHealthHost(new Resistances(startingFireResistance, startingColdResistance,
                startingLightningResistance, startingArcaneResistance, startingBludgeoningResistance,
                startingPiecingResistance, startingSlashingResistance), armorToughness, startingMaxHealth,
                startingMaxHealth);
        level.getHealthSystem().addHealth(entity, health);
        level.getRegenerationHostSystem().addRegeneration(entity, new Regeneration(startingHealthRegen, health));
        level.getCamera().position = position;

        final PlayerMovementControllerHost playerMovementController = new PlayerMovementControllerHost(position, startingSpeed);
        level.getUserInterfaceSystem().addLeftAnalogStickListener(entity, playerMovementController);
        //TODO add host spells
        return entity;
    }
}
