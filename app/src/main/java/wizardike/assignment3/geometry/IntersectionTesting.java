package wizardike.assignment3.geometry;

public class IntersectionTesting {
    public static boolean isIntersecting(float circleX, float circleY, float circleRadius,
                                         float rectangleCentreX, float rectangleCentreY,
                                         float rectangleHalfWidth, float rectangleHalfHeight){
        float circleDistX = Math.abs(circleX - rectangleCentreX);
        float distX = rectangleHalfWidth + circleRadius;
        if (circleDistX > distX)
            return false;
        float circleDistY = Math.abs(circleY - rectangleCentreY);
        float distY = rectangleHalfHeight + circleRadius;
        if (circleDistY > distY)
            return false;
        if (circleDistX <= rectangleHalfWidth || circleDistY <= rectangleHalfHeight)
            return true;
        float xCornerDistSq = circleDistX - rectangleHalfWidth;
        xCornerDistSq *= xCornerDistSq;
        float yCornerDistSq = circleDistY - rectangleHalfHeight;
        yCornerDistSq *= yCornerDistSq;
        float maxCornerDistSq = circleRadius * circleRadius;
        return xCornerDistSq + yCornerDistSq <= maxCornerDistSq;
    }
}
