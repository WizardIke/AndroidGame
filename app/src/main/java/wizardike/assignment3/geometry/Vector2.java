package wizardike.assignment3.geometry;

public class Vector2 {
    private float mX;
    private float mY;

    public Vector2(float x, float y) {
        mX = x;
        mY = y;
    }

    public final void setX(float value) {
        mX = value;
    }

    public final float getX() {
        return mX;
    }

    public final void setY(float value) {
        mY = value;
    }

    public final float getY() {
        return mY;
    }
}
