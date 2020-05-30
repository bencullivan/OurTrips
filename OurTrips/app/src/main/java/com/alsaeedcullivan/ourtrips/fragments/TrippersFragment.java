package com.alsaeedcullivan.ourtrips.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.ViewUserActivity;
import com.alsaeedcullivan.ourtrips.adapters.TripperAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class TrippersFragment extends Fragment {

    private String mTripId;
    private ArrayList<UserSummary> mTrippers;
    private TripperAdapter mAdapter;
    private List<DocumentSnapshot> mTripperDocs;

    public TrippersFragment() {
        // Required empty public constructor
    }

    public static TrippersFragment newInstance() {
        return new TrippersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;

        // instantiate the adapter
        mAdapter = new TripperAdapter(getActivity(), R.layout.fragment_trippers, new ArrayList<UserSummary>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trippers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // if there is no adapter, return
        if (mAdapter == null) return;

        Log.d(Const.TAG, "onViewCreated: trippers frag");

        // get the list view and set the adapter
        ListView list = view.findViewById(R.id.trippers_list);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTrippers == null || mTrippers.size() < position + 1 || getActivity() == null) return;

                // get the tripper at this position
                UserSummary user = mTrippers.get(position);
                if (user == null) return;

                // send the user to view user activity
                Intent intent = new Intent(getActivity(), ViewUserActivity.class);
                intent.putExtra(Const.USER_ID_KEY, user.getUserId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(Const.TAG, "onActivityCreated: trippers fragment");

        TripActivity activity = (TripActivity) getActivity();
        if (activity == null) return;

        // get the trip id
        mTripId = activity.getTripId();
        if (mTripId == null || mAdapter == null) return;
        // get the trippers documents
        new GetTrippersTask().execute();
    }

    /**
     * GetTrippersTask
     * gets the documents corresponding to each tripper on the trip
     */
    private class GetTrippersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null) return null;

            // get the list of documents corresponding to trippers
            AccessDB.getTrippers(mTripId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(Const.TAG, "onComplete: " + Thread.currentThread().getId());
                    QuerySnapshot result = task.getResult();
                    if (task.isSuccessful() && result != null && result.getDocuments().size() > 0) {
                        // get the list of documents and start the async task that will process them
                        mTripperDocs = result.getDocuments();
                        mTrippers = new ArrayList<>();
                        new ExtractTrippersTask().execute();
                    }
                }
            });

            return null;
        }
    }

    /**
     * ExtractTrippersTask
     * extracts a tripper from each document and then updates the list view
     */
    private class ExtractTrippersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripperDocs == null || mTripperDocs.size() == 0 || mTrippers == null) return null;

            // extract a user summary from each document
            for (DocumentSnapshot doc : mTripperDocs) {
                UserSummary tripper = new UserSummary();
                tripper.setUserId(doc.getId());
                String email = (String)doc.get(Const.USER_EMAIL_KEY);
                if (email != null) tripper.setEmail(email);
                String name = (String)doc.get(Const.USER_NAME_KEY);
                if (name != null) tripper.setName(name);
                mTrippers.add(tripper);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // update the list view
            if (mTrippers != null && mTrippers.size() > 0) {
                mAdapter.clear();
                mAdapter.addAll(mTrippers);
                mAdapter.notifyDataSetChanged();
            }

        }
    }
}
