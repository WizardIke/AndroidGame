package wizardike.assignment3.talents;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wizardike.assignment3.AnalogStick;
import wizardike.assignment3.R;
import wizardike.assignment3.fragments.CharacterCreationFragment;

public class UserInterface extends Fragment {
    public boolean leftStickDown = false;
    public float leftDirectionX = 0.0f;
    public float leftDirectionY = 0.0f;
    public boolean rightStickDown = false;
    public float rightDirectionX = 0.0f;
    public float rightDirectionY = 0.0f;

    public UserInterface() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_interface, container, false);
        AnalogStick leftStick = view.findViewById(R.id.leftAnalogStick);
        leftStick.setOnRotationListener(new AnalogStick.OnRotationListener() {
            @Override
            public void start(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    leftStickDown = true;
                    leftDirectionX = directionX;
                    leftDirectionY = directionY;
                }
            }

            @Override
            public void move(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    leftDirectionX = directionX;
                    leftDirectionY = directionY;
                }
            }

            @Override
            public void stop(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    leftStickDown = false;
                }
            }
        });
        AnalogStick rightStick = view.findViewById(R.id.rightAnalogStick);
        rightStick.setOnRotationListener(new AnalogStick.OnRotationListener() {
            @Override
            public void start(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    rightStickDown = true;
                    rightDirectionX = directionX;
                    rightDirectionY = directionY;
                }
            }

            @Override
            public void move(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    rightDirectionX = directionX;
                    rightDirectionY = directionY;
                }
            }

            @Override
            public void stop(float directionX, float directionY) {
                synchronized (UserInterface.this) {
                    rightStickDown = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Activity activity = getActivity();
        if(activity != null) {
            if(getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
            }
        }
    }
}
