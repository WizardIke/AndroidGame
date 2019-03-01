package wizardike.assignment3.assemblies;

import wizardike.assignment3.ai.BasicAIControllerHost;
import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.health.SkeletonHealthHost;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CollisionHandlers.Bite;
import wizardike.assignment3.physics.Collision.TriggeredCircleHitBox;

/**
 * Created by Ike on 30/01/2017.
 */
public class SkeletonHost {
    private static final float biteTime = 1.0f;
    private static final float biteDamage = 10.0f;
    private static final float minMass = 3.0f;
    private static final float massRange = 4.0f;
    private static final float radius = 0.108f;
    private static final float animationLength = 0.4f;

    public static int create(Level level, int master, float health, float maxHealth,
                      float posX, float posY, float speed, WalkingSpriteSheet spriteSheet) {
        final int entity = level.getEngine().getEntityAllocator().allocate();
        level.getHealthSystem().addHealth(entity, new SkeletonHealthHost(maxHealth, health));
        final Vector2 position = new Vector2(posX, posY);
        level.getPositionSystem().addPosition(entity, position);
        final Bite bite = new Bite(biteTime, biteDamage);
        level.getCollisionSystem().addCollidable(entity, new TriggeredCircleHitBox(position, radius,
                (float)(Math.random() * massRange + minMass), bite));
        level.getBasicAIControllerHostSystem().addBasicAIController(entity, new BasicAIControllerHost(position, speed));
        final Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                spriteSheet.xCoordinates[0], spriteSheet.yCoordinates[0], spriteSheet.spriteTextureWidth, spriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addSprite(entity, sprite);
        final WalkingAnimation walkingAnimation = new WalkingAnimation(spriteSheet, sprite, animationLength);
        level.getWalkingAnimationSystem().addWalkingAnimation(entity, walkingAnimation);
        final Faction faction = level.getFactionSystem().getFaction(master);
        if(faction != null) {
            level.getFactionSystem().addFaction(entity, faction);
        }
        level.getCategorySystem().addCategory(entity, Category.Creature);
        return entity;
    }
}
