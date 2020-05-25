package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.EditSummaryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.utils.Const;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment implements View.OnClickListener {

    // text widgets
    private TextView mTitle, mStartDate, mEndDate, mOverview;
    private Button mEdit;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Const.TAG, "onCreate: summary ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(Const.TAG, "onViewCreated: ");

        if (getActivity() == null) return;

        // get widget references
        mTitle = view.findViewById(R.id.summary_title);
        mStartDate = view.findViewById(R.id.sum_start_date_text);
        mEndDate = view.findViewById(R.id.sum_end_date_text);
        mOverview = view.findViewById(R.id.sum_overview);

        // set up the content in the widgets
        String title = ((TripActivity)getActivity()).getTripTitle();
        mTitle.setText(title);
        String start = "Start Date: " + ((TripActivity)getActivity()).getStartDate();
        mStartDate.setText(start);
        String end = "End Date: " + ((TripActivity)getActivity()).getEndDate();
        mEndDate.setText(end);
        String over = "Overview: " + ((TripActivity)getActivity()).getOverview();
        mOverview.setText(over);

        // set up the button
        mEdit = view.findViewById(R.id.sum_edit_button);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripActivity a = (TripActivity) getActivity();
                if (a == null) return;

                // send the user to edit summary activity
                Intent intent = new Intent(getActivity(), EditSummaryActivity.class);
                intent.putExtra(Const.TRIP_ID_TAG, a.getTripId());
                intent.putExtra(Const.TRIP_TITLE_TAG, a.getTripTitle());
                intent.putExtra(Const.TRIP_START_TAG, a.getStartDate());
                intent.putExtra(Const.TRIP_END_TAG, a.getEndDate());
                intent.putExtra(Const.TRIP_OVER_TAG, a.getOverview());
                startActivity(intent);

                // finish this activity
                if (getActivity() != null) getActivity().finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Const.TAG, "onStart: summary ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Const.TAG, "onresume: summary ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Const.TAG, "onPause: sum ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(Const.TAG, "onStop: sum");
    }
}
