package wizardike.assignment3.geometry;

public class AlignedRectangle {
    private Vector2 mPosition; //upper left corner
    private Vector2 mSize;

    public AlignedRectangle(float x, float y, float width, float height) {
        mPosition = new Vector2(x, y);
        mSize = new Vector2(width, height);
    }

    public AlignedRectangle(Vector2 position, float width, float height) {
        mPosition = position;
        mSize = new Vector2(width, height);
    }

    public float getX() {
        return mPosition.getX();
    }

    public float getY() {
        return mPosition.getY();
    }

    public Vector2 getPosition() {
        return mPosition;
    }

    public float getWidth() {
        return mSize.getX();
    }

    public float getHeight() {
        return mSize.getY();
    }
}
