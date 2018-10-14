package wizardike.assignment3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CharacterCreationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CharacterCreationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FrameLayout[] playerClasses;
    private int currentClass = 0;
    private FrameLayout[] playerRaces;
    private int currentRace = 0;

    public CharacterCreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_creation, container, false);

        final EditText nameBox = view.findViewById(R.id.name_input);

        playerClasses = new FrameLayout[]{
                view.findViewById(R.id.class_fire_mage_frame),
                view.findViewById(R.id.class_necromancer_frame)
        };

        for(int i = 0; i < playerClasses.length; ++i) {
            if(i == 0) {
                playerClasses[i].setBackgroundResource(R.drawable.selected_border);
            } else {
                playerClasses[i].setBackgroundResource(R.drawable.dark_border);
            }
            final int i2 = i;
            playerClasses[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerClasses[currentClass].setBackgroundResource(R.drawable.dark_border);
                    playerClasses[i2].setBackgroundResource(R.drawable.selected_border);
                    currentClass = i2;
                }
            });
        }
        playerRaces = new FrameLayout[]{
                view.findViewById(R.id.race_human_frame)
        };

        for(int i = 0; i < playerRaces.length; ++i) {
            if(i == 0) {
                playerRaces[i].setBackgroundResource(R.drawable.selected_border);
            } else {
                playerRaces[i].setBackgroundResource(R.drawable.dark_border);
            }
            final int i2 = i;
            playerRaces[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerRaces[currentRace].setBackgroundResource(R.drawable.dark_border);
                    playerRaces[i2].setBackgroundResource(R.drawable.selected_border);
                    currentRace = i2;
                }
            });
        }

        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startNewGame(nameBox.getText().toString(), currentClass, currentRace);
            }
        });
        return view;
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
        void startNewGame(String name, int playerClass, int race);
    }
}
