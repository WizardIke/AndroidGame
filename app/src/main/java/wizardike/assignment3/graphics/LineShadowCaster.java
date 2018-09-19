package wizardike.assignment3.graphics;

public class LineShadowCaster {
    public float startX, startY;
    public float endX, endY; //must be point with greater y
    public float height;
    public float ambientLightMultiplier;

    public LineShadowCaster(float startX, float startY, float endX, float endY, float height,
                            float ambientLightMultiplier) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.height = height;
        this.ambientLightMultiplier = ambientLightMultiplier;
    }
}
