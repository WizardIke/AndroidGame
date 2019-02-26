package wizardike.assignment3.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import wizardike.assignment3.PlayerInfo;
import wizardike.assignment3.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadGameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoadGameFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LoadGameFragment() {
        // Required empty public constructor
    }

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    private class LoadGameAdaptor extends RecyclerView.Adapter<LoadGameAdaptor.LoadGameViewHolder> {
        private File[] saveFiles;
        private RecyclerViewClickListener clickListener;

        LoadGameAdaptor(File[] saveFiles, RecyclerViewClickListener clickListener) {
            this.saveFiles = saveFiles;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public LoadGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.saved_game, parent, false);

            return new LoadGameViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LoadGameViewHolder holder, int position) {
            File saveFile = saveFiles[position];
            try {
                final DataInputStream save = new DataInputStream(new FileInputStream(saveFile));
                save.readInt();
                PlayerInfo playerInfo = new PlayerInfo(save);

                DateFormat format = SimpleDateFormat.getDateTimeInstance();
                String lastPlayedDateTime = format.format(playerInfo.getLastPlayedData());

                Resources res = getResources();
                String[] races = res.getStringArray(R.array.races);
                String[] playerClasses = res.getStringArray(R.array.player_classes);
                String playerClass = playerClasses[playerInfo.getPlayerClass()];
                String race = races[playerInfo.getPlayerRace()];
                String text = getString(R.string.saved_game_description, playerInfo.getName(),
                        playerInfo.getLevel(), race, playerClass, lastPlayedDateTime);
                holder.mTextView.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return saveFiles.length;
        }

        class LoadGameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTextView;
            private LoadGameViewHolder(TextView v) {
                super(v);
                mTextView = v;
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                clickListener.recyclerViewListClicked(v, this.getLayoutPosition());
            }
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_load_game, container, false);
        final RecyclerView saveList = view.findViewById(R.id.recycler_list_container);
        Activity activity = getActivity();
        if(activity != null) {
            final File[] saveFiles = activity.getFilesDir().listFiles();
            final LoadGameAdaptor adaptor = new LoadGameAdaptor(saveFiles, new RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position) {
                    mListener.playGame(Uri.fromFile(saveFiles[position]));
                }
            });
            saveList.setAdapter(adaptor);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        saveList.setLayoutManager(mLayoutManager);

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
        void playGame(Uri location);
    }
}
