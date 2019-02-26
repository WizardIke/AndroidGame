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
        float offsetX = (float)(index % width) / (float)width;
        float offsetY = (float)(index / width) / (float)height;
        float sizeX = 1.0f / width;
        float sizeY = 1.0f / height;

        return new Vector4(offsetX, 1.0f - offsetY - sizeY, sizeX, sizeY);
    }
    public void deallocate(Vector4 textureCoordinates) {
        float sizeY = 1.0f / height;
        float offsetY = 1.0f - (textureCoordinates.getY() + sizeY);
        final int index = (int)((textureCoordinates.getX() * (float)width)
                + (offsetY * width * width));
        freeList[freeCount] = index;
        ++freeCount;
    }

    public int getWidth() {
        return width;
    }
}
