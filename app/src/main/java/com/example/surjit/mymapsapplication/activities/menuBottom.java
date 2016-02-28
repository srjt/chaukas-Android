package com.example.surjit.mymapsapplication.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.surjit.mymapsapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link menuBottom.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link menuBottom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class menuBottom extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public menuBottom() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment menuBottom.
     */
    // TODO: Rename and change types and number of parameters
    public static menuBottom newInstance(String param1, String param2) {
        menuBottom fragment = new menuBottom();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View frgmnt= inflater.inflate(R.layout.fragment_menu_bottom, container, false);

        Button btnToday=(Button) frgmnt.findViewById(R.id.btnToday);
        btnToday.setOnClickListener(btnClickListener);

        Button btnWeek=(Button) frgmnt.findViewById(R.id.btnWeek);
        btnWeek.setOnClickListener(btnClickListener);

        Button btnMonth=(Button) frgmnt.findViewById(R.id.btnMonth);
        btnMonth.setOnClickListener(btnClickListener);

        Button btnYear=(Button) frgmnt.findViewById(R.id.btnYear);
        btnYear.setOnClickListener(btnClickListener);

        return frgmnt;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int id);
    }

    View.OnClickListener btnClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onButtonPressed(v.getId());
        }
    };
}
