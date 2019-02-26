package wizardike.assignment3.assemblies;

import wizardike.assignment3.animation.FireBoltAnimation;
import wizardike.assignment3.category.Category;
import wizardike.assignment3.faction.Faction;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.graphics.PointLight;
import wizardike.assignment3.graphics.Sprite;
import wizardike.assignment3.graphics.SpriteSheets.WalkingSpriteSheet;
import wizardike.assignment3.levels.Level;
import wizardike.assignment3.physics.Collision.CollisionHandlers.ExplodeHost;
import wizardike.assignment3.physics.Collision.TriggeredCircleHitBox;

/**
 * Created by Ike on 30/01/2017.
 */
public class FireBoltParticleHost {
    private static final float mass = 0.5f;

    public static int create(Level level, float posX, float posY, float radius, float dirX, float dirY,
                      float speed, float lifeTime, int caster, float damage, WalkingSpriteSheet spriteSheet) {
        int entity = level.getEngine().getEntityAllocator().allocate();
        Vector2 position = new Vector2(posX, posY);
        level.getPositionHostSystem().addPosition(entity, position);
        Sprite sprite = new Sprite(position, -radius, -radius, 2.0f * radius, 2.0f * radius,
                spriteSheet.xCoordinates[0], spriteSheet.yCoordinates[0], spriteSheet.spriteTextureWidth, spriteSheet.spriteTextureHeight);
        level.getGeometrySystem().addTransparentSprite(entity, sprite);
        level.getLightingSystem().addPointLight(entity, new PointLight(position, 0.0f, 0.0f, 1.5f, radius, 0.8f, 0.5f, 0.2f));
        FireBoltAnimation fireBoltAnimation = new FireBoltAnimation(spriteSheet, 36, sprite);
        level.getFireBoltAnimationSystem().addFireBoltAnimation(entity, fireBoltAnimation);
        ExplodeHost explode = new ExplodeHost(position, damage, lifeTime, dirX * speed, dirY * speed);
        TriggeredCircleHitBox triggeredCircleHitBox = new TriggeredCircleHitBox(position, radius, mass, explode);
        level.getCollisionSystem().add(entity, triggeredCircleHitBox);
        Faction faction = level.getFactionSystem().getFaction(caster);
        if(faction != null) {
            level.getFactionSystem().addFaction(entity, faction);
        }
        level.getCategorySystem().addCategory(entity, Category.Projectile);
        return entity;
    }
}
