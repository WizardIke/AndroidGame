package wizardike.assignment3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainMenuFragment extends Fragment {
    private static final String TAG = "MainMenuFragment";
    private OnFragmentInteractionListener mListener;

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        final Button continueButton = view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                String lastSavedGame = settings.getString("last_saved_game", null);
                if(lastSavedGame != null) {
                    mListener.playGame(Uri.parse(lastSavedGame));
                } else {
                    Log.e(TAG, "Missing last save file location in shared preferences");
                    v.setVisibility(View.GONE);
                }
            }
        });

        Button startButton = view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                if(fm != null) {
                    CharacterCreationFragment fragment = new CharacterCreationFragment();
                    fm.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        Button loadButton = view.findViewById(R.id.load_button);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if(fm != null) {
                    LoadGameFragment fragment = new LoadGameFragment();
                    fm.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        Button settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if(fm != null) {
                    SettingsFragment fragment = new SettingsFragment();
                    fm.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        Button quitButton = view.findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.quit();
            }
        });

        if(Build.VERSION.SDK_INT >= 21) {
            continueButton.setBackgroundTintList(ColorStateList.valueOf(0x78222222));
            startButton.setBackgroundTintList(ColorStateList.valueOf(0x78222222));
            loadButton.setBackgroundTintList(ColorStateList.valueOf(0x78222222));
            settingsButton.setBackgroundTintList(ColorStateList.valueOf(0x78222222));
            quitButton.setBackgroundTintList(ColorStateList.valueOf(0x78222222));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final View view = getView();
        if(view != null) {
            Button continueButton = view.findViewById(R.id.continue_button);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            if(settings.contains("last_saved_game")) {
                continueButton.setVisibility(View.VISIBLE);
            } else {
                continueButton.setVisibility(View.GONE);
            }

            Button loadButton = view.findViewById(R.id.load_button);
            Activity activity = getActivity();
            if(activity != null) {
                File dir = activity.getFilesDir();
                if(dir.list().length == 0){
                    loadButton.setVisibility(View.GONE);
                } else {
                    loadButton.setVisibility(View.VISIBLE);
                }
            }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void quit();
        void playGame(Uri location);
    }
}
