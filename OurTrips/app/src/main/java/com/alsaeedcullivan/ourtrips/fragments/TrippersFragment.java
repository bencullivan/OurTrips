package com.alsaeedcullivan.ourtrips.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alsaeedcullivan.ourtrips.R;


public class TrippersFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "TrippersFragment";


    public TrippersFragment() {
        // Required empty public constructor
    }

    public static TrippersFragment newInstance() {
        return new TrippersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trippers, container, false);
    }

    @Override
    public void onClick(View v) {

    }
}
