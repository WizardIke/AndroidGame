package wizardike.assignment3.physics;

import java.util.ArrayList;

import wizardike.assignment3.geometry.AlignedRectangle;
import wizardike.assignment3.geometry.Circle;
import wizardike.assignment3.graphics.UpdateListener;
import wizardike.assignment3.geometry.Vector2;
import wizardike.assignment3.Engine;

import static wizardike.assignment3.geometry.IntersectionTesting.isIntersecting;

public class PhysicsSystem implements UpdateListener{
    private Engine world;
    private ArrayList<Collidable> collidables = new ArrayList<>();

    public PhysicsSystem(Engine world) {
        this.world = world;
    }

    @Override
    public void update() {
        //note: some collisions might be missed if a Collidable is removed from collidables during this method
        for(int i = 0; i < collidables.size(); ++i) {
            for(int j = i + 1; j < collidables.size(); ++j) {
                collidables.get(i).collide(world, collidables.get(j));
            }
        }
    }

    public void add(Collidable collidable) {
        collidables.add(collidable);
    }

    public void remove(Collidable collidable) {
        int index = collidables.indexOf(collidable);
        collidables.set(index, collidables.get(collidables.size() - 1));
        collidables.remove(collidables.size() - 1);
    }

    public static void collide(Engine world, CircleHitBox obj1, CircleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public static void collide(Engine world, CircleHitBox obj1, AlignedRectangleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public static void collide(Engine world, AlignedRectangleHitBox obj1, AlignedRectangleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
        }
    }

    public static void collide(Engine world, TriggeredCircleHitBox obj1, AlignedRectangleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(world, obj2);
        }
    }

    public static void collide(Engine world, TriggeredCircleHitBox obj1, CircleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(world, obj2);
        }
    }

    public static void collide(Engine world, TriggeredCircleHitBox obj1, TriggeredCircleHitBox obj2) {
        if(isColliding(obj1, obj2)) {
            Vector2 displacement = getOverlap(obj1, obj2);
            resolveOverlap(displacement, obj1.getPosition(), obj1.getMass(), obj2.getPosition(), obj2.getMass());
            obj1.onCollision(world, obj2);
            obj2.onCollision(world, obj2);
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
                alignedRectangle1.getY() < alignedRectangle2.getY() + alignedRectangle2.getHight() ||
                        alignedRectangle2.getY() >= alignedRectangle1.getY() &&
                                alignedRectangle2.getY() < alignedRectangle1.getY() + alignedRectangle1.getHight());
    }

    private static boolean isColliding(Circle circle, AlignedRectangle rect)
    {
        float rectHalfWidth = rect.getWidth() * 0.5f;
        float rectHalfHeight = rect.getHight() * 0.5f;
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
        float rectangleMaxY = rectangle.getY() + rectangle.getHight();
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
            displacementY = (r1.getY() + r1.getHight()) - r2.getY();
        } else {
            displacementY = r1.getY() - (r2.getY() + r2.getHight());
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
