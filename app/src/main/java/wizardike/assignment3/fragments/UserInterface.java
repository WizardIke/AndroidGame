package wizardike.assignment3.fragments;

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

public class UserInterface extends Fragment {
    private AnalogStick leftAnalogStick = null;
    private AnalogStick.OnRotationListener leftAnalogStickOnRotationListener;
    private AnalogStick rightAnalogStick = null;
    private AnalogStick.OnRotationListener rightAnalogStickOnRotationListener;

    public UserInterface() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_interface, container, false);
        leftAnalogStick = view.findViewById(R.id.leftAnalogStick);
        leftAnalogStick.setOnRotationListener(leftAnalogStickOnRotationListener);
        rightAnalogStick = view.findViewById(R.id.rightAnalogStick);
        rightAnalogStick.setOnRotationListener(rightAnalogStickOnRotationListener);
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

    /**
     * Must be called on ui thread
     * @param onRotationListener the listener to set
     */
    public void setLeftAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        leftAnalogStickOnRotationListener = onRotationListener;
        if(leftAnalogStick != null) {
            leftAnalogStick.setOnRotationListener(onRotationListener);
        }
    }

    /**
     * Must be called on ui thread
     * @param onRotationListener the listener to set
     */
    public void setRightAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        rightAnalogStickOnRotationListener = onRotationListener;
        if(rightAnalogStick != null) {
            rightAnalogStick.setOnRotationListener(onRotationListener);
        }
    }
}
