package wizardike.assignment3.geometry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Vector2 {
    private float mX;
    private float mY;

    public Vector2(float x, float y) {
        mX = x;
        mY = y;
    }

    public Vector2(DataInputStream save) throws IOException {
        mX = save.readFloat();
        mY = save.readFloat();
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

    @Override
    public int hashCode() {
        final int h1 = Float.floatToIntBits(mX);
        final int h2 = Float.floatToIntBits(mY);
        return h1 ^ ((h2 >>> 16) | (h2 << 16));
    }

    @Override
    public boolean equals(Object other) {
        if(this.getClass() != other.getClass()) return false;
        Vector2 otherVector = (Vector2)other;
        return mX == otherVector.mX && mY == otherVector.mY;
    }

    public void save(DataOutputStream save) throws IOException {
        save.writeFloat(mX);
        save.writeFloat(mY);
    }
}
