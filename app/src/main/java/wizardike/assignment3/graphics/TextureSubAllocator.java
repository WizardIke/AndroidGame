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
        float offsetX = (float)(index % widthAndHeight) / widthAndHeight;
        float offsetY = (float)(index / widthAndHeight) / widthAndHeight;
        float size = 1.0f / widthAndHeight;

        return new Vector4(offsetX, 1.0f - offsetY - size, size, size);
    }
    public void deallocate(Vector4 textureCoordinates) {
        float size = 1.0f / widthAndHeight;
        float offsetY = 1.0f - (textureCoordinates.getY() + size);
        final int index = (int)((textureCoordinates.getX() * widthAndHeight)
                + (offsetY * widthAndHeight * widthAndHeight));
        freeList[freeCount] = index;
        ++freeCount;
    }

    public int getWidthAndHeight() {
        return widthAndHeight;
    }
}
