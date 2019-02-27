package wizardike.assignment3.graphics;

import wizardike.assignment3.geometry.Vector4;

public class TextureSubAllocator {
    private int freeCount;
    private int width;
    private int height;
    private int[] freeList;

    TextureSubAllocator(int width, int height) {
        this.width = width;
        this.height = height;
        this.freeCount = width * height;
        freeList = new int[freeCount];
        for(int i = 0; i != freeCount; ++i) {
            freeList[i] = i;
        }
    }

    public Vector4 allocate() {
        --freeCount;
        final int index = freeList[freeCount];
        final float sizeX = 1.0f / width;
        final float sizeY = 1.0f / height;
        float offsetX = (float)(index % width) * sizeX;
        float offsetY = (float)(index / width) * sizeY;

        return new Vector4(offsetX, offsetY, sizeX, sizeY);
    }

    public void deallocate(Vector4 textureCoordinates) {
        final int offsetX = (int)(textureCoordinates.getX() * (float)width);
        final int offsetY = (int)(textureCoordinates.getY() * (float)height);
        final int index = offsetX + width * offsetY;
        freeList[freeCount] = index;
        ++freeCount;
    }

    public int getWidth() {
        return width;
    }
}
