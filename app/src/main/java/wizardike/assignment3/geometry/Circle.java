package wizardike.assignment3.geometry;

public class Circle {
    private Vector2 mPosition;
    private float mRadius;

    public Circle(float x, float y, float radius) {
        mPosition = new Vector2(x, y);
        mRadius = radius;
    }

    public Circle(Vector2 position, float radius) {
        mPosition = position;
        mRadius = radius;
    }

    public final float getX() {
        return mPosition.getX();
    }

    public final float getY() {
        return mPosition.getY();
    }

    public final Vector2 getPosition() {
        return mPosition;
    }

    public final float getRadius() {
        return mRadius;
    }
}
