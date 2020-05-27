package com.alsaeedcullivan.ourtrips.fragments;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;


public class TrippersFragment extends Fragment {

    private String mTripId;
    private ArrayList<UserSummary> mTrippers;
    private TripperAdapter mAdapter;

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

        Log.d(Const.TAG, "onCreate: trippers frag");

        // get the trip id
        mTripId = ((TripActivity)getActivity()).getTripId();
        if (mTripId == null) return;

        // instantiate the adapter
        mAdapter = new TripperAdapter(getActivity(), R.layout.fragment_trippers, new ArrayList<UserSummary>());

        // get the list of trippers and add it to the adapter
        AccessDB.getTrippers(mTripId).addOnCompleteListener(new OnCompleteListener<List<UserSummary>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserSummary>> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                    mTrippers = (ArrayList<UserSummary>) task.getResult();
                    mAdapter.clear();
                    mAdapter.addAll(mTrippers);
                    mAdapter.notifyDataSetChanged();
                    Log.d(Const.TAG, "onComplete: trippers frag");
                }
            }
        });
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
}
