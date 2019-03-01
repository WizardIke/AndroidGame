package wizardike.assignment3.userInterface;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import wizardike.assignment3.AnalogStick;
import wizardike.assignment3.R;

public class UserInterface extends Fragment {
    private ArrayList<AnalogStick.OnRotationListener> leftAnalogStickOnRotationListeners = new ArrayList<>();
    private ArrayList<AnalogStick.OnRotationListener> rightAnalogStickOnRotationListeners = new ArrayList<>();

    public UserInterface() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_interface, container, false);
        AnalogStick leftAnalogStick = view.findViewById(R.id.leftAnalogStick);
        leftAnalogStick.setOnRotationListeners(leftAnalogStickOnRotationListeners);
        AnalogStick rightAnalogStick = view.findViewById(R.id.rightAnalogStick);
        rightAnalogStick.setOnRotationListeners(rightAnalogStickOnRotationListeners);
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
     * @param onRotationListener the listener to add
     */
    public void addLeftAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        leftAnalogStickOnRotationListeners.add(onRotationListener);
    }

    public void removeLeftAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        leftAnalogStickOnRotationListeners.remove(onRotationListener);
    }

    /**
     * Must be called on ui thread
     * @param onRotationListener the listener to add
     */
    public void addRightAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        rightAnalogStickOnRotationListeners.add(onRotationListener);
    }

    public void removeRightAnalogStickOnRotationListener(AnalogStick.OnRotationListener onRotationListener) {
        rightAnalogStickOnRotationListeners.remove(onRotationListener);
    }
}
