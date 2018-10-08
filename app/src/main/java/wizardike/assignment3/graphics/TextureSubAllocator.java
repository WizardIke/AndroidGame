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
        float offsetX = (float)(index % width) / (0.5f * width) - 1.0f;
        float offsetY = (float)(index / width) / (0.5f * height) - 1.0f;
        float sizeX = 2.0f / width;
        float sizeY = 2.0f / height;

        return new Vector4(offsetX, offsetY, sizeX, sizeY);
    }
    public void deallocate(Vector4 textureCoordinates) {
        final int index = (int)(((textureCoordinates.getX() + 1.0f) * (0.5f * width))
                + ((textureCoordinates.getY() + 1.0f) * (0.5f * height) * width));
        freeList[freeCount] = index;
        ++freeCount;
    }
}
