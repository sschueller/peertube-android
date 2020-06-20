package net.schueller.peertube.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.SelectServerActivity;
import net.schueller.peertube.activity.ServerAddressBookActivity;

import static android.app.Activity.RESULT_OK;


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

        Button pickServerUrl = mView.findViewById(R.id.pickServerUrl);
        pickServerUrl.setOnClickListener(view -> {
            Intent intentServer = new Intent(getActivity(), SelectServerActivity.class);
            this.startActivityForResult(intentServer, 1);
        });

        return mView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                String serverUrlTest = data.getStringExtra("serverUrl");
                //Log.d(TAG, "serverUrl " + serverUrlTest);
                EditText serverUrl = mView.findViewById(R.id.serverUrl);
                serverUrl.setText(serverUrlTest);

                EditText serverLabel = mView.findViewById(R.id.serverLabel);
                if ("".equals(serverLabel.getText().toString())) {
                    serverLabel.setText(data.getStringExtra("serverName"));
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
