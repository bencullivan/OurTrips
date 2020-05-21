package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.cloud.CloudFunctions;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private static final String LIST_KEY = "requests";

    private FirebaseUser mUser;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mList;
    private ListView mListView;
    private int selectedIndex;
    private String selectedEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // set title and back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_findFriends));

        // get the user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // get a reference to the ListView
        mListView = findViewById(R.id.request_list);

        //TODO: OMAR replace with custom adapter to make it look however you want
        // I am using a simple adapter only so that i can test my cloud functions

        // if there is a list in savedInstanceState, there is no need to load from the db
        if (savedInstanceState != null && savedInstanceState.getStringArrayList(LIST_KEY) != null) {
            mList = savedInstanceState.getStringArrayList(LIST_KEY);
            if (mList != null) {
                // set the adapter to the list view with the saved list
                mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(listListener());
            }
        } else if (mUser != null) {
            // instantiate the adapter
            mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
            // set the adapter to the ListView
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(listListener());
            // get all the requests
            Task<List<String>> emailTask = AccessDB.getFriendRequests(mUser.getUid());
            emailTask.addOnCompleteListener(new OnCompleteListener<List<String>>() {
                @Override
                public void onComplete(@NonNull Task<List<String>> task) {
                    if (task.isSuccessful()) {
                        mList = (ArrayList<String>) task.getResult();
                        Log.d(Const.TAG, "onComplete: " + mList);
                        // add them to an adapter
                        if (mList != null) {
                            mAdapter.addAll(mList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_request_button:
                requestDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mList != null && mList.size() > 0) {
            outState.putStringArrayList(LIST_KEY, mList);
        }
    }

    /**
     * requestDialog()
     * display a dialog that allows the user to send a friend request
     */
    private void requestDialog() {
        CustomDialogFragment.newInstance(CustomDialogFragment.FRIEND_ID)
                .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
    }

    /**
     * sendRequest()
     * called when a user presses "send"
     *
     * @param email the email of the person they are sending the request to
     */
    public void sendRequest(String email) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "sendRequest: " + email);
        Log.d(Const.TAG, "sendRequest: " + user.getEmail());
        // if there is a user, send the friend request
        CloudFunctions.sendFriendRequest(user.getEmail(), email);
    }

    /**
     * acceptRequest()
     * called when a user accepts a friend request
     *
     * @param email the email of the new friend
     */
    public void acceptRequest(String email) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "acceptRequest: " + email);
        // if there is a user, accept the friend request
        CloudFunctions.acceptFriendRequest(user.getUid(), email);
    }

    /**
     * declineRequest()
     * declines a friend request
     *
     * @param email the email of the person that sent the request
     */
    public void declineRequest(String email) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "declineRequest: " + email);
        AccessDB.deleteRequest(user.getUid(), email);
    }

    /**
     * removeRequest()
     * removes a request from the list view
     *
     * @param position the position of the request that will be removed
     */
    public void removeRequest(int position) {
        mList.remove(position);
        mAdapter.clear();
        mAdapter.addAll(mList);
        mAdapter.notifyDataSetChanged();
    }

    // returns an OnClickListener for the list view
    private AdapterView.OnItemClickListener listListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // save the data and display the dialog
                selectedIndex = position;
                selectedEmail = mList.get(position);
                CustomDialogFragment.newInstance(CustomDialogFragment.ACCEPT_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    // GETTERS

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectedEmail() {
        return selectedEmail;
    }
}