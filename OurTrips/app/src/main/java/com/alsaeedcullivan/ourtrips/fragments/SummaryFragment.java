package com.alsaeedcullivan.ourtrips.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.EditSummaryActivity;
import com.alsaeedcullivan.ourtrips.MapsActivity;
import com.alsaeedcullivan.ourtrips.MatchActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.Place;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    private Button addTripper;

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
        // text widgets
        TextView title1 = view.findViewById(R.id.summary_title);
        TextView startDate = view.findViewById(R.id.sum_start_date_text);
        TextView endDate = view.findViewById(R.id.sum_end_date_text);
        TextView overView = view.findViewById(R.id.sum_overview);

        // set up the content in the widgets
        String title = ((TripActivity)getActivity()).getTripTitle();
        title1.setText(title);
        String start = "Start Date: " + ((TripActivity)getActivity()).getStartDate();
        startDate.setText(start);
        String end = "End Date: " + ((TripActivity)getActivity()).getEndDate();
        endDate.setText(end);
        String over = "Overview: " + ((TripActivity)getActivity()).getOverview();
        overView.setText(over);

        // set up the buttons
        Button edit = view.findViewById(R.id.sum_edit_button);
        edit.setOnClickListener(editListener());
        Button locations = view.findViewById(R.id.sum_location_button);
        locations.setOnClickListener(locationListener());
        Button addTripper = view.findViewById(R.id.sum_add_tripper);
        addTripper.setOnClickListener(tripperListener());
    }

    /**
     * loadLocations()
     * loads the list of locations for this trip and sends the user to maps activity where they can
     * view those locations
     */
    private void loadLocations() {
        if (getActivity() == null) return;
        // get the trip id
        final String tripId = ((TripActivity)getActivity()).getTripId();
        // get the list of places from the database
        AccessDB.getTripLocations(tripId).addOnCompleteListener(new OnCompleteListener<List<Place>>() {
            @Override
            public void onComplete(@NonNull Task<List<Place>> task) {
                if (task.isSuccessful()) {
                    // get the results
                    ArrayList<Place> places = (ArrayList<Place>) task.getResult();
                    if (places == null) places = new ArrayList<>();
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra(Const.PLACE_LIST_TAG, places);
                    intent.putExtra(Const.TRIP_ID_TAG, tripId);
                    startActivity(intent);
                } else {
                    // tell the user that the locations could not be loaded
                    Toast t = Toast.makeText(getActivity(), "The locations of this trip " +
                            "could not be loaded.", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
            }
        });
    }

    // listeners

    private View.OnClickListener editListener() {
        return new View.OnClickListener() {
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
        };
    }
    private View.OnClickListener locationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load the locations
                loadLocations();
            }
        };
    }
    private View.OnClickListener tripperListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;
                String tripId = ((TripActivity)getActivity()).getTripId();
                if (tripId == null) return;

                // send the user to match activity where they will be able to choose a
                // friend to add to the trip
                Intent intent = new Intent(getActivity(), MatchActivity.class);
                intent.putExtra(Const.SOURCE_TAG, Const.TRIP_ACTIVITY_TAG);
                intent.putExtra(Const.TRIP_ID_TAG, tripId);
                startActivity(intent);
                getActivity().finish();
            }
        };
    }
}
