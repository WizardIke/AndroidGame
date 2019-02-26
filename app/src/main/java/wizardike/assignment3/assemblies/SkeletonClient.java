package wizardike.assignment3.assemblies;

import wizardike.assignment3.ai.BasicAIController;
import wizardike.assignment3.animation.WalkingAnimation;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.health.SkeletonHealth;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CollisionHandlers.BiteClient;
import wizardike.assignment3.physics.Collision.TriggeredCircleHitBox;
import wizardike.assignment3.physics.movement.Movement;

/**
 * Created by Ike on 30/01/2017.
 */

public class SkeletonClient {
    private static final float animationLength = 0.4f;
    private static final float radius = 0.108f;
    private static final float minMass = 3.0f;
    private static final float massRange = 4.0f;
    private static final float biteTime = 1.0f;

    public static int create(Level level, int master, float health, float maxHealth,
                      float posX, float posY, float speed, WalkingSpriteSheet spriteSheet) {
        int entity = level.getEngine().getEntityAllocator().allocate();
        level.getHealthSystem().addHealth(entity, new SkeletonHealth(maxHealth, health));
        Vector2 position = new Vector2(posX, posY);
        level.getPositionSystem().addPosition(entity, position);
        BiteClient bite = new BiteClient(biteTime);
        level.getCollisionSystem().addCollidable(entity, new TriggeredCircleHitBox(position, radius,
                (float)(Math.random() * massRange + minMass), bite));
        level.getBasicAIControllerSystem().addBasicAIController(entity, new BasicAIController(speed));
        Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                spriteSheet.xCoordinates[0], spriteSheet.yCoordinates[0], spriteSheet.spriteTextureWidth, spriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addSprite(entity, sprite);
        Movement movement = new Movement(position);
        level.getMovementSystem().addMovement(entity, movement);
        WalkingAnimation walkingAnimation = new WalkingAnimation(spriteSheet, movement, sprite, animationLength);
        level.getWalkingAnimationSystem().addWalkingAnimation(entity, walkingAnimation);
        Faction faction = level.getFactionSystem().getFaction(master);
        if(faction != null) {
            level.getFactionSystem().addFaction(entity, faction);
        }
        level.getCategorySystem().addCategory(entity, Category.Creature);
        return entity;
    }
}
