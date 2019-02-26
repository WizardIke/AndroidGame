package wizardike.assignment3.geometry;

public class Vector4 {
    private float x, y, z, w;

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    @Override
    public int hashCode() {
        final int h1 = Float.floatToIntBits(x);
        final int h2 = Float.floatToIntBits(y);
        final int h3 = Float.floatToIntBits(z);
        final int h4 = Float.floatToIntBits(w);

        int h = 1;
        h = 31 * h + h1;
        h = 31 * h + h2;
        h = 31 * h + h3;
        h = 31 * h + h4;
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    @Override
    public boolean equals(Object other) {
        if(this.getClass() != other.getClass()) return false;
        Vector4 otherVector = (Vector4)other;
        return x == otherVector.x && y == otherVector.y && z == otherVector.z && w == otherVector.w;
    }
}
