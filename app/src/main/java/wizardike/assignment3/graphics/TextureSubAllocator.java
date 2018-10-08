package wizardike.assignment3.graphics;

import wizardike.assignment3.geometry.Vector4;

public class TextureSubAllocator {
    private int freeCount;
    private int widthAndHeight;
    private int[] freeList;

    TextureSubAllocator(int widthAndHeight) {
        this.widthAndHeight = widthAndHeight;
        this.freeCount = widthAndHeight * widthAndHeight;
        freeList = new int[freeCount];
        for(int i = 0; i != freeCount; ++i) {
            freeList[i] = i;
        }
    }

    public Vector4 allocate() {
        --freeCount;
        final int index = freeList[freeCount];
        float offsetX = (float)(index % widthAndHeight) / (0.5f * widthAndHeight) - 1.0f;
        float offsetY = (float)(index / widthAndHeight) / (0.5f * widthAndHeight) - 1.0f;
        float size = 2.0f / widthAndHeight;

        return new Vector4(offsetX, offsetY, size, size);
    }
    public void deallocate(Vector4 textureCoordinates) {
        final int index = (int)(((textureCoordinates.getX() + 1.0f) * (0.5f * widthAndHeight))
                + ((textureCoordinates.getY() + 1.0f) * (0.5f * widthAndHeight) * widthAndHeight));
        freeList[freeCount] = index;
        ++freeCount;
    }

    public int getWidthAndHeight() {
        return widthAndHeight;
    }
}
