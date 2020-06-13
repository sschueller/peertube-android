package net.schueller.peertube.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.ServerAddressBookActivity;


public class AddServerFragment extends Fragment {

    public static final String TAG = "AddServerFragment";

    private OnFragmentInteractionListener mListener;

    private View mView;

    public AddServerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_add_server, container, false);

        // bind button click
        Button addServerButton = mView.findViewById(R.id.addServerButton);
        addServerButton.setOnClickListener(view -> {
            Activity act = getActivity();
            if (act instanceof ServerAddressBookActivity) {
                ((ServerAddressBookActivity) act).addServer(mView);
            }
        });

        Button testServerButton = mView.findViewById(R.id.testServerButton);
        testServerButton.setOnClickListener(view -> {
            Activity act = getActivity();
            if (act instanceof ServerAddressBookActivity) {
                ((ServerAddressBookActivity) act).testServer();
            }
        });

        return mView;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
