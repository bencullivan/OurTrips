package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.adapters.PlanAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.comparators.PlanComparator;
import com.alsaeedcullivan.ourtrips.models.Plan;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanFragment extends Fragment {

    private static final String TRIP_ID_KEY = "trip_id";
    private static final String PLANS_KEY = "plans";

    private ArrayList<Plan> mPlans = new ArrayList<>();
    private List<DocumentSnapshot> mDocs = new ArrayList<>();
    private String mTripId;
    private PlanAdapter mAdapter;
    private EditText mMessageEdit;
    private String mUserName;
    private RecyclerView mRecycle;
    private FirebaseUser mUser;
    private String mMessage;
    private Plan mPlan;

    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance() {
        return new PlanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) return;

        // instantiate the adapter
        mAdapter = new PlanAdapter(new ArrayList<Plan>());

        // if instance state has been saved
        if (savedInstanceState != null && savedInstanceState.getString(TRIP_ID_KEY) != null &&
                savedInstanceState.getParcelableArrayList(PLANS_KEY) != null) {
            mTripId = savedInstanceState.getString(TRIP_ID_KEY);
            mPlans = savedInstanceState.getParcelableArrayList(PLANS_KEY);
            mAdapter.setData(mPlans);
        }
        // if instance state has not been saved
        else {
            // get the trip id and user
            mTripId = ((TripActivity)getActivity()).getTripId();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mTripId == null || mUser == null) return;
            // db operations on background thread
            Log.d(Const.TAG, "onCreate: about to execute");
            new GetPlansTask().execute();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                }
//            }).start();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTripId != null) outState.putString(TRIP_ID_KEY, mTripId);
        if (mPlans.size() > 0) outState.putParcelableArrayList(PLANS_KEY, mPlans);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAdapter == null || view.getContext() == null) return;

        // set up the recycler view
        mRecycle = view.findViewById(R.id.plan_recycle);
        LinearLayoutManager man = new LinearLayoutManager(view.getContext());
        mRecycle.setLayoutManager(man);
        mRecycle.setAdapter(mAdapter);

        // edit text
        mMessageEdit = view.findViewById(R.id.plan_type_box);

        // set up the send button
        Button send = view.findViewById(R.id.plan_send);
        send.setOnClickListener(sendListener());
    }

    // on click listener for the send button
    private View.OnClickListener sendListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get this user
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                // get the message
                mMessage = mMessageEdit.getText().toString();
                // if they have not typed anything, do nothing
                if (mTripId == null || mUser == null || mUserName == null || mMessage
                        .replaceAll("\\s","").equals("")) return;
                mMessageEdit.setText("");

                // create a plan
                mPlan = new Plan();
                mPlan.setMessage(mMessage);
                mPlan.setPlanUserName(mUserName);
                mPlan.setPlanUserId(mUser.getUid());

                // send the plan
                new SendPlanTask().execute();

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // get the trip id and the user name
//                        String tripId = getTripId();
//                        String userName = getUserName();
//                        if (tripId == null || userName == null) return;
//
//                    }
//                }).start();
            }
        };
    }
//
//    // getters
//    public String getTripId() {
//        return mTripId;
//    }
//    public String getUserName() {
//        return mUserName;
//    }

    /**
     * SortPlanTask
     * sorts the plans and adds them to the recycler view
     */
    private class SortPlanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mPlans == null || mPlans.size() == 0) return null;
            mPlans.sort(new PlanComparator());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(Const.TAG, "onPostExecute: done sorting plans");
            // add the list of plans to the adapter
            if (mAdapter == null || mPlans == null || mRecycle == null) return;
            mAdapter.setData(mPlans);
            mRecycle.scrollToPosition(mPlans.size() - 1);
        }
    }

    /**
     * SetUpPlansTask
     * gets the list of plans from a list of documents
     */
    private class SetUpPlansTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mDocs == null || mDocs.size() == 0 || mPlans == null) return null;

            // extract a comment from each document
            for (DocumentSnapshot doc : mDocs) {
                Plan plan = new Plan();
                plan.setPlanUserId((String)doc.get(Const.USER_ID_KEY));
                plan.setPlanUserName((String)doc.get(Const.USER_NAME_KEY));
                plan.setMessage((String)doc.get(Const.TRIP_COMMENT_KEY));
                plan.setPlanDocId(doc.getId());
                plan.setPlanTimeStamp((long)doc.get(Const.TRIP_TIMESTAMP_KEY));
                mPlans.add(plan);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // if there are plans, start the async task that will sort them
            if (mPlans != null && mPlans.size() > 0) new SortPlanTask().execute();
        }
    }

    /**
     * GetPlansTask
     * gets the plans from the DB
     */
    private class GetPlansTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mTripId == null) return null;

            Log.d(Const.TAG, "doInBackground: plans task " + Thread.currentThread().getId());
            // get this user's name
            AccessDB.getUserName(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    Log.d(Const.TAG, "onComplete: name loaded");
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(Const.TAG, "onComplete: " + Thread.currentThread().getId());
                        mUserName = task.getResult();
                    }
                }
            });
            // get the list of plans, sort them and add them to the adapter
            AccessDB.getTripComments(mTripId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot result = task.getResult();
                    if (task.isSuccessful() && result != null && result.getDocuments().size() > 0) {
                        Log.d(Const.TAG, "onComplete: " + Thread.currentThread().getId());
                        mDocs = result.getDocuments();
                        mPlans = new ArrayList<>();
                        // extract the plans from the list of documents
                        new SetUpPlansTask().execute();
                    }
                }
            });
            return null;
        }
    }

    /**
     * SendPlanTask
     * adds the plan to the db
     */
    private class SendPlanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null || mMessage == null || mUser == null || mUserName == null) return null;

            // add the plan to the db
            AccessDB.addTripComment(mTripId, mMessage, mUserName, mUser.getUid(), new Date().getTime())
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.d(Const.TAG, "onComplete: done adding plan to db");
                            if (task.isSuccessful()) {
                                Log.d(Const.TAG, "onComplete: " + Thread.currentThread().getId());
                                if (mPlan == null || mAdapter == null || mRecycle == null) return;

                                // display this plan
                                mPlans.add(mPlan);
                                mAdapter.setData(mPlans);
                                mRecycle.scrollToPosition(mPlans.size() - 1);

                                // hide the keyboard
                                if (getActivity() == null) return;
                                InputMethodManager imm = (InputMethodManager) getActivity()
                                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                                if (imm != null) imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }
                    });
            return null;
        }
    }
}
