package wizardike.assignment3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class AnalogStick extends AppCompatImageView {
    public interface OnRotationListener {
        void start(float directionX, float directionY);
        void move(float directionX, float directionY);
        void stop(float directionX, float directionY);
    }

    private ArrayList<OnRotationListener> listeners;
    private boolean validTouch;
    private float imageAngleInDegrees;
    private float previousAngleInDegrees;

    public AnalogStick(Context context) {
        super(context);
        init();
    }

    public AnalogStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        imageAngleInDegrees = 0.0f;
        previousAngleInDegrees = 0.0f;
        validTouch = false;
    }

    public void setOnRotationListeners(ArrayList<OnRotationListener> listeners) {
        this.listeners = listeners;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        float halfWidth = getWidth() / 2.0f;
        float halfHeight = getHeight() / 2.0f;

        float directionX = x - halfWidth;
        float directionY = y - halfHeight;

        float angle = (float) Math.toDegrees(Math.atan2(directionX, -directionY));
        float distanceSquared = directionX * directionX + directionY * directionY;
        float distance = (float)Math.sqrt(distanceSquared);
        directionX /= distance;
        directionY /= distance;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(distanceSquared <= halfWidth * halfWidth) {
                    previousAngleInDegrees = angle;
                    validTouch = true;
                    for(OnRotationListener listener : listeners) {
                        listener.start(directionX, directionY);
                    }
                } else {
                    validTouch = false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(validTouch) {
                    float angleChange = angle - previousAngleInDegrees;
                    imageAngleInDegrees += angleChange;
                    setRotation(imageAngleInDegrees);
                    for(OnRotationListener listener : listeners) {
                        listener.move(directionX, directionY);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(validTouch) {
                    float angleChange = angle - previousAngleInDegrees;
                    imageAngleInDegrees += angleChange;
                    setRotation(imageAngleInDegrees);
                    for(OnRotationListener listener : listeners) {
                        listener.stop(directionX, directionY);
                    }
                }
                validTouch = false;
                break;
            }
        }
        return true;
    }
}
