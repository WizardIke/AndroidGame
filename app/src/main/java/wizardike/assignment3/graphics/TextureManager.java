package wizardike.assignment3.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.SparseArray;

import java.io.Closeable;

import wizardike.assignment3.Engine;
import wizardike.assignment3.geometry.Vector4;

public class TextureManager implements Closeable{
    public static abstract class Request {
        Request next;

        protected abstract void onLoadComplete(Vector4 textureCoordinates);
    }

    private static final class Descriptor {
        final Vector4 textureCoordinates;
        int referenceCount;
        Request requests;

        Descriptor(Vector4 textureCoordinates, Request request) {
            this.textureCoordinates = textureCoordinates;
            referenceCount = 1;
            request.next = null;
            requests = request;
        }

        void addRequest(Request request) {
            request.next = requests;
            requests = request;
        }

        boolean isLoaded() {
            return requests == null;
        }

        public void onLoadComplete() {
            while(requests != null) {
                requests.onLoadComplete(textureCoordinates);
                requests = requests.next;
            }
        }
    }

    private int textureHandle = -1;
    private SparseArray<Descriptor> descriptors = new SparseArray<>();
    private TextureSubAllocator textureSubAllocator = new TextureSubAllocator(4, 2);
    private Engine engine;
    private int textureWidth;
    private int textureHeight;

    TextureManager(Engine engine) {
        this.engine = engine;
        textureWidth = 1024 * 4;
        textureHeight = 1024 * 2;
    }

    int getTextureHandle() {
        return textureHandle;
    }

    public synchronized void loadTexture(int textureId, Request request) {
        int index = descriptors.indexOfKey(textureId);
        if(index >= 0) {
            Descriptor descriptor = descriptors.valueAt(index);
            descriptor.referenceCount += 1;
            if(descriptor.isLoaded()) {
                Vector4 textureCoordinates = descriptor.textureCoordinates;
                request.onLoadComplete(textureCoordinates);
            } else {
                descriptor.addRequest(request);
            }
        } else {
            Vector4 textureCoordinates = textureSubAllocator.allocate();
            Descriptor descriptor = new Descriptor(textureCoordinates, request);
            descriptors.put(textureId, descriptor);
            loadUniqueRequest(textureId, textureCoordinates);
        }
    }

    public synchronized void unloadTexture(int textureId) {
        final int index = descriptors.indexOfKey(textureId);
        final Descriptor descriptor = descriptors.valueAt(index);
        descriptor.referenceCount -= 1;
        if(descriptor.referenceCount == 0){
            final Vector4 textureCoordinates = descriptor.textureCoordinates;
            textureSubAllocator.deallocate(textureCoordinates);
            descriptors.removeAt(index);
        }
    }

    private void loadUniqueRequest(final int textureId, final Vector4 textureCoordinates) {
        engine.getBackgroundWorkManager().execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeResource(
                        engine.getGraphicsManager().getResources(),
                        textureId
                );
                engine.getGraphicsManager().queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //scale texture
                        int textureWidthAndWight = textureWidth / textureSubAllocator.getWidth();
                        //noinspection SuspiciousNameCombination
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, textureWidthAndWight,
                                textureWidthAndWight, true);
                        bitmap.recycle();
                        //copy image to texture
                        int offsetX = (int)(textureCoordinates.getX() * textureWidth);
                        int offsetY = (int)((1.0f - textureCoordinates.getY()) * textureHeight - textureWidthAndWight);
                        if(textureHandle != -1) {
                            GLUtils.texSubImage2D(textureHandle, 0, offsetX, offsetY, resizedBitmap);
                        }
                        resizedBitmap.recycle();
                        synchronized (TextureManager.this) {
                            Descriptor descriptor = descriptors.get(textureId);
                            descriptor.onLoadComplete();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void close() {
        int[] temp = new int[1];
        temp[0] = textureHandle;
        GLES20.glDeleteTextures(1, temp, 0);
    }

    /**
     * Causes all the textures to be reloaded at the same locations.
     * Must be called from the rendering thread.
     */
    public synchronized void reload() {
        createTexture();

        final int textureWidthAndWight = textureWidth / textureSubAllocator.getWidth();
        final Resources resources = engine.getGraphicsManager().getResources();

        final int descriptorsSize = descriptors.size();
        for(int i = 0; i != descriptorsSize; ++i) {
            final int textureId = descriptors.keyAt(i);
            final Descriptor descriptor = descriptors.valueAt(i);
            final Bitmap bitmap = BitmapFactory.decodeResource(resources, textureId);
            //noinspection SuspiciousNameCombination
            final Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, textureWidthAndWight,
                    textureWidthAndWight, true);
            bitmap.recycle();
            final Vector4 textureCoordinates = descriptor.textureCoordinates;
            final int offsetX = (int)(textureCoordinates.getX() * textureWidth);
            final int offsetY = (int)((1.0f - textureCoordinates.getY()) * textureHeight - textureWidthAndWight);
            GLUtils.texSubImage2D(textureHandle, 0, offsetX, offsetY, resizedBitmap);
            resizedBitmap.recycle();
        }
    }

    private void createTexture() {
        int[] temp = new int[1];
        GLES20.glGenTextures(1, temp, 0);
        textureHandle = temp[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB565, textureWidth, textureHeight, 0,
                GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
        //should probably be based on user settings
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }
}
