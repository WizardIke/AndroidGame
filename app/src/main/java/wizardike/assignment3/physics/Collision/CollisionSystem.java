package wizardike.assignment3.physics.Collision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

import wizardike.assignment3.ComponentStorage;
import wizardike.assignment3.Engine;
import wizardike.assignment3.entity.EntityAllocator;
import wizardike.assignment3.entity.EntityUpdater;
import wizardike.assignment3.geometry.AlignedRectangle;
import wizardike.assignment3.geometry.Circle;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.levels.Level;

import static wizardike.assignment3.geometry.IntersectionTesting.isIntersecting;

public class CollisionSystem {
    private final ComponentStorage<Collidable> collidableComponentStorage;

    public CollisionSystem() {
        collidableComponentStorage = new ComponentStorage<>(Collidable.class);
    }

    public CollisionSystem(DataInputStream save, Engine engine, final EntityUpdater entityUpdater,
                           Vector2[] positionRemappingTable) throws IOException {
        final EntityAllocator entityAllocator = engine.getEntityAllocator();

        final int collidableCount = save.readInt();
        Collidable[] collidables = new Collidable[collidableCount];
        for(int i = 0; i != collidableCount; ++i) {
            final int id = save.readInt();
            collidables[i] = CollidableLoader.load(id, save, positionRemappingTable);
        }
        int[] collidableEntities = new int[collidableCount];
        for(int i = 0; i != collidableCount; ++i) {
            final int oldEntity = save.readInt();
            collidableEntities[i] = entityUpdater.getEntity(oldEntity, entityAllocator);
        }
        collidableComponentStorage = new ComponentStorage<>(Collidable.class, collidableEntities, collidables);
    }

    public void update(Level level) {
        final Collidable[] collidables = collidableComponentStorage.getAllComponents();
        final int collidableCount = collidableComponentStorage.size();
        int[] entities = collidableComponentStorage.getAllEntities();

        for(int i = 0; i < collidableCount; ++i) {
            collidables[i].update(level, entities[i]);
        }

        for(int i = 0; i < collidableCount; ++i) {
            for(int j = i + 1; j < collidableCount; ++j) {
                collidables[i].collide(level, entities[i], collidables[j], entities[j]);
            }
        }
    }

    public void addCollidable(int entity, Collidable collidable) {
        collidableComponentStorage.addComponent(entity, collidable);
    }

    public void removeAll(int entity) {
        collidableComponentStorage.removeComponents(entity);
    }

    public static void collide(CircleHitBox obj1, CircleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public void save(DataOutputStream save, IdentityHashMap<Vector2, Integer> positionRemappingTable) throws IOException {
        final Collidable[] collidables = collidableComponentStorage.getAllComponents();
        final int collidableCount = collidableComponentStorage.size();
        save.writeInt(collidableCount);
        for(int i = 0; i != collidableCount; ++i) {
            Collidable collidable = collidables[i];
            save.writeInt(collidable.getId());
            collidable.save(save, positionRemappingTable);
        }

        int[] entities = collidableComponentStorage.getAllEntities();
        for (int i = 0; i != collidableCount; ++i) {
            save.writeInt(entities[i]);
        }
    }

    public void handleMessage(Level level, DataInputStream networkIn) throws IOException {
        int index = networkIn.readInt();
        collidableComponentStorage.getAllComponents()[index].handleMessage(level, networkIn, collidableComponentStorage.getAllEntities()[index]);
    }

    public int indexOf(int entity, Collidable collidable) {
        return collidableComponentStorage.indexOf(entity, collidable);
    }

    public static void collide(CircleHitBox obj1, AlignedRectangleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public static void collide(AlignedRectangleHitBox obj1, AlignedRectangleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public static void collide(Level level, TriggeredCircleHitBox obj1, int entity1, AlignedRectangleHitBox obj2, int entity2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(level, entity1, obj2, entity2);
        }
    }

    public static void collide(Level level, TriggeredCircleHitBox obj1, int entity1, CircleHitBox obj2, int entity2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(level, entity1, obj2, entity2);
        }
    }

    public static void collide(Level level, TriggeredCircleHitBox obj1, int entity1, TriggeredCircleHitBox obj2, int entity2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(level, entity1, obj2, entity2);
            obj2.onCollision(level, entity2, obj1, entity1);
        }
    }

    private static boolean isColliding(Circle circle1, Circle circle2) {
        return (circle1.getX() - circle2.getX()) * (circle1.getX() - circle2.getX()) +
                (circle1.getY() - circle2.getY()) * (circle1.getY() - circle2.getY()) <
                (circle1.getRadius() + circle2.getRadius()) * (circle1.getRadius() + circle2.getRadius());
    }

    private static boolean isColliding(AlignedRectangle alignedRectangle1, AlignedRectangle alignedRectangle2) {
        return (alignedRectangle1.getX() > alignedRectangle2.getX() &&
                alignedRectangle1.getX() < alignedRectangle2.getX() + alignedRectangle2.getWidth() ||
                alignedRectangle2.getX() >= alignedRectangle1.getX() &&
                        alignedRectangle2.getX() < alignedRectangle1.getX() + alignedRectangle1.getWidth()) &&
                (alignedRectangle1.getY() > alignedRectangle2.getY() &&
                alignedRectangle1.getY() < alignedRectangle2.getY() + alignedRectangle2.getHeight() ||
                        alignedRectangle2.getY() >= alignedRectangle1.getY() &&
                                alignedRectangle2.getY() < alignedRectangle1.getY() + alignedRectangle1.getHeight());
    }

    private static boolean isColliding(Circle circle, AlignedRectangle rect)
    {
        float rectHalfWidth = rect.getWidth() * 0.5f;
        float rectHalfHeight = rect.getHeight() * 0.5f;
        return isIntersecting(circle.getX(), circle.getY(), circle.getRadius(),
                rect.getX() + rectHalfWidth, rect.getY() + rectHalfHeight,
                rectHalfWidth, rectHalfHeight);
    }

    private static Vector2 getOverlap(Circle circle1, Circle circle2){
        float directionX = circle1.getX() - circle2.getX();
        float directionY = circle1.getY() - circle2.getY();
        if(directionX == 0.0f && directionY == 0.0f){
            directionX = 0.0001f;
        }
        float distance = (float)Math.sqrt(directionX * directionX + directionY * directionY);

        directionX /= distance;
        directionY /= distance;
        distance = circle1.getRadius() + circle2.getRadius() - distance;
        return new Vector2(directionX * distance, directionY * distance);
    }

    private static Vector2 getOverlap(Circle circle, AlignedRectangle rectangle) {
        float distanceX, distanceY;
        float rectangleMaxX = rectangle.getX() + rectangle.getWidth();
        float rectangleMaxY = rectangle.getY() + rectangle.getHeight();
        if(circle.getY() > rectangle.getY() && circle.getY() < rectangleMaxY) {
            distanceX = ((rectangle.getX() + rectangleMaxX) * 0.5f) - circle.getX();
            if(distanceX > 0.0f) {
                distanceX -= ((( rectangleMaxX - rectangle.getX()) * 0.5f) + circle.getRadius());
            }
            else {
                distanceX += ((( rectangleMaxX - rectangle.getX()) * 0.5f) + circle.getRadius());
            }
            distanceY = 0.0f;
        }
        else if(circle.getX() > rectangle.getX() && circle.getX() < rectangleMaxX){
            distanceY = ((rectangle.getY() + rectangleMaxY) * 0.5f) - circle.getY();
            if(distanceY > 0.0f) {
                distanceY -= (((rectangleMaxY - rectangle.getY()) * 0.5f) + circle.getRadius());
            }
            else {
                distanceY += (((rectangleMaxY - rectangle.getY()) * 0.5f) + circle.getRadius());
            }
            distanceX = 0.0f;
        }
        else {
            if(circle.getX() > rectangleMaxX && circle.getY() > rectangleMaxY) {
                float dirX = circle.getX() - rectangleMaxX;
                float dirY = circle.getY() - rectangleMaxY;
                float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);
                dirX /= distance;
                dirY /= distance;
                distance = circle.getRadius() - distance;
                distanceX = dirX * distance;
                distanceY = dirY * distance;
            }
            else if(circle.getX() > rectangleMaxX && circle.getY() < rectangle.getY()){
                float dirX = circle.getX() - rectangleMaxX;
                float dirY = circle.getY() - rectangle.getY();
                float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);
                dirX /= distance;
                dirY /= distance;
                distance = circle.getRadius() - distance;
                distanceX = dirX * distance;
                distanceY = dirY * distance;
            }
            else if(circle.getY() > rectangleMaxY){
                float dirX = circle.getX() - rectangle.getX();
                float dirY = circle.getY() - rectangleMaxY;
                float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);
                dirX /= distance;
                dirY /= distance;
                distance = circle.getRadius() - distance;
                distanceX = dirX * distance;
                distanceY = dirY * distance;
            }
            else{
                float dirX = circle.getX() - rectangle.getX();
                float dirY = circle.getY() - rectangle.getY();
                float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);
                dirX /= distance;
                dirY /= distance;
                distance = circle.getRadius() - distance;
                distanceX = dirX * distance;
                distanceY = dirY * distance;
            }
        }
        return new Vector2(distanceX, distanceY);
    }

    private static Vector2 getOverlap(AlignedRectangle r1, AlignedRectangle r2) {
        float displacementX;
        if(r1.getX() < r2.getX()) {
            displacementX = (r1.getX() + r1.getWidth()) - r2.getX();
        } else {
            displacementX = r1.getX() - (r2.getX() + r2.getWidth());
        }
        float displacementY;
        if(r1.getY() < r2.getY()) {
            displacementY = (r1.getY() + r1.getHeight()) - r2.getY();
        } else {
            displacementY = r1.getY() - (r2.getY() + r2.getHeight());
        }
        if(displacementX == 0.0f && displacementY == 0.0f){
            displacementX = 0.0001f;
        }
        return new Vector2(displacementX, displacementY);
    }

    private static void resolveOverlap(Vector2 overlap, Vector2 position1, float mass1,
                                       Vector2 position2, float mass2) {
        float distanceX = overlap.getX();
        float distanceY = overlap.getY();
        if (mass1 != Float.POSITIVE_INFINITY) {
            distanceX *= (mass1 / (mass1 * mass2));
            distanceY *= (mass1 / (mass1 * mass2));
        }
        position2.setX(position2.getX() - distanceX);
        position2.setY(position2.getY() - distanceY);

        distanceX = overlap.getX();
        distanceY = overlap.getY();
        if (mass2 != Float.POSITIVE_INFINITY) {
            distanceX *= (mass2 / (mass2 * mass1));
            distanceY *= (mass2 / (mass2 * mass1));
        }
        position1.setX(position1.getX() + distanceX);
        position1.setY(position1.getY() + distanceY);
    }
}
