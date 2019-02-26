package wizardike.assignment3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;
import android.util.Log;

import wizardike.assignment3.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";

    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final SeekBarPreference seekBar = (SeekBarPreference)findPreference("music_volume");
        if (seekBar != null) {
            seekBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Integer) {
                        final int newVolumeInt = (Integer)newValue;
                        //Convert volume from logarithmic scale to linear
                        float newQuietness = (float)(Math.log(seekBar.getMax() - newVolumeInt) / Math.log(seekBar.getMax()));
                        mListener.setMusicVolume(1 - newQuietness);
                        Log.d(TAG, "Music volume is now " + newVolumeInt);
                        return true;
                    } else {
                        String objType = newValue.getClass().getName();
                        Log.e(TAG, "SeekBarPreference is not a Integer, it is " + objType);
                        return false;
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setMusicVolume(float newVolume);
    }
}
