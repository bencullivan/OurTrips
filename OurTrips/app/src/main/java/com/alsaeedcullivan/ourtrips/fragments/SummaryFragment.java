package com.alsaeedcullivan.ourtrips.fragments;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.alsaeedcullivan.ourtrips.comparators.PlaceComparator;
import com.alsaeedcullivan.ourtrips.models.Place;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    private List<DocumentSnapshot> mDocs = new ArrayList<>();
    private List<Place> mPlaces = new ArrayList<>();
    private String mTripId;

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
        Button deleteTrip = view.findViewById(R.id.delete_trip);
        deleteTrip.setOnClickListener(deleteListener());
    }

    /**
     * loadLocations()
     * loads the list of locations for this trip and sends the user to maps activity where they can
     * view those locations
     */
    private void loadLocations() {
        if (getActivity() == null) return;
        // get the trip id
        mTripId = ((TripActivity)getActivity()).getTripId();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // get the list of places from the database
                AccessDB.getTripLocations(mTripId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        if (task.isSuccessful()) {
                            // get the documents and start the async task that will bring the user to
                            // maps activity
                            if (result != null) mDocs = result.getDocuments();
                            new LocationTask().execute();

//                    // get the results
//                    ArrayList<Place> places = (ArrayList<Place>) task.getResult();
//                    if (places == null) places = new ArrayList<>();
//                    Intent intent = new Intent(getActivity(), MapsActivity.class);
//                    intent.putExtra(Const.PLACE_LIST_TAG, places);
//                    intent.putExtra(Const.TRIP_ID_TAG, tripId);
//                    startActivity(intent);
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
        }).start();
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
                String title = ((TripActivity)getActivity()).getTripTitle();
                String start = ((TripActivity)getActivity()).getStartDate();

                // send the user to match activity where they will be able to choose a
                // friend to add to the trip
                Intent intent = new Intent(getActivity(), MatchActivity.class);
                intent.putExtra(Const.SOURCE_TAG, Const.TRIP_ACTIVITY_TAG);
                intent.putExtra(Const.TRIP_ID_TAG, tripId);
                intent.putExtra(Const.TRIP_TITLE_TAG, title);
                intent.putExtra(Const.TRIP_START_TAG, start);
                startActivity(intent);
                getActivity().finish();
            }
        };
    }
    private View.OnClickListener deleteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment.newInstance(CustomDialogFragment.DELETE_TRIP_ID)
                        .show(getParentFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }


    /**
     * LocationTask
     * gets a list of the locations of the trip and send the user to Maps activity
     */
    private class LocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mDocs == null || mDocs.size() == 0) return null;

            Log.d(Const.TAG, "doInBackground: loc " + Thread.currentThread().getId());

            mPlaces = new ArrayList<>();

            // extract a place from each document and return the list of places
            for (DocumentSnapshot doc : mDocs) {
                Place place = new Place();
                place.setDocId(doc.getId());
                place.setName((String)doc.get(Const.TRIP_LOCATION_NAME_KEY));
                String location = (String) doc.get(Const.TRIP_LOCATION_KEY);
                if (location == null) continue;
                String[] coordinates = location.split(",");
                if (coordinates.length < 2) continue;
                place.setLocation(new LatLng(Double.parseDouble(coordinates[0]),
                        Double.parseDouble(coordinates[1])));
                place.setTimeStamp((long)doc.get(Const.TRIP_TIMESTAMP_KEY));
                mPlaces.add(place);
            }

            // sort the places in the order they were added to the map
            if (mPlaces.size() > 0) mPlaces.sort(new PlaceComparator());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mPlaces == null) mPlaces = new ArrayList<>();
            if (mTripId == null) {
                if (getActivity() != null) mTripId = ((TripActivity)getActivity()).getTripId();
                else mTripId = "";
            }
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra(Const.PLACE_LIST_TAG, (ArrayList<Place>) mPlaces);
            intent.putExtra(Const.TRIP_ID_TAG, mTripId);
            startActivity(intent);
        }
    }
}
